package cn.stamp.modules.stamp.service.storage;

import cn.stamp.config.StampUploadProperties;
import lombok.RequiredArgsConstructor;
import net.coobird.thumbnailator.Thumbnails;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Locale;
import java.util.UUID;

/**
 * 邮票图片存储服务
 * <p>
 * 负责邮票原图的持久化存储以及缩略图的生成。
 * 支持根据邮票ID分目录存储，并自动生成固定尺寸的缩略图。
 */
@Service
@RequiredArgsConstructor
public class StampImageStorageService {

    private final StampUploadProperties props;

    /**
     * 保存邮票图片并生成缩略图
     *
     * @param file    上传的文件
     * @param stampId 邮票ID，用于确定存储路径
     * @return 保存结果，包含原图相对路径、缩略图相对路径、宽高及文件大小
     * @throws IOException 当文件读写发生错误时抛出
     */
    public SavedImageResult save(MultipartFile file, Long stampId) throws IOException {
        if (file == null || file.isEmpty()) {
            throw new IllegalArgumentException("上传文件不能为空");
        }

        // 1. 准备文件名与路径
        String original = file.getOriginalFilename();
        String ext = extractExtension(original);
        String uuid = UUID.randomUUID().toString().replace("-", "");
        String baseName = uuid + "_img" + ext;

        Path root = Paths.get(props.getRootPath()).toAbsolutePath().normalize();
        Path dir = root.resolve("stamps").resolve(String.valueOf(stampId));
        Files.createDirectories(dir);

        // 2. 保存原图
        Path target = dir.resolve(baseName);
        file.transferTo(target.toFile());

        // 3. 获取原图信息
        long size = Files.size(target);
        BufferedImage bi = ImageIO.read(target.toFile());
        int w = bi != null ? bi.getWidth() : 0;
        int h = bi != null ? bi.getHeight() : 0;

        // 构建原图相对路径（统一使用正斜杠）
        String relImage = Paths.get("stamps", String.valueOf(stampId), baseName)
                .toString().replace("\\", "/");

        // 4. 生成缩略图
        String relThumb = null;
        if (bi != null) {
            String thumbName = uuid + "_thumb.jpg";
            Path thumbPath = dir.resolve(thumbName);
            try {
                Thumbnails.of(target.toFile())
                        .size(props.getThumbMaxEdge(), props.getThumbMaxEdge())
                        .keepAspectRatio(true)
                        .outputFormat("jpg")
                        .toFile(thumbPath.toFile());
                relThumb = Paths.get("stamps", String.valueOf(stampId), thumbName)
                        .toString().replace("\\", "/");
            } catch (Exception ignored) {
                // 非标准图片或超大图可能导致缩略失败，仅保留原图，不中断主流程
            }
        }

        return new SavedImageResult(relImage, relThumb, w, h, size);
    }

    /**
     * 删除指定相对路径的文件
     *
     * @param relativePath 文件的相对路径
     */
    public void deleteIfExists(String relativePath) {
        if (relativePath == null || relativePath.isBlank()) {
            return;
        }
        Path root = Paths.get(props.getRootPath()).toAbsolutePath().normalize();
        Path p = root.resolve(relativePath.replace("/", java.io.File.separator));
        try {
            Files.deleteIfExists(p);
        } catch (IOException ignored) {
            // 忽略删除异常，避免影响主业务逻辑
        }
    }

    /**
     * 提取文件扩展名
     * <p>
     * 如果文件名为空、无扩展名或扩展名过长，则默认返回 ".dat"
     *
     * @param filename 原始文件名
     * @return 小写格式的扩展名（含点号）
     */
    private static String extractExtension(String filename) {
        if (filename == null || !filename.contains(".")) {
            return ".dat";
        }
        String ext = filename.substring(filename.lastIndexOf('.')).toLowerCase(Locale.ROOT);
        if (ext.length() > 8) {
            return ".dat";
        }
        return ext;
    }
}
