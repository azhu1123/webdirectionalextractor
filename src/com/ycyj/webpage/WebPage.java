package com.ycyj.webpage;

import java.io.*;
import java.util.*;

import org.apache.commons.io.FileUtils;
import org.apache.log4j.Logger;


import com.ycyj.webpage.util.*;


/**
 * ��װһ����ҳHTMl�ĵ�
 * 
 * һ��WebPageҪ�����������ɸ�Segment
 * 
 * һ��Segment��ӦҪ�Ӹ���ҳHTML�ĵ��г�ȡ�����ġ�һ�ࡱ��Ϣ
 * һ��Segment�Ĵ��������Ҫ����Regex����ȡ����Filter�����ˣ�
 * 
 * @author ����
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
