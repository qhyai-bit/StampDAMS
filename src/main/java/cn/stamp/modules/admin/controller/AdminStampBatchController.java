package cn.stamp.modules.admin.controller;

import cn.dev33.satoken.stp.StpUtil;
import cn.stamp.common.api.ApiResponse;
import cn.stamp.modules.admin.service.StampBatchService;
import cn.stamp.modules.admin.service.StampImageBatchService;
import cn.stamp.modules.admin.vo.ImportResultVO;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

@RestController
@RequestMapping("/api/admin/stamps")
@RequiredArgsConstructor
@Tag(name = "系统管理-批量导入导出")
public class AdminStampBatchController {

    private final StampBatchService stampBatchService;
    private final StampImageBatchService stampImageBatchService;

    @Operation(summary = "批量导入邮票（CSV，需登录）")
    @PostMapping(value = "/import-csv", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ApiResponse<ImportResultVO> importCsv(@RequestParam("file") MultipartFile file) throws IOException {
        StpUtil.checkLogin();
        return ApiResponse.success(stampBatchService.importStampsCsv(file));
    }

    @Operation(summary = "导出全部邮票（CSV，需登录）")
    @GetMapping("/export-csv")
    public void exportCsv(HttpServletResponse response) throws IOException {
        StpUtil.checkLogin();
        stampBatchService.exportStampsCsv(response);
    }

    @Operation(summary = "批量导入邮票图片（ZIP，按 code 自动识别 FRONT/BACK，需登录）")
    @PostMapping(value = "/import-images-zip", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ApiResponse<ImportResultVO> importImagesZip(@RequestParam("file") MultipartFile file) throws IOException {
        StpUtil.checkLogin();
        return ApiResponse.success(stampImageBatchService.importImagesZip(file));
    }
}

