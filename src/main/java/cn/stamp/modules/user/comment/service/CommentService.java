package cn.stamp.modules.user.comment.service;

import cn.stamp.modules.user.comment.entity.Comment;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;

public interface CommentService {

    Long create(Long stampId, Long userId, String content);

    void delete(Long commentId, Long userId);

    Page<Comment> pageByStamp(Long stampId, Integer pageNum, Integer pageSize);

    long likeToggle(Long commentId, Long userId);

    long likeCount(Long commentId);
}

