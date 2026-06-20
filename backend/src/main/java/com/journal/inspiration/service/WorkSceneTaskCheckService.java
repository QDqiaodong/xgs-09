package com.journal.inspiration.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.journal.inspiration.entity.WorkSceneTaskCheck;
import com.journal.inspiration.vo.SceneTaskCheckVO;

import java.util.List;

public interface WorkSceneTaskCheckService extends IService<WorkSceneTaskCheck> {

    void saveCheckList(Long workId, Long userId, Long sceneCategoryId, List<Long> checkedTaskIds);

    List<SceneTaskCheckVO> getCheckListByWorkId(Long workId, Long sceneCategoryId);
}
