package com.github.aly8246.autoConfiguration;

import com.github.aly8246.collectionutil.core.PageInterface;
import com.github.aly8246.collectionutil.exception.CollectionException;
import com.github.aly8246.collectionutil.main.CollectionInterceptor;
import lombok.extern.slf4j.Slf4j;
import org.apache.ibatis.session.SqlSessionFactory;
import org.mybatis.spring.boot.autoconfigure.MybatisAutoConfiguration;
import org.springframework.boot.autoconfigure.AutoConfigureAfter;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;

import javax.annotation.PostConstruct;
import javax.annotation.Resource;
import java.util.List;
import java.util.Properties;

/**
 * 自动装配
 *
 * @author 南有乔木
 * @version v.1.0.2
 * @see PageInterface
 * @see CollectionProperties
 * @see CollectionInterceptor
 * @see SqlSessionFactory
 * @see MybatisAutoConfiguration
 */
@Configuration
@ConditionalOnBean({SqlSessionFactory.class})
@EnableConfigurationProperties(value = CollectionProperties.class)
@AutoConfigureAfter({MybatisAutoConfiguration.class})
@Slf4j
public class CollectionAutoConfiguration {
@Resource
private CollectionProperties collectionProperties;
@Resource
private List<SqlSessionFactory> sqlSessionFactoryList;

/**
 * 将拦截器放置到mybatis的拦截器链
 *
 * @author 南有乔木
 * @see CollectionInterceptor
 */
@PostConstruct
public void addPageInterceptor() {
	CollectionInterceptor interceptor = new CollectionInterceptor();
	Properties properties = new Properties();
	properties.putAll(new Properties());
	properties.putAll(this.collectionProperties.getProperties());
	interceptor.setProperties(properties);
	log.info("CollectionInterceptor init!");
	for (SqlSessionFactory sqlSessionFactory : this.sqlSessionFactoryList) {
		log.debug("addPageInterceptor");
		sqlSessionFactory.getConfiguration().addInterceptor(interceptor);
	}
	
}
}
