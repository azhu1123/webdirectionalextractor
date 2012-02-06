package com.ycyj.webpage;

import java.util.List;

import com.ycyj.webpage.util.*;
import com.ycyj.webpage.filter.*;

/**
 * 一个网页片段
 * 
 * 对于一个完整的HTML文档，都是由一些Segment组成
 * 为了分析一个HTMl文档， 需要先生成好相应的Segment，然后组装起来
 * 
 * 对于抽取出来的一个链接，由于该链接将得到一个HTML文档，所以也应该先生成好它的Segment
 * 
 * 所以，对于一个抽取任务，就是要从底层开始，逐层的建立好Segment，然后拼装成一个网页。
 * 
 * @author 朱亮
 *
 */
public abstract class Segment implements Cloneable{
	
		
	/**
	 * 一个Segment的类型 
	 */
	public enum Type {
		link, 
		subdirectory, 
		target, 
	}
	
	protected String name;		// 这个片段的名字
	protected int level;		// 这个片段所在页面的深度
	protected String pageHTML;	// 这个片段所在的网页——只需要其HTML文本
	protected String url;		// 这个片段所在的网页的URL
	
	protected Regex regex;		// 这个片段从网页中抽取出来的正则
	
	protected List<Filter> filters;
	
	public Segment(String name,  int level, Regex regex) {
		super();
		this.name = name;
		this.level = level;
		this.regex = regex;
	}
	
	public Segment(String name,  int level, Regex regex, List<Filter> f) {
		this(name, level, regex);
		this.filters = f;
	}
	
	
	public Segment (Segment o) {
		this(o.name, o.level, o.regex);
		this.filters = o.filters;			// the filter is single instance in one application
	}
		
	
	public void setWebPage (WebPage page){
		this.pageHTML = page.html;
		this.url = page.url;
		regex.setInput(pageHTML);
		// 所有的构造器都传入了Regex，即regex不会为null
	}
	
	protected String resultContent;
	
	protected abstract void processOne();
	
	public void process () {
		while ((resultContent = regex.next()) != null) {
//TODO:		filter.isValid();
			processOne();
		}
	}
	

//	public void setFilter(Filter filter) {
////		this.filter = filter;
//	}


	public static void main(String[] args) {
		Segment s = new Links ("",0,null);
		System.out.println(s.getClass().getName());
	}
	

}
