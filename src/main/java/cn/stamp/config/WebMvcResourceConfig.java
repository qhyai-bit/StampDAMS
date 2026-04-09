package cn.stamp.config;

import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import java.nio.file.Path;
import java.nio.file.Paths;

/**
 * 将本地 uploads 目录映射为 /files/** 静态访问
 */
@Configuration
@RequiredArgsConstructor
public class WebMvcResourceConfig implements WebMvcConfigurer {

    private final StampUploadProperties uploadProperties;

    /**
     * 配置静态资源处理器，将本地上传目录映射为 Web 可访问路径
     *
     * @param registry 资源处理器注册表
     */
    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        // 获取上传根目录的绝对路径并标准化
        Path root = Paths.get(uploadProperties.getRootPath()).toAbsolutePath().normalize();
        // 构建文件协议路径，注意末尾需要添加斜杠
        String location = "file:" + root + "/";
        // 获取公共访问前缀
        String prefix = uploadProperties.getPublicUrlPrefix();
        // 确保前缀以 "/" 开头
        if (!prefix.startsWith("/")) {
            prefix = "/" + prefix;
        }
        // 注册资源处理器：将 prefix/** 映射到本地 location
        registry.addResourceHandler(prefix + "/**")
                .addResourceLocations(location);
    }
}
