package com.journal.inspiration.service.impl;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.journal.inspiration.entity.ColorPalette;
import com.journal.inspiration.mapper.ColorPaletteMapper;
import com.journal.inspiration.service.ColorPaletteService;
import com.journal.inspiration.vo.ColorPaletteVO;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ColorPaletteServiceImpl extends ServiceImpl<ColorPaletteMapper, ColorPalette> implements ColorPaletteService {

    @Override
    public List<ColorPaletteVO> getPaletteList(List<Long> categoryIds) {
        LambdaQueryWrapper<ColorPalette> wrapper = new LambdaQueryWrapper<>();
        wrapper.orderByAsc(ColorPalette::getSort);
        List<ColorPalette> palettes = list(wrapper);

        List<ColorPaletteVO> result = palettes.stream()
                .map(this::convertToVO)
                .collect(Collectors.toList());

        if (categoryIds != null && !categoryIds.isEmpty()) {
            result.sort((a, b) -> {
                boolean aMatch = a.getCategoryIdList() != null && !Collections.disjoint(a.getCategoryIdList(), categoryIds);
                boolean bMatch = b.getCategoryIdList() != null && !Collections.disjoint(b.getCategoryIdList(), categoryIds);
                if (aMatch && !bMatch) return -1;
                if (!aMatch && bMatch) return 1;
                return Integer.compare(b.getUseCount() != null ? b.getUseCount() : 0,
                        a.getUseCount() != null ? a.getUseCount() : 0);
            });
        }

        return result;
    }

    @Override
    public ColorPaletteVO getPaletteDetail(Long id) {
        ColorPalette palette = getById(id);
        if (palette == null) {
            return null;
        }
        return convertToVO(palette);
    }

    @Override
    public boolean incrementUseCount(Long id) {
        ColorPalette palette = getById(id);
        if (palette == null) {
            return false;
        }
        palette.setUseCount((palette.getUseCount() != null ? palette.getUseCount() : 0) + 1);
        return updateById(palette);
    }

    private ColorPaletteVO convertToVO(ColorPalette palette) {
        ColorPaletteVO vo = new ColorPaletteVO();
        BeanUtil.copyProperties(palette, vo);
        if (StrUtil.isNotBlank(palette.getCategoryIds())) {
            try {
                List<Long> ids = Arrays.stream(palette.getCategoryIds().split(","))
                        .map(String::trim)
                        .filter(StrUtil::isNotBlank)
                        .map(Long::parseLong)
                        .collect(Collectors.toList());
                vo.setCategoryIdList(ids);
            } catch (Exception e) {
                vo.setCategoryIdList(List.of());
            }
        } else {
            vo.setCategoryIdList(List.of());
        }
        return vo;
    }
}
