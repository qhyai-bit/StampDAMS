package cn.stamp.modules.stamp.vo;

import cn.stamp.modules.market.entity.MarketPrice;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
/**
 * 单枚邮票的对比详情
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class StampComparisonDetailVO {
    /** 邮票鉴赏聚合数据 */
    private AppreciationBundleVO bundle;
    /** 最新市场参考价 */
    private MarketPrice latestPrice;
}
