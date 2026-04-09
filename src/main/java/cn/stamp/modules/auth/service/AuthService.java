package cn.stamp.modules.auth.service;

import cn.stamp.modules.auth.dto.LoginRequest;
import cn.stamp.modules.auth.vo.LoginResponse;
import cn.stamp.modules.user.entity.SysUser;

public interface AuthService {

    /**
     * 用户登录
     *
     * @param request 登录请求参数
     * @return 登录响应信息，包含令牌等
     */
    LoginResponse login(LoginRequest request);

    /**
     * 用户登出
     */
    void logout();

    /**
     * 获取当前登录用户信息
     *
     * @return 当前系统用户对象
     */
    SysUser currentUser();
}

