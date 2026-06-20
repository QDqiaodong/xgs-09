package com.journal.inspiration.service.impl;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.journal.inspiration.dto.WorkElementDTO;
import com.journal.inspiration.dto.WorkPublishDTO;
import com.journal.inspiration.dto.WorkQueryDTO;
import com.journal.inspiration.common.ColorSchemeValidator;
import com.journal.inspiration.common.CoverTypeEnum;
import com.journal.inspiration.common.ElementCategoryEnum;
import com.journal.inspiration.common.LayoutTemplateConfig;
import com.journal.inspiration.common.WorkStatusEnum;
import com.journal.inspiration.entity.*;
import com.journal.inspiration.mapper.*;
import com.journal.inspiration.service.*;
import com.journal.inspiration.vo.CategoryStatsVO;
import com.journal.inspiration.vo.CategoryVO;
import com.journal.inspiration.vo.StatusStatsVO;
import com.journal.inspiration.vo.WorkElementGroupVO;
import com.journal.inspiration.vo.WorkElementVO;
import com.journal.inspiration.vo.WorkStatsVO;
import com.journal.inspiration.vo.WorkVO;
import lombok.RequiredArgsConstructor;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class JournalWorkServiceImpl extends ServiceImpl<JournalWorkMapper, JournalWork> implements JournalWorkService {

    private final WorkCategoryMapper workCategoryMapper;
    private final CategoryMapper categoryMapper;
    private final UserMapper userMapper;
    private final FavoriteService favoriteService;
    private final WorkElementMapper workElementMapper;

    @Override
    @Transactional(rollbackFor = Exception.class)
    @CacheEvict(value = {"latestWorks", "hotWorks"}, allEntries = true)
    public Long publishWork(WorkPublishDTO dto) {
        User user = userMapper.selectById(dto.getUserId());
        if (user == null) {
            throw new IllegalArgumentException("用户不存在");
        }

        List<Long> categoryIds = new ArrayList<>();
        if (dto.getCategoryIds() != null) {
            categoryIds = dto.getCategoryIds().stream().distinct().collect(Collectors.toList());
        }
        List<Category> categories = new ArrayList<>();
        if (!categoryIds.isEmpty()) {
            categories = categoryMapper.selectBatchIds(categoryIds);
            if (categories.size() != categoryIds.size()) {
                throw new IllegalArgumentException("存在无效或已失效的分类编号");
            }
        }

        if (dto.getElements() != null && !dto.getElements().isEmpty()) {
            for (WorkElementDTO elementDTO : dto.getElements()) {
                if (ElementCategoryEnum.getByCode(elementDTO.getCategory()) == null) {
                    throw new IllegalArgumentException("存在无效的元素分类: " + elementDTO.getCategory());
                }
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

        if (work.getLayoutConfig() == null || work.getLayoutConfig().trim().isEmpty()) {
            String defaultLayoutJson = LayoutTemplateConfig.getDefaultLayoutJson(categories);
            if (defaultLayoutJson != null) {
                work.setLayoutConfig(defaultLayoutJson);
            }
        }

        work.setViewCount(0);
        work.setLikeCount(0);
        work.setFavoriteCount(0);
        work.setStatus(WorkStatusEnum.PUBLIC.getCode());
        save(work);

        for (Long categoryId : categoryIds) {
            WorkCategory workCategory = new WorkCategory();
            workCategory.setWorkId(work.getId());
            workCategory.setCategoryId(categoryId);
            workCategoryMapper.insert(workCategory);
        }

        if (dto.getElements() != null && !dto.getElements().isEmpty()) {
            int sort = 0;
            for (WorkElementDTO elementDTO : dto.getElements()) {
                WorkElement element = new WorkElement();
                BeanUtil.copyProperties(elementDTO, element);
                element.setWorkId(work.getId());
                if (element.getQuantity() == null || element.getQuantity() < 1) {
                    element.setQuantity(1);
                }
                if (element.getSort() == null) {
                    element.setSort(sort++);
                }
                workElementMapper.insert(element);
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

        WorkVO vo = convertToVO(work);
        
        if (userId != null) {
            vo.setIsFavorite(favoriteService.isFavorite(userId, id));
        }

        return vo;
    }

    @Override
    public Page<WorkVO> getWorkList(WorkQueryDTO dto) {
        LambdaQueryWrapper<JournalWork> wrapper = new LambdaQueryWrapper<>();

        if (dto.getStatus() != null) {
            wrapper.eq(JournalWork::getStatus, dto.getStatus());
        } else if (dto.getUserId() == null) {
            wrapper.eq(JournalWork::getStatus, WorkStatusEnum.PUBLIC.getCode());
        }

        if (StrUtil.isNotBlank(dto.getKeyword())) {
            wrapper.and(w -> w.like(JournalWork::getTitle, dto.getKeyword())
                    .or().like(JournalWork::getContent, dto.getKeyword()));
        }

        if (dto.getUserId() != null) {
            wrapper.eq(JournalWork::getUserId, dto.getUserId());
        }

        List<Long> filteredWorkIds = getFilteredWorkIds(dto);
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

    private List<Long> getFilteredWorkIds(WorkQueryDTO dto) {
        List<Long> specificCategoryIds = new ArrayList<>();
        if (dto.getCategoryId() != null) {
            specificCategoryIds.add(dto.getCategoryId());
        }
        if (dto.getStyleCategoryId() != null) {
            specificCategoryIds.add(dto.getStyleCategoryId());
        }
        if (dto.getSceneCategoryId() != null) {
            specificCategoryIds.add(dto.getSceneCategoryId());
        }

        Set<Long> validCategoryIds = null;
        if (!specificCategoryIds.isEmpty()) {
            List<Category> existing = categoryMapper.selectBatchIds(specificCategoryIds);
            validCategoryIds = existing.stream().map(Category::getId).collect(Collectors.toSet());
        }

        List<List<Long>> dimensionWorkIds = new ArrayList<>();
        if (dto.getCategoryId() != null) {
            dimensionWorkIds.add(workIdsByCategory(dto.getCategoryId(), validCategoryIds));
        }
        if (dto.getStyleCategoryId() != null) {
            dimensionWorkIds.add(workIdsByCategory(dto.getStyleCategoryId(), validCategoryIds));
        }
        if (dto.getSceneCategoryId() != null) {
            dimensionWorkIds.add(workIdsByCategory(dto.getSceneCategoryId(), validCategoryIds));
        }

        if (dto.getCategoryType() != null) {
            List<Long> typeCategoryIds = categoryMapper.selectList(
                    new LambdaQueryWrapper<Category>().eq(Category::getType, dto.getCategoryType())
            ).stream().map(Category::getId).collect(Collectors.toList());

            if (typeCategoryIds.isEmpty()) {
                dimensionWorkIds.add(new ArrayList<>());
            } else {
                List<Long> typeWorkIds = workCategoryMapper.selectList(
                        new LambdaQueryWrapper<WorkCategory>().in(WorkCategory::getCategoryId, typeCategoryIds)
                ).stream().map(WorkCategory::getWorkId).distinct().collect(Collectors.toList());
                dimensionWorkIds.add(typeWorkIds);
            }
        }

        if (dimensionWorkIds.isEmpty()) {
            return null;
        }

        List<Long> result = new ArrayList<>(dimensionWorkIds.get(0));
        for (int i = 1; i < dimensionWorkIds.size(); i++) {
            Set<Long> other = new HashSet<>(dimensionWorkIds.get(i));
            result.removeIf(id -> !other.contains(id));
        }
        return result;
    }

    private List<Long> workIdsByCategory(Long categoryId, Set<Long> validCategoryIds) {
        if (validCategoryIds != null && !validCategoryIds.contains(categoryId)) {
            return new ArrayList<>();
        }
        return workCategoryMapper.selectList(
                new LambdaQueryWrapper<WorkCategory>().eq(WorkCategory::getCategoryId, categoryId)
        ).stream().map(WorkCategory::getWorkId).distinct().collect(Collectors.toList());
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

        LambdaQueryWrapper<JournalWork> workWrapper = new LambdaQueryWrapper<>();
        workWrapper.eq(JournalWork::getUserId, userId);
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
                Set<Long> validCategoryIds = categories.stream()
                        .map(Category::getId)
                        .collect(Collectors.toSet());

                java.util.Map<Long, Long> categoryCountMap = workCategories.stream()
                        .filter(wc -> validCategoryIds.contains(wc.getCategoryId()))
                        .collect(Collectors.groupingBy(
                                WorkCategory::getCategoryId,
                                Collectors.mapping(WorkCategory::getWorkId, Collectors.toSet())))
                        .entrySet().stream()
                        .collect(Collectors.toMap(Map.Entry::getKey, e -> (long) e.getValue().size()));

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

        if (StrUtil.isNotBlank(work.getColorScheme())) {
            vo.setColorScheme(enrichColorSchemeWithType(work.getColorScheme()));
        }

        vo.setElementGroups(getWorkElementGroups(work.getId()));

        vo.setIsFavorite(false);
        return vo;
    }

    private List<WorkElementGroupVO> getWorkElementGroups(Long workId) {
        List<WorkElement> elements = workElementMapper.selectList(
                new LambdaQueryWrapper<WorkElement>()
                        .eq(WorkElement::getWorkId, workId)
                        .orderByAsc(WorkElement::getCategory, WorkElement::getSort)
        );

        if (elements.isEmpty()) {
            return new ArrayList<>();
        }

        Map<Integer, List<WorkElement>> groupMap = elements.stream()
                .collect(Collectors.groupingBy(WorkElement::getCategory));

        List<WorkElementGroupVO> groupVOList = new ArrayList<>();

        for (ElementCategoryEnum categoryEnum : ElementCategoryEnum.values()) {
            List<WorkElement> categoryElements = groupMap.get(categoryEnum.getCode());
            if (categoryElements != null && !categoryElements.isEmpty()) {
                WorkElementGroupVO groupVO = new WorkElementGroupVO();
                groupVO.setCategory(categoryEnum.getCode());
                groupVO.setCategoryDesc(categoryEnum.getDesc());
                groupVO.setCategoryIcon(categoryEnum.getIcon());

                List<WorkElementVO> elementVOList = categoryElements.stream()
                        .sorted(Comparator.comparing(WorkElement::getSort, Comparator.nullsLast(Integer::compareTo)))
                        .map(el -> {
                            WorkElementVO evo = new WorkElementVO();
                            BeanUtil.copyProperties(el, evo);
                            evo.setCategoryDesc(categoryEnum.getDesc());
                            evo.setCategoryIcon(categoryEnum.getIcon());
                            return evo;
                        })
                        .collect(Collectors.toList());

                groupVO.setElements(elementVOList);

                int totalCount = elementVOList.stream()
                        .mapToInt(evo -> evo.getQuantity() != null ? evo.getQuantity() : 0)
                        .sum();
                groupVO.setTotalCount(totalCount);

                groupVOList.add(groupVO);
            }
        }

        return groupVOList;
    }

    private String enrichColorSchemeWithType(String colorSchemeJson) {
        try {
            com.fasterxml.jackson.databind.ObjectMapper mapper = new com.fasterxml.jackson.databind.ObjectMapper();
            java.util.List<java.util.Map<String, Object>> swatches = mapper.readValue(
                    colorSchemeJson,
                    new com.fasterxml.jackson.core.type.TypeReference<java.util.List<java.util.Map<String, Object>>>() {}
            );

            if (swatches == null || swatches.isEmpty()) {
                return colorSchemeJson;
            }

            boolean hasType = swatches.stream().anyMatch(s -> s.get("type") != null);
            if (hasType) {
                return colorSchemeJson;
            }

            for (java.util.Map<String, Object> swatch : swatches) {
                Object purposeObj = swatch.get("purpose");
                Object nameObj = swatch.get("name");
                String purpose = purposeObj instanceof String ? (String) purposeObj : "";
                String name = nameObj instanceof String ? (String) nameObj : "";
                String combined = (purpose + " " + name).toLowerCase();

                String inferredType = inferColorType(combined);
                if (inferredType != null) {
                    swatch.put("type", inferredType);
                }
            }

            return mapper.writeValueAsString(swatches);
        } catch (Exception e) {
            return colorSchemeJson;
        }
    }

    private String inferColorType(String text) {
        if (text.contains("主色") || text.contains("主色调") || text.contains("primary")) {
            return ColorSchemeValidator.TYPE_PRIMARY;
        }
        if (text.contains("辅助色") || text.contains("次要") || text.contains("secondary")) {
            return ColorSchemeValidator.TYPE_SECONDARY;
        }
        if (text.contains("点缀色") || text.contains("强调色") || text.contains("亮点") || text.contains("accent")) {
            return ColorSchemeValidator.TYPE_ACCENT;
        }
        if (text.contains("背景")) {
            return ColorSchemeValidator.TYPE_SECONDARY;
        }
        return null;
    }
}
