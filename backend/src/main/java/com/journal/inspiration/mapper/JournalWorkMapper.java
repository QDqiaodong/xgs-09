package com.journal.inspiration.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.journal.inspiration.entity.JournalWork;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Update;

@Mapper
public interface JournalWorkMapper extends BaseMapper<JournalWork> {

    @Update("UPDATE journal_work SET view_count = view_count + 1 WHERE id = #{id} AND deleted = 0")
    int incrementViewCountById(Long id);
}
