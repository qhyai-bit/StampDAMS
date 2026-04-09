package cn.stamp.modules.category.controller;

import cn.dev33.satoken.stp.StpUtil;
import cn.stamp.common.api.ApiResponse;
import cn.stamp.modules.category.dto.StampCategoryBindDTO;
import cn.stamp.modules.category.service.StampCategoryService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@Tag(name = "邮票-分类关联")
public class StampCategoryController {

    private final StampCategoryService stampCategoryService;

    @Operation(summary = "绑定某枚邮票的分类（需登录）")
    @PostMapping("/api/stamps/{stampId}/categories")
    public ApiResponse<Void> bind(@PathVariable Long stampId, @Valid @RequestBody StampCategoryBindDTO dto) {
        StpUtil.checkLogin();
        stampCategoryService.bind(stampId, dto);
        return ApiResponse.success();
    }

    @Operation(summary = "查询某枚邮票绑定的分类ID列表（可匿名）")
    @GetMapping("/api/stamps/{stampId}/categories")
    public ApiResponse<List<Long>> list(@PathVariable Long stampId) {
        return ApiResponse.success(stampCategoryService.listCategoryIds(stampId));
    }
}

