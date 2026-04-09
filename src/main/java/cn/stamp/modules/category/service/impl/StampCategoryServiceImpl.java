package cn.stamp.modules.category.service.impl;

import cn.stamp.modules.category.dto.StampCategoryBindDTO;
import cn.stamp.modules.category.entity.Category;
import cn.stamp.modules.category.entity.StampCategoryRel;
import cn.stamp.modules.category.mapper.CategoryMapper;
import cn.stamp.modules.category.mapper.StampCategoryRelMapper;
import cn.stamp.modules.category.service.StampCategoryService;
import cn.stamp.modules.stamp.service.StampService;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

/**
 * 邮票分类关联服务实现类
 */
@Service
@RequiredArgsConstructor
public class StampCategoryServiceImpl implements StampCategoryService {

    private final StampService stampService;
    private final CategoryMapper categoryMapper;
    private final StampCategoryRelMapper relMapper;

    /**
     * 绑定邮票与分类的关系
     * <p>
     * 1. 校验邮票是否存在
     * 2. 校验所有待绑定的分类ID是否存在
     * 3. 删除该邮票原有的所有分类关联
     * 4. 批量插入新的分类关联关系
     *
     * @param stampId 邮票ID
     * @param dto     包含分类ID列表的DTO
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public void bind(Long stampId, StampCategoryBindDTO dto) {
        // 1. 校验邮票是否存在
        if (stampService.findById(stampId) == null) {
            throw new IllegalArgumentException("邮票不存在");
        }

        // 2. 校验分类存在性
        if (dto.getCategoryIds() != null) {
            for (Long cid : dto.getCategoryIds()) {
                Category c = categoryMapper.selectById(cid);
                if (c == null) {
                    throw new IllegalArgumentException("分类不存在: " + cid);
                }
            }
        }

        // 3. 清空旧关系
        relMapper.delete(new LambdaQueryWrapper<StampCategoryRel>().eq(StampCategoryRel::getStampId, stampId));

        // 4. 插入新关系
        if (dto.getCategoryIds() != null && !dto.getCategoryIds().isEmpty()) {
            LocalDateTime now = LocalDateTime.now();
            for (Long cid : dto.getCategoryIds()) {
                StampCategoryRel row = new StampCategoryRel();
                row.setStampId(stampId);
                row.setCategoryId(cid);
                row.setCreatedAt(now);
                relMapper.insert(row);
            }
        }
    }

    /**
     * 查询指定邮票关联的分类ID列表
     *
     * @param stampId 邮票ID
     * @return 分类ID列表，按关联记录ID升序排列
     */
    @Override
    public List<Long> listCategoryIds(Long stampId) {
        List<StampCategoryRel> rels = relMapper.selectList(new LambdaQueryWrapper<StampCategoryRel>()
                .eq(StampCategoryRel::getStampId, stampId)
                .orderByAsc(StampCategoryRel::getId));
        
        List<Long> ids = new ArrayList<>();
        if (rels != null) {
            for (StampCategoryRel r : rels) {
                ids.add(r.getCategoryId());
            }
        }
        return ids;
    }
}

