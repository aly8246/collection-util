package com.github.aly8246.collectionutil.core.impl;

import com.github.aly8246.collectionutil.core.PageInterface;
import com.github.aly8246.collectionutil.main.CollectionInterceptor;
import com.github.aly8246.collectionutil.model.Index;
import com.github.aly8246.collectionutil.model.Limit;
import com.github.aly8246.collectionutil.model.Page;
import lombok.extern.slf4j.Slf4j;
import org.apache.ibatis.cache.CacheKey;
import org.apache.ibatis.executor.Executor;
import org.apache.ibatis.mapping.BoundSql;
import org.apache.ibatis.mapping.MappedStatement;
import org.apache.ibatis.mapping.ResultMap;
import org.apache.ibatis.mapping.ResultMapping;
import org.apache.ibatis.session.Configuration;
import org.apache.ibatis.session.ResultHandler;
import org.apache.ibatis.session.RowBounds;
import org.slf4j.LoggerFactory;
import org.springframework.boot.logging.LogLevel;
import org.springframework.boot.logging.LoggingSystem;
import org.springframework.stereotype.Service;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.sql.SQLException;
import java.util.*;
import java.util.stream.Collectors;

/**
 * MYSQL实现接口
 * <pre>
 * 为mysql提供支持
 * </pre>
 *
 * @author 南有乔木
 * @version v.1.0.2
 * @see PageInterface
 * @see CollectionInterceptor
 */
