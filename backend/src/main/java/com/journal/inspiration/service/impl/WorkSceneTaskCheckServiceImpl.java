package com.journal.inspiration.service.impl;

import cn.hutool.core.bean.BeanUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.journal.inspiration.entity.SceneTask;
import com.journal.inspiration.entity.WorkSceneTaskCheck;
import com.journal.inspiration.mapper.SceneTaskMapper;
import com.journal.inspiration.mapper.WorkSceneTaskCheckMapper;
import com.journal.inspiration.service.WorkSceneTaskCheckService;
import com.journal.inspiration.vo.SceneTaskCheckVO;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class WorkSceneTaskCheckServiceImpl extends ServiceImpl<WorkSceneTaskCheckMapper, WorkSceneTaskCheck> implements WorkSceneTaskCheckService {

    private final SceneTaskMapper sceneTaskMapper;

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void saveCheckList(Long workId, Long userId, Long sceneCategoryId, List<Long> checkedTaskIds) {
        if (workId == null || sceneCategoryId == null) {
            return;
        }

        LambdaQueryWrapper<WorkSceneTaskCheck> deleteWrapper = new LambdaQueryWrapper<>();
        deleteWrapper.eq(WorkSceneTaskCheck::getWorkId, workId);
        deleteWrapper.eq(WorkSceneTaskCheck::getSceneCategoryId, sceneCategoryId);
        remove(deleteWrapper);

        LambdaQueryWrapper<SceneTask> taskWrapper = new LambdaQueryWrapper<>();
        taskWrapper.eq(SceneTask::getSceneCategoryId, sceneCategoryId);
        taskWrapper.orderByAsc(SceneTask::getSort);
        List<SceneTask> allTasks = sceneTaskMapper.selectList(taskWrapper);

        List<WorkSceneTaskCheck> checkList = new ArrayList<>();
        for (SceneTask task : allTasks) {
            WorkSceneTaskCheck check = new WorkSceneTaskCheck();
            check.setWorkId(workId);
            check.setSceneTaskId(task.getId());
            check.setSceneCategoryId(sceneCategoryId);
            check.setUserId(userId);
            check.setChecked(checkedTaskIds != null && checkedTaskIds.contains(task.getId()) ? 1 : 0);
            checkList.add(check);
        }

        if (!checkList.isEmpty()) {
            saveBatch(checkList);
        }
    }

    @Override
    public List<SceneTaskCheckVO> getCheckListByWorkId(Long workId, Long sceneCategoryId) {
        if (sceneCategoryId == null) {
            return List.of();
        }

        LambdaQueryWrapper<SceneTask> taskWrapper = new LambdaQueryWrapper<>();
        taskWrapper.eq(SceneTask::getSceneCategoryId, sceneCategoryId);
        taskWrapper.orderByAsc(SceneTask::getSort);
        List<SceneTask> tasks = sceneTaskMapper.selectList(taskWrapper);

        Map<Long, Integer> checkMap = Map.of();
        if (workId != null) {
            LambdaQueryWrapper<WorkSceneTaskCheck> checkWrapper = new LambdaQueryWrapper<>();
            checkWrapper.eq(WorkSceneTaskCheck::getWorkId, workId);
            checkWrapper.eq(WorkSceneTaskCheck::getSceneCategoryId, sceneCategoryId);
            List<WorkSceneTaskCheck> checkList = list(checkWrapper);
            checkMap = checkList.stream()
                    .collect(Collectors.toMap(WorkSceneTaskCheck::getSceneTaskId, WorkSceneTaskCheck::getChecked));
        }

        List<SceneTaskCheckVO> result = new ArrayList<>();
        for (SceneTask task : tasks) {
            SceneTaskCheckVO vo = new SceneTaskCheckVO();
            BeanUtil.copyProperties(task, vo);
            vo.setChecked(checkMap.getOrDefault(task.getId(), 0));
            result.add(vo);
        }

        return result;
    }
}
