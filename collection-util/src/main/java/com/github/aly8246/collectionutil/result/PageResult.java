package com.github.aly8246.collectionutil.result;

import com.github.aly8246.collectionutil.main.CollectionInterceptor;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.util.List;

@EqualsAndHashCode(callSuper = true)
@Data
public class PageResult<T> extends CollectionInterceptor {
/**
 * 当前在第几页
 */
private int page;

/**
 * 每页的大小
 */
private int pageSize;

/**
 * 总共有多少数据
 */
private int total;

/**
 * 总共有多少页
 */
private int totalPage;

/**
 * 数据
 */
private List<T> data;
}
