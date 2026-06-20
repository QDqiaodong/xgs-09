package com.journal.inspiration.common;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.util.*;
import java.util.stream.Collectors;

public class LayoutConfigValidator {

    private static final Set<String> VALID_WHITE_SPACE_POSITIONS = new HashSet<>(Arrays.asList(
            "top", "bottom", "left", "right", "around", "all-around", "organic", "scattered", "editorial", "none"
    ));

    private static final Set<String> VALID_PARTITION_MODES = new HashSet<>(Arrays.asList(
            "fixed", "flow", "center", "masonry", "minimal", "asymmetric", "editorial", "auto"
    ));

    private static final Set<String> VALID_IMAGE_TEXT_LAYOUTS = new HashSet<>(Arrays.asList(
            "left-image-right-text", "left-text-right-image", "top-image-bottom-text",
            "top-text-bottom-image", "center-text-with-decoration", "text-only-with-minimal-decoration",
            "left-nature-right-text", "magazine-layout", "timeline", "mixed-collage", "overlay", "side-by-side"
    ));

    private static final Set<String> VALID_LAYOUT_DIRECTIONS = new HashSet<>(Arrays.asList(
            "vertical", "horizontal", "mixed"
    ));

    private static final Set<String> VALID_GRID_STYLES = new HashSet<>(Arrays.asList(
            "solid", "dashed", "dotted", "double", "wavy", "none"
    ));

    private static final Set<String> VALID_AREA_TYPES = new HashSet<>(Arrays.asList(
            "text", "image", "sticker", "handwriting", "tape", "stamp"
    ));

    private static final Set<String> VALID_IMAGE_TEXT_RELATIONS = new HashSet<>(Arrays.asList(
            "left-image-right-text", "left-text-right-image", "top-image-bottom-text",
            "top-text-bottom-image", "image-overlay-text", "text-overlay-image",
            "side-by-side", "timeline", "center-text-with-decoration",
            "text-only-with-minimal-decoration", "left-nature-right-text",
            "magazine-layout", "mixed-collage", null
    ));

    private static final Set<String> VALID_TEXT_ALIGNS = new HashSet<>(Arrays.asList(
            "left", "center", "right", "justify", null
    ));

    private static final Set<String> VALID_TEXT_VERTICAL_ALIGNS = new HashSet<>(Arrays.asList(
            "top", "middle", "bottom", null
    ));

    private static final Set<String> VALID_DECORATIVE_ELEMENT_TYPES = new HashSet<>(Arrays.asList(
            "sticker", "tape", "stamp", "doodle", "frame", "border", "corner", "divider", "icon"
    ));

    private static final int MAX_AREAS = 20;
    private static final int MAX_DECORATIVE_ELEMENTS_PER_AREA = 10;
    private static final double MIN_RATIO = 0.01;
    private static final double MAX_RATIO = 1.0;
    private static final int MAX_COLUMNS = 6;

    private static final ObjectMapper objectMapper = new ObjectMapper();

    public static class ValidationResult {
        private final boolean valid;
        private final String message;

        public ValidationResult(boolean valid, String message) {
            this.valid = valid;
            this.message = message;
        }

        public boolean isValid() {
            return valid;
        }

        public String getMessage() {
            return message;
        }

        public static ValidationResult success() {
            return new ValidationResult(true, null);
        }

        public static ValidationResult fail(String message) {
            return new ValidationResult(false, message);
        }
    }

    public static ValidationResult validate(String layoutConfigJson) {
        if (layoutConfigJson == null || layoutConfigJson.trim().isEmpty()) {
            return ValidationResult.success();
        }

        Map<String, Object> config;
        try {
            config = objectMapper.readValue(layoutConfigJson, new TypeReference<Map<String, Object>>() {});
        } catch (Exception e) {
            return ValidationResult.fail("版式配置格式错误，必须是有效的JSON对象");
        }

        if (config == null || config.isEmpty()) {
            return ValidationResult.success();
        }

        try {
            validateColumns(config);
            validateWhiteSpace(config);
            validatePartition(config);
            validateImageTextLayout(config);
            validateGridStyle(config);
            validateAreas(config);
        } catch (IllegalArgumentException e) {
            return ValidationResult.fail(e.getMessage());
        }

        return ValidationResult.success();
    }

