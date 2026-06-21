package com.journal.inspiration.service.impl;

import cn.hutool.core.bean.BeanUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.journal.inspiration.entity.Category;
import com.journal.inspiration.entity.SceneStyleRelation;
import com.journal.inspiration.mapper.CategoryMapper;
import com.journal.inspiration.mapper.SceneStyleRelationMapper;
import com.journal.inspiration.service.SceneStyleRelationService;
import com.journal.inspiration.vo.SceneStyleRelationVO;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class SceneStyleRelationServiceImpl extends ServiceImpl<SceneStyleRelationMapper, SceneStyleRelation> implements SceneStyleRelationService {

    private final CategoryMapper categoryMapper;

    @Override
    public List<SceneStyleRelationVO> getRelationsBySceneCategoryId(Long sceneCategoryId) {
        if (sceneCategoryId == null) {
            return new ArrayList<>();
        }
        LambdaQueryWrapper<SceneStyleRelation> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(SceneStyleRelation::getSceneCategoryId, sceneCategoryId);
        wrapper.orderByDesc(SceneStyleRelation::getIsPrimary, SceneStyleRelation::getMatchScore, SceneStyleRelation::getSort);
        List<SceneStyleRelation> relations = list(wrapper);
        return convertToVOList(relations);
    }

    @Override
    public List<SceneStyleRelationVO> getRelationsByStyleCategoryId(Long styleCategoryId) {
        if (styleCategoryId == null) {
            return new ArrayList<>();
        }
        LambdaQueryWrapper<SceneStyleRelation> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(SceneStyleRelation::getStyleCategoryId, styleCategoryId);
        wrapper.orderByDesc(SceneStyleRelation::getMatchScore, SceneStyleRelation::getSort);
        List<SceneStyleRelation> relations = list(wrapper);
        return convertToVOList(relations);
    }

    @Override
    public SceneStyleRelationVO getPrimaryStyleBySceneCategoryId(Long sceneCategoryId) {
        if (sceneCategoryId == null) {
            return null;
        }
        LambdaQueryWrapper<SceneStyleRelation> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(SceneStyleRelation::getSceneCategoryId, sceneCategoryId);
        wrapper.eq(SceneStyleRelation::getIsPrimary, 1);
        wrapper.last("LIMIT 1");
        SceneStyleRelation relation = getOne(wrapper);
        if (relation == null) {
            return null;
        }
        List<SceneStyleRelationVO> voList = convertToVOList(Collections.singletonList(relation));
        return voList.isEmpty() ? null : voList.get(0);
    }

    private List<SceneStyleRelationVO> convertToVOList(List<SceneStyleRelation> relations) {
        if (relations == null || relations.isEmpty()) {
            return new ArrayList<>();
        }

        Set<Long> sceneCategoryIds = new HashSet<>();
        Set<Long> styleCategoryIds = new HashSet<>();
        for (SceneStyleRelation rel : relations) {
            sceneCategoryIds.add(rel.getSceneCategoryId());
            styleCategoryIds.add(rel.getStyleCategoryId());
        }

        Set<Long> allCategoryIds = new HashSet<>();
        allCategoryIds.addAll(sceneCategoryIds);
        allCategoryIds.addAll(styleCategoryIds);
        List<Category> categories = categoryMapper.selectBatchIds(new ArrayList<>(allCategoryIds));
        Map<Long, Category> categoryMap = categories.stream()
                .collect(Collectors.toMap(Category::getId, c -> c));

        return relations.stream()
                .map(rel -> {
                    SceneStyleRelationVO vo = new SceneStyleRelationVO();
                    BeanUtil.copyProperties(rel, vo);
                    Category sceneCat = categoryMap.get(rel.getSceneCategoryId());
                    if (sceneCat != null) {
                        vo.setSceneCategoryName(sceneCat.getName());
                    }
                    Category styleCat = categoryMap.get(rel.getStyleCategoryId());
                    if (styleCat != null) {
                        vo.setStyleCategoryName(styleCat.getName());
                        vo.setStyleCategoryIcon(styleCat.getIcon());
                    }
                    return vo;
                })
                .collect(Collectors.toList());
    }
}
