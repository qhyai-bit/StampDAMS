package cn.stamp.modules.user.favorite.dto;

import lombok.Data;

@Data
public class ShareCreateDTO {

    /**
     * 有效期（天）
     * 默认 7 天
     */
    private Integer expireDays = 7;
}
