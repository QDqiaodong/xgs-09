package com.journal.inspiration.service.impl;

import cn.hutool.core.bean.BeanUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.journal.inspiration.entity.Category;
import com.journal.inspiration.entity.SceneTask;
import com.journal.inspiration.mapper.CategoryMapper;
import com.journal.inspiration.mapper.SceneTaskMapper;
import com.journal.inspiration.service.SceneTaskService;
import com.journal.inspiration.vo.SceneTaskVO;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class SceneTaskServiceImpl extends ServiceImpl<SceneTaskMapper, SceneTask> implements SceneTaskService {

    private final CategoryMapper categoryMapper;

    @Override
    public List<SceneTaskVO> getSceneTaskList(Long sceneCategoryId) {
        if (sceneCategoryId == null || sceneCategoryId <= 0) {
            return List.of();
        }
        Category category = categoryMapper.selectById(sceneCategoryId);
        if (category == null || !"scene".equals(category.getType())) {
            return List.of();
        }
        LambdaQueryWrapper<SceneTask> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(SceneTask::getSceneCategoryId, sceneCategoryId);
        wrapper.orderByAsc(SceneTask::getSort);
        List<SceneTask> tasks = list(wrapper);
        return tasks.stream()
                .map(task -> {
                    SceneTaskVO vo = new SceneTaskVO();
                    BeanUtil.copyProperties(task, vo);
                    return vo;
                })
                .collect(Collectors.toList());
    }

    @Override
    public int countBySceneCategoryId(Long sceneCategoryId) {
        if (sceneCategoryId == null || sceneCategoryId <= 0) {
            return 0;
        }
        Category category = categoryMapper.selectById(sceneCategoryId);
        if (category == null || !"scene".equals(category.getType())) {
            return 0;
        }
        LambdaQueryWrapper<SceneTask> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(SceneTask::getSceneCategoryId, sceneCategoryId);
        return (int) count(wrapper);
    }
}
