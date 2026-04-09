package cn.stamp.modules.stamp.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * 邮票图像资源实体类
 * 存储邮票的正反面、细节图等图像信息
 */
@Data
@TableName("stamp_image")
public class StampImage {

    /**
     * 主键ID，自增
     */
    @TableId(type = IdType.AUTO)
    private Long id;

    /**
     * 关联的邮票ID
     */
    private Long stampId;

    /**
     * 图像类型
     * FRONT: 正面
     * BACK: 背面
     * DETAIL: 细节图
     */
    private String imageType;

    /**
     * 图像相对根路径的地址
     * 例如: stamps/1/xxx.jpg
     */
    private String imageUrl;

    /**
     * 缩略图相对根路径的地址
     */
    private String thumbUrl;

    /**
     * 图像分辨率 (DPI)
     */
    private Integer dpi;

    /**
     * 图像宽度 (像素)
     */
    private Integer widthPx;

    /**
     * 图像高度 (像素)
     */
    private Integer heightPx;

    /**
     * 文件大小 (字节)
     */
    private Long fileSize;

    /**
     * 排序号
     */
    private Integer sortNo;

    /**
     * 创建时间
     */
    private LocalDateTime createdAt;

    /**
     * 更新时间
     */
    private LocalDateTime updatedAt;
}
