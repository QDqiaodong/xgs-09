package com.journal.inspiration.common;

import java.awt.Color;
import java.util.*;
import java.util.stream.Collectors;

public class ColorAnalysisUtil {

    public static class HSL {
        private final float h;
        private final float s;
        private final float l;

        public HSL(float h, float s, float l) {
            this.h = h;
            this.s = s;
            this.l = l;
        }

        public float getH() { return h; }
        public float getS() { return s; }
        public float getL() { return l; }
    }

    public static class ColorFamily {
        private final String key;
        private final String name;
        private final String representativeColor;
        private final float minHue;
        private final float maxHue;
        private final Float minSaturation;
        private final Float maxSaturation;
        private final Float minLightness;
        private final Float maxLightness;

        public ColorFamily(String key, String name, String representativeColor,
                          float minHue, float maxHue,
                          Float minSaturation, Float maxSaturation,
                          Float minLightness, Float maxLightness) {
            this.key = key;
            this.name = name;
            this.representativeColor = representativeColor;
            this.minHue = minHue;
            this.maxHue = maxHue;
            this.minSaturation = minSaturation;
            this.maxSaturation = maxSaturation;
            this.minLightness = minLightness;
            this.maxLightness = maxLightness;
        }

        public boolean matches(HSL hsl) {
            float hue = hsl.getH();
            boolean hueMatch;
            if (minHue > maxHue) {
                hueMatch = hue >= minHue || hue <= maxHue;
            } else {
                hueMatch = hue >= minHue && hue <= maxHue;
            }
            if (!hueMatch) return false;

            if (minSaturation != null && hsl.getS() < minSaturation) return false;
            if (maxSaturation != null && hsl.getS() > maxSaturation) return false;
            if (minLightness != null && hsl.getL() < minLightness) return false;
            if (maxLightness != null && hsl.getL() > maxLightness) return false;

            return true;
        }

        public String getKey() { return key; }
        public String getName() { return name; }
        public String getRepresentativeColor() { return representativeColor; }
    }

    private static final List<ColorFamily> COLOR_FAMILIES = Arrays.asList(
        new ColorFamily("red", "红色系", "#E74C3C", 350f, 15f, 0.4f, null, 0.25f, 0.75f),
        new ColorFamily("pink", "粉色系", "#FF69B4", 310f, 350f, 0.3f, null, 0.5f, 0.9f),
        new ColorFamily("orange", "橙色系", "#E67E22", 15f, 45f, 0.4f, null, 0.3f, 0.8f),
        new ColorFamily("yellow", "黄色系", "#F1C40F", 45f, 75f, 0.4f, null, 0.4f, 0.85f),
        new ColorFamily("green", "绿色系", "#27AE60", 75f, 165f, 0.3f, null, 0.2f, 0.8f),
        new ColorFamily("cyan", "青色系", "#1ABC9C", 165f, 195f, 0.3f, null, 0.3f, 0.8f),
        new ColorFamily("blue", "蓝色系", "#3498DB", 195f, 255f, 0.3f, null, 0.2f, 0.8f),
        new ColorFamily("purple", "紫色系", "#9B59B6", 255f, 310f, 0.3f, null, 0.25f, 0.75f),
        new ColorFamily("brown", "棕色系", "#8B4513", 15f, 45f, 0.2f, 0.6f, 0.15f, 0.45f),
        new ColorFamily("gray", "灰色系", "#95A5A6", 0f, 360f, 0f, 0.15f, 0.15f, 0.85f),
        new ColorFamily("black", "黑色系", "#2C3E50", 0f, 360f, 0f, 0.3f, 0f, 0.15f),
        new ColorFamily("white", "白色系", "#ECF0F1", 0f, 360f, 0f, 0.2f, 0.85f, 1.0f)
    );

    public static HSL hexToHsl(String hex) {
        Color color = hexToColor(hex);
        if (color == null) {
            return null;
        }
        return rgbToHsl(color.getRed(), color.getGreen(), color.getBlue());
    }