@Slf4j
public class MysqlPageImpl implements PageInterface {
private static final List<ResultMapping> EMPTY_RESULTMAPPING = new ArrayList<>(0);
private final Properties properties = new Properties();

/**
 * 根据ms来创建一个返回结果集，指定返回类型为String
 *
 * @param ms MappedStatement
 * @return MappedStatement
 * @author 南有乔木
 */
private MappedStatement buildMappedStatement(MappedStatement ms) {
	String countMsId = ms.getId() + "_COUNT";
	
	MappedStatement.Builder builder = new MappedStatement
			                                      .Builder(ms.getConfiguration(), countMsId, ms.getSqlSource(), ms.getSqlCommandType());
	builder.resource(ms.getResource());
	builder.fetchSize(ms.getFetchSize());
	builder.statementType(ms.getStatementType());
	builder.keyGenerator(ms.getKeyGenerator());
	if (ms.getKeyProperties() != null && ms.getKeyProperties().length != 0) {
		StringBuilder keyProperties = new StringBuilder();
		for (String keyProperty : ms.getKeyProperties()) {
			keyProperties.append(keyProperty).append(",");
		}
		keyProperties.delete(keyProperties.length() - 1, keyProperties.length());
		builder.keyProperty(keyProperties.toString());
	}
	builder.timeout(ms.getTimeout());
	builder.parameterMap(ms.getParameterMap());
	//count查询返回值int
	List<ResultMap> resultMaps = new ArrayList<>();
	ResultMap resultMap = new ResultMap.Builder(ms.getConfiguration(), ms.getId(), String.class, EMPTY_RESULTMAPPING).build();
	resultMaps.add(resultMap);
	builder.resultMaps(resultMaps);
	builder.resultSetType(ms.getResultSetType());
	builder.cache(ms.getCache());
	builder.flushCacheRequired(ms.isFlushCacheRequired());
	builder.useCache(ms.isUseCache());
	return builder.build();
}

//select u.*,p.* from user u left join phone p on p.xx=u.xx
//↓   ↓   ↓
//select u.id from user u left join phone p on p.xx=u.xx

// step1. 将一个完整的sql通过from来切割
// result user u left join phone p on p.xx=u.xx

// step2. 将user u left join phone p on p.xx=u.xx根据left来切割
// result user u

// step3. 将结果转换成List，得到最后一个
// Import 通过将user u转换成List取最后一个是考虑到用户主表没有别名u,那就取user
// result u

// step4. build Count SQL
// SELECT + mainTableID(user) .id FROM + user u left join phone p on p.xx=u.xx
// SELECT u.id FROM user u left join phone p on p.xx=u.xx

/**
 * 根据原始sql来拼接查询id集合的sql
 *
 * @param s 原始sql
 * @return 查询id集合的sql
 * @author 南有乔木
 */
private String getCountSql(String s) {
	s = s.replace("\n", "");
	String from_;
	try {
		from_ = s.split("from ")[1];
	} catch (ArrayIndexOutOfBoundsException e) {
		from_ = s.split("FROM ")[1];
	}
	List<String> bodySQL = Arrays.stream(from_.split(" ")).collect(Collectors.toList());
	bodySQL.removeIf(e -> e.equals(""));
	
	List<String> tempList = new ArrayList<>();
	for (int i = 0; i < 2; i++) {
		tempList.add(bodySQL.get(i).replace("left", "LEFT"));
	}
	
	List<String> mysqlTable = Arrays.stream(String.join(" ", tempList).split("LEFT")[0].split(" ")).collect(Collectors.toList());
	
	String mainTableName = mysqlTable.get(mysqlTable.size() - 1);
	log.debug("mainTableName:" + mainTableName);
	String countSql = "SELECT " + mainTableName + ".id FROM " + from_;
	log.debug("countSql:" + countSql);
	
	return countSql;
}

/**
 * @author 南有乔木
 * @version 1.0.8.RELEASE
 * @since 1.8
 */
@Override
public List<Index> getIndexList(MappedStatement ms, Executor executor, Object parameter, BoundSql boundSql, RowBounds rowBounds, ResultHandler resultHandler) throws SQLException {
	MappedStatement mappedStatement = this.buildMappedStatement(ms);
	CacheKey countKey = executor.createCacheKey(mappedStatement, parameter, RowBounds.DEFAULT, boundSql);
	List<String> idList;
	String countSql = this.getCountSql(boundSql.getSql());
	BoundSql countBoundSql = new BoundSql(mappedStatement.getConfiguration(), countSql, boundSql.getParameterMappings(), parameter);
	
	this.closeSqlStd();
	Object countResultList = executor.query(mappedStatement, parameter, RowBounds.DEFAULT, resultHandler, countKey, countBoundSql);
	//TODO 开启打印sql
	this.openSqlStd();
	idList = (List<String>) countResultList;
	
	List<Index> indexList = new ArrayList<>();
	List<String> collect = idList.stream().distinct().collect(Collectors.toList());
	for (int i = 0, collectSize = collect.size(); i < collectSize; i++) {
		indexList.add(new Index(i + 1, Collections.frequency(idList, collect.get(i))));
	}
	return PageInterface.init(indexList);
}

private void closeSqlStd() {

}

private void openSqlStd() {

}

/**
 * @author 南有乔木
 * @version 1.0.2.RELEASE
 * @since 1.8
 */
@Override
public List query(MappedStatement ms, Executor executor,
                  Object parameter, BoundSql boundSql,
                  RowBounds rowBounds, ResultHandler resultHandler, CacheKey cacheKey, Limit limit) throws SQLException {
	String pageSql = boundSql.getSql() + " LIMIT " + limit.getStart() + "," + limit.getEnd();
	
	Configuration configuration = ms.getConfiguration();
	BoundSql pageBoundSql = new BoundSql(configuration, pageSql, boundSql.getParameterMappings(), parameter);
	
	return executor.query(ms, parameter, RowBounds.DEFAULT, resultHandler, cacheKey, pageBoundSql);
}

/**
 * @author 南有乔木
 * @version 1.0.2.RELEASE
 * @since 1.8
 */
@Override
public Limit Limit(List<Index> indexList, Page page) {
	if (indexList.size() <= 0) return null;
	return PageInterface.getLimit(indexList, page);
}

}
