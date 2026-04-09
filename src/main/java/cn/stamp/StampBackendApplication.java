package cn.stamp;

import cn.stamp.config.StampUploadProperties;
import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;

@SpringBootApplication
@MapperScan({
        "cn.stamp.modules.stamp.mapper",
        "cn.stamp.modules.user.mapper",
        "cn.stamp.modules.market.mapper"
})
@EnableConfigurationProperties(StampUploadProperties.class)
public class StampBackendApplication {

    public static void main(String[] args) {
        SpringApplication.run(StampBackendApplication.class, args);
    }
}

