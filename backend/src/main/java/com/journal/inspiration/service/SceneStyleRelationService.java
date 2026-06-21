package com.journal.inspiration.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.journal.inspiration.entity.SceneStyleRelation;
import com.journal.inspiration.vo.SceneStyleRelationVO;

import java.util.List;

public interface SceneStyleRelationService extends IService<SceneStyleRelation> {

    List<SceneStyleRelationVO> getRelationsBySceneCategoryId(Long sceneCategoryId);

    List<SceneStyleRelationVO> getRelationsByStyleCategoryId(Long styleCategoryId);

    SceneStyleRelationVO getPrimaryStyleBySceneCategoryId(Long sceneCategoryId);
}
