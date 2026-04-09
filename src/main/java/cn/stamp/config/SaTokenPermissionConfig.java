package cn.stamp.config;

import cn.dev33.satoken.stp.StpInterface;
import cn.stamp.modules.user.entity.Role;
import cn.stamp.modules.user.entity.UserRole;
import cn.stamp.modules.user.mapper.RoleMapper;
import cn.stamp.modules.user.mapper.UserRoleMapper;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
/**
 * Sa-Token 权限与角色加载配置
 * <p>
 * 实现 {@link StpInterface} 接口，用于在用户登录或鉴权时，
 * 从数据库中动态获取用户的权限列表和角色列表。
 */
@Component
@RequiredArgsConstructor
public class SaTokenPermissionConfig implements StpInterface {

    private final UserRoleMapper userRoleMapper;
    private final RoleMapper roleMapper;

    /**
     * 获取指定用户的权限码列表
     *
     * @param loginId   用户ID
     * @param loginType 登录类型
     * @return 权限码列表，当前项目未细化权限表，暂时返回空列表
     */
    @Override
    public List<String> getPermissionList(Object loginId, String loginType) {
        // 目前项目未细化 permission 表，先返回空即可
        return Collections.emptyList();
    }

    /**
     * 获取指定用户的角色标识列表
     *
     * @param loginId   用户ID
     * @param loginType 登录类型
     * @return 角色标识（roleCode）列表
     */
    @Override
    public List<String> getRoleList(Object loginId, String loginType) {
        // 转换用户ID
        Long uid = Long.valueOf(String.valueOf(loginId));
        
        // 查询用户关联的所有角色记录
        List<UserRole> rels = userRoleMapper.selectList(new LambdaQueryWrapper<UserRole>()
                .eq(UserRole::getUserId, uid));
        
        if (rels == null || rels.isEmpty()) {
            return Collections.emptyList();
        }
        
        // 提取角色编码
        List<String> roles = new ArrayList<>();
        for (UserRole ur : rels) {
            Role r = roleMapper.selectById(ur.getRoleId());
            if (r != null && r.getRoleCode() != null) {
                roles.add(r.getRoleCode());
            }
        }
        return roles;
    }
}

