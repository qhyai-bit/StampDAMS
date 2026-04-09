package cn.stamp.modules.stamp.service.impl;

import cn.stamp.modules.stamp.dto.AnnotationSaveDTO;
import cn.stamp.modules.stamp.entity.ImageAnnotation;
import cn.stamp.modules.stamp.entity.StampImage;
import cn.stamp.modules.stamp.mapper.ImageAnnotationMapper;
import cn.stamp.modules.stamp.mapper.StampImageMapper;
import cn.stamp.modules.stamp.service.ImageAnnotationService;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

/**
 * 图像标注服务实现类
 */
@Service
@RequiredArgsConstructor
public class ImageAnnotationServiceImpl implements ImageAnnotationService {

    private final ImageAnnotationMapper annotationMapper;
    private final StampImageMapper stampImageMapper;

    /**
     * 根据图像ID查询标注列表
     *
     * @param imageId 图像ID
     * @return 标注列表，按ID升序排列
     */
    @Override
    public List<ImageAnnotation> listByImageId(Long imageId) {
        return annotationMapper.selectList(new LambdaQueryWrapper<ImageAnnotation>()
                .eq(ImageAnnotation::getImageId, imageId)
                .orderByAsc(ImageAnnotation::getId));
    }

    /**
     * 保存图像标注信息
     *
     * @param imageId 图像ID
     * @param dto     标注保存数据传输对象
     * @param userId  创建人ID
     * @return 新创建的标注ID
     * @throws IllegalArgumentException 当指定的图像不存在时抛出异常
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public Long save(Long imageId, AnnotationSaveDTO dto, Long userId) {
        // 验证图像是否存在
        StampImage img = stampImageMapper.selectById(imageId);
        if (img == null) {
            throw new IllegalArgumentException("图像不存在");
        }

        // 构建标注实体
        ImageAnnotation row = new ImageAnnotation();
        row.setImageId(imageId);
        row.setAnnType(dto.getAnnType().toUpperCase());
        row.setAnnDataJson(dto.getAnnDataJson());
        row.setAnnText(dto.getAnnText());
        row.setColor(dto.getColor());
        row.setCreatedBy(userId);
        row.setCreatedAt(LocalDateTime.now());

        // 插入数据库
        annotationMapper.insert(row);
        return row.getId();
    }

    /**
     * 删除指定ID的标注
     *
     * @param annotationId 标注ID
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public void delete(Long annotationId) {
        annotationMapper.deleteById(annotationId);
    }
}
