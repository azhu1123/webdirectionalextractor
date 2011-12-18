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
	 * 再获取到第一页的HTML文本之后，开始自动翻页，将后续页上抽取出来的Segment合并进来
	 * 类似于Firefox的某个翻页工具
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