    public static Color hexToColor(String hex) {
        if (hex == null || hex.isEmpty()) {
            return null;
        }
        String cleanHex = hex.trim();
        if (cleanHex.startsWith("#")) {
            cleanHex = cleanHex.substring(1);
        }
        if (cleanHex.length() == 3) {
            cleanHex = "" + cleanHex.charAt(0) + cleanHex.charAt(0)
                      + cleanHex.charAt(1) + cleanHex.charAt(1)
                      + cleanHex.charAt(2) + cleanHex.charAt(2);
        }
        if (cleanHex.length() != 6) {
            return null;
        }
        try {
            int r = Integer.parseInt(cleanHex.substring(0, 2), 16);
            int g = Integer.parseInt(cleanHex.substring(2, 4), 16);
            int b = Integer.parseInt(cleanHex.substring(4, 6), 16);
            return new Color(r, g, b);
        } catch (NumberFormatException e) {
            return null;
        }
    }

    public static HSL rgbToHsl(int r, int g, int b) {
        float rf = r / 255f;
        float gf = g / 255f;
        float bf = b / 255f;

        float max = Math.max(Math.max(rf, gf), bf);
        float min = Math.min(Math.min(rf, gf), bf);
        float h = 0, s, l = (max + min) / 2;

        if (max == min) {
            h = s = 0;
        } else {
            float d = max - min;
            s = l > 0.5 ? d / (2 - max - min) : d / (max + min);

            if (max == rf) {
                h = ((gf - bf) / d + (gf < bf ? 6 : 0)) / 6;
            } else if (max == gf) {
                h = ((bf - rf) / d + 2) / 6;
            } else {
                h = ((rf - gf) / d + 4) / 6;
            }
        }

        return new HSL(h * 360, s, l);
    }

    public static ColorFamily classifyColor(String hex) {
        HSL hsl = hexToHsl(hex);
        if (hsl == null) {
            return COLOR_FAMILIES.get(9);
        }

        for (ColorFamily family : COLOR_FAMILIES) {
            if (family.matches(hsl)) {
                return family;
            }
        }

        HSL grayHsl = new HSL(0, 0, hsl.getL());
        for (ColorFamily family : COLOR_FAMILIES.subList(9, 12)) {
            if (family.matches(grayHsl)) {
                return family;
            }
        }

        return COLOR_FAMILIES.get(9);
    }

    public static String getColorName(String hex) {
        HSL hsl = hexToHsl(hex);
        if (hsl == null) {
            return "未知颜色";
        }

        ColorFamily family = classifyColor(hex);
        String baseName = family.getName().replace("系", "");

        float l = hsl.getL();
        float s = hsl.getS();

        String lightnessDesc;
        if (l > 0.85) {
            lightnessDesc = "浅";
        } else if (l > 0.7) {
            lightnessDesc = "淡";
        } else if (l > 0.55) {
            lightnessDesc = "亮";
        } else if (l < 0.25) {
            lightnessDesc = "深";
        } else if (l < 0.4) {
            lightnessDesc = "暗";
        } else {
            lightnessDesc = "";
        }

        String saturationDesc = "";
        if (s < 0.3 && l > 0.25 && l < 0.85) {
            saturationDesc = "灰";
        } else if (s > 0.7) {
            saturationDesc = "鲜";
        }

        return lightnessDesc + saturationDesc + baseName;
    }

    public static double colorDistance(String hex1, String hex2) {
        Color c1 = hexToColor(hex1);
        Color c2 = hexToColor(hex2);
        if (c1 == null || c2 == null) {
            return Double.MAX_VALUE;
        }

        int r1 = c1.getRed(), g1 = c1.getGreen(), b1 = c1.getBlue();
        int r2 = c2.getRed(), g2 = c2.getGreen(), b2 = c2.getBlue();

        double rmean = (r1 + r2) / 2.0;
        double r = r1 - r2;
        double g = g1 - g2;
        double b = b1 - b2;

        double weightR = 2 + rmean / 256;
        double weightG = 4.0;
        double weightB = 2 + (255 - rmean) / 256;

        return Math.sqrt(weightR * r * r + weightG * g * g + weightB * b * b);
    }

