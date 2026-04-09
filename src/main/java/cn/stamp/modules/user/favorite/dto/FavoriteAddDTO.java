package cn.stamp.modules.user.favorite.dto;

import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class FavoriteAddDTO {

    /**
     * 邮票ID
     */
    @NotNull(message = "邮票ID不能为空")
    private Long stampId;
}

