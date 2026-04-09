package cn.stamp.modules.stamp.controller;

import cn.dev33.satoken.stp.StpUtil;
import cn.stamp.common.api.ApiResponse;
import cn.stamp.modules.stamp.service.StampImageService;
import cn.stamp.modules.stamp.vo.StampImageVO;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@RestController
@RequestMapping("/api/stamps/{stampId}/images")
@RequiredArgsConstructor
@Tag(name = "邮票数字化采集-图像")
public class StampImageController {

    private final StampImageService stampImageService;

    @Operation(summary = "上传邮票图像（需登录，建议管理员）")
    @PostMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ApiResponse<StampImageVO> upload(
            @PathVariable Long stampId,
            @RequestParam String imageType,
            @RequestParam("file") MultipartFile file) {
        StpUtil.checkLogin();
        return ApiResponse.success(stampImageService.upload(stampId, imageType, file));
    }

    @Operation(summary = "查询某枚邮票的全部图像（可匿名浏览）")
    @GetMapping
    public ApiResponse<List<StampImageVO>> list(@PathVariable Long stampId) {
        return ApiResponse.success(stampImageService.listByStampId(stampId));
    }

    @Operation(summary = "删除图像及关联标注（需登录）")
    @DeleteMapping("/{imageId}")
    public ApiResponse<Void> delete(@PathVariable Long stampId, @PathVariable Long imageId) {
        StpUtil.checkLogin();
        stampImageService.delete(imageId);
        return ApiResponse.success();
    }
}
