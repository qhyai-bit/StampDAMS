package cn.stamp.modules.market.mapper;

import cn.stamp.modules.market.entity.MarketPrice;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;

@Mapper
/**
 * 市场价格 Mapper 接口
 */
public interface MarketPriceMapper extends BaseMapper<MarketPrice> {
}
