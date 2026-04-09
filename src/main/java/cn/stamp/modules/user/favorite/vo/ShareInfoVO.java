package cn.stamp.modules.user.favorite.vo;

import lombok.AllArgsConstructor;
import lombok.Data;
import java.time.LocalDateTime;

@Data
@AllArgsConstructor
public class ShareInfoVO {
    /**
     * 分享码
     */
    private String shareCode;

    /**
     * 分享链接
     */
    private String shareUrl;

    /**
     * 过期时间
     */
    private LocalDateTime expireAt;
}
