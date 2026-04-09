package cn.stamp.modules.stamp.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * 图像标注实体类
 * 用于存储图像中的标注信息，支持点、矩形、多边形等多种形状。
 * 坐标及形状详细数据以 JSON 格式存储在 annDataJson 字段中。
 */
@Data
@TableName("image_annotation")
public class ImageAnnotation {

    /**
     * 主键ID，自增
     */
    @TableId(type = IdType.AUTO)
    private Long id;

    /**
     * 关联的图像ID
     */
    private Long imageId;

    /**
     * 标注类型
     * 枚举值：POINT (点), RECT (矩形), POLYGON (多边形)
     */
    private String annType;

    /**
     * 标注数据JSON字符串
     * 包含坐标点、形状参数等详细信息
     */
    private String annDataJson;

    /**
     * 标注文本内容（如标签名称、备注等）
     */
    private String annText;

    /**
     * 标注显示颜色（通常为十六进制颜色代码）
     */
    private String color;

    /**
     * 创建人ID
     */
    private Long createdBy;

    /**
     * 创建时间
     */
    private LocalDateTime createdAt;
}
