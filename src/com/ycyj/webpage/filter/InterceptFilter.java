package com.ycyj.webpage.filter;


/**
 * ���ع�������Ϊlink��target���á�
 * ���˵�����Ҫ��һ�����������
 * 
 * @author ����
 *
 */
public interface InterceptFilter extends Filter{
	
	/**
	 * @return  true	ͨ��
	 * 			false	�����˵�
	 */
	public Boolean filter (String input);
	
}
