package cn.stamp.modules.stamp.service.storage;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class SavedImageResult {

    /** 相对 root 的原图路径 */
    private String relativeImagePath;

    /** 相对 root 的缩略图路径，可能为空 */
    private String relativeThumbPath;

    /** 图片宽度（像素） */
    private int widthPx;

    /** 图片高度（像素） */
    private int heightPx;

    /** 文件大小（字节） */
    private long fileSize;
}