    private static void validateColumns(Map<String, Object> config) {
        Object columnsObj = config.get("columns");
        if (columnsObj != null) {
            if (!(columnsObj instanceof Number)) {
                throw new IllegalArgumentException("columns 必须是数字类型");
            }
            int columns = ((Number) columnsObj).intValue();
            if (columns < 1 || columns > MAX_COLUMNS) {
                throw new IllegalArgumentException("columns 必须在 1 到 " + MAX_COLUMNS + " 之间");
            }
        }
    }

    private static void validateWhiteSpace(Map<String, Object> config) {
        Object whiteSpacePosObj = config.get("whiteSpacePosition");
        if (whiteSpacePosObj != null) {
            String position = whiteSpacePosObj.toString();
            if (!VALID_WHITE_SPACE_POSITIONS.contains(position)) {
                throw new IllegalArgumentException("无效的留白位置: " + position + "，有效值为: " + VALID_WHITE_SPACE_POSITIONS);
            }
        }

        Object whiteSpaceSizeObj = config.get("whiteSpaceSize");
        if (whiteSpaceSizeObj != null) {
            String size = whiteSpaceSizeObj.toString();
            if (!size.matches("^\\d+(px|em|rem|%)?$")) {
                throw new IllegalArgumentException("留白尺寸格式错误: " + size + "，应为带单位的数值，如 '20px'");
            }
        }
    }

    private static void validatePartition(Map<String, Object> config) {
        Object partitionModeObj = config.get("partitionMode");
        if (partitionModeObj != null) {
            String mode = partitionModeObj.toString();
            if (!VALID_PARTITION_MODES.contains(mode)) {
                throw new IllegalArgumentException("无效的分区模式: " + mode + "，有效值为: " + VALID_PARTITION_MODES);
            }
        }

        Object partitionRatiosObj = config.get("partitionRatios");
        if (partitionRatiosObj != null) {
            if (!(partitionRatiosObj instanceof List)) {
                throw new IllegalArgumentException("partitionRatios 必须是数组类型");
            }
            List<?> ratios = (List<?>) partitionRatiosObj;
            double sum = 0;
            for (Object ratioObj : ratios) {
                if (!(ratioObj instanceof Number)) {
                    throw new IllegalArgumentException("分区比例必须是数字类型");
                }
                double ratio = ((Number) ratioObj).doubleValue();
                if (ratio < MIN_RATIO || ratio > MAX_RATIO) {
                    throw new IllegalArgumentException("分区比例必须在 " + MIN_RATIO + " 到 " + MAX_RATIO + " 之间");
                }
                sum += ratio;
            }
            if (Math.abs(sum - 1.0) > 0.01) {
                throw new IllegalArgumentException("分区比例之和必须约等于 1.0，当前和为: " + sum);
            }
        }
    }

    private static void validateImageTextLayout(Map<String, Object> config) {
        Object layoutObj = config.get("imageTextLayout");
        if (layoutObj != null) {
            String layout = layoutObj.toString();
            if (!VALID_IMAGE_TEXT_LAYOUTS.contains(layout)) {
                throw new IllegalArgumentException("无效的图文布局: " + layout + "，有效值为: " + VALID_IMAGE_TEXT_LAYOUTS);
            }
        }

        Object directionObj = config.get("layoutDirection");
        if (directionObj != null) {
            String direction = directionObj.toString();
            if (!VALID_LAYOUT_DIRECTIONS.contains(direction)) {
                throw new IllegalArgumentException("无效的布局方向: " + direction + "，有效值为: " + VALID_LAYOUT_DIRECTIONS);
            }
        }
    }

