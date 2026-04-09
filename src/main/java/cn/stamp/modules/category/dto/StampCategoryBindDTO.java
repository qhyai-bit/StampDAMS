package cn.stamp.modules.category.dto;

import jakarta.validation.constraints.NotEmpty;
import lombok.Data;

import java.util.List;

/**
 * 印章分类绑定DTO
 */
@Data
public class StampCategoryBindDTO {

    /**
     * 分类ID列表
     */
    @NotEmpty(message = "分类ID列表不能为空")
    private List<Long> categoryIds;
}

