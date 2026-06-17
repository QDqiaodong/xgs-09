package com.journal.inspiration.service.impl;

import cn.hutool.core.bean.BeanUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.journal.inspiration.entity.SceneTask;
import com.journal.inspiration.mapper.SceneTaskMapper;
import com.journal.inspiration.service.SceneTaskService;
import com.journal.inspiration.vo.SceneTaskVO;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class SceneTaskServiceImpl extends ServiceImpl<SceneTaskMapper, SceneTask> implements SceneTaskService {

    @Override
    public List<SceneTaskVO> getSceneTaskList(Long sceneCategoryId) {
        LambdaQueryWrapper<SceneTask> wrapper = new LambdaQueryWrapper<>();
        if (sceneCategoryId != null) {
            wrapper.eq(SceneTask::getSceneCategoryId, sceneCategoryId);
        }
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
        if (sceneCategoryId == null) {
            return 0;
        }
        LambdaQueryWrapper<SceneTask> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(SceneTask::getSceneCategoryId, sceneCategoryId);
        return (int) count(wrapper);
    }
}
