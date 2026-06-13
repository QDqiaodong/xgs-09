package com.journal.inspiration.controller;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.journal.inspiration.common.Result;
import com.journal.inspiration.service.FavoriteService;
import com.journal.inspiration.vo.WorkVO;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/favorites")
@RequiredArgsConstructor
public class FavoriteController {

    private final FavoriteService favoriteService;

    @PostMapping("/toggle")
    public Result<Boolean> toggle(@RequestParam Long userId, @RequestParam Long workId) {
        return Result.success(favoriteService.toggleFavorite(userId, workId));
    }

    @GetMapping("/check")
    public Result<Boolean> check(@RequestParam Long userId, @RequestParam Long workId) {
        return Result.success(favoriteService.isFavorite(userId, workId));
    }

    @GetMapping("/user/{userId}")
    public Result<Page<WorkVO>> userFavorites(@PathVariable Long userId,
                                              @RequestParam(defaultValue = "1") Integer pageNum,
                                              @RequestParam(defaultValue = "10") Integer pageSize) {
        return Result.success(favoriteService.getUserFavorites(userId, pageNum, pageSize));
    }
}
