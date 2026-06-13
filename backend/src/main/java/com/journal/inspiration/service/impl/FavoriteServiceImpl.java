package com.journal.inspiration.service.impl;

import cn.hutool.core.bean.BeanUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.journal.inspiration.entity.Favorite;
import com.journal.inspiration.entity.JournalWork;
import com.journal.inspiration.entity.User;
import com.journal.inspiration.mapper.FavoriteMapper;
import com.journal.inspiration.mapper.JournalWorkMapper;
import com.journal.inspiration.mapper.UserMapper;
import com.journal.inspiration.service.CategoryService;
import com.journal.inspiration.service.FavoriteService;
import com.journal.inspiration.vo.WorkVO;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class FavoriteServiceImpl extends ServiceImpl<FavoriteMapper, Favorite> implements FavoriteService {

    private final JournalWorkMapper workMapper;
    private final UserMapper userMapper;
    private final CategoryService categoryService;

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean toggleFavorite(Long userId, Long workId) {
        LambdaQueryWrapper<Favorite> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(Favorite::getUserId, userId).eq(Favorite::getWorkId, workId);
        Favorite favorite = getOne(wrapper);

        JournalWork work = workMapper.selectById(workId);

        if (favorite != null) {
            removeById(favorite.getId());
            if (work != null) {
                work.setFavoriteCount(Math.max(0, work.getFavoriteCount() - 1));
                workMapper.updateById(work);
            }
            return false;
        } else {
            Favorite newFavorite = new Favorite();
            newFavorite.setUserId(userId);
            newFavorite.setWorkId(workId);
            save(newFavorite);
            if (work != null) {
                work.setFavoriteCount(work.getFavoriteCount() + 1);
                workMapper.updateById(work);
            }
            return true;
        }
    }

    @Override
    public boolean isFavorite(Long userId, Long workId) {
        if (userId == null || workId == null) {
            return false;
        }
        LambdaQueryWrapper<Favorite> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(Favorite::getUserId, userId).eq(Favorite::getWorkId, workId);
        return count(wrapper) > 0;
    }

    @Override
    public Page<WorkVO> getUserFavorites(Long userId, Integer pageNum, Integer pageSize) {
        Page<Favorite> favoritePage = page(new Page<>(pageNum, pageSize),
                new LambdaQueryWrapper<Favorite>().eq(Favorite::getUserId, userId).orderByDesc(Favorite::getCreateTime));

        Page<WorkVO> voPage = new Page<>(favoritePage.getCurrent(), favoritePage.getSize(), favoritePage.getTotal());
        voPage.setRecords(favoritePage.getRecords().stream()
                .map(fav -> {
                    JournalWork work = workMapper.selectById(fav.getWorkId());
                    if (work == null) {
                        return null;
                    }
                    WorkVO vo = new WorkVO();
                    BeanUtil.copyProperties(work, vo);
                    User user = userMapper.selectById(work.getUserId());
                    if (user != null) {
                        vo.setNickname(user.getNickname());
                        vo.setAvatar(user.getAvatar());
                    }
                    vo.setCategories(categoryService.getWorkCategories(work.getId()));
                    vo.setIsFavorite(true);
                    return vo;
                })
                .filter(vo -> vo != null)
                .collect(Collectors.toList()));

        return voPage;
    }
}
