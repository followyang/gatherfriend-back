package com.yang.gatherfriendsback;

import com.yang.gatherfriendsback.config.RedissonConfig;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableConfigurationProperties(RedissonConfig.class)
public class GatherfriendsBackApplication {

	public static void main(String[] args) {
		SpringApplication.run(GatherfriendsBackApplication.class, args);
	}

}
