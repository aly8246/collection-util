package com.github.aly8246.collectionutil.util;

import com.github.aly8246.collectionutil.core.PageInterface;
import com.github.aly8246.collectionutil.main.CollectionInterceptor;
import com.github.aly8246.collectionutil.result.PageResult;

import java.util.List;

/**
 * 为用户提供获取分页数据的方法
 *
 * @author 南有乔木
 * @version v.1.0.2
 * @see PageInterface
 * @see CollectionInterceptor
 */
public class CollectionUtil extends CollectionInterceptor {
    /**
     * 从本地线程中获取分页数据
     *
     * @return CollectionUtil工具的原始分页结果
     * @author 南有乔木
     */
    public static PageResult get() {
        return CollectionInterceptor.pageResultThreadLocal.get() == null ? new PageResult() : CollectionInterceptor.pageResultThreadLocal.get();
    }

    /**
     * 传递一个参数只是为了解决强迫症患者的问题，放进来实际上没有任何意义，只是代码可以放成一行而已
     *
     * @param o 可以为任意
     * @return CollectionUtil工具的原始分页结果
     * @author 南有乔木
     */
    public static PageResult get(Object o) {
        return get();
    }

    /**
     * 为方便前端使用，将返回结果打包成Mybatis Plus的返回结果，以便无缝切换CollectionUtil和Mybatis Plus分页，各司其职
     *
     * @return Mybatis Plus的返回结果
     * @author 南有乔木
     */
    public static IPage packMPResult() {
        PageResult pageResult = CollectionInterceptor.pageResultThreadLocal.get() == null ? new PageResult() : CollectionInterceptor.pageResultThreadLocal.get();
        return new IPage() {
            @Override
            public List getRecords() {
                return pageResult.getData();
            }

            @Override
            public IPage setRecords(List records) {
                return null;
            }

            @Override
            public long getTotal() {
                return pageResult.getTotal();
            }

            @Override
            public IPage setTotal(long total) {
                return null;
            }

            @Override
            public long getSize() {
                return pageResult.getPageSize();
            }

            @Override
            public IPage setSize(long size) {
                return null;
            }

            @Override
            public long getCurrent() {
                return pageResult.getPage();
            }

            @Override
            public IPage setCurrent(long current) {
                return null;
            }
        };
    }

    /**
     * 传递一个参数只是为了解决强迫症患者的问题，放进来实际上没有任何意义，只是代码可以放成一行而已
     *
     * @param o 可以为任意
     * @return CollectionUtil工具的原始分页结果
     * @author 南有乔木
     */
    public static IPage packMPResult(Object o) {
        return packMPResult();
    }


}
