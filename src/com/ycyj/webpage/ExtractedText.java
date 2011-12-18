package com.ycyj.webpage;

import java.util.*;

import com.ycyj.webpage.filter.*;
import com.ycyj.webpage.processor.ExtractedProcessor;
import com.ycyj.webpage.util.Regex;



public class ExtractedText extends Segment{
	
	/**
	 * 抽取出来的文本的处理类
	 */
	List<ExtractedProcessor> processors;
	
	public ExtractedText(String name,  int level, Regex regex) {
		super (name, level, regex);
	}
	
	
	public ExtractedText(String name,  int level, Regex regex, List<ExtractedProcessor> ps) {
		super (name, level, regex);
		setProcessors(ps);
	}
	
	public ExtractedText(String name,  int level, Regex regex , List<ExtractedProcessor> ps, List<Filter> f) {
		super (name, level, regex, f);
		setProcessors(ps);
	}
	
	/**
	 * 设置处理类
	 */
	public void setProcessors(List<ExtractedProcessor> ps) {
		processors = ps;
	}
	
	/**
	 * 为当前的抽取结果增加一个处理类
	 */
	public void addProcessor(ExtractedProcessor pro) {
		processors.add(pro);
	}
	
	
	// over ride the parent's resutlContent
	List<String> resultContent = new ArrayList<String>();
	@Override
	public void process() {
		while (true) {
			regex.next(regex.getGroup(), resultContent);
			if (this.resultContent.size() == 0)
				break;
			
			resultContent.add(this.url);	//TODO 在最后把该片段的URL加上？
			processOne();
		}
	}

	@Override
	protected void processOne() {
		if (filter())
			for (ExtractedProcessor p : processors)
				p.process(resultContent);
	}
	
	static final int filterIndex = 0; // 过滤器只针对抽取文本的第一个捕获组起作用
	protected boolean filter () {
		for (Filter f : filters) {
			if (f instanceof InterceptFilter) {
				InterceptFilter itf = (InterceptFilter) f;
				if (!itf.filter(resultContent.get(filterIndex)))
					return false;
			}
			
			else if (f instanceof ReplaceFilter) {
				ReplaceFilter rf = (ReplaceFilter)f;
				int index = rf.getGroup() - 1;
				resultContent.set(index, rf.filter(resultContent.get(index)));
			}
		}
		
		return true;
	}
	
	@Override
	public String toString() {
		StringBuilder sb = new StringBuilder();
		sb.append(this.name + "\t");
		for (String str : this.resultContent) {
			sb.append(str + " ");
		}
		
		return sb.toString();
	}
	
//	private static final ThreadLocal<Map<Class<? extends ExtractedProcessor>, ExtractedProcessor>> localProcessors = 
//		new ThreadLocal<Map<Class<? extends ExtractedProcessor>, ExtractedProcessor>>();
	
	/**
	 * 维护一个线程本地变量
	 * 使得在当前线程中，配置中指定的处理器的class，在当前线程中只会有一个副本。
	 * 
	 * @param proClass
	 * @return 当前线程中的、配置文件中指定的处理类的引用
	 */
//	private ExtractedProcessor getLocalProcessor (Class proClass) {
//		Map<Class<? extends ExtractedProcessor>, ExtractedProcessor> localMap
//			=localProcessors.get();
//		if (localMap == null)
//			localMap = new HashMap<Class<? extends ExtractedProcessor>, ExtractedProcessor>();
//		
//		ExtractedProcessor pro = localMap.get(proClass);
//		if (pro == null) {
//			try {
//				pro = (ExtractedProcessor) proClass.newInstance();
//				localMap.put(proClass, pro);
//			} catch (Exception e) {
//				RuntimeException re = new RuntimeException("获取ExtractedProcessor出错");
//				re.initCause(e);
//				throw re;
//			}
//		}
//		
//		localProcessors.set(localMap);
//		return pro;
//	}
	
	
}
