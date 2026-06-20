package com.journal.inspiration.common;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.util.*;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

public class ColorSchemeValidator {

    private static final Pattern HEX_COLOR_PATTERN = Pattern.compile("^#([A-Fa-f0-9]{6}|[A-Fa-f0-9]{3})$");
    private static final Pattern HEX_3_DIGIT_PATTERN = Pattern.compile("^#([A-Fa-f0-9]{3})$");

    public static final int MAX_PRIMARY = 2;
    public static final int MAX_SECONDARY = 4;
    public static final int MAX_ACCENT = 3;
    public static final int MIN_PRIMARY = 1;
    public static final int MIN_SECONDARY = 1;
    public static final int MAX_TOTAL = 8;

    public static final String TYPE_PRIMARY = "primary";
    public static final String TYPE_SECONDARY = "secondary";
    public static final String TYPE_ACCENT = "accent";

    private static final String DEFAULT_COLOR_NAME = "未命名";

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

    public static boolean isValidHexColor(String color) {
        return color != null && HEX_COLOR_PATTERN.matcher(color).matches();
    }

    public static ValidationResult validate(String colorSchemeJson) {
        if (colorSchemeJson == null || colorSchemeJson.trim().isEmpty()) {
            return ValidationResult.success();
        }

        List<Map<String, Object>> swatches;
        try {
            swatches = objectMapper.readValue(colorSchemeJson, new TypeReference<List<Map<String, Object>>>() {});
        } catch (Exception e) {
            return ValidationResult.fail("色彩方案格式错误，必须是有效的JSON数组");
        }

        if (swatches == null || swatches.isEmpty()) {
            return ValidationResult.success();
        }

        if (swatches.size() > MAX_TOTAL) {
            return ValidationResult.fail("颜色总数不能超过 " + MAX_TOTAL + " 个");
        }

        List<Map<String, Object>> validSwatches = new ArrayList<>();
        Set<String> colorSet = new HashSet<>();

        for (Map<String, Object> swatch : swatches) {
            Object colorObj = swatch.get("color");
            if (!(colorObj instanceof String)) {
                return ValidationResult.fail("颜色值格式错误");
            }
            String color = (String) colorObj;
            if (!isValidHexColor(color)) {
                return ValidationResult.fail("无效的颜色格式: " + color + "，请使用十六进制颜色");
            }

            String lowerColor = color.toLowerCase();
            if (colorSet.contains(lowerColor)) {
                return ValidationResult.fail("存在重复的颜色值: " + color);
            }
            colorSet.add(lowerColor);

            Object nameObj = swatch.get("name");
            Object purposeObj = swatch.get("purpose");
            if ((nameObj == null || ((String) nameObj).trim().isEmpty()) &&
                (purposeObj == null || ((String) purposeObj).trim().isEmpty())) {
                continue;
            }

            validSwatches.add(swatch);
        }

        if (validSwatches.isEmpty()) {
            return ValidationResult.success();
        }

        int primaryCount = 0;
        int secondaryCount = 0;
        int accentCount = 0;

        for (Map<String, Object> swatch : validSwatches) {
            Object typeObj = swatch.get("type");
            String type = typeObj instanceof String ? (String) typeObj : null;

            if (TYPE_PRIMARY.equals(type)) {
                primaryCount++;
            } else if (TYPE_SECONDARY.equals(type)) {
                secondaryCount++;
            } else if (TYPE_ACCENT.equals(type)) {
                accentCount++;
            }
        }

        boolean hasTypedColors = primaryCount > 0 || secondaryCount > 0 || accentCount > 0;

        if (hasTypedColors) {
            if (primaryCount < MIN_PRIMARY) {
                return ValidationResult.fail("至少需要 " + MIN_PRIMARY + " 个主色");
            }
            if (secondaryCount < MIN_SECONDARY) {
                return ValidationResult.fail("至少需要 " + MIN_SECONDARY + " 个辅助色");
            }
        }

        if (primaryCount > MAX_PRIMARY) {
            return ValidationResult.fail("主色不能超过 " + MAX_PRIMARY + " 个");
        }
        if (secondaryCount > MAX_SECONDARY) {
            return ValidationResult.fail("辅助色不能超过 " + MAX_SECONDARY + " 个");
        }
        if (accentCount > MAX_ACCENT) {
            return ValidationResult.fail("点缀色不能超过 " + MAX_ACCENT + " 个");
        }

        return ValidationResult.success();
    }