    private static void validateGridStyle(Map<String, Object> config) {
        Object gridStyleObj = config.get("gridStyle");
        if (gridStyleObj != null) {
            String style = gridStyleObj.toString();
            if (!VALID_GRID_STYLES.contains(style)) {
                throw new IllegalArgumentException("无效的网格样式: " + style + "，有效值为: " + VALID_GRID_STYLES);
            }
        }
    }

    private static void validateAreas(Map<String, Object> config) {
        Object areasObj = config.get("areas");
        if (areasObj == null) {
            return;
        }
        if (!(areasObj instanceof List)) {
            throw new IllegalArgumentException("areas 必须是数组类型");
        }
        List<?> areas = (List<?>) areasObj;
        if (areas.size() > MAX_AREAS) {
            throw new IllegalArgumentException("区域数量不能超过 " + MAX_AREAS + " 个");
        }

        for (int i = 0; i < areas.size(); i++) {
            Object areaObj = areas.get(i);
            if (!(areaObj instanceof Map)) {
                throw new IllegalArgumentException("第 " + (i + 1) + " 个区域格式错误，必须是对象类型");
            }
            validateArea((Map<?, ?>) areaObj, i + 1);
        }
    }

    private static void validateArea(Map<?, ?> area, int index) {
        Object typeObj = area.get("type");
        if (typeObj == null) {
            throw new IllegalArgumentException("第 " + index + " 个区域缺少 type 字段");
        }
        String type = typeObj.toString();
        if (!VALID_AREA_TYPES.contains(type)) {
            throw new IllegalArgumentException("第 " + index + " 个区域 type 无效: " + type + "，有效值为: " + VALID_AREA_TYPES);
        }

        Object areaRatioObj = area.get("areaRatio");
        if (areaRatioObj != null) {
            if (!(areaRatioObj instanceof Number)) {
                throw new IllegalArgumentException("第 " + index + " 个区域 areaRatio 必须是数字类型");
            }
            double ratio = ((Number) areaRatioObj).doubleValue();
            if (ratio < MIN_RATIO || ratio > MAX_RATIO) {
                throw new IllegalArgumentException("第 " + index + " 个区域 areaRatio 必须在 " + MIN_RATIO + " 到 " + MAX_RATIO + " 之间");
            }
        }

        Object imageTextRelationObj = area.get("imageTextRelation");
        if (imageTextRelationObj != null) {
            String relation = imageTextRelationObj.toString();
            if (!VALID_IMAGE_TEXT_RELATIONS.contains(relation)) {
                throw new IllegalArgumentException("第 " + index + " 个区域 imageTextRelation 无效: " + relation);
            }
        }

        Object textAlignObj = area.get("textAlign");
        if (textAlignObj != null) {
            String align = textAlignObj.toString();
            if (!VALID_TEXT_ALIGNS.contains(align)) {
                throw new IllegalArgumentException("第 " + index + " 个区域 textAlign 无效: " + align);
            }
        }

        Object textVerticalAlignObj = area.get("textVerticalAlign");
        if (textVerticalAlignObj != null) {
            String valign = textVerticalAlignObj.toString();
            if (!VALID_TEXT_VERTICAL_ALIGNS.contains(valign)) {
                throw new IllegalArgumentException("第 " + index + " 个区域 textVerticalAlign 无效: " + valign);
            }
        }

        Object decorativeElementsObj = area.get("decorativeElements");
        if (decorativeElementsObj != null) {
            if (!(decorativeElementsObj instanceof List)) {
                throw new IllegalArgumentException("第 " + index + " 个区域 decorativeElements 必须是数组类型");
            }
            List<?> elements = (List<?>) decorativeElementsObj;
            if (elements.size() > MAX_DECORATIVE_ELEMENTS_PER_AREA) {
                throw new IllegalArgumentException("第 " + index + " 个区域装饰元素数量不能超过 " + MAX_DECORATIVE_ELEMENTS_PER_AREA + " 个");
            }
            for (int i = 0; i < elements.size(); i++) {
                Object elemObj = elements.get(i);
                if (!(elemObj instanceof Map)) {
                    throw new IllegalArgumentException("第 " + index + " 个区域第 " + (i + 1) + " 个装饰元素格式错误");
                }
                validateDecorativeElement((Map<?, ?>) elemObj, index, i + 1);
            }
        }
    }

