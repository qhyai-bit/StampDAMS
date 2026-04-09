package cn.stamp.modules.market.service;

import cn.stamp.modules.market.dto.MarketPriceCreateDTO;
import cn.stamp.modules.market.entity.MarketPrice;

import java.util.List;

public interface MarketPriceService {

    Long add(MarketPriceCreateDTO dto, Long userId);

    List<MarketPrice> trend(Long stampId, String priceType);

    MarketPrice latest(Long stampId, String priceType);
}
