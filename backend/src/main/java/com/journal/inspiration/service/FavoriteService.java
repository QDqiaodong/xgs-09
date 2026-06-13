package com.journal.inspiration.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;
import com.journal.inspiration.entity.Favorite;
import com.journal.inspiration.vo.WorkVO;

public interface FavoriteService extends IService<Favorite> {

    boolean toggleFavorite(Long userId, Long workId);

    boolean isFavorite(Long userId, Long workId);

    Page<WorkVO> getUserFavorites(Long userId, Integer pageNum, Integer pageSize);
}
