package com.ycyj.webpage;

import java.util.*;

import com.ycyj.webpage.filter.*;
import com.ycyj.webpage.processor.ExtractedProcessor;
import com.ycyj.webpage.util.Regex;



public class ExtractedText extends Segment{
	
	/**
	 * ��ȡ�������ı��Ĵ�����
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
	 * ���ô�����
	 */
	public void setProcessors(List<ExtractedProcessor> ps) {
		processors = ps;
	}
	
	/**
	 * Ϊ��ǰ�ĳ�ȡ�������һ��������
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
			
			resultContent.add(this.url);	//TODO �����Ѹ�Ƭ�ε�URL���ϣ�
			processOne();
		}
	}

	@Override
	protected void processOne() {
		if (filter())
			for (ExtractedProcessor p : processors)
				p.process(resultContent);
	}
	
	static final int filterIndex = 0; // ������ֻ��Գ�ȡ�ı��ĵ�һ��������������
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
	 * ά��һ���̱߳��ر���
	 * ʹ���ڵ�ǰ�߳��У�������ָ���Ĵ�������class���ڵ�ǰ�߳���ֻ����һ��������
	 * 
	 * @param proClass
	 * @return ��ǰ�߳��еġ������ļ���ָ���Ĵ����������
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
//				RuntimeException re = new RuntimeException("��ȡExtractedProcessor����");
//				re.initCause(e);
//				throw re;
//			}
//		}
//		
//		localProcessors.set(localMap);
//		return pro;
//	}
	
	
}
