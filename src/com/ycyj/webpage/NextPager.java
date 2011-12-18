package com.ycyj.webpage;

import com.ycyj.webpage.util.Regex;

public class NextPager {
	
	String firstHTML;
	int max;
	int count;
	Regex regex;
	
	public NextPager (String reg, int max) {
		this.regex = new Regex (reg, 1);
		this.max = max;
	}
	
	
	/**
	 * �ٻ�ȡ����һҳ��HTML�ı�֮�󣬿�ʼ�Զ���ҳ��������ҳ�ϳ�ȡ������Segment�ϲ�����
	 * ������Firefox��ĳ����ҳ����
	 */
	public void start () {
		count = 1;
	}
	
	
	public String nextPageURL (WebPage cur) {
		if (count++ > max)
			return null;
		
		regex.setInput(cur.html);
		return regex.next();
	}
	
	
	
	
	
	
	

}