    private static void validateDecorativeElement(Map<?, ?> element, int areaIndex, int elemIndex) {
        Object typeObj = element.get("elementType");
        if (typeObj == null) {
            throw new IllegalArgumentException("第 " + areaIndex + " 区域第 " + elemIndex + " 装饰元素缺少 elementType 字段");
        }
        String type = typeObj.toString();
        if (!VALID_DECORATIVE_ELEMENT_TYPES.contains(type)) {
            throw new IllegalArgumentException("第 " + areaIndex + " 区域第 " + elemIndex + " 装饰元素 elementType 无效: " + type);
        }

        Object quantityObj = element.get("quantity");
        if (quantityObj != null) {
            if (!(quantityObj instanceof Number)) {
                throw new IllegalArgumentException("第 " + areaIndex + " 区域第 " + elemIndex + " 装饰元素 quantity 必须是数字");
            }
            int quantity = ((Number) quantityObj).intValue();
            if (quantity < 1 || quantity > 100) {
                throw new IllegalArgumentException("第 " + areaIndex + " 区域第 " + elemIndex + " 装饰元素 quantity 必须在 1 到 100 之间");
            }
        }
    }

    public static String normalize(String layoutConfigJson) {
        if (layoutConfigJson == null || layoutConfigJson.trim().isEmpty()) {
            return null;
        }

        Map<String, Object> config;
        try {
            config = objectMapper.readValue(layoutConfigJson, new TypeReference<Map<String, Object>>() {});
        } catch (Exception e) {
            return null;
        }

        if (config == null || config.isEmpty()) {
            return null;
        }

        Map<String, Object> normalizedConfig = new LinkedHashMap<>();

        putIfNotNull(normalizedConfig, "columns", config.get("columns"));
        putIfNotNull(normalizedConfig, "columnGap", config.get("columnGap"));
        putIfNotNull(normalizedConfig, "rowGap", config.get("rowGap"));
        putIfNotNull(normalizedConfig, "padding", config.get("padding"));

        putIfValid(normalizedConfig, "whiteSpacePosition", config.get("whiteSpacePosition"), VALID_WHITE_SPACE_POSITIONS);
        putIfNotNull(normalizedConfig, "whiteSpaceSize", config.get("whiteSpaceSize"));
        putIfValid(normalizedConfig, "partitionMode", config.get("partitionMode"), VALID_PARTITION_MODES);
        putIfNotNull(normalizedConfig, "partitionRatios", normalizePartitionRatios(config.get("partitionRatios")));
        putIfValid(normalizedConfig, "imageTextLayout", config.get("imageTextLayout"), VALID_IMAGE_TEXT_LAYOUTS);
        putIfValid(normalizedConfig, "layoutDirection", config.get("layoutDirection"), VALID_LAYOUT_DIRECTIONS);
        putIfNotNull(normalizedConfig, "pageBackground", config.get("pageBackground"));
        putIfValid(normalizedConfig, "gridStyle", config.get("gridStyle"), VALID_GRID_STYLES);

        Object areasObj = config.get("areas");
        if (areasObj instanceof List) {
            List<?> areas = (List<?>) areasObj;
            List<Map<String, Object>> normalizedAreas = new ArrayList<>();
            for (Object areaObj : areas) {
                if (areaObj instanceof Map) {
                    Map<String, Object> normalizedArea = normalizeArea((Map<?, ?>) areaObj);
                    if (normalizedArea != null && !normalizedArea.isEmpty()) {
                        normalizedAreas.add(normalizedArea);
                    }
                }
            }
            if (!normalizedAreas.isEmpty()) {
                normalizedConfig.put("areas", normalizedAreas);
            }
        }

        try {
            return objectMapper.writeValueAsString(normalizedConfig);
        } catch (Exception e) {
            return null;
        }
    }

