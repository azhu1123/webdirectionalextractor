package com.ycyj.webpage.filter;

public interface ReplaceFilter extends Filter{

	public String filter (String input);
	
	/**
	 * ��Ҫ�Ǹ�target�Ĺ���ʱ��ָ��target�Ĳ������еĵڼ�����Ҫ�����˴���
	 * @param group
	 */
	public void setGroup(int group);
	
	public int getGroup ();
	
}
