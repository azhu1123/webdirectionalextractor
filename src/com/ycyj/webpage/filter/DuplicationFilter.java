package com.ycyj.webpage.filter;


/**
 * URL����ȥ�ع�����
 * @author ����
 *
 */
public interface DuplicationFilter extends InterceptFilter{
	
	/**
	 * ��ĳ��URL����ʧ�ܵ�ʱ�򣬵��ô˺���������URL���������б���ȥ��
	 * @param input
	 * @return
	 */
	public boolean remove (String input);

}
