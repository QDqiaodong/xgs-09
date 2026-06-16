package com.journal.inspiration.common;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.util.*;
import java.util.regex.Pattern;

public class ColorSchemeValidator {

    private static final Pattern HEX_COLOR_PATTERN = Pattern.compile("^#([A-Fa-f0-9]{6}|[A-Fa-f0-9]{3})$");

    public static final int MAX_PRIMARY = 2;
    public static final int MAX_SECONDARY = 4;
    public static final int MAX_ACCENT = 3;
    public static final int MIN_PRIMARY = 1;
    public static final int MIN_SECONDARY = 1;
    public static final int MAX_TOTAL = 8;

    public static final String TYPE_PRIMARY = "primary";
    public static final String TYPE_SECONDARY = "secondary";
    public static final String TYPE_ACCENT = "accent";

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
}
