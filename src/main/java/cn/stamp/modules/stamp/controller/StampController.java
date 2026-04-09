package cn.stamp.modules.stamp.controller;

import cn.dev33.satoken.stp.StpUtil;
import cn.stamp.common.api.ApiResponse;
import cn.stamp.modules.stamp.dto.StampAppreciationSaveDTO;
import cn.stamp.modules.stamp.entity.Stamp;
import cn.stamp.modules.stamp.service.AppreciationBundleService;
import cn.stamp.modules.stamp.service.StampAppreciationService;
import cn.stamp.modules.stamp.service.StampService;
import cn.stamp.modules.stamp.vo.AppreciationBundleVO;
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
    private final AppreciationBundleService appreciationBundleService;
    private final StampAppreciationService stampAppreciationService;

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

    @Operation(summary = "鉴赏聚合数据：基础信息+鉴赏扩展+图像及标注（前端详情页推荐）")
    @GetMapping("/{id}/appreciation-bundle")
    public ApiResponse<AppreciationBundleVO> appreciationBundle(@PathVariable Long id) {
        return ApiResponse.success(appreciationBundleService.buildBundle(id));
    }

    @Operation(summary = "维护鉴赏要点与价值分析（需登录）")
    @PutMapping("/{id}/appreciation")
    public ApiResponse<Void> saveAppreciation(@PathVariable Long id,
                                              @RequestBody StampAppreciationSaveDTO dto) {
        StpUtil.checkLogin();
        stampAppreciationService.saveOrUpdate(id, dto);
        return ApiResponse.success();
    }

    @Operation(summary = "分页查询邮票列表")
    @GetMapping
    public ApiResponse<Page<Stamp>> page(@RequestParam(required = false) String keyword,
                                         @RequestParam(required = false) String country,
                                         @RequestParam(required = false) Integer year,
                                         @RequestParam(required = false) String theme,
                                         @RequestParam(required = false) String type,
                                         @RequestParam(required = false) Long categoryId,
                                         @RequestParam(defaultValue = "1") Integer pageNum,
                                         @RequestParam(defaultValue = "10") Integer pageSize) {
        return ApiResponse.success(stampService.page(keyword, country, year, theme, type, categoryId, pageNum, pageSize));
    }
}

