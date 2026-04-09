package cn.stamp.modules.user.comment.mapper;

import cn.stamp.modules.user.comment.entity.CommentLike;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;

/**
 * 评论点赞 Mapper 接口
 */
@Mapper
public interface CommentLikeMapper extends BaseMapper<CommentLike> {
}

