package com.ycyj.webpage;

import java.util.*;

import com.ycyj.webpage.util.Regex;

public class Subdirectory extends Segment{

	List<Segment> segments = new ArrayList<Segment>();

	public Subdirectory(String name, int level, Regex regex) {
		super(name, level, regex);
	}
	
	public void addSegment(Segment s) {
		s.regex = new Regex(this.regex, s.regex);	// ���Լ���Regex����ֱ��������HTML�ı���
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
	 * ���ڿ��ǵ�Subdirectory������һ��ҳ�����ظ�����
	 * ���Բ����ڴ��������ƥ��
	 */
	@Override
	public void setWebPage (WebPage page){
		for (Segment s : segments)
			s.setWebPage(page);
	}

}
