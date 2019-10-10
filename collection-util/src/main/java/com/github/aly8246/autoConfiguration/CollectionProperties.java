package com.github.aly8246.autoConfiguration;


import com.github.aly8246.collectionutil.core.PageInterface;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

import java.util.Properties;

/**
 * 配置文件
 *
 * @author 南有乔木
 * @version v.1.0.2
 * @see PageInterface
 */
@ConfigurationProperties(
		prefix = "collection-util"
)
@Component
public class CollectionProperties {
private Properties properties = new Properties();
/**
 * 零边界
 */
private Boolean safeModel;
/**
 * 数据库[默认:mysql]
 */
private String database;

public CollectionProperties() {
	this.properties.setProperty("database", "mysql");
	this.properties.setProperty("safe-model", Boolean.toString(false
	));
}

public Properties getProperties() {
	return this.properties;
}

public Boolean getSafeModel() {
	return Boolean.valueOf(properties.getProperty("safe-model"));
}

public void setSafeModel(Boolean safeModel) {
	this.properties.setProperty("safe-model", safeModel.toString());
}

public String getDatabase() {
	return this.properties.getProperty("database");
}

public void setDatabase(String database) {
	this.properties.setProperty("database", database);
}
}
