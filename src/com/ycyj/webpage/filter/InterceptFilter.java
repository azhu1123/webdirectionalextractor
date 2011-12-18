package com.ycyj.webpage.filter;


/**
 * 拦截过滤器：为link和target配置。
 * 过滤掉不需要进一步处理的数据
 * 
 * @author 朱亮
 *
 */
public interface InterceptFilter extends Filter{
	
	/**
	 * @return  true	通过
	 * 			false	被过滤掉
	 */
	public Boolean filter (String input);
	
}
