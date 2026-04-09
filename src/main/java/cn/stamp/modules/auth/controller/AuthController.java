package cn.stamp.modules.auth.controller;

import cn.dev33.satoken.stp.StpUtil;
import cn.stamp.common.api.ApiResponse;
import cn.stamp.modules.auth.dto.LoginRequest;
import cn.stamp.modules.auth.dto.RegisterRequest;
import cn.stamp.modules.auth.service.AuthService;
import cn.stamp.modules.auth.vo.LoginResponse;
import cn.stamp.modules.auth.vo.MeResponse;
import cn.stamp.modules.user.entity.SysUser;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
@Tag(name = "认证模块")
public class AuthController {

    private final AuthService authService;

    @Operation(summary = "账号登录")
    @PostMapping("/login")
    public ApiResponse<LoginResponse> login(@Valid @RequestBody LoginRequest request) {
        return ApiResponse.success(authService.login(request));
    }

    @Operation(summary = "账号注册")
    @PostMapping("/register")
    public ApiResponse<Long> register(@Valid @RequestBody RegisterRequest request) {
        return ApiResponse.success(authService.register(request));
    }

    @Operation(summary = "退出登录")
    @PostMapping("/logout")
    public ApiResponse<Void> logout() {
        authService.logout();
        return ApiResponse.success();
    }

    @Operation(summary = "获取当前登录用户")
    @GetMapping("/me")
    public ApiResponse<SysUser> me() {
        StpUtil.checkLogin();
        return ApiResponse.success(authService.currentUser());
    }

    @Operation(summary = "获取当前登录用户 + 角色（用于前端动态菜单）")
    @GetMapping("/me-with-roles")
    public ApiResponse<MeResponse> meWithRoles() {
        StpUtil.checkLogin();
        SysUser user = authService.currentUser();
        List<String> roles = cn.dev33.satoken.stp.StpUtil.getRoleList();
        return ApiResponse.success(new MeResponse(user, roles));
    }
}

