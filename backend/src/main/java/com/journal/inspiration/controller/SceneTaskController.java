package com.journal.inspiration.controller;

import com.journal.inspiration.common.Result;
import com.journal.inspiration.service.SceneTaskService;
import com.journal.inspiration.vo.SceneTaskVO;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/scene-tasks")
@RequiredArgsConstructor
public class SceneTaskController {

    private final SceneTaskService sceneTaskService;

    @GetMapping
    public Result<List<SceneTaskVO>> list(@RequestParam(required = false) Long sceneCategoryId) {
        return Result.success(sceneTaskService.getSceneTaskList(sceneCategoryId));
    }
}
