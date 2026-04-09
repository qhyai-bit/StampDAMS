package cn.stamp.modules.stamp.vo;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 印章图片视图对象（VO）
 * 用于向前端返回图片的详细信息及可访问的 URL 地址
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class StampImageVO {

    /**
     * 图片ID
     */
    private Long id;

    /**
     * 关联的印章ID
     */
    private Long stampId;

    /**
     * 图片类型（如：jpg, png等）
     */
    private String imageType;

    /**
     * 原图访问URL
     * 浏览器可直接访问的完整路径，例如：/files/stamps/1/xxx.jpg
     */
    private String imageAccessUrl;

    /**
     * 缩略图访问URL
     */
    private String thumbAccessUrl;

    /**
     * 分辨率（DPI）
     */
    private Integer dpi;

    /**
     * 图片宽度（像素）
     */
    private Integer widthPx;

    /**
     * 图片高度（像素）
     */
    private Integer heightPx;

    /**
     * 文件大小（字节）
     */
    private Long fileSize;

    /**
     * 排序号
     */
    private Integer sortNo;
}
