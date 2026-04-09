package cn.stamp.modules.stamp.controller;

import cn.stamp.common.api.ApiResponse;
import cn.stamp.modules.stamp.service.StampCompareService;
import cn.stamp.modules.stamp.vo.ComparisonBundleVO;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

/**
 * 邮票对比控制器
 * 提供邮票之间的对比分析功能
 */
@RestController
@RequestMapping("/api/stamps/compare")
@RequiredArgsConstructor
@Tag(name = "邮票对比浏览", description = "提供两枚邮票的鉴赏包对比及差异分析接口")
public class StampCompareController {

    private final StampCompareService compareService;

    /**
     * 对比两枚邮票
     *
     * @param stampIdA 第一枚邮票ID
     * @param stampIdB 第二枚邮票ID
     * @return 包含两枚邮票鉴赏包及差异分析结果的响应
     */
    @Operation(summary = "对比两枚邮票", description = "根据提供的两个邮票ID，返回两者的鉴赏包详情及差异分析报告")
    @GetMapping
    public ApiResponse<ComparisonBundleVO> compare(
            @RequestParam Long stampIdA,
            @RequestParam Long stampIdB) {
        return ApiResponse.success(compareService.compare(stampIdA, stampIdB));
    }
}
