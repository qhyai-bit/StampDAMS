package cn.stamp.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import org.springframework.boot.autoconfigure.jackson.Jackson2ObjectMapperBuilderCustomizer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * Jackson 全局配置
 * 用于解决 LocalDateTime 等 Java 8 时间类型在 JSON 序列化时的报错问题
 */
@Configuration
public class JacksonConfig {

    /**
     * 显式提供全局 ObjectMapper，确保 JavaTimeModule 生效
     */
    @Bean
    public ObjectMapper objectMapper() {
        ObjectMapper mapper = new ObjectMapper();
        mapper.registerModule(new JavaTimeModule());
        return mapper;
    }

    /**
     * 配置 Jackson 序列化器
     * 确保全局（包括 Controller 返回的 JSON 和 Redis 默认序列化器）都支持 Java 8 时间类型
     */
    @Bean
    public Jackson2ObjectMapperBuilderCustomizer jsonCustomizer() {
        return builder -> {
            // 显式注册 JavaTimeModule，解决 LocalDateTime 序列化报错
            builder.modules(new JavaTimeModule());
        };
    }
}
