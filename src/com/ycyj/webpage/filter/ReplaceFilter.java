package com.ycyj.webpage.filter;

public interface ReplaceFilter extends Filter{

	public String filter (String input);
	
	/**
	 * 主要是给target的过滤时，指向target的捕获组中的第几个需要被过滤处理
	 * @param group
	 */
	public void setGroup(int group);
	
	public int getGroup ();
	
}
