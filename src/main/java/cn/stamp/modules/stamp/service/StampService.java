package cn.stamp.modules.stamp.service;

import cn.stamp.modules.stamp.entity.Stamp;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;

public interface StampService {

    Long create(Stamp stamp);

    void update(Stamp stamp);

    void delete(Long id);

    Stamp findById(Long id);

    Page<Stamp> page(String keyword, String country, Integer year, String theme, String type,
                       Integer pageNum, Integer pageSize);
}

