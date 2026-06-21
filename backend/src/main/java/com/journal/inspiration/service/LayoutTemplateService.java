package com.journal.inspiration.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.journal.inspiration.entity.LayoutTemplate;
import com.journal.inspiration.vo.LayoutTemplateVO;

import java.util.List;

public interface LayoutTemplateService extends IService<LayoutTemplate> {

    List<LayoutTemplateVO> getTemplateList(String templateType, List<Long> categoryIds);

    LayoutTemplateVO getTemplateById(Long id);

    LayoutTemplateVO getTemplateByCode(String templateCode);

    boolean incrementUseCount(Long id);
}
