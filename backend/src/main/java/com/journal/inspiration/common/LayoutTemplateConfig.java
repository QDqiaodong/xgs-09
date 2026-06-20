package com.journal.inspiration.common;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.journal.inspiration.entity.Category;

import java.util.*;

public class LayoutTemplateConfig {

    private static final ObjectMapper objectMapper = new ObjectMapper();

    public static final Map<String, LayoutConfig> LAYOUT_TEMPLATES = new HashMap<>();

    public static final Map<String, String> STYLE_LAYOUT_MAP = new HashMap<>();

    public static final Map<String, String> SCENE_LAYOUT_MAP = new HashMap<>();

    static {
        LAYOUT_TEMPLATES.put("twoColumnLeftImage", createTwoColumnLeftImage());
        LAYOUT_TEMPLATES.put("twoColumnRightImage", createTwoColumnRightImage());
        LAYOUT_TEMPLATES.put("timelineVertical", createTimelineVertical());
        LAYOUT_TEMPLATES.put("centerFocus", createCenterFocus());
        LAYOUT_TEMPLATES.put("collageStyle", createCollageStyle());
        LAYOUT_TEMPLATES.put("minimalist", createMinimalist());
        LAYOUT_TEMPLATES.put("naturalStyle", createNaturalStyle());
        LAYOUT_TEMPLATES.put("threeColumnMagazine", createThreeColumnMagazine());

        STYLE_LAYOUT_MAP.put("复古风", "twoColumnRightImage");
        STYLE_LAYOUT_MAP.put("简约风", "minimalist");
        STYLE_LAYOUT_MAP.put("可爱风", "centerFocus");
        STYLE_LAYOUT_MAP.put("森系", "naturalStyle");
        STYLE_LAYOUT_MAP.put("盐系", "minimalist");
        STYLE_LAYOUT_MAP.put("甜系", "centerFocus");
        STYLE_LAYOUT_MAP.put("暗黑系", "threeColumnMagazine");
        STYLE_LAYOUT_MAP.put("ins风", "twoColumnLeftImage");

        SCENE_LAYOUT_MAP.put("日常记录", "timelineVertical");
        SCENE_LAYOUT_MAP.put("节日主题", "centerFocus");
        SCENE_LAYOUT_MAP.put("旅行手账", "collageStyle");
        SCENE_LAYOUT_MAP.put("读书摘抄", "twoColumnLeftImage");
        SCENE_LAYOUT_MAP.put("美食记录", "collageStyle");
        SCENE_LAYOUT_MAP.put("观影心得", "twoColumnRightImage");
        SCENE_LAYOUT_MAP.put("工作计划", "timelineVertical");
        SCENE_LAYOUT_MAP.put("习惯打卡", "threeColumnMagazine");
    }

    public static LayoutConfig getDefaultLayout(List<Category> categories) {
        if (categories == null || categories.isEmpty()) {
            return LAYOUT_TEMPLATES.get("twoColumnLeftImage");
        }

        for (Category cat : categories) {
            if ("style".equals(cat.getType()) && STYLE_LAYOUT_MAP.containsKey(cat.getName())) {
                return LAYOUT_TEMPLATES.get(STYLE_LAYOUT_MAP.get(cat.getName()));
            }
        }

        for (Category cat : categories) {
            if ("scene".equals(cat.getType()) && SCENE_LAYOUT_MAP.containsKey(cat.getName())) {
                return LAYOUT_TEMPLATES.get(SCENE_LAYOUT_MAP.get(cat.getName()));
            }
        }

        return LAYOUT_TEMPLATES.get("twoColumnLeftImage");
    }

    public static String getDefaultLayoutJson(List<Category> categories) {
        try {
            return objectMapper.writeValueAsString(getDefaultLayout(categories));
        } catch (JsonProcessingException e) {
            return null;
        }
    }

    private static LayoutConfig createTwoColumnLeftImage() {
        LayoutConfig config = new LayoutConfig();
        config.setColumns(2);
        config.setColumnGap("16px");
        config.setRowGap("12px");
        config.setPadding("24px");
        config.setWhiteSpacePosition("bottom");
        config.setWhiteSpaceSize("60px");
        config.setPartitionMode("fixed");
        config.setPartitionRatios(java.util.Arrays.asList(0.4, 0.6));
        config.setImageTextLayout("left-image-right-text");
        config.setLayoutDirection("vertical");
        config.setGridStyle("dashed");
        List<LayoutArea> areas = new ArrayList<>();
        areas.add(createArea("image", "图片区", 1, 2, null, 0.4, "left-image-right-text"));
        areas.add(createArea("text", "主文字区", 1, 1, null, 0.3, "left-image-right-text"));
        areas.add(createArea("sticker", "装饰贴纸", 1, 1, 4, 0.3, null));
        config.setAreas(areas);
        return config;
    }

