package cn.stamp.modules.category.service;

import cn.stamp.modules.category.dto.StampCategoryBindDTO;

import java.util.List;

public interface StampCategoryService {

    void bind(Long stampId, StampCategoryBindDTO dto);

    List<Long> listCategoryIds(Long stampId);
}

