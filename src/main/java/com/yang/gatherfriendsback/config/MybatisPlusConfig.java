package com.yang.gatherfriendsback.config;

import com.baomidou.mybatisplus.annotation.DbType;
import com.baomidou.mybatisplus.extension.plugins.MybatisPlusInterceptor;
import com.baomidou.mybatisplus.extension.plugins.inner.PaginationInnerInterceptor;
import org.mybatis.spring.annotation.MapperScan;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * MyBatisPlus 配置
 */
@Configuration  // 标记这是一个配置类，Spring 会自动扫描并加载
@MapperScan("com.yang.gatherfriendsback.mapper")  // 指定 Mapper 接口所在的包路径，让 MyBatis 自动扫描并生成实现类
public class MybatisPlusConfig {
    // 配置 MyBatis-Plus 插件
    @Bean  // 将方法返回的对象注入到 Spring 容器中，供全局使用
    public MybatisPlusInterceptor mybatisPlusInterceptor() {
        MybatisPlusInterceptor interceptor = new MybatisPlusInterceptor();
        // 添加分页插件，指定数据库类型为 MySQL
        interceptor.addInnerInterceptor(new PaginationInnerInterceptor(DbType.MYSQL));
        return interceptor;
    }
}