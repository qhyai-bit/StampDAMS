package cn.stamp.modules.user.favorite.controller;

import cn.dev33.satoken.stp.StpUtil;
import cn.stamp.common.api.ApiResponse;
import cn.stamp.modules.stamp.entity.Stamp;
import cn.stamp.modules.user.favorite.dto.FavoriteAddDTO;
import cn.stamp.modules.user.favorite.dto.FavoriteFolderSaveDTO;
import cn.stamp.modules.user.favorite.entity.FavoriteFolder;
import cn.stamp.modules.user.favorite.service.FavoriteService;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/favorites")
@RequiredArgsConstructor
@Tag(name = "用户收藏模块")
public class FavoriteController {

    private final FavoriteService favoriteService;

    @Operation(summary = "创建收藏夹（需登录）")
    @PostMapping("/folders")
    public ApiResponse<Long> createFolder(@Valid @RequestBody FavoriteFolderSaveDTO dto) {
        StpUtil.checkLogin();
        long uid = StpUtil.getLoginIdAsLong();
        return ApiResponse.success(favoriteService.createFolder(uid, dto));
    }

    @Operation(summary = "修改收藏夹（需登录）")
    @PutMapping("/folders/{folderId}")
    public ApiResponse<Void> updateFolder(@PathVariable Long folderId, @Valid @RequestBody FavoriteFolderSaveDTO dto) {
        StpUtil.checkLogin();
        long uid = StpUtil.getLoginIdAsLong();
        favoriteService.updateFolder(uid, folderId, dto);
        return ApiResponse.success();
    }

    @Operation(summary = "删除收藏夹（需登录）")
    @DeleteMapping("/folders/{folderId}")
    public ApiResponse<Void> deleteFolder(@PathVariable Long folderId) {
        StpUtil.checkLogin();
        long uid = StpUtil.getLoginIdAsLong();
        favoriteService.deleteFolder(uid, folderId);
        return ApiResponse.success();
    }

    @Operation(summary = "列出我的收藏夹（需登录）")
    @GetMapping("/folders")
    public ApiResponse<List<FavoriteFolder>> myFolders() {
        StpUtil.checkLogin();
        long uid = StpUtil.getLoginIdAsLong();
        return ApiResponse.success(favoriteService.listFolders(uid));
    }

    @Operation(summary = "收藏一枚邮票到收藏夹（需登录）")
    @PostMapping("/folders/{folderId}/items")
    public ApiResponse<Void> addToFolder(@PathVariable Long folderId, @Valid @RequestBody FavoriteAddDTO dto) {
        StpUtil.checkLogin();
        long uid = StpUtil.getLoginIdAsLong();
        favoriteService.addToFolder(uid, folderId, dto);
        return ApiResponse.success();
    }

    @Operation(summary = "从收藏夹移除一枚邮票（需登录）")
    @DeleteMapping("/folders/{folderId}/items/{stampId}")
    public ApiResponse<Void> removeFromFolder(@PathVariable Long folderId, @PathVariable Long stampId) {
        StpUtil.checkLogin();
        long uid = StpUtil.getLoginIdAsLong();
        favoriteService.removeFromFolder(uid, folderId, stampId);
        return ApiResponse.success();
    }

    @Operation(summary = "分页查看收藏夹内邮票（需登录）")
    @GetMapping("/folders/{folderId}/items")
    public ApiResponse<Page<Stamp>> pageFolderItems(@PathVariable Long folderId,
                                                    @RequestParam(defaultValue = "1") Integer pageNum,
                                                    @RequestParam(defaultValue = "10") Integer pageSize) {
        StpUtil.checkLogin();
        long uid = StpUtil.getLoginIdAsLong();
        return ApiResponse.success(favoriteService.pageFolderItems(uid, folderId, pageNum, pageSize));
    }
}

