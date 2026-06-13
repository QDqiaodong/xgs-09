package com.journal.inspiration.service.impl;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.journal.inspiration.entity.Category;
import com.journal.inspiration.entity.WorkCategory;
import com.journal.inspiration.mapper.CategoryMapper;
import com.journal.inspiration.mapper.WorkCategoryMapper;
import com.journal.inspiration.service.CategoryService;
import com.journal.inspiration.vo.CategoryVO;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class CategoryServiceImpl extends ServiceImpl<CategoryMapper, Category> implements CategoryService {

    private final WorkCategoryMapper workCategoryMapper;

    @Override
    public List<CategoryVO> getCategoryList(String type) {
        LambdaQueryWrapper<Category> wrapper = new LambdaQueryWrapper<>();
        if (StrUtil.isNotBlank(type)) {
            wrapper.eq(Category::getType, type);
        }
        wrapper.orderByAsc(Category::getSort);
        List<Category> categories = list(wrapper);
        return categories.stream()
                .map(cat -> {
                    CategoryVO vo = new CategoryVO();
                    BeanUtil.copyProperties(cat, vo);
                    return vo;
                })
                .collect(Collectors.toList());
    }

    @Override
    public List<CategoryVO> getWorkCategories(Long workId) {
        List<WorkCategory> workCategories = workCategoryMapper.selectList(
                new LambdaQueryWrapper<WorkCategory>().eq(WorkCategory::getWorkId, workId)
        );

        if (workCategories.isEmpty()) {
            return List.of();
        }

        List<Long> categoryIds = workCategories.stream()
                .map(WorkCategory::getCategoryId)
                .collect(Collectors.toList());

        List<Category> categories = listByIds(categoryIds);
        return categories.stream()
                .map(cat -> {
                    CategoryVO vo = new CategoryVO();
                    BeanUtil.copyProperties(cat, vo);
                    return vo;
                })
                .collect(Collectors.toList());
    }
}
