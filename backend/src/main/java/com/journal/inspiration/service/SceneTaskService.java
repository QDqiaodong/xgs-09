package com.journal.inspiration.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.journal.inspiration.entity.SceneTask;
import com.journal.inspiration.vo.SceneTaskVO;

import java.util.List;

public interface SceneTaskService extends IService<SceneTask> {

    List<SceneTaskVO> getSceneTaskList(Long sceneCategoryId);

    int countBySceneCategoryId(Long sceneCategoryId);
}
