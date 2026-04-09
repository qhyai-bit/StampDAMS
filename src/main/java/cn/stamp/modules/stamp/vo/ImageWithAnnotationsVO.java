package cn.stamp.modules.stamp.vo;

import cn.stamp.modules.stamp.entity.ImageAnnotation;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * 图片及其标注信息视图对象
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ImageWithAnnotationsVO {

    /**
     * 图片基本信息
     */
    private StampImageVO image;

    /**
     * 标注信息列表
     */
    private List<ImageAnnotation> annotations;
}
