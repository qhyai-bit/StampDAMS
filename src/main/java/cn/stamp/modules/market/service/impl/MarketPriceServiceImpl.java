package cn.stamp.modules.market.service.impl;

import cn.stamp.modules.market.dto.MarketPriceCreateDTO;
import cn.stamp.modules.market.entity.MarketPrice;
import cn.stamp.modules.market.mapper.MarketPriceMapper;
import cn.stamp.modules.market.service.MarketPriceService;
import cn.stamp.modules.stamp.service.StampService;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;


@Service
@RequiredArgsConstructor
public class MarketPriceServiceImpl implements MarketPriceService {

    private final MarketPriceMapper marketPriceMapper;
    private final StampService stampService;

    /**
     * 新增市场价格记录
     *
     * @param dto    创建数据传输对象，包含邮票ID、价格类型、参考价格等信息
     * @param userId 当前操作用户ID
     * @return 新创建的市场价格记录ID
     * @throws IllegalArgumentException 如果指定的邮票不存在
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public Long add(MarketPriceCreateDTO dto, Long userId) {
        // 验证邮票是否存在
        if (stampService.findById(dto.getStampId()) == null) {
            throw new IllegalArgumentException("邮票不存在");
        }

        // 构建市场价格实体对象
        MarketPrice row = new MarketPrice();
        row.setStampId(dto.getStampId());
        // 价格类型统一转为大写存储
        row.setPriceType(dto.getPriceType().toUpperCase());
        row.setReferencePrice(dto.getReferencePrice());
        // 货币类型默认为人民币(CNY)
        row.setCurrency(dto.getCurrency() != null ? dto.getCurrency() : "CNY");
        row.setRecordDate(dto.getRecordDate());
        row.setSource(dto.getSource());
        row.setRemark(dto.getRemark());
        row.setCreatedBy(userId);
        row.setCreatedAt(LocalDateTime.now());

        // 插入数据库并返回生成的ID
        marketPriceMapper.insert(row);
        return row.getId();
    }

    /**
     * 查询指定邮票的价格趋势
     *
     * @param stampId   邮票ID
     * @param priceType 价格类型（可选），如不传则查询所有类型
     * @return 按记录日期升序排列的市场价格列表
     */
    @Override
    public List<MarketPrice> trend(Long stampId, String priceType) {
        LambdaQueryWrapper<MarketPrice> w = new LambdaQueryWrapper<MarketPrice>()
                .eq(MarketPrice::getStampId, stampId)
                .orderByAsc(MarketPrice::getRecordDate)
                .orderByAsc(MarketPrice::getId);

        // 如果指定了价格类型，则添加过滤条件
        if (priceType != null && !priceType.isBlank()) {
            w.eq(MarketPrice::getPriceType, priceType.toUpperCase());
        }

        return marketPriceMapper.selectList(w);
    }

    /**
     * 查询指定邮票的最新一条价格记录
     *
     * @param stampId   邮票ID
     * @param priceType 价格类型（可选），如不传则查询所有类型中最新的一条
     * @return 最新的市场价格记录，若不存在则返回null
     */
    @Override
    public MarketPrice latest(Long stampId, String priceType) {
        LambdaQueryWrapper<MarketPrice> w = new LambdaQueryWrapper<MarketPrice>()
                .eq(MarketPrice::getStampId, stampId)
                .orderByDesc(MarketPrice::getRecordDate)
                .orderByDesc(MarketPrice::getId)
                .last("limit 1");

        // 如果指定了价格类型，则添加过滤条件
        if (priceType != null && !priceType.isBlank()) {
            w.eq(MarketPrice::getPriceType, priceType.toUpperCase());
        }

        return marketPriceMapper.selectOne(w);
    }
}
