package com.journal.inspiration.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.journal.inspiration.entity.Category;
import com.journal.inspiration.vo.CategoryVO;

import java.util.List;

public interface CategoryService extends IService<Category> {

    List<CategoryVO> getCategoryList(String type);

    List<CategoryVO> getWorkCategories(Long workId);
}
