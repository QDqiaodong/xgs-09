package com.journal.inspiration.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.journal.inspiration.entity.Favorite;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface FavoriteMapper extends BaseMapper<Favorite> {
}
