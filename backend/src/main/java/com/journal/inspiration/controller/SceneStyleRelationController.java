package com.journal.inspiration.controller;

import com.journal.inspiration.common.Result;
import com.journal.inspiration.service.SceneStyleRelationService;
import com.journal.inspiration.vo.SceneStyleRelationVO;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/scene-style-relations")
@RequiredArgsConstructor
public class SceneStyleRelationController {

    private final SceneStyleRelationService sceneStyleRelationService;

    @GetMapping("/by-scene")
    public Result<List<SceneStyleRelationVO>> getBySceneCategoryId(@RequestParam Long sceneCategoryId) {
        return Result.success(sceneStyleRelationService.getRelationsBySceneCategoryId(sceneCategoryId));
    }

    @GetMapping("/by-style")
    public Result<List<SceneStyleRelationVO>> getByStyleCategoryId(@RequestParam Long styleCategoryId) {
        return Result.success(sceneStyleRelationService.getRelationsByStyleCategoryId(styleCategoryId));
    }

    @GetMapping("/primary-style")
    public Result<SceneStyleRelationVO> getPrimaryStyle(@RequestParam Long sceneCategoryId) {
        SceneStyleRelationVO vo = sceneStyleRelationService.getPrimaryStyleBySceneCategoryId(sceneCategoryId);
        if (vo == null) {
            return Result.error(404, "未找到该场景的首选风格");
        }
        return Result.success(vo);
    }
}
