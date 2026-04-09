package cn.stamp.modules.auth.service.impl;

import cn.dev33.satoken.stp.StpUtil;
import cn.stamp.modules.auth.dto.LoginRequest;
import cn.stamp.modules.auth.dto.RegisterRequest;
import cn.stamp.modules.auth.service.AuthService;
import cn.stamp.modules.auth.vo.LoginResponse;
import cn.stamp.modules.user.entity.SysUser;
import cn.stamp.modules.user.entity.Role;
import cn.stamp.modules.user.entity.UserRole;
import cn.stamp.modules.user.mapper.RoleMapper;
import cn.stamp.modules.user.mapper.SysUserMapper;
import cn.stamp.modules.user.mapper.UserRoleMapper;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class AuthServiceImpl implements AuthService {

    private final SysUserMapper sysUserMapper;
    private final RoleMapper roleMapper;
    private final UserRoleMapper userRoleMapper;
    private final PasswordEncoder passwordEncoder;

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
        if (user == null || !passwordEncoder.matches(request.getPassword(), user.getPassword())) {
            throw new IllegalArgumentException("用户名或密码错误");
        }
        
        // 校验账号状态
        if (!"ENABLED".equalsIgnoreCase(user.getStatus())) {
            throw new IllegalArgumentException("账号已禁用");
        }

        // 执行Sa-Token登录
        StpUtil.login(user.getId());

        // 读取角色（用于前端动态菜单）
        List<String> roles = userRoleMapper.selectList(new LambdaQueryWrapper<UserRole>()
                        .eq(UserRole::getUserId, user.getId()))
                .stream()
                .map(ur -> roleMapper.selectById(ur.getRoleId()))
                .filter(r -> r != null && r.getRoleCode() != null)
                .map(Role::getRoleCode)
                .distinct()
                .collect(Collectors.toList());
        
        // 构建并返回登录响应
        return new LoginResponse(
                StpUtil.getTokenName(),
                StpUtil.getTokenValue(),
                user.getId(),
                user.getUsername(),
                user.getNickname(),
                roles
        );
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Long register(RegisterRequest request) {
        String username = request.getUsername();
        if (sysUserMapper.selectCount(new LambdaQueryWrapper<SysUser>()
                .eq(SysUser::getUsername, username)
                .eq(SysUser::getDeleted, 0)) > 0) {
            throw new IllegalArgumentException("用户名已存在");
        }

        SysUser user = new SysUser();
        user.setUsername(username);
        user.setPassword(passwordEncoder.encode(request.getPassword()));
        user.setNickname(request.getNickname());
        user.setStatus("ENABLED");
        user.setDeleted(0);
        user.setCreatedAt(LocalDateTime.now());
        user.setUpdatedAt(LocalDateTime.now());
        sysUserMapper.insert(user);

        Role role = roleMapper.selectOne(new LambdaQueryWrapper<Role>()
                .eq(Role::getRoleCode, "USER")
                .last("limit 1"));
        if (role != null) {
            UserRole ur = new UserRole();
            ur.setUserId(user.getId());
            ur.setRoleId(role.getId());
            ur.setCreatedAt(LocalDateTime.now());
            userRoleMapper.insert(ur);
        }
        return user.getId();
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

