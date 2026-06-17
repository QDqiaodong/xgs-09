package com.journal.inspiration.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.journal.inspiration.entity.WorkCategory;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;

@Mapper
public interface WorkCategoryMapper extends BaseMapper<WorkCategory> {

    @Select("SELECT COUNT(DISTINCT wc.work_id) FROM work_category wc " +
            "INNER JOIN journal_work jw ON wc.work_id = jw.id " +
            "WHERE wc.category_id = #{categoryId} AND jw.status = 1 AND jw.deleted = 0 AND wc.deleted = 0")
    int countWorksByCategoryId(Long categoryId);
}
