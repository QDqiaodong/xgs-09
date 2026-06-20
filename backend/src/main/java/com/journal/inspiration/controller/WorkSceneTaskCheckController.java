package com.journal.inspiration.controller;

import com.journal.inspiration.common.Result;
import com.journal.inspiration.service.WorkSceneTaskCheckService;
import com.journal.inspiration.vo.SceneTaskCheckVO;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/work-scene-task-check")
@RequiredArgsConstructor
public class WorkSceneTaskCheckController {

    private final WorkSceneTaskCheckService workSceneTaskCheckService;

    @GetMapping("/list")
    public Result<List<SceneTaskCheckVO>> getCheckList(
            @RequestParam(required = false) Long workId,
            @RequestParam Long sceneCategoryId) {
        return Result.success(workSceneTaskCheckService.getCheckListByWorkId(workId, sceneCategoryId));
    }
}
