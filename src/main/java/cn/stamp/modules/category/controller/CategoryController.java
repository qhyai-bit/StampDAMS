package cn.stamp.modules.category.controller;

import cn.dev33.satoken.stp.StpUtil;
import cn.stamp.common.api.ApiResponse;
import cn.stamp.modules.category.dto.CategorySaveDTO;
import cn.stamp.modules.category.service.CategoryService;
import cn.stamp.modules.category.vo.CategoryNodeVO;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/categories")
@RequiredArgsConstructor
@Tag(name = "分类索引模块")
public class CategoryController {

    private final CategoryService categoryService;

    @Operation(summary = "获取分类树（可匿名）")
    @GetMapping("/tree")
    public ApiResponse<List<CategoryNodeVO>> tree() {
        return ApiResponse.success(categoryService.tree());
    }

    @Operation(summary = "新增分类（需登录）")
    @PostMapping
    public ApiResponse<Long> create(@Valid @RequestBody CategorySaveDTO dto) {
        StpUtil.checkLogin();
        return ApiResponse.success(categoryService.create(dto));
    }

    @Operation(summary = "更新分类（需登录）")
    @PutMapping("/{id}")
    public ApiResponse<Void> update(@PathVariable Long id, @Valid @RequestBody CategorySaveDTO dto) {
        StpUtil.checkLogin();
        categoryService.update(id, dto);
        return ApiResponse.success();
    }

    @Operation(summary = "删除分类（需登录）")
    @DeleteMapping("/{id}")
    public ApiResponse<Void> delete(@PathVariable Long id) {
        StpUtil.checkLogin();
        categoryService.delete(id);
        return ApiResponse.success();
    }
}

