package cn.stamp.modules.auth.vo;

import cn.stamp.modules.user.entity.SysUser;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * 当前登录用户信息响应对象
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class MeResponse {
    /**
     * 用户详细信息
     */
    private SysUser user;

    /**
     * 用户角色列表
     */
    private List<String> roles;
}

