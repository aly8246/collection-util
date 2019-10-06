package com.github.aly8246.collectionutil.main;

import com.github.aly8246.collectionutil.core.impl.MysqlPageImpl;
import com.github.aly8246.collectionutil.exception.CollectionException;
import com.github.aly8246.collectionutil.result.PageResult;
import com.github.aly8246.collectionutil.model.Index;
import com.github.aly8246.collectionutil.model.Limit;
import com.github.aly8246.collectionutil.model.Page;
import com.github.aly8246.collectionutil.util.CollectionUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.ibatis.cache.CacheKey;
import org.apache.ibatis.executor.Executor;
import org.apache.ibatis.mapping.BoundSql;
import org.apache.ibatis.mapping.MappedStatement;
import org.apache.ibatis.plugin.*;
import org.apache.ibatis.session.ResultHandler;
import org.apache.ibatis.session.RowBounds;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.lang.reflect.Method;
import java.lang.reflect.Parameter;
import java.util.*;
import java.util.stream.Collectors;


@Intercepts(
        {
                @Signature(type = Executor.class, method = "query", args = {MappedStatement.class, Object.class, RowBounds.class, org.apache.ibatis.session.ResultHandler.class}),
                @Signature(type = Executor.class, method = "query", args = {MappedStatement.class, Object.class, RowBounds.class, ResultHandler.class, CacheKey.class, BoundSql.class}),
        }
)
@Service
@RequiredArgsConstructor
@Slf4j
public class CollectionInterceptor implements Interceptor {
    //TODO 先调用mysql处理器
    @Resource(name = "mysqlPageImpl")
    private MysqlPageImpl mysqlPage;

    /**
     * 存储分页返回结果
     *
     * @author 南有乔木
     * @version v.1.0.2
     * @see CollectionUtil
     */
    protected static ThreadLocal<PageResult> pageResultThreadLocal = new ThreadLocal<>();

    @Override
    public Object intercept(Invocation invocation) throws Throwable {
        log.info("Collection-util passive running!");
        Object[] args = invocation.getArgs();
        MappedStatement ms = (MappedStatement) args[0];
        Object sqlParamObj = args[1];
        Map<String, Object> sqlParamMap = (Map<String, Object>) sqlParamObj;
        RowBounds rowBounds = (RowBounds) args[2];
        ResultHandler resultHandler = (ResultHandler) args[3];
        Executor executor = (Executor) invocation.getTarget();
        CacheKey cacheKey;
        BoundSql boundSql;

        if (args.length == 4) {
            boundSql = ms.getBoundSql(sqlParamObj);
            cacheKey = executor.createCacheKey(ms, sqlParamObj, rowBounds, boundSql);
        } else {
            cacheKey = (CacheKey) args[4];
            boundSql = (BoundSql) args[5];
        }

        String className = this.getClassName(ms.getId());
        String methodName = this.getMethodName(ms.getId());

        Class<?> clazz = Class.forName(className);

        List<Method> methodList = Arrays.stream(clazz.getMethods())
                .filter(e -> e.getName().equals(methodName))
                .collect(Collectors.toList());
        if (methodList.size() > 0) {
            List<Parameter> parameterList = Arrays
                    .stream(methodList
                            .get(0)
                            .getParameters())
                    .collect(Collectors.toList());

            for (Parameter param : parameterList) {//如果参数中有分页类
                if (param.getType().getName().equals(Page.class.getName())) {
                    //需要分页
                    log.info("Start paging...");
                    log.debug("className:" + className);
                    log.debug("methodName:" + methodName);
                    Page page = this.getPageByParam(sqlParamMap);
                    log.debug("page:" + page);
                    List<Index> indexList = mysqlPage.getIndexList(ms, executor, sqlParamObj, boundSql, rowBounds, resultHandler);
                    log.debug("indexList:" + indexList);
                    Limit limit = mysqlPage.Limit(indexList, page);
                    log.debug("limit:" + limit);

                    if (limit != null)//数量满足条件，可以进行分页
                    {
                        List query = mysqlPage.query(ms, executor, sqlParamObj, boundSql, rowBounds, resultHandler, cacheKey, limit);
                        PageResult pageResult = new PageResult();
                        pageResult.setPage(page.getPage());
                        pageResult.setPageSize(page.getPageSize());
                        pageResult.setData(query);
                        pageResult.setTotalPage((indexList.size() + page.getPageSize() - 1) / page.getPageSize());
                        pageResult.setTotal(indexList.size());
                        pageResultThreadLocal.set(pageResult);
                        log.debug("success: " + pageResult);
                        return query;
                    }
                }
            }

        }

        return invocation.proceed();

    }


    @Override
    public Object plugin(Object target) {
        log.info("Collection-util active!");
        return Plugin.wrap(target, this);
    }

    @Override
    public void setProperties(Properties properties) {
    }

    /**
     * 从msId中获取执行类路径
     *
     * @param s msId
     * @return 执行类路径
     * @throws CollectionException 获取失败
     * @author 南有乔木
     */
    private String getClassName(String s) {
        if (s.length() <= 0)
            throw new CollectionException("Get class name error!please contact 1558146696@qq.com");
        List<String> pathList = Arrays
                .stream(s.split("\\."))
                .collect(Collectors.toCollection(LinkedList::new));
        pathList.remove(pathList.size() - 1);//移除方法名

        return String.join(".", pathList);
    }

    /**
     * 从msId中获取执行方法
     *
     * @param s msId
     * @return 执行方法
     * @throws CollectionException 获取失败
     * @author 南有乔木
     */
    private String getMethodName(String s) {
        if (s.length() <= 0)
            throw new CollectionException("Get method name error!please contact 1558146696@qq.com");
        List<String> pathList = Arrays
                .stream(s.split("\\."))
                .collect(Collectors.toCollection(LinkedList::new));

        return pathList.get(pathList.size() - 1);
    }

    /**
     * 获取Page对象，里面有分页信息
     *
     * @param o mapper接口上的所有参数
     * @return Page对象
     * @author 南有乔木
     */
    private Page getPageByParam(Map<String, Object> o) {
        System.out.println(o);
        Page page = new Page();
        for (Map.Entry<String, Object> entry : o.entrySet()) {
            try {
                page = (Page) entry.getValue();
                if (page != null) return page;
            } catch (Exception ignored) {
            }
        }
        return page;
    }
}
