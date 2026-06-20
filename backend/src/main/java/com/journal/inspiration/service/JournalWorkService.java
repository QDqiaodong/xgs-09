package com.journal.inspiration.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;
import com.journal.inspiration.dto.WorkPublishDTO;
import com.journal.inspiration.dto.WorkQueryDTO;
import com.journal.inspiration.dto.WorkUpdateDTO;
import com.journal.inspiration.entity.JournalWork;
import com.journal.inspiration.vo.WorkStatsVO;
import com.journal.inspiration.vo.WorkVO;

public interface JournalWorkService extends IService<JournalWork> {

    Long publishWork(WorkPublishDTO dto);

    boolean updateWork(WorkUpdateDTO dto);

    WorkVO getWorkDetail(Long id, Long userId);

    Page<WorkVO> getWorkList(WorkQueryDTO dto);

    Page<WorkVO> getLatestWorks(Integer pageNum, Integer pageSize);

    Page<WorkVO> getHotWorks(Integer pageNum, Integer pageSize);

    Page<WorkVO> getUserWorks(Long userId, Integer pageNum, Integer pageSize);

    boolean updateWorkStatus(Long id, Integer status, Long operatorId);

    boolean incrementViewCount(Long id);

    WorkStatsVO getUserWorkStats(Long userId);
}
