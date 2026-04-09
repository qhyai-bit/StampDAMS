package cn.stamp.modules.stamp.service.impl;

import cn.stamp.modules.stamp.entity.ImageAnnotation;
import cn.stamp.modules.stamp.entity.Stamp;
import cn.stamp.modules.stamp.entity.StampAppreciation;
import cn.stamp.modules.stamp.service.AppreciationBundleService;
import cn.stamp.modules.stamp.service.ImageAnnotationService;
import cn.stamp.modules.stamp.service.StampAppreciationService;
import cn.stamp.modules.stamp.service.StampImageService;
import cn.stamp.modules.stamp.service.StampService;
import cn.stamp.modules.stamp.vo.AppreciationBundleVO;
import cn.stamp.modules.stamp.vo.ImageWithAnnotationsVO;
import cn.stamp.modules.stamp.vo.StampImageVO;
import lombok.RequiredArgsConstructor;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class AppreciationBundleServiceImpl implements AppreciationBundleService {

    private final StampService stampService;
    private final StampAppreciationService stampAppreciationService;
    private final StampImageService stampImageService;
    private final ImageAnnotationService imageAnnotationService;

    /**
     * 构建邮票鉴赏数据包
     *
     * @param stampId 邮票ID
     * @return 包含邮票基本信息、鉴赏内容及图片标注信息的完整数据包
     */
    @Override
    @Cacheable(value = "stamp:bundle", key = "#stampId", unless = "#result == null")
    public AppreciationBundleVO buildBundle(Long stampId) {
        // 1. 获取邮票基础信息，若不存在则终止流程
        Stamp stamp = stampService.findById(stampId);
        if (stamp == null) {
            throw new IllegalArgumentException("邮票不存在");
        }

        // 2. 获取邮票对应的鉴赏文本内容
        StampAppreciation appreciation = stampAppreciationService.getByStampId(stampId);

        // 3. 获取邮票关联的图片列表，并为每张图片加载其标注信息
        List<StampImageVO> imageVos = stampImageService.listByStampId(stampId);
        List<ImageWithAnnotationsVO> list = new ArrayList<>();
        for (StampImageVO vo : imageVos) {
            // 查询当前图片下的所有标注
            List<ImageAnnotation> ann = imageAnnotationService.listByImageId(vo.getId());
            // 组装图片与其对应的标注数据
            list.add(ImageWithAnnotationsVO.builder()
                    .image(vo)
                    .annotations(ann)
                    .build());
        }

        // 4. 整合所有数据并返回最终结果对象
        return AppreciationBundleVO.builder()
                .stamp(stamp)
                .appreciation(appreciation)
                .images(list)
                .build();
    }
}
