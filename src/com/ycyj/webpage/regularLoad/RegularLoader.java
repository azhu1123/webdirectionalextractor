package com.ycyj.webpage.regularLoad;

import java.io.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.Logger;
import org.dom4j.*;
import org.dom4j.io.SAXReader;

import com.ycyj.webpage.*;
import com.ycyj.webpage.filter.*;
import com.ycyj.webpage.processor.ExtractedProcessor;
import com.ycyj.webpage.util.Regex;
import com.ycyj.webpage.util.URLParser;

public class RegularLoader {
	
	static Logger log = Logger.getLogger(RegularLoader.class);
	
	String rootURL;
	WebPage rootPage;
	URLParser urlParser;
	
	public RegularLoader (String xmlPath)  throws Exception{
		configured(new File(xmlPath));
	}
	
	public RegularLoader (File xmlFile)  throws Exception{
		configured(xmlFile);
	}
	
	/**
	 * parse the XML file to generate a WebPage object
	 * @param xml
	 * @return
	 */
	private void configured (File file) throws Exception{
		Document xml;
		try {
			xml = new SAXReader().read(file);
		}catch (DocumentException e) {
			log.error("解析XML文件出错");
			throw e;
		}
		Element root = xml.getRootElement();
		this.rootURL = root.attributeValue("url");
		rootPage = downloadWebPage (this.rootURL);
		this.urlParser = new URLParser(root.attributeValue("base"));
		
		for(Element child : root.elements("segment")) {
			rootPage.addSegment(parse(child, 0));
		}
		
		log.debug("configured successfully: " + this.rootURL);
	}
	
	public void start () {
		rootPage.process();
	}
	
	/**
	 * parse each element in the XML file to generate the Segment object
	 * @param e			an element of a file
	 * @param level		the level of current element
	 * @return
	 */
	private Segment parse (Element e, int level) {
		String name = e.attributeValue("name");
		
		// 加入了Subdirectory，应该是不需要用上一级Regex了吧。。。
		Regex curRegex = getRegex(e.attributeValue("regex"),
				e.attributeValue("group"));
		
		List<Filter> filters = getFilters(e);
		 		
		String type = e.attributeValue("type");
		switch (Segment.Type.valueOf(type)) {		
		case subdirectory:				// 只是为下一级提供了一个Regex，但是这里可能会有多个Segment呢！
			Subdirectory dir = new Subdirectory(name, level, curRegex);
			for (Element child : e.elements("segment")) {
				dir.addSegment(parse(child, level + 1));
			}
			return dir;
			
			
		case link:						// 抽取出一个链接，并加入对应WebPage的Segment
			Links links = new Links (name, level, curRegex, filters);
			links.setURLParser(urlParser);
			
			String pageRex = e.attributeValue("pager");
			String strMax = e.attributeValue("max");
			NextPager pager = null;
			if (StringUtils.isNotBlank(pageRex) && StringUtils.isNotBlank(strMax))
				pager = new NextPager(pageRex, Integer.parseInt(strMax));
			links.setPager(pager);
			
			for (Element child : e.elements("segment")) {
				links.addNextLevelSegment(parse(child, level + 1));
			}
			return links;
			
			
		case target:					// 最后抽取的目标了，直接生成之即可
			List<ExtractedProcessor> ps = getProcessor(e);
			return new ExtractedText(name, level, curRegex, ps, filters);
			
		default:
			return null;
		}
	}
	
	private List<Filter> getFilters (Element curElement) {
		List<Filter> filters = new ArrayList<Filter>(0);
		for (Element eFilter : curElement.elements("filter")) {
			filters.add(parseFilter(eFilter));
		}
		
		return filters;
	}
	
	private Filter parseFilter (Element e) {
		return ConfiguredFilterFactory.product(e.attributeValue("type")
				, e.attributeValue("replaceReg")
				, e.attributeValue("replacement")
				, e.attributeValue("group")
				, e.attributeValue("classPath"));
	}
	
	private List<ExtractedProcessor> getProcessor (Element target) {
		List<ExtractedProcessor> ps = new ArrayList<ExtractedProcessor>();
		for (Element e : target.elements("processor"))
			ps.add (parseProcessor (e));
		
		return ps;
	}
	
	private ExtractedProcessor parseProcessor (Element p) {
		ExtractedProcessor processor = newProcessor(p);
		processor.init(parseParameters(p));
		return processor;
	}
	
	@SuppressWarnings("unchecked")
	private ExtractedProcessor newProcessor (Element p) {
		try {
			return ((Class<ExtractedProcessor>)Class.forName(p.attributeValue("classPath"))).newInstance();
		}  catch (Exception e) {
			RuntimeException re = new RuntimeException();
			re.initCause(e);
			throw re;
		}
	}
	
	private Map<String, String> parseParameters (Element p) {
		Map<String, String> prm = new HashMap<String, String>();
		for (Element e : p.elements("param")) 
			prm.put(e.element("param-name").getTextTrim(), e.element("param-value").getTextTrim());
		
		return prm;
	}
	
	protected void checkDocument () {
		// TODO: 如何检查文档是否符合规范？
	}
	
	private WebPage downloadWebPage (String url) throws IOException {
		// TODO : 加入重试机制
		try {
			return WebPage.download(url);
		} catch (IOException e) {
			log.error("根节点网页下载失败 :\t" + url);
			throw e;
		}
	}
	
	private Regex getRegex(String reg, String group) {
		return new Regex(reg, Integer.valueOf(group));
	}
}
