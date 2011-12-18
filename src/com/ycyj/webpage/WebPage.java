package com.ycyj.webpage;

import java.io.*;
import java.util.*;

import org.apache.commons.io.FileUtils;
import org.apache.log4j.Logger;


import com.ycyj.webpage.util.*;


/**
 * 封装一个网页HTMl文档
 * 
 * 一个WebPage要被解析成若干个Segment
 * 
 * 一个Segment对应要从该网页HTML文档中抽取出来的“一类”信息
 * 一个Segment的处理过程需要设置Regex（抽取）和Filter（过滤）
 * 
 * @author 朱亮
 * 
 */
public class WebPage {
	
	static Logger logger = Logger.getLogger(WebPage.class);
	String url;
	String originalCharset;
	String html;
	
	List<Segment> segments = new ArrayList<Segment>();
	
	protected WebPage() {
		throw new AssertionError("cannot initial this class"); 
	}
	
	public WebPage(String url, String html, String originalCharset) {
		this.url = url;
		this.originalCharset = originalCharset;
		this.html = html;
	}
	
	public void addSegment (Segment seg) {
		seg.setWebPage(this);
		segments.add(seg);
	}
	
	public void process () {
		for (Segment seg : segments) 
			seg.process();
	}


	public static WebPage download(String url) throws IOException{
		logger.debug("downloading.... "+ url);
		return StaticDownloader.download(url);
	}
	
	public static void main(String[] args) throws Exception {
		//old_test ();
		testDownload ();
		
	}
	
	private static void testDownload () throws Exception {
		WebPage p = download("http://www.tianya.cn/publicforum/articleslist/0/free.shtml");
		FileUtils.writeStringToFile(new File("c:/1.html"), p.html, p.originalCharset);
	}
}
