package cn.stamp.modules.user.comment.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

/**
 * 回复创建数据传输对象
 */
@Data
public class ReplyCreateDTO {
    /**
     * 回复内容
     */
    @NotBlank(message = "回复内容不能为空")
    private String content;
}
