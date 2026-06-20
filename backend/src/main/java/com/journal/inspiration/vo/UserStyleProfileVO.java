package com.journal.inspiration.vo;

import lombok.Data;
import java.util.List;

@Data
public class UserStyleProfileVO {

    private Long userId;

    private String nickname;

    private String avatar;

    private String bio;

    private Integer totalWorks;

    private Integer totalPublicWorks;

    private List<StylePreferenceVO> stylePreferences;

    private List<ScenePreferenceVO> scenePreferences;

    private ColorUsageVO colorTendency;

    private List<WorkCoverVO> featuredWorks;

    private String signatureStyle;

    private String dominantColor;
}
