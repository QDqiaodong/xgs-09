package com.journal.inspiration.controller;

import com.journal.inspiration.common.Result;
import com.journal.inspiration.service.ColorPaletteService;
import com.journal.inspiration.vo.ColorPaletteVO;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/color-palettes")
@RequiredArgsConstructor
public class ColorPaletteController {

    private final ColorPaletteService colorPaletteService;

    @GetMapping
    public Result<List<ColorPaletteVO>> list(
            @RequestParam(required = false) String categoryIds) {
        List<Long> categoryIdList = null;
        if (categoryIds != null && !categoryIds.trim().isEmpty()) {
            try {
                categoryIdList = Arrays.stream(categoryIds.split(","))
                        .map(String::trim)
                        .filter(s -> !s.isEmpty())
                        .map(Long::parseLong)
                        .collect(Collectors.toList());
            } catch (NumberFormatException e) {
                categoryIdList = null;
            }
        }
        return Result.success(colorPaletteService.getPaletteList(categoryIdList));
    }

    @GetMapping("/{id}")
    public Result<ColorPaletteVO> detail(@PathVariable Long id) {
        ColorPaletteVO vo = colorPaletteService.getPaletteDetail(id);
        if (vo == null) {
            return Result.error("色彩灵感卡组不存在");
        }
        return Result.success(vo);
    }

    @PostMapping("/{id}/use")
    public Result<Boolean> usePalette(@PathVariable Long id) {
        boolean success = colorPaletteService.incrementUseCount(id);
        return Result.success(success);
    }
}
