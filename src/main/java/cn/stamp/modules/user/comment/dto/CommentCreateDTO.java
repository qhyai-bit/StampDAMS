package cn.stamp.modules.user.comment.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class CommentCreateDTO {

    /**
     * 评论内容
     */
    @NotBlank(message = "评论内容不能为空")
    private String content;
}

