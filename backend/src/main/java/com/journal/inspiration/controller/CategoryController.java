package com.journal.inspiration.controller;

import com.journal.inspiration.common.Result;
import com.journal.inspiration.service.CategoryService;
import com.journal.inspiration.vo.CategoryVO;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/categories")
@RequiredArgsConstructor
public class CategoryController {

    private final CategoryService categoryService;

    @GetMapping
    public Result<List<CategoryVO>> list(@RequestParam(required = false) String type) {
        return Result.success(categoryService.getCategoryList(type));
    }

    @GetMapping("/work/{workId}")
    public Result<List<CategoryVO>> workCategories(@PathVariable Long workId) {
        return Result.success(categoryService.getWorkCategories(workId));
    }
}
