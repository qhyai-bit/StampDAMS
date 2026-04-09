package cn.stamp.modules.user.comment.mapper;

import cn.stamp.modules.user.comment.entity.Comment;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;

/**
 * 评论 Mapper 接口
 *
 * @author StampDAMS
 */
@Mapper
public interface CommentMapper extends BaseMapper<Comment> {

}

