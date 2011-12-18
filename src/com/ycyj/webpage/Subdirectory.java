package com.ycyj.webpage;

import java.util.*;

import com.ycyj.webpage.util.Regex;

public class Subdirectory extends Segment{

	List<Segment> segments = new ArrayList<Segment>();

	public Subdirectory(String name, int level, Regex regex) {
		super(name, level, regex);
	}
	
	public void addSegment(Segment s) {
		s.regex = new Regex(this.regex, s.regex);	// 它自己的Regex并不直接作用在HTML文本上
		segments.add(s);
	}

	@Override
	protected final void processOne() {}
	
	@Override
	public void process () {
		for (Segment s : segments)
			s.process();
	}

	/**
	 * 由于考虑到Subdirectory可能在一个页面中重复出现
	 * 所以并不在此完成正则匹配
	 */
	@Override
	public void setWebPage (WebPage page){
		for (Segment s : segments)
			s.setWebPage(page);
	}

}
