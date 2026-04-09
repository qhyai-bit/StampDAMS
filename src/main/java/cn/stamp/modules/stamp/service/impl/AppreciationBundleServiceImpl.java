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
    public AppreciationBundleVO buildBundle(Long stampId) {
        // 1. 查询邮票基本信息，若不存在则抛出异常
        Stamp stamp = stampService.findById(stampId);
        if (stamp == null) {
            throw new IllegalArgumentException("邮票不存在");
        }

        // 2. 查询邮票鉴赏内容
        StampAppreciation appreciation = stampAppreciationService.getByStampId(stampId);

        // 3. 查询邮票关联的图片列表，并组装每张图片的标注信息
        List<StampImageVO> imageVos = stampImageService.listByStampId(stampId);
        List<ImageWithAnnotationsVO> list = new ArrayList<>();
        for (StampImageVO vo : imageVos) {
            // 获取当前图片的标注列表
            List<ImageAnnotation> ann = imageAnnotationService.listByImageId(vo.getId());
            // 构建包含图片及其标注的对象
            list.add(ImageWithAnnotationsVO.builder()
                    .image(vo)
                    .annotations(ann)
                    .build());
        }

        // 4. 组装并返回完整的鉴赏数据包
        return AppreciationBundleVO.builder()
                .stamp(stamp)
                .appreciation(appreciation)
                .images(list)
                .build();
    }
}
