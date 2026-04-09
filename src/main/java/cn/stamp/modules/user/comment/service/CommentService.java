package cn.stamp.modules.user.comment.service;

import cn.stamp.modules.user.comment.entity.Comment;
import cn.stamp.modules.user.comment.entity.CommentReply;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;

import java.util.List;

public interface CommentService {

    Long create(Long stampId, Long userId, String content);

    void delete(Long commentId, Long userId);

    Page<Comment> pageByStamp(Long stampId, Integer pageNum, Integer pageSize);

    long likeToggle(Long commentId, Long userId);

    long likeCount(Long commentId);

    // 发布回复
    Long addReply(Long commentId, Long userId, String content);

    // 查询某评论的回复列表
    List<CommentReply> listReplies(Long commentId);

}

