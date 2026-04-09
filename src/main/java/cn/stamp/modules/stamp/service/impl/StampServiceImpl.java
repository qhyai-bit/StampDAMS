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

    /**
     * 创建邮票记录
     *
     * @param stamp 邮票实体对象
     * @return 新创建的邮票ID
     */
    @Override
    public Long create(Stamp stamp) {
        // 确保ID为空，以便数据库自动生成
        stamp.setId(null);
        LocalDateTime now = LocalDateTime.now();
        // 设置创建时间和更新时间
        stamp.setCreatedAt(now);
        stamp.setUpdatedAt(now);
        stampMapper.insert(stamp);
        return stamp.getId();
    }

    /**
     * 更新邮票记录
     *
     * @param stamp 包含更新信息的邮票实体对象
     */
    @Override
    public void update(Stamp stamp) {
        // 更新最后修改时间
        stamp.setUpdatedAt(LocalDateTime.now());
        stampMapper.updateById(stamp);
    }

    /**
     * 根据ID删除邮票记录
     *
     * @param id 邮票ID
     */
    @Override
    public void delete(Long id) {
        stampMapper.deleteById(id);
    }

    /**
     * 根据ID查询邮票详情
     *
     * @param id 邮票ID
     * @return 邮票实体对象，若不存在则返回null
     */
    @Override
    public Stamp findById(Long id) {
        return stampMapper.selectById(id);
    }

    /**
     * 分页查询邮票列表
     * 支持按关键词（名称或编码）、国家、年份进行筛选
     *
     * @param keyword  搜索关键词（匹配名称或编码）
     * @param country  国家筛选条件
     * @param year     年份筛选条件
     * @param pageNum  页码
     * @param pageSize 每页大小
     * @return 分页结果
     */
    @Override
    public Page<Stamp> page(String keyword, String country, Integer year, Integer pageNum, Integer pageSize) {
        Page<Stamp> page = new Page<>(pageNum, pageSize);
        LambdaQueryWrapper<Stamp> wrapper = new LambdaQueryWrapper<>();
        
        // 关键词模糊搜索：匹配名称或编码
        if (keyword != null && !keyword.isBlank()) {
            wrapper.like(Stamp::getName, keyword)
                    .or()
                    .like(Stamp::getCode, keyword);
        }
        
        // 国家精确匹配
        if (country != null && !country.isBlank()) {
            wrapper.eq(Stamp::getCountry, country);
        }
        
        // 年份精确匹配
        if (year != null) {
            wrapper.eq(Stamp::getYear, year);
        }
        
        // 排序：按年份降序，编码升序
        wrapper.orderByDesc(Stamp::getYear).orderByAsc(Stamp::getCode);
        
        return stampMapper.selectPage(page, wrapper);
    }
}

