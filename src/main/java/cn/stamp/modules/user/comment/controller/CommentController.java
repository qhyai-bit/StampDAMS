package cn.stamp.modules.user.comment.controller;

import cn.dev33.satoken.stp.StpUtil;
import cn.stamp.common.api.ApiResponse;
import cn.stamp.modules.user.comment.dto.CommentCreateDTO;
import cn.stamp.modules.user.comment.dto.ReplyCreateDTO;
import cn.stamp.modules.user.comment.entity.Comment;
import cn.stamp.modules.user.comment.entity.CommentReply;
import cn.stamp.modules.user.comment.service.CommentService;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@Tag(name = "用户评论模块")
public class CommentController {

    private final CommentService commentService;

    @Operation(summary = "分页获取某枚邮票的评论列表（可匿名）")
    @GetMapping("/api/stamps/{stampId}/comments")
    public ApiResponse<Page<Comment>> pageByStamp(@PathVariable Long stampId,
                                                  @RequestParam(defaultValue = "1") Integer pageNum,
                                                  @RequestParam(defaultValue = "10") Integer pageSize) {
        return ApiResponse.success(commentService.pageByStamp(stampId, pageNum, pageSize));
    }

    @Operation(summary = "发布评论（需登录）")
    @PostMapping("/api/stamps/{stampId}/comments")
    public ApiResponse<Long> create(@PathVariable Long stampId, @Valid @RequestBody CommentCreateDTO dto) {
        StpUtil.checkLogin();
        long uid = StpUtil.getLoginIdAsLong();
        return ApiResponse.success(commentService.create(stampId, uid, dto.getContent()));
    }

    @Operation(summary = "删除评论（仅本人，需登录）")
    @DeleteMapping("/api/comments/{commentId}")
    public ApiResponse<Void> delete(@PathVariable Long commentId) {
        StpUtil.checkLogin();
        long uid = StpUtil.getLoginIdAsLong();
        commentService.delete(commentId, uid);
        return ApiResponse.success();
    }

    @Operation(summary = "点赞/取消点赞（需登录），返回最新点赞数")
    @PostMapping("/api/comments/{commentId}/like-toggle")
    public ApiResponse<Long> likeToggle(@PathVariable Long commentId) {
        StpUtil.checkLogin();
        long uid = StpUtil.getLoginIdAsLong();
        return ApiResponse.success(commentService.likeToggle(commentId, uid));
    }

    @Operation(summary = "发布回复（需登录）")
    @PostMapping("/api/comments/{commentId}/replies")
    public ApiResponse<Long> addReply(@PathVariable Long commentId, @Valid @RequestBody ReplyCreateDTO dto) {
        StpUtil.checkLogin();
        long uid = StpUtil.getLoginIdAsLong();
        return ApiResponse.success(commentService.addReply(commentId, uid, dto.getContent()));
    }

    @Operation(summary = "查询某条评论的回复列表")
    @GetMapping("/api/comments/{commentId}/replies")
    public ApiResponse<List<CommentReply>> listReplies(@PathVariable Long commentId) {
        return ApiResponse.success(commentService.listReplies(commentId));
    }
}

