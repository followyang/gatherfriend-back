package com.yang.gatherfriendsback.config;

import lombok.Data;
import org.redisson.Redisson;
import org.redisson.api.RedissonClient;
import org.redisson.config.Config;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@ConfigurationProperties(prefix = "spring.data.redis") // 修改前缀以匹配application.yml中的配置
@Data
public class RedissonConfig {

    private String host;
    private Integer port; // 改为Integer类型
    private String password;
    private Integer database = 0; // 添加默认值

    @Bean
    public RedissonClient redissonClient() {

        Config config = new Config();
        String redisAddress = String.format("redis://%s:%s", host, port);

        config.useSingleServer()
                .setAddress(redisAddress)
                .setDatabase(database != null ? database : 0)
                .setPassword(password);
        return Redisson.create(config);
    }
}