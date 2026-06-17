package com.journal.inspiration.service.impl;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.journal.inspiration.dto.WorkPublishDTO;
import com.journal.inspiration.dto.WorkQueryDTO;
import com.journal.inspiration.common.ColorSchemeValidator;
import com.journal.inspiration.common.CoverTypeEnum;
import com.journal.inspiration.common.UserContext;
import com.journal.inspiration.common.WorkStatusEnum;
import com.journal.inspiration.entity.*;
import com.journal.inspiration.mapper.*;
import com.journal.inspiration.service.*;
import com.journal.inspiration.vo.CategoryStatsVO;
import com.journal.inspiration.vo.CategoryVO;
import com.journal.inspiration.vo.StatusStatsVO;
import com.journal.inspiration.vo.WorkStatsVO;
import com.journal.inspiration.vo.WorkVO;
import lombok.RequiredArgsConstructor;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class JournalWorkServiceImpl extends ServiceImpl<JournalWorkMapper, JournalWork> implements JournalWorkService {

    private final WorkCategoryMapper workCategoryMapper;
    private final CategoryMapper categoryMapper;
    private final UserMapper userMapper;
    private final FavoriteService favoriteService;

    @Override
    @Transactional(rollbackFor = Exception.class)
    @CacheEvict(value = {"latestWorks", "hotWorks"}, allEntries = true)
    public Long publishWork(WorkPublishDTO dto) {
        User user = userMapper.selectById(dto.getUserId());
        if (user == null) {
            throw new IllegalArgumentException("用户不存在");
        }

        if (dto.getCategoryIds() != null && !dto.getCategoryIds().isEmpty()) {
            List<Category> categories = categoryMapper.selectBatchIds(dto.getCategoryIds());
            if (categories.size() != dto.getCategoryIds().size()) {
                throw new IllegalArgumentException("存在无效的分类编号");
            }
        }

        ColorSchemeValidator.ValidationResult colorValidation = ColorSchemeValidator.validate(dto.getColorScheme());
        if (!colorValidation.isValid()) {
            throw new IllegalArgumentException(colorValidation.getMessage());
        }

        JournalWork work = new JournalWork();
        BeanUtil.copyProperties(dto, work);
        if (work.getCoverType() == null || CoverTypeEnum.getByCode(work.getCoverType()) == null) {
            work.setCoverType(CoverTypeEnum.getDefault().getCode());
        }
        work.setViewCount(0);
        work.setLikeCount(0);
        work.setFavoriteCount(0);
        work.setStatus(WorkStatusEnum.PUBLIC.getCode());
        save(work);

        if (dto.getCategoryIds() != null && !dto.getCategoryIds().isEmpty()) {
            for (Long categoryId : dto.getCategoryIds()) {
                WorkCategory workCategory = new WorkCategory();
                workCategory.setWorkId(work.getId());
                workCategory.setCategoryId(categoryId);
                workCategoryMapper.insert(workCategory);
            }
        }

        return work.getId();
    }

    @Override
    public WorkVO getWorkDetail(Long id, Long userId) {
        JournalWork work = getById(id);
        if (work == null) {
            return null;
        }

        boolean isOwner = userId != null && work.getUserId().equals(userId);
        if (!WorkStatusEnum.isVisibleToPublic(work.getStatus()) && !isOwner) {
            return null;
        }

        if (WorkStatusEnum.isPublic(work.getStatus())) {
            incrementViewCount(id);
        }

        WorkVO vo = convertToVO(work);

        if (userId != null) {
            vo.setIsFavorite(favoriteService.isFavorite(userId, id));
        }

        return vo;
    }

    @Override
    public Page<WorkVO> getWorkList(WorkQueryDTO dto) {
        LambdaQueryWrapper<JournalWork> wrapper = new LambdaQueryWrapper<>();

        Long currentUserId = UserContext.getCurrentUserIdSafe();
        boolean isViewingOwn = dto.getUserId() != null && dto.getUserId().equals(currentUserId);

        if (dto.getStatus() != null) {
            wrapper.eq(JournalWork::getStatus, dto.getStatus());
        } else if (dto.getUserId() == null || !isViewingOwn) {
            wrapper.eq(JournalWork::getStatus, WorkStatusEnum.PUBLIC.getCode());
        }

        if (StrUtil.isNotBlank(dto.getKeyword())) {
            wrapper.like(JournalWork::getTitle, dto.getKeyword())
                    .or().like(JournalWork::getContent, dto.getKeyword());
        }

        if (dto.getUserId() != null) {
            wrapper.eq(JournalWork::getUserId, dto.getUserId());
        }

        List<Long> filteredWorkIds = collectWorkIdsByCategories(dto);
        if (filteredWorkIds != null) {
            if (filteredWorkIds.isEmpty()) {
                return new Page<>(dto.getPageNum(), dto.getPageSize(), 0);
            }
            wrapper.in(JournalWork::getId, filteredWorkIds);
        }

        wrapper.orderByDesc(JournalWork::getCreateTime);

        Page<JournalWork> page = page(new Page<>(dto.getPageNum(), dto.getPageSize()), wrapper);
        Page<WorkVO> voPage = new Page<>(page.getCurrent(), page.getSize(), page.getTotal());
        voPage.setRecords(page.getRecords().stream().map(this::convertToVO).collect(Collectors.toList()));

        return voPage;
    }

    private List<Long> collectWorkIdsByCategories(WorkQueryDTO dto) {
        List<Long> categoryIds = new java.util.ArrayList<>();

        if (dto.getStyleCategoryId() != null) {
            categoryIds.add(dto.getStyleCategoryId());
        }
        if (dto.getSceneCategoryId() != null) {
            categoryIds.add(dto.getSceneCategoryId());
        }
        if (dto.getCategoryId() != null) {
            categoryIds.add(dto.getCategoryId());
        }

        if (categoryIds.isEmpty()) {
            return null;
        }

        LambdaQueryWrapper<WorkCategory> wcWrapper = new LambdaQueryWrapper<>();
        wcWrapper.in(WorkCategory::getCategoryId, categoryIds);
        List<WorkCategory> workCategories = workCategoryMapper.selectList(wcWrapper);

        if (workCategories.isEmpty()) {
            return new java.util.ArrayList<>();
        }

        java.util.Map<Long, Long> workCategoryCountMap = workCategories.stream()
                .collect(Collectors.groupingBy(WorkCategory::getWorkId, Collectors.counting()));

        long requiredMatchCount = categoryIds.size();

        return workCategoryCountMap.entrySet().stream()
                .filter(entry -> entry.getValue() >= requiredMatchCount)
                .map(java.util.Map.Entry::getKey)
                .collect(Collectors.toList());
    }

    @Override
    @Cacheable(value = "latestWorks", key = "#pageNum + '_' + #pageSize")
    public Page<WorkVO> getLatestWorks(Integer pageNum, Integer pageSize) {
        WorkQueryDTO dto = new WorkQueryDTO();
        dto.setPageNum(pageNum);
        dto.setPageSize(pageSize);
        dto.setStatus(WorkStatusEnum.PUBLIC.getCode());
        return getWorkList(dto);
    }

    @Override
    @Cacheable(value = "hotWorks", key = "#pageNum + '_' + #pageSize")
    public Page<WorkVO> getHotWorks(Integer pageNum, Integer pageSize) {
        LambdaQueryWrapper<JournalWork> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(JournalWork::getStatus, WorkStatusEnum.PUBLIC.getCode());
        wrapper.orderByDesc(JournalWork::getViewCount, JournalWork::getFavoriteCount);

        Page<JournalWork> page = page(new Page<>(pageNum, pageSize), wrapper);
        Page<WorkVO> voPage = new Page<>(page.getCurrent(), page.getSize(), page.getTotal());
        voPage.setRecords(page.getRecords().stream().map(this::convertToVO).collect(Collectors.toList()));

        return voPage;
    }

    @Override
    public Page<WorkVO> getUserWorks(Long userId, Integer pageNum, Integer pageSize) {
        WorkQueryDTO dto = new WorkQueryDTO();
        dto.setUserId(userId);
        dto.setPageNum(pageNum);
        dto.setPageSize(pageSize);
        return getWorkList(dto);
    }

    @Override
    @CacheEvict(value = {"latestWorks", "hotWorks"}, allEntries = true)
    public boolean updateWorkStatus(Long id, Integer status, Long operatorId) {
        if (WorkStatusEnum.getByCode(status) == null) {
            throw new IllegalArgumentException("非法的状态值: " + status);
        }
        JournalWork existWork = getById(id);
        if (existWork == null) {
            throw new IllegalArgumentException("作品不存在");
        }
        if (operatorId == null || !existWork.getUserId().equals(operatorId)) {
            throw new SecurityException("无权限修改该作品状态");
        }
        JournalWork work = new JournalWork();
        work.setId(id);
        work.setStatus(status);
        return updateById(work);
    }

    @Override
    public boolean incrementViewCount(Long id) {
        return baseMapper.incrementViewCountById(id) > 0;
    }

    @Override
    public WorkStatsVO getUserWorkStats(Long userId) {
        WorkStatsVO stats = new WorkStatsVO();

        Long currentUserId = UserContext.getCurrentUserIdSafe();
        boolean isViewingOwn = userId != null && userId.equals(currentUserId);

        LambdaQueryWrapper<JournalWork> workWrapper = new LambdaQueryWrapper<>();
        workWrapper.eq(JournalWork::getUserId, userId);
        if (!isViewingOwn) {
            workWrapper.eq(JournalWork::getStatus, WorkStatusEnum.PUBLIC.getCode());
        }
        List<JournalWork> allWorks = list(workWrapper);

        int total = allWorks.size();
        int publicCount = 0;
        int privateCount = 0;
        int archivedCount = 0;

        for (JournalWork work : allWorks) {
            if (WorkStatusEnum.isPublic(work.getStatus())) {
                publicCount++;
            } else if (WorkStatusEnum.isPrivate(work.getStatus())) {
                privateCount++;
            } else if (WorkStatusEnum.isArchived(work.getStatus())) {
                archivedCount++;
            }
        }

        if (isViewingOwn) {
            stats.setTotalWorks(total);
            stats.setPublicWorks(publicCount);
            stats.setPrivateWorks(privateCount);
            stats.setArchivedWorks(archivedCount);

            List<StatusStatsVO> statusStatsList = new java.util.ArrayList<>();
            for (WorkStatusEnum statusEnum : WorkStatusEnum.values()) {
                StatusStatsVO statusStats = new StatusStatsVO();
                statusStats.setStatus(statusEnum.getCode());
                statusStats.setStatusDesc(statusEnum.getDesc());
                int count = 0;
                if (WorkStatusEnum.PUBLIC.equals(statusEnum)) {
                    count = publicCount;
                } else if (WorkStatusEnum.PRIVATE.equals(statusEnum)) {
                    count = privateCount;
                } else if (WorkStatusEnum.ARCHIVED.equals(statusEnum)) {
                    count = archivedCount;
                }
                statusStats.setCount(count);
                statusStatsList.add(statusStats);
            }
            stats.setStatusStats(statusStatsList);
        } else {
            stats.setTotalWorks(publicCount);
            stats.setPublicWorks(publicCount);
            stats.setPrivateWorks(0);
            stats.setArchivedWorks(0);

            List<StatusStatsVO> statusStatsList = new java.util.ArrayList<>();
            StatusStatsVO publicStats = new StatusStatsVO();
            publicStats.setStatus(WorkStatusEnum.PUBLIC.getCode());
            publicStats.setStatusDesc(WorkStatusEnum.PUBLIC.getDesc());
            publicStats.setCount(publicCount);
            statusStatsList.add(publicStats);
            stats.setStatusStats(statusStatsList);
        }

        if (!allWorks.isEmpty()) {
            List<Long> workIds = allWorks.stream()
                    .map(JournalWork::getId)
                    .collect(Collectors.toList());

            List<WorkCategory> workCategories = workCategoryMapper.selectList(
                    new LambdaQueryWrapper<WorkCategory>().in(WorkCategory::getWorkId, workIds)
            );

            if (!workCategories.isEmpty()) {
                List<Long> categoryIds = workCategories.stream()
                        .map(WorkCategory::getCategoryId)
                        .distinct()
                        .collect(Collectors.toList());

                List<Category> categories = categoryMapper.selectBatchIds(categoryIds);

                java.util.Map<Long, Long> categoryCountMap = workCategories.stream()
                        .collect(Collectors.groupingBy(WorkCategory::getCategoryId, Collectors.counting()));

                List<CategoryStatsVO> styleStats = new java.util.ArrayList<>();
                List<CategoryStatsVO> sceneStats = new java.util.ArrayList<>();

                for (Category category : categories) {
                    CategoryStatsVO categoryStats = new CategoryStatsVO();
                    categoryStats.setId(category.getId());
                    categoryStats.setName(category.getName());
                    categoryStats.setType(category.getType());
                    categoryStats.setCount(categoryCountMap.getOrDefault(category.getId(), 0L).intValue());

                    if ("style".equals(category.getType())) {
                        styleStats.add(categoryStats);
                    } else if ("scene".equals(category.getType())) {
                        sceneStats.add(categoryStats);
                    }
                }

                styleStats.sort((a, b) -> b.getCount() - a.getCount());
                sceneStats.sort((a, b) -> b.getCount() - a.getCount());

                stats.setStyleStats(styleStats);
                stats.setSceneStats(sceneStats);
            }
        }

        if (stats.getStyleStats() == null) {
            stats.setStyleStats(new java.util.ArrayList<>());
        }
        if (stats.getSceneStats() == null) {
            stats.setSceneStats(new java.util.ArrayList<>());
        }

        return stats;
    }

    private WorkVO convertToVO(JournalWork work) {
        WorkVO vo = new WorkVO();
        BeanUtil.copyProperties(work, vo);

        CoverTypeEnum coverType = CoverTypeEnum.getByCode(work.getCoverType());
        if (coverType == null) {
            coverType = CoverTypeEnum.getDefault();
        }
        vo.setCoverType(coverType.getCode());
        vo.setCoverTypeDesc(coverType.getDesc());
        vo.setCoverWidthRatio(coverType.getWidthRatio());
        vo.setCoverHeightRatio(coverType.getHeightRatio());
        vo.setCoverAspectRatio(coverType.getAspectRatio());

        User user = userMapper.selectById(work.getUserId());
        if (user != null) {
            vo.setNickname(user.getNickname());
            vo.setAvatar(user.getAvatar());
        }

        List<WorkCategory> workCategories = workCategoryMapper.selectList(
                new LambdaQueryWrapper<WorkCategory>().eq(WorkCategory::getWorkId, work.getId())
        );

        if (!workCategories.isEmpty()) {
            List<Long> categoryIds = workCategories.stream()
                    .map(WorkCategory::getCategoryId)
                    .collect(Collectors.toList());

            List<Category> categories = categoryMapper.selectBatchIds(categoryIds);
            List<CategoryVO> categoryVOS = categories.stream()
                    .map(cat -> {
                        CategoryVO catVO = new CategoryVO();
                        BeanUtil.copyProperties(cat, catVO);
                        return catVO;
                    })
                    .collect(Collectors.toList());
            vo.setCategories(categoryVOS);
        }

        vo.setIsFavorite(false);
        return vo;
    }
}