    private static List<Double> normalizePartitionRatios(Object ratiosObj) {
        if (!(ratiosObj instanceof List)) {
            return null;
        }
        List<?> ratios = (List<?>) ratiosObj;
        List<Double> normalized = new ArrayList<>();
        for (Object ratioObj : ratios) {
            if (ratioObj instanceof Number) {
                normalized.add(((Number) ratioObj).doubleValue());
            }
        }
        return normalized.isEmpty() ? null : normalized;
    }

    private static Map<String, Object> normalizeArea(Map<?, ?> area) {
        Map<String, Object> normalized = new LinkedHashMap<>();

        Object type = area.get("type");
        if (type == null) {
            return null;
        }
        normalized.put("type", type.toString());

        putIfNotNull(normalized, "label", area.get("label"));
        putIfNotNull(normalized, "columnSpan", area.get("columnSpan"));
        putIfNotNull(normalized, "rowSpan", area.get("rowSpan"));
        putIfNotNull(normalized, "stickerCount", area.get("stickerCount"));
        putIfNotNull(normalized, "alignSelf", area.get("alignSelf"));
        putIfNotNull(normalized, "justifySelf", area.get("justifySelf"));
        putIfNotNull(normalized, "areaRatio", area.get("areaRatio"));
        putIfValid(normalized, "imageTextRelation", area.get("imageTextRelation"), VALID_IMAGE_TEXT_RELATIONS);
        putIfValid(normalized, "textAlign", area.get("textAlign"), VALID_TEXT_ALIGNS);
        putIfValid(normalized, "textVerticalAlign", area.get("textVerticalAlign"), VALID_TEXT_VERTICAL_ALIGNS);
        putIfNotNull(normalized, "backgroundStyle", area.get("backgroundStyle"));
        putIfNotNull(normalized, "borderStyle", area.get("borderStyle"));

        Object decorativeElementsObj = area.get("decorativeElements");
        if (decorativeElementsObj instanceof List) {
            List<?> elements = (List<?>) decorativeElementsObj;
            List<Map<String, Object>> normalizedElements = new ArrayList<>();
            for (Object elemObj : elements) {
                if (elemObj instanceof Map) {
                    Map<String, Object> normalizedElem = normalizeDecorativeElement((Map<?, ?>) elemObj);
                    if (normalizedElem != null && !normalizedElem.isEmpty()) {
                        normalizedElements.add(normalizedElem);
                    }
                }
            }
            if (!normalizedElements.isEmpty()) {
                normalized.put("decorativeElements", normalizedElements);
            }
        }

        return normalized;
    }

    private static Map<String, Object> normalizeDecorativeElement(Map<?, ?> element) {
        Map<String, Object> normalized = new LinkedHashMap<>();

        Object type = element.get("elementType");
        if (type == null) {
            return null;
        }
        if (!VALID_DECORATIVE_ELEMENT_TYPES.contains(type.toString())) {
            return null;
        }
        normalized.put("elementType", type.toString());

        putIfNotNull(normalized, "elementName", element.get("elementName"));
        putIfNotNull(normalized, "quantity", element.get("quantity"));
        putIfNotNull(normalized, "position", element.get("position"));
        putIfNotNull(normalized, "color", element.get("color"));
        putIfNotNull(normalized, "size", element.get("size"));
        putIfNotNull(normalized, "style", element.get("style"));

        return normalized;
    }

    private static void putIfNotNull(Map<String, Object> map, String key, Object value) {
        if (value != null) {
            map.put(key, value);
        }
    }

    private static void putIfValid(Map<String, Object> map, String key, Object value, Set<String> validValues) {
        if (value == null) {
            return;
        }
        String strValue = value.toString();
        if (validValues.contains(strValue)) {
            map.put(key, strValue);
        }
    }
}
