package com.github.aly8246.collectionutil.util;

/**
 * @Author ：南有乔木
 * @Email ：1558146696@qq.com
 * @date ：Created in 2019/10/10 上午 11:56
 * @description：
 * @version: ：V
 */
public class StdOutImpl extends org.apache.ibatis.logging.stdout.StdOutImpl {
public StdOutImpl(String clazz) {
	super(clazz);
}

@Override
public boolean isDebugEnabled() {
	return super.isDebugEnabled();
}

@Override
public boolean isTraceEnabled() {
	return super.isTraceEnabled();
}

@Override
public void error(String s, Throwable e) {
	super.error(s, e);
}

@Override
public void error(String s) {
	super.error(s);
}

@Override
public void debug(String s) {
	super.debug(s);
}

@Override
public void trace(String s) {
	super.trace(s);
}

@Override
public void warn(String s) {
	super.warn(s);
}
}