    private static LayoutConfig createTwoColumnRightImage() {
        LayoutConfig config = new LayoutConfig();
        config.setColumns(2);
        config.setColumnGap("16px");
        config.setRowGap("12px");
        config.setPadding("24px");
        config.setWhiteSpacePosition("top");
        config.setWhiteSpaceSize("40px");
        config.setPartitionMode("fixed");
        config.setPartitionRatios(java.util.Arrays.asList(0.55, 0.45));
        config.setImageTextLayout("left-text-right-image");
        config.setLayoutDirection("vertical");
        config.setGridStyle("solid");
        List<LayoutArea> areas = new ArrayList<>();
        areas.add(createArea("text", "主文字区", 1, 2, null, 0.55, "left-text-right-image"));
        areas.add(createArea("image", "图片区", 1, 1, null, 0.3, "left-text-right-image"));
        areas.add(createArea("sticker", "贴纸区", 1, 1, 3, 0.15, null));
        config.setAreas(areas);
        return config;
    }

    private static LayoutConfig createTimelineVertical() {
        LayoutConfig config = new LayoutConfig();
        config.setColumns(1);
        config.setColumnGap("0");
        config.setRowGap("16px");
        config.setPadding("20px");
        config.setWhiteSpacePosition("left");
        config.setWhiteSpaceSize("30px");
        config.setPartitionMode("flow");
        config.setPartitionRatios(java.util.Arrays.asList(0.15, 0.25, 0.6));
        config.setImageTextLayout("timeline");
        config.setLayoutDirection("vertical");
        config.setGridStyle("dotted");
        List<LayoutArea> areas = new ArrayList<>();
        areas.add(createArea("text", "标题区", 1, 1, null, 0.15, "timeline"));
        areas.add(createArea("sticker", "便签区", 1, 1, 3, 0.25, null));
        areas.add(createArea("text", "记录区", 1, 2, null, 0.6, "timeline"));
        config.setAreas(areas);
        return config;
    }

    private static LayoutConfig createCenterFocus() {
        LayoutConfig config = new LayoutConfig();
        config.setColumns(2);
        config.setColumnGap("12px");
        config.setRowGap("14px");
        config.setPadding("28px");
        config.setWhiteSpacePosition("around");
        config.setWhiteSpaceSize("50px");
        config.setPartitionMode("center");
        config.setPartitionRatios(java.util.Arrays.asList(0.2, 0.6, 0.2));
        config.setImageTextLayout("center-text-with-decoration");
        config.setLayoutDirection("vertical");
        config.setGridStyle("double");
        List<LayoutArea> areas = new ArrayList<>();
        areas.add(createArea("sticker", "顶部装饰", 2, 1, 5, 0.2, "center-text-with-decoration"));
        areas.add(createArea("text", "主内容区", 2, 2, null, 0.6, "center-text-with-decoration"));
        areas.add(createArea("image", "配图区", 1, 1, null, 0.1, "center-text-with-decoration"));
        areas.add(createArea("sticker", "小贴纸", 1, 1, 4, 0.1, null));
        config.setAreas(areas);
        return config;
    }

    private static LayoutConfig createCollageStyle() {
        LayoutConfig config = new LayoutConfig();
        config.setColumns(3);
        config.setColumnGap("8px");
        config.setRowGap("8px");
        config.setPadding("16px");
        config.setWhiteSpacePosition("scattered");
        config.setWhiteSpaceSize("20px");
        config.setPartitionMode("masonry");
        config.setPartitionRatios(java.util.Arrays.asList(0.3, 0.35, 0.35));
        config.setImageTextLayout("mixed-collage");
        config.setLayoutDirection("mixed");
        config.setGridStyle("none");
        List<LayoutArea> areas = new ArrayList<>();
        areas.add(createArea("image", "照片1", 1, 1, null, 0.2, "mixed-collage"));
        areas.add(createArea("text", "文字", 1, 1, null, 0.2, "mixed-collage"));
        areas.add(createArea("sticker", "票根", 1, 1, 2, 0.15, null));
        areas.add(createArea("text", "记录", 2, 1, null, 0.25, "mixed-collage"));
        areas.add(createArea("image", "照片2", 1, 2, null, 0.3, "mixed-collage"));
        areas.add(createArea("sticker", "贴纸", 2, 1, 4, 0.1, null));
        config.setAreas(areas);
        return config;
    }