    public static String normalize(String colorSchemeJson) {
        if (colorSchemeJson == null || colorSchemeJson.trim().isEmpty()) {
            return null;
        }

        List<Map<String, Object>> swatches;
        try {
            swatches = objectMapper.readValue(colorSchemeJson, new TypeReference<List<Map<String, Object>>>() {});
        } catch (Exception e) {
            return null;
        }

        if (swatches == null || swatches.isEmpty()) {
            return null;
        }

        List<Map<String, Object>> normalizedSwatches = new ArrayList<>();
        Set<String> colorSet = new HashSet<>();
        int primaryCount = 0;
        int secondaryCount = 0;
        int accentCount = 0;

        for (Map<String, Object> swatch : swatches) {
            Object colorObj = swatch.get("color");
            if (!(colorObj instanceof String)) {
                continue;
            }

            String color = normalizeColorValue((String) colorObj);
            if (!isValidHexColor(color)) {
                continue;
            }

            if (colorSet.contains(color)) {
                continue;
            }
            colorSet.add(color);

            Object nameObj = swatch.get("name");
            Object purposeObj = swatch.get("purpose");
            Object typeObj = swatch.get("type");

            String name = nameObj instanceof String ? ((String) nameObj).trim() : "";
            String purpose = purposeObj instanceof String ? ((String) purposeObj).trim() : "";
            String type = typeObj instanceof String ? ((String) typeObj).trim() : "";

            if (name.isEmpty() && purpose.isEmpty()) {
                continue;
            }

            if (name.isEmpty()) {
                name = generateColorName(color, purpose);
            }

            if (type.isEmpty()) {
                type = inferColorTypeByNameAndPurpose(name, purpose);
            }

            type = normalizeType(type, primaryCount, secondaryCount, accentCount);
            if (TYPE_PRIMARY.equals(type)) {
                primaryCount++;
            } else if (TYPE_SECONDARY.equals(type)) {
                secondaryCount++;
            } else if (TYPE_ACCENT.equals(type)) {
                accentCount++;
            }

            Map<String, Object> normalizedSwatch = new LinkedHashMap<>();
            normalizedSwatch.put("color", color);
            normalizedSwatch.put("name", name);
            if (!purpose.isEmpty()) {
                normalizedSwatch.put("purpose", purpose);
            }
            normalizedSwatch.put("type", type);

            normalizedSwatches.add(normalizedSwatch);

            if (normalizedSwatches.size() >= MAX_TOTAL) {
                break;
            }
        }

        if (normalizedSwatches.isEmpty()) {
            return null;
        }

        normalizedSwatches = sortSwatches(normalizedSwatches);

        try {
            return objectMapper.writeValueAsString(normalizedSwatches);
        } catch (Exception e) {
            return null;
        }
    }

    private static String normalizeColorValue(String color) {
        if (color == null) {
            return null;
        }
        color = color.trim().toLowerCase();
        if (HEX_3_DIGIT_PATTERN.matcher(color).matches()) {
            char r = color.charAt(1);
            char g = color.charAt(2);
            char b = color.charAt(3);
            return "#" + r + r + g + g + b + b;
        }
        return color;
    }

    private static String generateColorName(String color, String purpose) {
        if (purpose != null && !purpose.isEmpty()) {
            int maxLen = Math.min(8, purpose.length());
            return purpose.substring(0, maxLen) + "色";
        }
        return DEFAULT_COLOR_NAME;
    }

    private static String inferColorTypeByNameAndPurpose(String name, String purpose) {
        String combined = (name + " " + purpose).toLowerCase();

        if (combined.contains("主色") || combined.contains("主色调") || combined.contains("primary")
                || combined.contains("核心") || combined.contains("基调")) {
            return TYPE_PRIMARY;
        }
        if (combined.contains("辅助色") || combined.contains("次要") || combined.contains("secondary")
                || combined.contains("背景") || combined.contains("协调")) {
            return TYPE_SECONDARY;
        }
        if (combined.contains("点缀色") || combined.contains("强调色") || combined.contains("亮点")
                || combined.contains("accent") || combined.contains("突出") || combined.contains("重点")) {
            return TYPE_ACCENT;
        }
        return "";
    }

    private static String normalizeType(String type, int primaryCount, int secondaryCount, int accentCount) {
        if (TYPE_PRIMARY.equals(type)) {
            if (primaryCount < MAX_PRIMARY) {
                return TYPE_PRIMARY;
            }
            return TYPE_SECONDARY;
        }
        if (TYPE_SECONDARY.equals(type)) {
            if (secondaryCount < MAX_SECONDARY) {
                return TYPE_SECONDARY;
            }
            return TYPE_ACCENT;
        }
        if (TYPE_ACCENT.equals(type)) {
            if (accentCount < MAX_ACCENT) {
                return TYPE_ACCENT;
            }
            return TYPE_SECONDARY;
        }

        if (primaryCount < MIN_PRIMARY) {
            return TYPE_PRIMARY;
        }
        if (secondaryCount < MIN_SECONDARY) {
            return TYPE_SECONDARY;
        }
        if (secondaryCount < MAX_SECONDARY) {
            return TYPE_SECONDARY;
        }
        if (accentCount < MAX_ACCENT) {
            return TYPE_ACCENT;
        }
        return TYPE_SECONDARY;
    }

    private static List<Map<String, Object>> sortSwatches(List<Map<String, Object>> swatches) {
        Map<String, Integer> typeOrder = new HashMap<>();
        typeOrder.put(TYPE_PRIMARY, 1);
        typeOrder.put(TYPE_SECONDARY, 2);
        typeOrder.put(TYPE_ACCENT, 3);

        return swatches.stream()
                .sorted(Comparator.comparingInt(s -> {
                    String type = (String) s.get("type");
                    return typeOrder.getOrDefault(type, 99);
                }))
                .collect(Collectors.toList());
    }
}
