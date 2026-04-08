package cn.stamp.modules.stamp.controller;

import cn.stamp.common.api.ApiResponse;
import cn.stamp.modules.stamp.entity.Stamp;
import cn.stamp.modules.stamp.service.StampService;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/stamps")
@RequiredArgsConstructor
@Tag(name = "邮票基础信息模块")
public class StampController {

    private final StampService stampService;

    @Operation(summary = "新增邮票")
    @PostMapping
    public ApiResponse<Long> create(@Validated @RequestBody Stamp stamp) {
        Long id = stampService.create(stamp);
        return ApiResponse.success(id);
    }

    @Operation(summary = "更新邮票")
    @PutMapping("/{id}")
    public ApiResponse<Void> update(@PathVariable Long id, @RequestBody Stamp stamp) {
        stamp.setId(id);
        stampService.update(stamp);
        return ApiResponse.success();
    }

    @Operation(summary = "删除邮票")
    @DeleteMapping("/{id}")
    public ApiResponse<Void> delete(@PathVariable Long id) {
        stampService.delete(id);
        return ApiResponse.success();
    }

    @Operation(summary = "获取邮票详情")
    @GetMapping("/{id}")
    public ApiResponse<Stamp> detail(@PathVariable Long id) {
        return ApiResponse.success(stampService.findById(id));
    }

    @Operation(summary = "分页查询邮票列表")
    @GetMapping
    public ApiResponse<Page<Stamp>> page(@RequestParam(required = false) String keyword,
                                         @RequestParam(required = false) String country,
                                         @RequestParam(required = false) Integer year,
                                         @RequestParam(defaultValue = "1") Integer pageNum,
                                         @RequestParam(defaultValue = "10") Integer pageSize) {
        return ApiResponse.success(stampService.page(keyword, country, year, pageNum, pageSize));
    }
}

