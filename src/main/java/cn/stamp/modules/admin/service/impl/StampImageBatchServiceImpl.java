package cn.stamp.modules.admin.service.impl;

import cn.stamp.modules.admin.service.StampImageBatchService;
import cn.stamp.modules.admin.vo.ImportResultVO;
import cn.stamp.modules.stamp.entity.Stamp;
import cn.stamp.modules.stamp.entity.StampImage;
import cn.stamp.modules.stamp.mapper.StampImageMapper;
import cn.stamp.modules.stamp.mapper.StampMapper;
import cn.stamp.modules.stamp.service.storage.SavedImageResult;
import cn.stamp.modules.stamp.service.storage.StampImageStorageService;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.io.InputStream;
import java.time.LocalDateTime;
import java.util.*;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

@Service
@RequiredArgsConstructor
public class StampImageBatchServiceImpl implements StampImageBatchService {

    private final StampMapper stampMapper;
    private final StampImageMapper stampImageMapper;
    private final StampImageStorageService storageService;
    private final RedisTemplate<String, Object> redisTemplate;

    /**
     * 批量导入邮票图片
     * <p>
     * 解析 ZIP 包中的图片文件，根据目录结构或文件名识别邮票编码（code）及图片类型（正面/背面）。
     * 将图片上传至存储服务，并将元数据存入数据库。最后清理受影响邮票的缓存。
     * </p>
     *
     * @param zipFile 包含邮票图片的 ZIP 文件
     * @return 导入结果统计（总数、成功数、失败数、消息）
     * @throws IOException 读取文件或处理 ZIP 流时发生 IO 异常
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public ImportResultVO importImagesZip(MultipartFile zipFile) throws IOException {
        if (zipFile == null || zipFile.isEmpty()) {
            return new ImportResultVO(0, 0, 0, "ZIP 文件为空");
        }

        int total = 0;      // 尝试处理的文件总数
        int success = 0;    // 成功入库的文件数
        int failed = 0;     // 处理失败的文件数

        // 记录本次导入涉及到的邮票 ID，用于后续批量清理缓存
        Set<Long> touchedStampIds = new HashSet<>();

        try (InputStream is = zipFile.getInputStream();
             ZipInputStream zis = new ZipInputStream(is)) {
            ZipEntry entry;
            while ((entry = zis.getNextEntry()) != null) {
                // 跳过目录条目
                if (entry.isDirectory()) {
                    continue;
                }
                
                String name = entry.getName();
                // 过滤系统生成的隐藏文件或无关文件
                if (name == null || name.contains("__MACOSX") || name.endsWith(".DS_Store")) {
                    continue;
                }
                
                total++;

                try {
                    // 1. 解析文件名，提取邮票 code 和图片类型
                    Parsed parsedInfo = parse(name);
                    if (parsedInfo == null) {
                        throw new IllegalArgumentException("无法识别邮票编码或图片类型: " + name);
                    }

                    // 2. 根据 code 查询邮票实体
                    Stamp stamp = stampMapper.selectOne(new LambdaQueryWrapper<Stamp>()
                            .eq(Stamp::getCode, parsedInfo.code)
                            .last("limit 1"));
                    if (stamp == null) {
                        throw new IllegalArgumentException("未找到对应的邮票，code=" + parsedInfo.code);
                    }

                    // 3. 保存图片文件
                    // 注意：ZipInputStream 是流式的，必须在当前 entry 作用域内完成读取和上传
                    SavedImageResult savedResult = storageService.save(zis, name, stamp.getId());

                    // 4. 计算排序号 (sortNo)
                    // 获取当前邮票下已有的最大 sortNo，新图片排序号为 max + 1
                    List<StampImage> existingImages = stampImageMapper.selectList(new LambdaQueryWrapper<StampImage>()
                            .eq(StampImage::getStampId, stamp.getId())
                            .orderByDesc(StampImage::getSortNo)
                            .last("limit 1"));
                    
                    int maxSortNo = 0;
                    if (!existingImages.isEmpty() && existingImages.get(0).getSortNo() != null) {
                        maxSortNo = existingImages.get(0).getSortNo();
                    }
                    int nextSortNo = maxSortNo + 1;

                    // 5. 构建并插入邮票图片记录
                    StampImage imageEntity = new StampImage();
                    imageEntity.setStampId(stamp.getId());
                    imageEntity.setImageType(parsedInfo.imageType);
                    imageEntity.setImageUrl(savedResult.getRelativeImagePath());
                    imageEntity.setThumbUrl(savedResult.getRelativeThumbPath());
                    imageEntity.setWidthPx(savedResult.getWidthPx());
                    imageEntity.setHeightPx(savedResult.getHeightPx());
                    imageEntity.setFileSize(savedResult.getFileSize());
                    imageEntity.setSortNo(nextSortNo);
                    
                    LocalDateTime now = LocalDateTime.now();
                    imageEntity.setCreatedAt(now);
                    imageEntity.setUpdatedAt(now);
                    
                    stampImageMapper.insert(imageEntity);

                    // 记录受影响的邮票 ID
                    touchedStampIds.add(stamp.getId());
                    success++;
                } catch (Exception e) {
                    // 单张图片处理失败不影响其他图片，记录失败计数
                    failed++;
                } finally {
                    // 关闭当前 Entry，准备读取下一个
                    zis.closeEntry();
                }
            }
        }

        // 6. 批量清理缓存
        // 删除所有被修改过图片的邮票的鉴赏详情缓存
        for (Long stampId : touchedStampIds) {
            redisTemplate.delete("stamp:bundle::" + stampId);
        }

        return new ImportResultVO(total, success, failed,
                "导入完成。支持格式：{code}/front.*、{code}/back.* 或文件名包含 front/back 关键字");
    }

    /**
     * 解析结果内部类
     */
    private static class Parsed {
        String code;       // 邮票编码
        String imageType;  // 图片类型：FRONT(正面), BACK(背面)

