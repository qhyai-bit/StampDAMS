package cn.stamp.modules.stamp.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class AnnotationSaveDTO {

    /**
     * 标注类型
     */
    @NotBlank(message = "标注类型不能为空")
    private String annType;

    /**
     * 标注数据（JSON格式）
     */
    @NotBlank(message = "标注数据不能为空")
    private String annDataJson;

    /**
     * 标注文本内容
     */
    @NotBlank(message = "标注文本不能为空")
    private String annText;

    /**
     * 标注颜色
     */
    private String color;
}
