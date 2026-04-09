package cn.stamp.modules.stamp.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * 邮票鉴赏实体类
 * 用于存储每枚邮票的鉴赏要点、价值分析等扩展信息
 */
@Data
@TableName("stamp_appreciation")
public class StampAppreciation {

    /**
     * 主键ID
     */
    @TableId(type = IdType.AUTO)
    private Long id;

    /**
     * 关联的邮票ID
     */
    private Long stampId;

    /**
     * 鉴赏要点
     */
    private String appreciationPoints;

    /**
     * 价值分析
     */
    private String valueAnalysis;

    /**
     * 稀有度等级
     */
    private String rarityLevel;

    /**
     * 是否允许水印下载：0-否，1-是
     */
    private Integer watermarkDownload;

    /**
     * 创建时间
     */
    private LocalDateTime createdAt;

    /**
     * 更新时间
     */
    private LocalDateTime updatedAt;
}
