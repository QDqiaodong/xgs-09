package com.journal.inspiration.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.journal.inspiration.entity.ColorPalette;
import com.journal.inspiration.vo.ColorPaletteVO;

import java.util.List;

public interface ColorPaletteService extends IService<ColorPalette> {

    List<ColorPaletteVO> getPaletteList(List<Long> categoryIds);

    ColorPaletteVO getPaletteDetail(Long id);

    boolean incrementUseCount(Long id);
}
