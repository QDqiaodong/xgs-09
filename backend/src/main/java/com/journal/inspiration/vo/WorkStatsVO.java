package com.journal.inspiration.vo;

import lombok.Data;
import java.util.List;

@Data
public class WorkStatsVO {

    private Integer totalWorks;

    private Integer publicWorks;

    private Integer privateWorks;

    private Integer archivedWorks;

    private List<CategoryStatsVO> styleStats;

    private List<CategoryStatsVO> sceneStats;

    private List<StatusStatsVO> statusStats;
}
