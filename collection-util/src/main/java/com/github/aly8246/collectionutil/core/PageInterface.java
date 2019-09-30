package com.github.aly8246.collectionutil.core;

import com.github.aly8246.collectionutil.core.impl.MysqlPageImpl;
import com.github.aly8246.collectionutil.main.CollectionInterceptor;
import com.github.aly8246.collectionutil.model.Index;
import com.github.aly8246.collectionutil.model.Limit;
import com.github.aly8246.collectionutil.model.Page;
import org.apache.ibatis.cache.CacheKey;
import org.apache.ibatis.executor.Executor;
import org.apache.ibatis.mapping.BoundSql;
import org.apache.ibatis.mapping.MappedStatement;
import org.apache.ibatis.session.ResultHandler;
import org.apache.ibatis.session.RowBounds;

import java.sql.SQLException;
import java.util.Iterator;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

/**
 * 核心接口
 * <pre>
 * 为数据库方言提供sql字段解析，查询数量sql拼接和动态LIMIT核心算法实现
 * </pre>
 *
 * @author 南有乔木
 * @version v.1.0.2
 * @see MysqlPageImpl
 * @see CollectionInterceptor
 */
public interface PageInterface {

    /**
     * 获取id结果集
     *
     * @param ms            MappedStatement
     * @param executor      执行器
     * @param parameter     参数
     * @param boundSql      原始sql
     * @param rowBounds     原始分页
     * @param resultHandler 返回结果处理器
     * @return id结果集
     * @author 南有乔木
     */
    List<Index> getIndexList(MappedStatement ms, Executor executor,
                             Object parameter, BoundSql boundSql,
                             RowBounds rowBounds, ResultHandler resultHandler) throws SQLException;

    /**
     * 根据分页包装结果来查询真正分页需要的数据
     *
     * @param ms            MappedStatement
     * @param executor      执行器
     * @param parameter     参数
     * @param boundSql      原始sql
     * @param rowBounds     原始分页
     * @param resultHandler 返回结果处理器
     * @param cacheKey      缓存
     * @param limit         分页包装结果
     * @return 查询结果数据
     * @author 南有乔木
     */
    List query(MappedStatement ms, Executor executor,
               Object parameter, BoundSql boundSql,
               RowBounds rowBounds, ResultHandler resultHandler,
               CacheKey cacheKey, Limit limit) throws SQLException;


    /**
     * 根据分页条件和查询结果集来计算limit
     *
     * @param indexList id结果集
     * @param page      分页对象
     * @return 分页包装结果
     * @author 南有乔木
     */
    Limit Limit(List<Index> indexList, Page page);


    /**
     * 根据分页条件和查询结果集来计算limit
     *
     * @param indexList id结果集
     * @return id结果集(已初始化)
     * @author 南有乔木
     */
    static List<Index> init(List<Index> indexList) {
        //第一个index的开始一定为0，结束为0到total
        //第二个index的开始为上一个index的end，结束为total+自己的start
        Iterator<Index> iterator = indexList.iterator();
        Index temp = new Index();
        while (iterator.hasNext()) {
            Index next = iterator.next();
            Index pre = temp;
            if (next.getIndex() == 1) {
                next.setStart(0);
                next.setEnd(next.getTotal());
            } else {
                next.setStart(pre.getEnd());
                next.setEnd(next.getStart() + next.getTotal());
            }
            temp = next;
        }
        return indexList;
    }


    /**
     * 根据分页条件和查询结果集来计算limit
     *
     * @param indexList id结果集
     * @param page      分页对象
     * @return 分页包装结果
     * @author 南有乔木
     */
    static Limit getLimit(List<Index> indexList, Page page) {
        if (page.getPage() == 0) page.setPage(1);
        if (page.getPageSize() == 0) page.setPage(10);
        //TODO 是否开启分页数据保护 还是返回空
        //if (page.getPage() > indexList.size()) page.setPage(indexList.size());
        if (page.getPage() > indexList.size()) return null;

        if (page.getPageSize() == 1) {
            Index index = indexList.get(page.getPage() - 1);
            return new Limit(index.getStart(), index.getTotal());
        } else {

            int end = (page.getPage() * page.getPageSize());
            int start = end - page.getPageSize();
            List<Index> tempList = IntStream
                    .range(start, end)
                    .filter(i -> i < indexList.size())
                    .mapToObj(indexList::get)
                    .collect(Collectors.toList());

            //limit等于list里第一个数据的开始到最后一个数据的结束
            if (tempList.size() == 1)
                return new Limit(tempList.get(0).getStart(), tempList.get(0).getTotal());
            else if (tempList.size() > 1) {
                return new Limit(tempList.get(0).getStart(), tempList.stream().mapToInt(Index::getTotal).sum());
            }
        }
        //无分页数据
        return null;
    }
}