    private static LayoutConfig createMinimalist() {
        LayoutConfig config = new LayoutConfig();
        config.setColumns(1);
        config.setColumnGap("0");
        config.setRowGap("20px");
        config.setPadding("40px");
        config.setWhiteSpacePosition("all-around");
        config.setWhiteSpaceSize("80px");
        config.setPartitionMode("minimal");
        config.setPartitionRatios(java.util.Arrays.asList(0.15, 0.75, 0.1));
        config.setImageTextLayout("text-only-with-minimal-decoration");
        config.setLayoutDirection("vertical");
        config.setGridStyle("none");
        List<LayoutArea> areas = new ArrayList<>();
        areas.add(createArea("text", "标题", 1, 1, null, 0.15, "text-only-with-minimal-decoration"));
        areas.add(createArea("text", "正文", 1, 3, null, 0.75, "text-only-with-minimal-decoration"));
        areas.add(createArea("sticker", "小装饰", 1, 1, 2, 0.1, null));
        config.setAreas(areas);
        return config;
    }

    private static LayoutConfig createNaturalStyle() {
        LayoutConfig config = new LayoutConfig();
        config.setColumns(2);
        config.setColumnGap("20px");
        config.setRowGap("16px");
        config.setPadding("24px");
        config.setWhiteSpacePosition("organic");
        config.setWhiteSpaceSize("50px");
        config.setPartitionMode("asymmetric");
        config.setPartitionRatios(java.util.Arrays.asList(0.45, 0.55));
        config.setImageTextLayout("left-nature-right-text");
        config.setLayoutDirection("vertical");
        config.setGridStyle("wavy");
        List<LayoutArea> areas = new ArrayList<>();
        areas.add(createArea("image", "干花区", 1, 2, null, 0.45, "left-nature-right-text"));
        areas.add(createArea("text", "主文字", 1, 1, null, 0.35, "left-nature-right-text"));
        areas.add(createArea("sticker", "树叶装饰", 1, 1, 3, 0.2, null));
        config.setAreas(areas);
        return config;
    }

    private static LayoutConfig createThreeColumnMagazine() {
        LayoutConfig config = new LayoutConfig();
        config.setColumns(3);
        config.setColumnGap("10px");
        config.setRowGap("10px");
        config.setPadding("20px");
        config.setWhiteSpacePosition("editorial");
        config.setWhiteSpaceSize("30px");
        config.setPartitionMode("editorial");
        config.setPartitionRatios(java.util.Arrays.asList(0.25, 0.5, 0.25));
        config.setImageTextLayout("magazine-layout");
        config.setLayoutDirection("mixed");
        config.setGridStyle("solid");
        List<LayoutArea> areas = new ArrayList<>();
        areas.add(createArea("image", "大图", 2, 2, null, 0.5, "magazine-layout"));
        areas.add(createArea("text", "侧栏文字", 1, 2, null, 0.25, "magazine-layout"));
        areas.add(createArea("text", "标题", 2, 1, null, 0.2, "magazine-layout"));
        areas.add(createArea("sticker", "装饰", 1, 1, 3, 0.05, null));
        config.setAreas(areas);
        return config;
    }

    private static LayoutArea createArea(String type, String label, Integer columnSpan, Integer rowSpan, Integer stickerCount) {
        return createArea(type, label, columnSpan, rowSpan, stickerCount, null, null);
    }

    private static LayoutArea createArea(String type, String label, Integer columnSpan, Integer rowSpan, Integer stickerCount, Double areaRatio, String imageTextRelation) {
        LayoutArea area = new LayoutArea();
        area.setType(type);
        area.setLabel(label);
        area.setColumnSpan(columnSpan);
        area.setRowSpan(rowSpan);
        area.setStickerCount(stickerCount);
        area.setAreaRatio(areaRatio);
        area.setImageTextRelation(imageTextRelation);
        if ("text".equals(type)) {
            area.setTextAlign("left");
            area.setTextVerticalAlign("top");
        }
        return area;
    }
}
