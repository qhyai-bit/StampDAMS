package cn.stamp.modules.auth.service.impl;

import cn.dev33.satoken.stp.StpUtil;
import cn.stamp.modules.auth.dto.LoginRequest;
import cn.stamp.modules.auth.service.AuthService;
import cn.stamp.modules.auth.vo.LoginResponse;
import cn.stamp.modules.user.entity.SysUser;
import cn.stamp.modules.user.mapper.SysUserMapper;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AuthServiceImpl implements AuthService {

    private final SysUserMapper sysUserMapper;

    /**
     * 用户登录
     *
     * @param request 登录请求参数，包含用户名和密码
     * @return 登录响应信息，包含Token及用户基本信息
     * @throws IllegalArgumentException 当用户名/密码错误或账号被禁用时抛出
     */
    @Override
    public LoginResponse login(LoginRequest request) {
        // 查询用户信息
        SysUser user = sysUserMapper.selectOne(new LambdaQueryWrapper<SysUser>()
                .eq(SysUser::getUsername, request.getUsername())
                .eq(SysUser::getDeleted, 0)
                .last("limit 1"));
        
        // 校验用户存在性及密码正确性
        if (user == null || !request.getPassword().equals(user.getPassword())) {
            throw new IllegalArgumentException("用户名或密码错误");
        }
        
        // 校验账号状态
        if (!"ENABLED".equalsIgnoreCase(user.getStatus())) {
            throw new IllegalArgumentException("账号已禁用");
        }

        // 执行Sa-Token登录
        StpUtil.login(user.getId());
        
        // 构建并返回登录响应
        return new LoginResponse(
                StpUtil.getTokenName(),
                StpUtil.getTokenValue(),
                user.getId(),
                user.getUsername(),
                user.getNickname()
        );
    }

    /**
     * 用户登出
     */
    @Override
    public void logout() {
        StpUtil.logout();
    }

    /**
     * 获取当前登录用户信息
     *
     * @return 当前登录的系统用户对象
     */
    @Override
    public SysUser currentUser() {
        long loginId = StpUtil.getLoginIdAsLong();
        return sysUserMapper.selectById(loginId);
    }
}

