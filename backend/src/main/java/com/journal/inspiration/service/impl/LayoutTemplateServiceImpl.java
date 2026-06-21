package com.journal.inspiration.service.impl;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.journal.inspiration.entity.Category;
import com.journal.inspiration.entity.LayoutTemplate;
import com.journal.inspiration.mapper.CategoryMapper;
import com.journal.inspiration.mapper.LayoutTemplateMapper;
import com.journal.inspiration.service.LayoutTemplateService;
import com.journal.inspiration.vo.LayoutTemplateVO;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class LayoutTemplateServiceImpl extends ServiceImpl<LayoutTemplateMapper, LayoutTemplate> implements LayoutTemplateService {

    private final CategoryMapper categoryMapper;

    private static final Map<String, String> TEMPLATE_TYPE_NAME_MAP = new LinkedHashMap<>();
    static {
        TEMPLATE_TYPE_NAME_MAP.put("twoColumn", "双栏布局");
        TEMPLATE_TYPE_NAME_MAP.put("collage", "拼贴风格");
        TEMPLATE_TYPE_NAME_MAP.put("minimalist", "极简留白");
        TEMPLATE_TYPE_NAME_MAP.put("bordered", "边框装饰");
        TEMPLATE_TYPE_NAME_MAP.put("timeline", "时间轴");
        TEMPLATE_TYPE_NAME_MAP.put("centerFocus", "居中聚焦");
        TEMPLATE_TYPE_NAME_MAP.put("magazine", "杂志风");
        TEMPLATE_TYPE_NAME_MAP.put("natural", "自然森系");
    }

    @Override
    public List<LayoutTemplateVO> getTemplateList(String templateType, List<Long> categoryIds) {
        LambdaQueryWrapper<LayoutTemplate> wrapper = new LambdaQueryWrapper<>();
        if (StrUtil.isNotBlank(templateType)) {
            wrapper.eq(LayoutTemplate::getTemplateType, templateType);
        }
        wrapper.orderByAsc(LayoutTemplate::getSort, LayoutTemplate::getId);
        List<LayoutTemplate> templates = list(wrapper);

        Set<String> selectedStyleNames = new HashSet<>();
        Set<String> selectedSceneNames = new HashSet<>();
        if (categoryIds != null && !categoryIds.isEmpty()) {
            List<Category> selectedCategories = categoryMapper.selectBatchIds(categoryIds);
            for (Category cat : selectedCategories) {
                if (cat != null) {
                    if ("style".equals(cat.getType())) {
                        selectedStyleNames.add(cat.getName());
                    } else if ("scene".equals(cat.getType())) {
                        selectedSceneNames.add(cat.getName());
                    }
                }
            }
        }

        return templates.stream()
                .map(tpl -> convertToVO(tpl, selectedStyleNames, selectedSceneNames))
                .sorted((a, b) -> {
                    if (Boolean.TRUE.equals(a.getIsRecommended()) && !Boolean.TRUE.equals(b.getIsRecommended())) {
                        return -1;
                    }
                    if (!Boolean.TRUE.equals(a.getIsRecommended()) && Boolean.TRUE.equals(b.getIsRecommended())) {
                        return 1;
                    }
                    return 0;
                })
                .collect(Collectors.toList());
    }

    @Override
    public LayoutTemplateVO getTemplateById(Long id) {
        if (id == null) {
            return null;
        }
        LayoutTemplate template = getById(id);
        if (template == null) {
            return null;
        }
        return convertToVO(template, Collections.emptySet(), Collections.emptySet());
    }

    @Override
    public LayoutTemplateVO getTemplateByCode(String templateCode) {
        if (StrUtil.isBlank(templateCode)) {
            return null;
        }
        LambdaQueryWrapper<LayoutTemplate> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(LayoutTemplate::getTemplateCode, templateCode);
        wrapper.last("LIMIT 1");
        LayoutTemplate template = getOne(wrapper);
        if (template == null) {
            return null;
        }
        return convertToVO(template, Collections.emptySet(), Collections.emptySet());
    }

    @Override
    public boolean incrementUseCount(Long id) {
        if (id == null) {
            return false;
        }
        LayoutTemplate template = getById(id);
        if (template == null) {
            return false;
        }
        template.setUseCount((template.getUseCount() == null ? 0 : template.getUseCount()) + 1);
        return updateById(template);
    }

    private LayoutTemplateVO convertToVO(LayoutTemplate template, Set<String> selectedStyleNames, Set<String> selectedSceneNames) {
        LayoutTemplateVO vo = new LayoutTemplateVO();
        BeanUtil.copyProperties(template, vo);
        vo.setTemplateTypeName(TEMPLATE_TYPE_NAME_MAP.getOrDefault(template.getTemplateType(), template.getTemplateType()));

        if (StrUtil.isNotBlank(template.getStyleTags())) {
            List<String> styleTagList = Arrays.stream(template.getStyleTags().split(","))
                    .map(String::trim)
                    .filter(s -> !s.isEmpty())
                    .collect(Collectors.toList());
            vo.setStyleTagList(styleTagList);
        } else {
            vo.setStyleTagList(new ArrayList<>());
        }

        if (StrUtil.isNotBlank(template.getSceneTags())) {
            List<String> sceneTagList = Arrays.stream(template.getSceneTags().split(","))
                    .map(String::trim)
                    .filter(s -> !s.isEmpty())
                    .collect(Collectors.toList());
            vo.setSceneTagList(sceneTagList);
        } else {
            vo.setSceneTagList(new ArrayList<>());
        }

        boolean isRecommended = false;
        if (!selectedStyleNames.isEmpty() && vo.getStyleTagList() != null) {
            for (String styleName : selectedStyleNames) {
                if (vo.getStyleTagList().contains(styleName)) {
                    isRecommended = true;
                    break;
                }
            }
        }
        if (!isRecommended && !selectedSceneNames.isEmpty() && vo.getSceneTagList() != null) {
            for (String sceneName : selectedSceneNames) {
                if (vo.getSceneTagList().contains(sceneName)) {
                    isRecommended = true;
                    break;
                }
            }
        }
        vo.setIsRecommended(isRecommended);

        return vo;
    }
}
