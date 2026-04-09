package cn.stamp.modules.stamp.service.impl;

import cn.stamp.config.StampUploadProperties;
import cn.stamp.modules.stamp.entity.StampImage;
import cn.stamp.modules.stamp.mapper.StampImageMapper;
import cn.stamp.modules.stamp.mapper.ImageAnnotationMapper;
import cn.stamp.modules.stamp.entity.ImageAnnotation;
import cn.stamp.modules.stamp.service.StampImageService;
import cn.stamp.modules.stamp.service.StampService;
import cn.stamp.modules.stamp.service.storage.SavedImageResult;
import cn.stamp.modules.stamp.service.storage.StampImageStorageService;
import cn.stamp.modules.stamp.vo.StampImageVO;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class StampImageServiceImpl implements StampImageService {

    private final StampService stampService;
    private final StampImageMapper stampImageMapper;
    private final ImageAnnotationMapper imageAnnotationMapper;
    private final StampImageStorageService storageService;
    private final StampUploadProperties uploadProperties;

    // 注入 RedisTemplate 用于缓存管理
    private final RedisTemplate<String, Object> redisTemplate;

    /**
     * 上传邮票图片
     *
     * @param stampId   邮票ID
     * @param imageType 图片类型（如：FRONT, BACK, DETAIL）
     * @param file      上传的文件
     * @return 邮票图片VO
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public StampImageVO upload(Long stampId, String imageType, MultipartFile file) {
        // 校验邮票是否存在
        if (stampService.findById(stampId) == null) {
            throw new IllegalArgumentException("邮票不存在");
        }
        // 校验图片类型
        if (imageType == null || imageType.isBlank()) {
            throw new IllegalArgumentException("imageType 不能为空，如 FRONT、BACK、DETAIL");
        }

        SavedImageResult saved;
        try {
            // 保存文件到存储服务
            saved = storageService.save(file, stampId);
        } catch (IOException e) {
            throw new IllegalArgumentException("文件保存失败: " + e.getMessage());
        }

        // 计算排序号：获取当前最大排序号并加1
        List<StampImage> lastSort = stampImageMapper.selectList(new LambdaQueryWrapper<StampImage>()
                .eq(StampImage::getStampId, stampId)
                .orderByDesc(StampImage::getSortNo)
                .last("limit 1"));
        int nextSort = lastSort.isEmpty() ? 0 : (lastSort.get(0).getSortNo() == null ? 0 : lastSort.get(0).getSortNo());

        // 构建邮票图片实体
        StampImage row = new StampImage();
        row.setStampId(stampId);
        row.setImageType(imageType.toUpperCase());
        row.setImageUrl(saved.getRelativeImagePath());
        row.setThumbUrl(saved.getRelativeThumbPath());
        row.setWidthPx(saved.getWidthPx());
        row.setHeightPx(saved.getHeightPx());
        row.setFileSize(saved.getFileSize());
        row.setSortNo(nextSort + 1);
        LocalDateTime now = LocalDateTime.now();
        row.setCreatedAt(now);
        row.setUpdatedAt(now);
        
        // 插入数据库
        stampImageMapper.insert(row);

        // 图片变更后，清除该邮票的鉴赏聚合缓存，保证前端看到的是最新图片
        redisTemplate.delete("stamp:bundle::" + stampId);

        return toVo(row);
    }

    /**
     * 根据邮票ID查询图片列表
     *
     * @param stampId 邮票ID
     * @return 邮票图片VO列表
     */
    @Override
    public List<StampImageVO> listByStampId(Long stampId) {
        List<StampImage> list = stampImageMapper.selectList(new LambdaQueryWrapper<StampImage>()
                .eq(StampImage::getStampId, stampId)
                .orderByAsc(StampImage::getSortNo)
                .orderByDesc(StampImage::getId));
        return list.stream().map(this::toVo).collect(Collectors.toList());
    }

    /**
     * 删除邮票图片
     *
     * @param imageId 图片ID
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public void delete(Long imageId) {
        StampImage img = stampImageMapper.selectById(imageId);
        if (img == null) {
            return;
        }
        
        // 删除关联的标注信息
        imageAnnotationMapper.delete(new LambdaQueryWrapper<ImageAnnotation>()
                .eq(ImageAnnotation::getImageId, imageId));
        
        // 删除数据库记录
        stampImageMapper.deleteById(imageId);
        
        // 删除存储文件
        storageService.deleteIfExists(img.getImageUrl());
        storageService.deleteIfExists(img.getThumbUrl());
    }

    /**
     * 实体转VO
     *
     * @param e 邮票图片实体
     * @return 邮票图片VO
     */
    private StampImageVO toVo(StampImage e) {
        return StampImageVO.builder()
                .id(e.getId())
                .stampId(e.getStampId())
                .imageType(e.getImageType())
                .imageAccessUrl(toAccessUrl(e.getImageUrl()))
                .thumbAccessUrl(toAccessUrl(e.getThumbUrl()))
                .dpi(e.getDpi())
                .widthPx(e.getWidthPx())
                .heightPx(e.getHeightPx())
                .fileSize(e.getFileSize())
                .sortNo(e.getSortNo())
                .build();
    }

    /**
     * 生成访问URL
     *
     * @param relative 相对路径
     * @return 完整访问URL
     */
    private String toAccessUrl(String relative) {
        if (relative == null || relative.isBlank()) {
            return null;
        }
        String prefix = uploadProperties.getPublicUrlPrefix();
        if (!prefix.startsWith("/")) {
            prefix = "/" + prefix;
        }
        return prefix + "/" + relative.replace("\\", "/");
    }
}
