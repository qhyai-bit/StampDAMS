package cn.stamp.modules.stamp.vo;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
/**
 * 最终返回的对比结果
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ComparisonBundleVO {
    /** 邮票 A 详情 */
    private StampComparisonDetailVO stampA;
    /** 邮票 B 详情 */
    private StampComparisonDetailVO stampB;
    /** 对比摘要 */
    private ComparisonSummaryVO summary;
}
