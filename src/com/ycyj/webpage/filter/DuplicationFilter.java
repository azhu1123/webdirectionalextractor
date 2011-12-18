package com.ycyj.webpage.filter;


/**
 * URL下载去重过滤器
 * @author 朱亮
 *
 */
public interface DuplicationFilter extends InterceptFilter{
	
	/**
	 * 当某个URL下载失败的时候，调用此函数，将此URL从已下载列表中去除
	 * @param input
	 * @return
	 */
	public boolean remove (String input);

}
