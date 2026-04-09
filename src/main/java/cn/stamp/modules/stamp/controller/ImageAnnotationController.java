package cn.stamp.modules.stamp.controller;

import cn.dev33.satoken.stp.StpUtil;
import cn.stamp.common.api.ApiResponse;
import cn.stamp.modules.stamp.dto.AnnotationSaveDTO;
import cn.stamp.modules.stamp.entity.ImageAnnotation;
import cn.stamp.modules.stamp.service.ImageAnnotationService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/stamp-images/{imageId}/annotations")
@RequiredArgsConstructor
@Tag(name = "邮票数字化采集-标注")
public class ImageAnnotationController {

    private final ImageAnnotationService imageAnnotationService;

    @Operation(summary = "列出某张图的全部标注")
    @GetMapping
    public ApiResponse<List<ImageAnnotation>> list(@PathVariable Long imageId) {
        return ApiResponse.success(imageAnnotationService.listByImageId(imageId));
    }

    @Operation(summary = "新增标注（需登录）")
    @PostMapping
    public ApiResponse<Long> save(@PathVariable Long imageId,
                                  @Valid @RequestBody AnnotationSaveDTO dto) {
        StpUtil.checkLogin();
        long uid = StpUtil.getLoginIdAsLong();
        Long id = imageAnnotationService.save(imageId, dto, uid);
        return ApiResponse.success(id);
    }

    @Operation(summary = "删除标注（需登录）")
    @DeleteMapping("/{annotationId}")
    public ApiResponse<Void> delete(@PathVariable Long imageId,
                                    @PathVariable Long annotationId) {
        StpUtil.checkLogin();
        imageAnnotationService.delete(annotationId);
        return ApiResponse.success();
    }
}