    public static boolean areColorsSimilar(String hex1, String hex2, double threshold) {
        return colorDistance(hex1, hex2) < threshold;
    }

    public static List<String> generateCombinationKey(List<String> colors) {
        if (colors == null || colors.isEmpty()) {
            return new ArrayList<>();
        }

        List<String> normalized = new ArrayList<>();
        for (String color : colors) {
            String normalizedColor = normalizeColor(color);
            if (normalizedColor != null) {
                normalized.add(normalizedColor);
            }
        }

        if (normalized.size() >= 2) {
            boolean isSorted = false;
            while (!isSorted) {
                isSorted = true;
                for (int i = 0; i < normalized.size() - 1; i++) {
                    String key1 = generateSortKey(normalized.get(i));
                    String key2 = generateSortKey(normalized.get(i + 1));
                    if (key1.compareTo(key2) > 0) {
                        Collections.swap(normalized, i, i + 1);
                        isSorted = false;
                    }
                }
            }
        }

        return normalized;
    }

    private static String generateSortKey(String hex) {
        HSL hsl = hexToHsl(hex);
        if (hsl == null) {
            return "zzz";
        }
        ColorFamily family = classifyColor(hex);
        int familyIndex = COLOR_FAMILIES.indexOf(family);
        return String.format("%02d_%03d_%03d_%03d",
                familyIndex,
                (int) (hsl.getH()),
                (int) (hsl.getS() * 100),
                (int) (hsl.getL() * 100));
    }

    public static String normalizeColor(String hex) {
        if (hex == null || hex.trim().isEmpty()) {
            return null;
        }
        String cleanHex = hex.trim();
        if (cleanHex.startsWith("#")) {
            cleanHex = cleanHex.substring(1);
        }
        if (cleanHex.length() == 3) {
            cleanHex = "" + cleanHex.charAt(0) + cleanHex.charAt(0)
                      + cleanHex.charAt(1) + cleanHex.charAt(1)
                      + cleanHex.charAt(2) + cleanHex.charAt(2);
        }
        if (cleanHex.length() != 6) {
            return null;
        }
        return "#" + cleanHex.toUpperCase();
    }

    public static List<ColorFamily> getAllFamilies() {
        return Collections.unmodifiableList(COLOR_FAMILIES);
    }

    public static String inferStyleTag(List<String> colors) {
        if (colors == null || colors.isEmpty()) {
            return null;
        }

        Set<String> familyKeys = colors.stream()
                .map(c -> classifyColor(c).getKey())
                .collect(Collectors.toSet());

        if (familyKeys.contains("pink") && familyKeys.contains("purple")) {
            return "梦幻甜美";
        }
        if (familyKeys.contains("green") && familyKeys.contains("brown")) {
            return "自然森系";
        }
        if (familyKeys.contains("blue") && familyKeys.contains("cyan")) {
            return "清新治愈";
        }
        if (familyKeys.contains("red") && familyKeys.contains("green") && familyKeys.contains("yellow")) {
            return "节日喜庆";
        }
        if (familyKeys.contains("orange") && familyKeys.contains("yellow") && familyKeys.contains("brown")) {
            return "温暖秋日";
        }
        if (familyKeys.contains("gray") && familyKeys.contains("blue")) {
            return "简约商务";
        }
        if (familyKeys.contains("white") && familyKeys.size() <= 2) {
            return "极简盐系";
        }
        if (familyKeys.contains("red") && familyKeys.contains("pink")) {
            return "热情活力";
        }
        if (colors.size() <= 3 && (familyKeys.contains("gray") || familyKeys.contains("black"))) {
            return "简约克制";
        }
        if (familyKeys.size() >= 4) {
            return "丰富多彩";
        }

        ColorFamily primaryFamily = classifyColor(colors.get(0));
        switch (primaryFamily.getKey()) {
            case "pink":
                return "温柔甜美";
            case "blue":
                return "冷静沉稳";
            case "green":
                return "清新自然";
            case "yellow":
                return "明亮活泼";
            case "purple":
                return "神秘浪漫";
            case "orange":
                return "温暖热情";
            case "brown":
                return "复古文艺";
            default:
                return null;
        }
    }
}
