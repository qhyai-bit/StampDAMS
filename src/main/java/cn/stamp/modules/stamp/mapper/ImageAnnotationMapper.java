package cn.stamp.modules.stamp.mapper;

import cn.stamp.modules.stamp.entity.ImageAnnotation;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;

@Mapper
/**
 * 图像标注 Mapper 接口
 */
public interface ImageAnnotationMapper extends BaseMapper<ImageAnnotation> {
}
