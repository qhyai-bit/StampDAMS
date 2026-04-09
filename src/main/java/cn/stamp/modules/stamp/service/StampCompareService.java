package cn.stamp.modules.stamp.service;

import cn.stamp.modules.stamp.vo.ComparisonBundleVO;

public interface StampCompareService {
    /**
     * 对比两枚邮票
     * @param stampIdA 邮票 A ID
     * @param stampIdB 邮票 B ID
     * @return 对比结果包
     */
    ComparisonBundleVO compare(Long stampIdA, Long stampIdB);
}
