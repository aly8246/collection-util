package com.github.aly8246.collectionutil.util;

import java.io.Serializable;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

public interface IPage<T> extends Serializable {
default String[] descs() {
	return null;
}

default String[] ascs() {
	return null;
}

default Map<Object, Object> condition() {
	return null;
}

default boolean optimizeCountSql() {
	return true;
}

default boolean isSearchCount() {
	return true;
}

default long offset() {
	return this.getCurrent() > 0L ? (this.getCurrent() - 1L) * this.getSize() : 0L;
}

default long getPages() {
	if (this.getSize() == 0L) {
		return 0L;
	} else {
		long pages = this.getTotal() / this.getSize();
		if (this.getTotal() % this.getSize() != 0L) {
			++pages;
		}
		
		return pages;
	}
}

default IPage<T> setPages(long pages) {
	return this;
}

List<T> getRecords();

IPage<T> setRecords(List<T> records);

long getTotal();

IPage<T> setTotal(long total);

long getSize();

IPage<T> setSize(long size);

long getCurrent();

IPage<T> setCurrent(long current);

default <R> IPage convert(Function<? super T, ? extends R> mapper) {
	List collect = this.getRecords().stream().map(mapper).collect(Collectors.toList());
	return this.setRecords(collect);
}
}
