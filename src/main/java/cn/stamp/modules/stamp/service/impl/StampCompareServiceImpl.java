package cn.stamp.modules.stamp.service.impl;

import cn.stamp.modules.market.entity.MarketPrice;
import cn.stamp.modules.market.service.MarketPriceService;
import cn.stamp.modules.stamp.service.AppreciationBundleService;
import cn.stamp.modules.stamp.service.StampCompareService;
import cn.stamp.modules.stamp.vo.*;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;

@Service
@RequiredArgsConstructor
public class StampCompareServiceImpl implements StampCompareService {

    private final AppreciationBundleService bundleService;
    private final MarketPriceService marketPriceService;

    /**
     * 对比两枚邮票的详细信息，包括鉴赏数据、最新价格及综合摘要。
     *
     * @param stampIdA 邮票A的ID
     * @param stampIdB 邮票B的ID
     * @return 包含两枚邮票详情及对比摘要的结果对象
     */
    @Override
    public ComparisonBundleVO compare(Long stampIdA, Long stampIdB) {
        // 1. 获取两枚邮票的完整鉴赏数据包（包含邮票基础信息、赏析信息等）
        AppreciationBundleVO bundleA = bundleService.buildBundle(stampIdA);
        AppreciationBundleVO bundleB = bundleService.buildBundle(stampIdB);

        // 2. 获取两枚邮票的最新市场参考价格（默认查询品相为"NEW"的价格）
        MarketPrice priceA = marketPriceService.latest(stampIdA, "NEW");
        MarketPrice priceB = marketPriceService.latest(stampIdB, "NEW");

        // 3. 封装单枚邮票的对比详情对象
        StampComparisonDetailVO detailA = StampComparisonDetailVO.builder()
                .bundle(bundleA)
                .latestPrice(priceA)
                .build();
        StampComparisonDetailVO detailB = StampComparisonDetailVO.builder()
                .bundle(bundleB)
                .latestPrice(priceB)
                .build();

        // 4. 计算并生成对比摘要信息（年份差、价差、稀有度对比等）
        ComparisonSummaryVO summary = buildSummary(bundleA, bundleB, priceA, priceB);

        // 5. 组装最终返回结果
        return ComparisonBundleVO.builder()
                .stampA(detailA)
                .stampB(detailB)
                .summary(summary)
                .build();
    }

    /**
     * 构建邮票对比摘要信息。
     * 计算年份差异、价格差异及百分比、稀有度对比，并提供简要建议。
     *
     * @param a  邮票A的鉴赏数据包
     * @param b  邮票B的鉴赏数据包
     * @param pa 邮票A的市场价格信息
     * @param pb 邮票B的市场价格信息
     * @return 对比摘要对象
     */
    private ComparisonSummaryVO buildSummary(AppreciationBundleVO a, AppreciationBundleVO b, MarketPrice pa, MarketPrice pb) {
        // 计算发行年份差的绝对值
        int yearDiff = Math.abs(a.getStamp().getYear() - b.getStamp().getYear());

        // 获取参考价格，若价格为空则默认为0
        BigDecimal priceA = pa != null ? pa.getReferencePrice() : BigDecimal.ZERO;
        BigDecimal priceB = pb != null ? pb.getReferencePrice() : BigDecimal.ZERO;
        
        // 计算价格差额 (A - B)
        BigDecimal priceDiff = priceA.subtract(priceB);

        // 计算价格差异百分比 ((A - B) / B * 100)，保留两位小数，四舍五入
        BigDecimal percent = BigDecimal.ZERO;
        if (priceB.compareTo(BigDecimal.ZERO) > 0) {
            percent = priceDiff.divide(priceB, 2, RoundingMode.HALF_UP).multiply(new BigDecimal("100"));
        }

        // 获取稀有度等级，若不存在则显示"未知"
        String rarityA = a.getAppreciation() != null ? a.getAppreciation().getRarityLevel() : "未知";
        String rarityB = b.getAppreciation() != null ? b.getAppreciation().getRarityLevel() : "未知";

        return ComparisonSummaryVO.builder()
                .yearDiff(yearDiff)
                .priceDiff(priceDiff)
                .priceDiffPercent(percent)
                .rarityComparison(rarityA + " vs " + rarityB)
                .summary("建议根据预算与收藏目标选择。")
                .build();
    }
}
