package cn.stamp.modules.user.favorite.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;
@Data
public class FavoriteFolderSaveDTO {

    /**
     * 收藏夹名称
     */
    @NotBlank(message = "收藏夹名称不能为空")
    private String folderName;

    /**
     * 是否公开：0-私有，1-公开
     */
    private Integer isPublic = 0;
}

