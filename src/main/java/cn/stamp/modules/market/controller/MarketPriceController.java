package cn.stamp.modules.market.controller;

import cn.dev33.satoken.stp.StpUtil;
import cn.stamp.common.api.ApiResponse;
import cn.stamp.modules.market.dto.MarketPriceCreateDTO;
import cn.stamp.modules.market.entity.MarketPrice;
import cn.stamp.modules.market.service.MarketPriceService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/market/prices")
@RequiredArgsConstructor
@Tag(name = "邮票市场信息-参考价")
public class MarketPriceController {

    private final MarketPriceService marketPriceService;

    @Operation(summary = "录入一条参考价记录（需登录）")
    @PostMapping
    public ApiResponse<Long> add(@Valid @RequestBody MarketPriceCreateDTO dto) {
        StpUtil.checkLogin();
        long uid = StpUtil.getLoginIdAsLong();
        return ApiResponse.success(marketPriceService.add(dto, uid));
    }

    @Operation(summary = "按时间序列查询价格走势（ECharts 折线）")
    @GetMapping("/stamp/{stampId}/trend")
    public ApiResponse<List<MarketPrice>> trend(@PathVariable Long stampId,
                                                @RequestParam(required = false) String priceType) {
        return ApiResponse.success(marketPriceService.trend(stampId, priceType));
    }

    @Operation(summary = "查询某类型最新一条参考价")
    @GetMapping("/stamp/{stampId}/latest")
    public ApiResponse<MarketPrice> latest(@PathVariable Long stampId,
                                           @RequestParam(required = false) String priceType) {
        return ApiResponse.success(marketPriceService.latest(stampId, priceType));
    }
}
