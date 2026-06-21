package com.journal.inspiration.controller;

import com.journal.inspiration.common.Result;
import com.journal.inspiration.service.LayoutTemplateService;
import com.journal.inspiration.vo.LayoutTemplateVO;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/layout-templates")
@RequiredArgsConstructor
public class LayoutTemplateController {

    private final LayoutTemplateService layoutTemplateService;

    @GetMapping
    public Result<List<LayoutTemplateVO>> list(
            @RequestParam(required = false) String templateType,
            @RequestParam(required = false) List<Long> categoryIds) {
        return Result.success(layoutTemplateService.getTemplateList(templateType, categoryIds));
    }

    @GetMapping("/{id}")
    public Result<LayoutTemplateVO> getById(@PathVariable Long id) {
        LayoutTemplateVO vo = layoutTemplateService.getTemplateById(id);
        if (vo == null) {
            return Result.error(404, "模板不存在");
        }
        return Result.success(vo);
    }

    @GetMapping("/code/{templateCode}")
    public Result<LayoutTemplateVO> getByCode(@PathVariable String templateCode) {
        LayoutTemplateVO vo = layoutTemplateService.getTemplateByCode(templateCode);
        if (vo == null) {
            return Result.error(404, "模板不存在");
        }
        return Result.success(vo);
    }

    @PostMapping("/{id}/use")
    public Result<Boolean> incrementUseCount(@PathVariable Long id) {
        return Result.success(layoutTemplateService.incrementUseCount(id));
    }
}
