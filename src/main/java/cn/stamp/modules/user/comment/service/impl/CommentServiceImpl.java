package cn.stamp.modules.user.comment.service.impl;

import cn.stamp.modules.stamp.service.StampService;
import cn.stamp.modules.user.comment.entity.Comment;
import cn.stamp.modules.user.comment.entity.CommentLike;
import cn.stamp.modules.user.comment.entity.CommentReply;
import cn.stamp.modules.user.comment.mapper.CommentLikeMapper;
import cn.stamp.modules.user.comment.mapper.CommentMapper;
import cn.stamp.modules.user.comment.mapper.CommentReplyMapper;
import cn.stamp.modules.user.comment.service.CommentService;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class CommentServiceImpl implements CommentService {

    private final CommentMapper commentMapper;
    private final CommentLikeMapper likeMapper;
    private final StampService stampService;
    private final CommentReplyMapper replyMapper;

    /**
     * 创建评论
     *
     * @param stampId 邮票ID
     * @param userId 用户ID
     * @param content 评论内容
     * @return 评论ID
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public Long create(Long stampId, Long userId, String content) {
        // 校验邮票是否存在
        if (stampService.findById(stampId) == null) {
            throw new IllegalArgumentException("邮票不存在");
        }
        // 构建评论对象
        Comment row = new Comment();
        row.setStampId(stampId);
        row.setUserId(userId);
        row.setContent(content);
        row.setStatus("NORMAL");
        LocalDateTime now = LocalDateTime.now();
        row.setCreatedAt(now);
        row.setUpdatedAt(now);
        // 插入数据库
        commentMapper.insert(row);
        return row.getId();
    }

    /**
     * 删除评论
     *
     * @param commentId 评论ID
     * @param userId 用户ID
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public void delete(Long commentId, Long userId) {
        // 查询评论
        Comment c = commentMapper.selectById(commentId);
        if (c == null) {
            return;
        }
        // 校验权限：仅允许评论作者删除
        if (!userId.equals(c.getUserId())) {
            throw new IllegalArgumentException("无权限删除该评论");
        }
        // 删除关联的点赞记录
        likeMapper.delete(new LambdaQueryWrapper<CommentLike>().eq(CommentLike::getCommentId, commentId));
        // 删除评论
        commentMapper.deleteById(commentId);
    }

    /**
     * 分页查询指定邮票下的评论
     *
     * @param stampId 邮票ID
     * @param pageNum 页码
     * @param pageSize 每页大小
     * @return 评论分页数据
     */
    @Override
    public Page<Comment> pageByStamp(Long stampId, Integer pageNum, Integer pageSize) {
        Page<Comment> page = new Page<>(pageNum, pageSize);
        return commentMapper.selectPage(page, new LambdaQueryWrapper<Comment>()
                .eq(Comment::getStampId, stampId)
                .eq(Comment::getStatus, "NORMAL")
                .orderByDesc(Comment::getCreatedAt)
                .orderByDesc(Comment::getId));
    }

    /**
     * 切换点赞状态（点赞/取消点赞）
     *
     * @param commentId 评论ID
     * @param userId 用户ID
     * @return 当前点赞总数
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public long likeToggle(Long commentId, Long userId) {
        // 校验评论是否存在
        Comment c = commentMapper.selectById(commentId);
        if (c == null) {
            throw new IllegalArgumentException("评论不存在");
        }
        // 查询用户是否已点赞
        CommentLike exist = likeMapper.selectOne(new LambdaQueryWrapper<CommentLike>()
                .eq(CommentLike::getCommentId, commentId)
                .eq(CommentLike::getUserId, userId)
                .last("limit 1"));
        if (exist == null) {
            // 未点赞，执行点赞
            CommentLike row = new CommentLike();
            row.setCommentId(commentId);
            row.setUserId(userId);
            row.setCreatedAt(LocalDateTime.now());
            likeMapper.insert(row);
        } else {
            // 已点赞，取消点赞
            likeMapper.deleteById(exist.getId());
        }
        // 返回最新点赞数
        return likeCount(commentId);
    }

    /**
     * 获取评论点赞数
     *
     * @param commentId 评论ID
     * @return 点赞数量
     */
    @Override
    public long likeCount(Long commentId) {
        Long cnt = likeMapper.selectCount(new LambdaQueryWrapper<CommentLike>().eq(CommentLike::getCommentId, commentId));
        return cnt == null ? 0 : cnt;
    }

    /**
     * 添加评论回复
     *
     * @param commentId 评论ID
     * @param userId 用户ID
     * @param content 回复内容
     * @return 回复ID
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public Long addReply(Long commentId, Long userId, String content) {
        // 校验评论是否存在
        if (commentMapper.selectById(commentId) == null) {
            throw new IllegalArgumentException("评论不存在");
        }
        // 构建回复对象
        CommentReply reply = new CommentReply();
        reply.setCommentId(commentId);
        reply.setUserId(userId);
        reply.setContent(content);
        LocalDateTime now = LocalDateTime.now();
        reply.setCreatedAt(now);
        // 插入数据库
        replyMapper.insert(reply);
        return reply.getId();
    }

    /**
     * 查询指定评论下的回复列表
     *
     * @param commentId 评论ID
     * @return 回复列表
     */
    @Override
    public List<CommentReply> listReplies(Long commentId) {
        return replyMapper.selectList(new LambdaQueryWrapper<CommentReply>()
                .eq(CommentReply::getCommentId, commentId)
                .orderByAsc(CommentReply::getCreatedAt));
    }
}

