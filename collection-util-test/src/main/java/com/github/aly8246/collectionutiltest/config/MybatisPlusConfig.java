package com.github.aly8246.collectionutiltest.config;


import com.baomidou.mybatisplus.extension.plugins.PaginationInterceptor;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@ConditionalOnProperty(prefix = "spring.datasource", name = "driver-class-name", havingValue = "com.mysql.cj.jdbc.Driver")
public class MybatisPlusConfig {
@Bean
public PaginationInterceptor paginationInterceptor() {
	PaginationInterceptor page = new PaginationInterceptor();
	page.setDialectType("mysql");
	return page;
}

}
