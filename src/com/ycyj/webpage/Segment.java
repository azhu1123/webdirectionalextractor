package com.ycyj.webpage;

import java.util.List;

import com.ycyj.webpage.util.*;
import com.ycyj.webpage.filter.*;

/**
 * һ����ҳƬ��
 * 
 * ����һ��������HTML�ĵ���������һЩSegment���
 * Ϊ�˷���һ��HTMl�ĵ��� ��Ҫ�����ɺ���Ӧ��Segment��Ȼ����װ����
 * 
 * ���ڳ�ȡ������һ�����ӣ����ڸ����ӽ��õ�һ��HTML�ĵ�������ҲӦ�������ɺ�����Segment
 * 
 * ���ԣ�����һ����ȡ���񣬾���Ҫ�ӵײ㿪ʼ�����Ľ�����Segment��Ȼ��ƴװ��һ����ҳ��
 * 
 * @author ����
 *
 */
public abstract class Segment implements Cloneable{
	
		
	/**
	 * һ��Segment������ 
	 */
	public enum Type {
		link, 
		subdirectory, 
		target, 
	}
	
	protected String name;		// ���Ƭ�ε�����
	protected int level;		// ���Ƭ������ҳ������
	protected String pageHTML;	// ���Ƭ�����ڵ���ҳ����ֻ��Ҫ��HTML�ı�
	protected String url;		// ���Ƭ�����ڵ���ҳ��URL
	
	protected Regex regex;		// ���Ƭ�δ���ҳ�г�ȡ����������
	
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
		// ���еĹ�������������Regex����regex����Ϊnull
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
