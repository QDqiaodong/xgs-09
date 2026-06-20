package com.journal.inspiration.vo;

import lombok.Data;
import java.time.LocalDateTime;

@Data
public class WorkCoverVO {

    private Long workId;

    private String title;

    private String coverImage;

    private Integer coverType;

    private String coverTypeDesc;

    private Integer coverWidthRatio;

    private Integer coverHeightRatio;

    private Double coverAspectRatio;

    private Integer viewCount;

    private Integer favoriteCount;

    private LocalDateTime createTime;
}
