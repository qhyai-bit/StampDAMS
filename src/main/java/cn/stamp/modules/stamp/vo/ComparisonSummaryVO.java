package cn.stamp.modules.stamp.vo;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
/**
 * 对比分析摘要
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ComparisonSummaryVO {
    /** 年份差距 */
    private int yearDiff;
    /** 价格差距 */
    private BigDecimal priceDiff;
    /** 价格差异百分比 */
    private BigDecimal priceDiffPercent;
    /** 稀缺度对比结论 */
    private String rarityComparison;
    /** 智能建议 */
    private String summary;
}
