package cn.stamp.modules.category.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * 印章分类关联实体类
 * 用于存储印章与分类之间的关联关系
 */
@Data
@TableName("stamp_category_rel")
public class StampCategoryRel {

    /**
     * 主键ID，自增
     */
    @TableId(type = IdType.AUTO)
    private Long id;

    /**
     * 印章ID
     */
    private Long stampId;

    /**
     * 分类ID
     */
    private Long categoryId;

    /**
     * 创建时间
     */
    private LocalDateTime createdAt;
}

