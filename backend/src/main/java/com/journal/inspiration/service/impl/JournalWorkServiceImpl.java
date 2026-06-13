package com.journal.inspiration.service.impl;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.journal.inspiration.dto.WorkPublishDTO;
import com.journal.inspiration.dto.WorkQueryDTO;
import com.journal.inspiration.entity.*;
import com.journal.inspiration.mapper.*;
import com.journal.inspiration.service.*;
import com.journal.inspiration.vo.CategoryVO;
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
        JournalWork work = new JournalWork();
        BeanUtil.copyProperties(dto, work);
        work.setViewCount(0);
        work.setLikeCount(0);
        work.setFavoriteCount(0);
        work.setStatus(1);
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

        WorkVO vo = convertToVO(work);
        
        if (userId != null) {
            vo.setIsFavorite(favoriteService.isFavorite(userId, id));
        }

        return vo;
    }

    @Override
    public Page<WorkVO> getWorkList(WorkQueryDTO dto) {
        LambdaQueryWrapper<JournalWork> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(JournalWork::getStatus, 1);

        if (StrUtil.isNotBlank(dto.getKeyword())) {
            wrapper.like(JournalWork::getTitle, dto.getKeyword())
                    .or().like(JournalWork::getContent, dto.getKeyword());
        }

        if (dto.getUserId() != null) {
            wrapper.eq(JournalWork::getUserId, dto.getUserId());
        }

        if (dto.getStatus() != null) {
            wrapper.eq(JournalWork::getStatus, dto.getStatus());
        }

        wrapper.orderByDesc(JournalWork::getCreateTime);

        Page<JournalWork> page = page(new Page<>(dto.getPageNum(), dto.getPageSize()), wrapper);
        Page<WorkVO> voPage = new Page<>(page.getCurrent(), page.getSize(), page.getTotal());
        voPage.setRecords(page.getRecords().stream().map(this::convertToVO).collect(Collectors.toList()));

        return voPage;
    }

    @Override
    @Cacheable(value = "latestWorks", key = "#pageNum + '_' + #pageSize")
    public Page<WorkVO> getLatestWorks(Integer pageNum, Integer pageSize) {
        WorkQueryDTO dto = new WorkQueryDTO();
        dto.setPageNum(pageNum);
        dto.setPageSize(pageSize);
        dto.setStatus(1);
        return getWorkList(dto);
    }

    @Override
    @Cacheable(value = "hotWorks", key = "#pageNum + '_' + #pageSize")
    public Page<WorkVO> getHotWorks(Integer pageNum, Integer pageSize) {
        LambdaQueryWrapper<JournalWork> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(JournalWork::getStatus, 1);
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
    public boolean updateWorkStatus(Long id, Integer status) {
        JournalWork work = new JournalWork();
        work.setId(id);
        work.setStatus(status);
        return updateById(work);
    }

    @Override
    public boolean incrementViewCount(Long id) {
        JournalWork work = getById(id);
        if (work != null) {
            work.setViewCount(work.getViewCount() + 1);
            return updateById(work);
        }
        return false;
    }

    private WorkVO convertToVO(JournalWork work) {
        WorkVO vo = new WorkVO();
        BeanUtil.copyProperties(work, vo);

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
