package cn.stamp.modules.category.service;

import cn.stamp.modules.category.dto.CategorySaveDTO;
import cn.stamp.modules.category.vo.CategoryNodeVO;

import java.util.List;

public interface CategoryService {

    Long create(CategorySaveDTO dto);

    void update(Long id, CategorySaveDTO dto);

    void delete(Long id);

    List<CategoryNodeVO> tree();
}

