package cn.stamp.modules.auth.vo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
/**
 * 登录响应对象
 */
public class LoginResponse {

    /** 令牌名称 */
    private String tokenName;
    /** 令牌值 */
    private String tokenValue;
    /** 用户ID */
    private Long userId;
    /** 用户名 */
    private String username;
    /** 昵称 */
    private String nickname;

    /** 角色列表：ADMIN/USER 等（用于前端动态菜单） */
    private java.util.List<String> roles;
}

