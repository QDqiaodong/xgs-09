package com.journal.inspiration.controller;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.journal.inspiration.common.Result;
import com.journal.inspiration.common.UserContext;
import com.journal.inspiration.dto.WorkPublishDTO;
import com.journal.inspiration.dto.WorkQueryDTO;
import com.journal.inspiration.service.JournalWorkService;
import com.journal.inspiration.vo.WorkStatsVO;
import com.journal.inspiration.vo.WorkVO;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/works")
@RequiredArgsConstructor
public class JournalWorkController {

    private final JournalWorkService workService;

    @PostMapping
    public Result<Long> publish(@Valid @RequestBody WorkPublishDTO dto) {
        Long id = workService.publishWork(dto);
        return Result.success(id);
    }

    @GetMapping("/{id}")
    public Result<WorkVO> detail(@PathVariable Long id, @RequestParam(required = false) Long userId) {
        workService.incrementViewCount(id);
        WorkVO vo = workService.getWorkDetail(id, userId);
        if (vo == null) {
            return Result.error(404, "作品不存在");
        }
        return Result.success(vo);
    }

    @GetMapping("/list")
    public Result<Page<WorkVO>> list(WorkQueryDTO dto) {
        return Result.success(workService.getWorkList(dto));
    }

    @GetMapping("/latest")
    public Result<Page<WorkVO>> latest(@RequestParam(defaultValue = "1") Integer pageNum,
                                       @RequestParam(defaultValue = "10") Integer pageSize) {
        return Result.success(workService.getLatestWorks(pageNum, pageSize));
    }

    @GetMapping("/hot")
    public Result<Page<WorkVO>> hot(@RequestParam(defaultValue = "1") Integer pageNum,
                                    @RequestParam(defaultValue = "10") Integer pageSize) {
        return Result.success(workService.getHotWorks(pageNum, pageSize));
    }

    @GetMapping("/user/{userId}")
    public Result<Page<WorkVO>> userWorks(@PathVariable Long userId,
                                          @RequestParam(defaultValue = "1") Integer pageNum,
                                          @RequestParam(defaultValue = "10") Integer pageSize) {
        return Result.success(workService.getUserWorks(userId, pageNum, pageSize));
    }

    @PutMapping("/{id}/status")
    public Result<Boolean> updateStatus(@PathVariable Long id,
                                        @RequestParam Integer status) {
        try {
            Long operatorId = UserContext.getCurrentUserId();
            return Result.success(workService.updateWorkStatus(id, status, operatorId));
        } catch (IllegalArgumentException e) {
            return Result.error(400, e.getMessage());
        } catch (SecurityException e) {
            return Result.error(403, e.getMessage());
        }
    }

    @GetMapping("/user/{userId}/stats")
    public Result<WorkStatsVO> userWorkStats(@PathVariable Long userId) {
        return Result.success(workService.getUserWorkStats(userId));
    }
}
