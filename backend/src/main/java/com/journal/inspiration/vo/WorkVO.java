package com.journal.inspiration.vo;

import lombok.Data;
import java.time.LocalDateTime;
import java.util.List;

@Data
public class WorkVO {

    private Long id;

    private Long userId;

    private String nickname;

    private String avatar;

    private String title;

    private String coverImage;

    private Integer coverType;

    private String coverTypeDesc;

    private Integer coverWidthRatio;

    private Integer coverHeightRatio;

    private Double coverAspectRatio;

    private String content;

    private String layoutIdea;

    private String layoutConfig;

    private String colorScheme;

    private String inspiration;

    private Integer viewCount;

    private Integer likeCount;

    private Integer favoriteCount;

    private Integer status;

    private List<CategoryVO> categories;

    private Boolean isFavorite;

    private LocalDateTime createTime;
}
