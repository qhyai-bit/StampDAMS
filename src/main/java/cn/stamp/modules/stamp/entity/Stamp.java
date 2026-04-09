package cn.stamp.modules.stamp.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@TableName("stamp")
/**
 * 邮票实体类
 */
public class Stamp {

    /**
     * 主键ID
     */
    @TableId(type = IdType.AUTO)
    private Long id;

    /**
     * 邮票编码
     */
    private String code;

    /**
     * 邮票名称
     */
    private String name;

    /**
     * 发行国家
     */
    private String country;

    /**
     * 发行年份
     */
    private Integer year;

    /**
     * 面值
     */
    private String faceValue;

    /**
     * 类型
     */
    private String type;

    /**
     * 齿孔度数
     */
    private String perforation;

    /**
     * 印刷技术
     */
    private String printingTech;

    /**
     * 主题
     */
    private String theme;

    /**
     * 背景描述
     */
    private String background;

    /**
     * 设计者
     */
    private String designer;

    /**
     * 印刷厂
     */
    private String printer;

    /**
     * 状态
     */
    private String status;

    /**
     * 创建时间
     */
    private LocalDateTime createdAt;

    /**
     * 更新时间
     */
    private LocalDateTime updatedAt;
}

