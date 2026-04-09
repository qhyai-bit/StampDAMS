package cn.stamp.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * 邮票图片本地存储配置
 */
@Data
@ConfigurationProperties(prefix = "stamp.upload")
public class StampUploadProperties {

    /**
     * 物理根目录，如 uploads 或 D:/data/stamp-uploads
     */
    private String rootPath = "uploads";

    /**
     * 对外访问前缀，如 /files，最终 URL 为 /files/stamps/{id}/xxx.jpg
     */
    private String publicUrlPrefix = "/files";

    /**
     * 缩略图最大边长（像素）
     */
    private int thumbMaxEdge = 400;
}