        Parsed(String code, String imageType) {
            this.code = code;
            this.imageType = imageType;
        }
    }

    /**
     * 从文件路径中解析邮票编码（code）和图片类型（imageType）
     * <p>
     * 支持的命名规范示例：
     * <ul>
     *   <li>目录结构：CN-1980-001/front.jpg -> code: CN-1980-001, type: FRONT</li>
     *   <li>目录结构：CN-1980-001/back.png -> code: CN-1980-001, type: BACK</li>
     *   <li>文件名包含：CN-1980-001_FRONT.jpg -> code: CN-1980-001, type: FRONT</li>
     *   <li>文件名包含：CN-1980-001-back.jpeg -> code: CN-1980-001, type: BACK</li>
     * </ul>
     *
     * @param path ZIP 条目路径
     * @return 解析结果对象，若无法识别则返回 null
     */
    private static Parsed parse(String path) {
        // 统一路径分隔符为 '/'
        String normalizedPath = path.replace("\\", "/");
        
        // 提取文件名和父目录名
        String filename = normalizedPath.substring(normalizedPath.lastIndexOf('/') + 1);
        String parentDir = null;
        int lastSlashIndex = normalizedPath.lastIndexOf('/');
        if (lastSlashIndex > 0) {
            String fullPathDir = normalizedPath.substring(0, lastSlashIndex);
            // 取最后一层目录作为 code 候选
            if (fullPathDir.contains("/")) {
                parentDir = fullPathDir.substring(fullPathDir.lastIndexOf('/') + 1);
            } else {
                parentDir = fullPathDir;
            }
        }

        // 1. 识别图片类型 (FRONT/BACK)
        String lowerFilename = filename.toLowerCase(Locale.ROOT);
        String imageType = null;
        if (lowerFilename.contains("front") || lowerFilename.contains("zheng") || lowerFilename.contains("正面")) {
            imageType = "FRONT";
        } else if (lowerFilename.contains("back") || lowerFilename.contains("fan") || lowerFilename.contains("背面")) {
            imageType = "BACK";
        }
        
        // 如果文件名中未包含类型标识，直接返回 null
        if (imageType == null) {
            return null;
        }

        // 2. 识别邮票编码 (code)
        String code = null;
        // 优先使用父目录名作为 code
        if (parentDir != null && !parentDir.isBlank()) {
            code = parentDir;
        }
        
        // 如果没有父目录或父目录无效，尝试从文件名中提取 code
        if (code == null || code.isBlank()) {
            // 去除扩展名
            String baseName = filename;
            int dotIndex = baseName.lastIndexOf('.');
            if (dotIndex > 0) {
                baseName = baseName.substring(0, dotIndex);
            }
            
            // 尝试通过下划线分割获取 code (假设格式为 CODE_TYPE.ext)
            String[] parts = baseName.split("[_]");
            if (parts.length > 0) {
                code = parts[0];
            } else {
                code = baseName;
            }
        }
        
        if (code == null || code.isBlank()) {
            return null;
        }
        
        return new Parsed(code.trim(), imageType);
    }
}

