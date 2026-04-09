package cn.stamp.modules.category.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;
/**
 * 分类保存DTO
 */
@Data
public class CategorySaveDTO {

    /**
     * 父级ID，默认为0表示顶级分类
     */
    private Long parentId = 0L;

    /**
     * 分类名称
     */
    @NotBlank(message = "分类名称不能为空")
    private String categoryName;

    /**
     * 排序号，数值越小越靠前，默认为0
     */
    private Integer sortNo = 0;
}

