package cn.stamp.modules.stamp.service;

import cn.stamp.modules.stamp.dto.AnnotationSaveDTO;
import cn.stamp.modules.stamp.entity.ImageAnnotation;

import java.util.List;

public interface ImageAnnotationService {

    List<ImageAnnotation> listByImageId(Long imageId);

    Long save(Long imageId, AnnotationSaveDTO dto, Long userId);

    void delete(Long annotationId);
}
