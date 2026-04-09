package cn.stamp.modules.stamp.vo;

import cn.stamp.modules.stamp.entity.Stamp;
import cn.stamp.modules.stamp.entity.StampAppreciation;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * 鉴赏页数据聚合视图对象 (VO)
 * <p>
 * 用于封装鉴赏页面所需的全量数据，实现一次请求获取所有必要信息，包括：
 * <ul>
 *     <li>邮票基础信息</li>
 *     <li>鉴赏扩展详情</li>
 *     <li>关联图像及其标注信息</li>
 * </ul>
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AppreciationBundleVO {

    /**
     * 邮票基础信息
     */
    private Stamp stamp;

    /**
     * 鉴赏扩展信息
     */
    private StampAppreciation appreciation;

    /**
     * 图像及标注列表
     */
    private List<ImageWithAnnotationsVO> images;
}
