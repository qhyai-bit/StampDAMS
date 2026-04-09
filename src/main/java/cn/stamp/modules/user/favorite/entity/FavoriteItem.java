package cn.stamp.modules.user.favorite.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * 收藏项实体类
 * 对应数据库表：favorite_item
 */
@Data
@TableName("favorite_item")
public class FavoriteItem {

    /**
     * 主键ID，自增
     */
    @TableId(type = IdType.AUTO)
    private Long id;

    /**
     * 文件夹ID
     */
    private Long folderId;

    /**
     * 邮票ID
     */
    private Long stampId;

    /**
     * 创建时间
     */
    private LocalDateTime createdAt;
}

