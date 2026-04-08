package cn.stamp.modules.stamp.service.impl;

import cn.stamp.modules.stamp.entity.Stamp;
import cn.stamp.modules.stamp.mapper.StampMapper;
import cn.stamp.modules.stamp.service.StampService;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class StampServiceImpl implements StampService {

    private final StampMapper stampMapper;

    @Override
    public Long create(Stamp stamp) {
        stamp.setId(null);
        LocalDateTime now = LocalDateTime.now();
        stamp.setCreatedAt(now);
        stamp.setUpdatedAt(now);
        stampMapper.insert(stamp);
        return stamp.getId();
    }

    @Override
    public void update(Stamp stamp) {
        stamp.setUpdatedAt(LocalDateTime.now());
        stampMapper.updateById(stamp);
    }

    @Override
    public void delete(Long id) {
        stampMapper.deleteById(id);
    }

    @Override
    public Stamp findById(Long id) {
        return stampMapper.selectById(id);
    }

    @Override
    public Page<Stamp> page(String keyword, String country, Integer year, Integer pageNum, Integer pageSize) {
        Page<Stamp> page = new Page<>(pageNum, pageSize);
        LambdaQueryWrapper<Stamp> wrapper = new LambdaQueryWrapper<>();
        if (keyword != null && !keyword.isBlank()) {
            wrapper.like(Stamp::getName, keyword)
                    .or()
                    .like(Stamp::getCode, keyword);
        }
        if (country != null && !country.isBlank()) {
            wrapper.eq(Stamp::getCountry, country);
        }
        if (year != null) {
            wrapper.eq(Stamp::getYear, year);
        }
        wrapper.orderByDesc(Stamp::getYear).orderByAsc(Stamp::getCode);
        return stampMapper.selectPage(page, wrapper);
    }
}

