package com.ycyj.webpage;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.*;

import org.apache.log4j.Logger;

import com.ycyj.webpage.filter.*;
import com.ycyj.webpage.util.*;

public class Links extends Segment{
	
	static Logger log = Logger.getLogger(Links.class);
	
	private URLParser urlParser;
	
	private NextPager pager = null;
	
	public Links(String name,  int level, Regex regex) {
		super (name, level, regex);
	}
	
	public Links(String name,  int level, Regex regex, List<Filter> f) {
		super (name, level, regex, f);
	}
	
	public void setURLParser (URLParser p) {
		urlParser = p;
	}
	
	public void setPager (NextPager p) {
		this.pager = p;
	}
	
	public void addNextLevelSegment(Segment forNextLevel) {
		this.nextLevelPageSegments.add(forNextLevel);
	}

	private List<Segment> nextLevelPageSegments = new ArrayList<Segment>();

	@Override
	/**
	 * �Գ�ȡ������һ�����ӣ�����֮
	 * ��ȡ���Ӧ��ҳ�棬�����������ӽڵ�
	 * �����ҳ��Ҫ��ҳ�����൱�ڡ�������ǰҳ��
	 */
	protected void processOne () {
		if (!filter ()) 
			return;
		
		String downloadURL = urlParser.parse(resultContent);
		WebPage page = downloadPage (downloadURL);
		processGettedPage (page);
		
		if (pager == null)
			return;
		
		pager.start();
		for(String nextURL = pager.nextPageURL(page);
			nextURL != null;
			nextURL = pager.nextPageURL(page)) {
			downloadURL = urlParser.parse(nextURL);
			page = downloadPage (downloadURL);
			processGettedPage (page);
		}
	}
	
	protected boolean filter ()  {
		for (Filter f : filters) {
			if (f instanceof InterceptFilter) {
				InterceptFilter itf = (InterceptFilter) f;
				if (!itf.filter(resultContent)) {
					return false;
				}
			}
			
			else if (f instanceof ReplaceFilter) {
				ReplaceFilter rf = (ReplaceFilter)f;
				resultContent = rf.filter(resultContent);
			}
		}
		
		return true;
	}
	
	
	/**
	 * ����ǰ��ȡ���������ӣ����ص���Ӧ����ҳ
	 * @param downloadURL
	 * @return
	 */
	private WebPage downloadPage (String downloadURL)  {
		WebPage page;
		while (true) {
			try {
				page = StaticDownloader
						.download(downloadURL);
				if (page != null)
					return page;
				else
					errorDownloading (downloadURL);
			} catch (IOException e) {
				errorDownloading (downloadURL);
			}
		}
	}
	
	private void processGettedPage (WebPage page) {
		log.debug("successfully download web page : " + page.url);
		page.segments = nextLevelPageSegments;
		for (Segment s : nextLevelPageSegments)
			s.setWebPage(page);
		//TODO : ���������߳�Σ�յġ�����
		page.process();
	}
	
	private void errorDownloading (String downloadURL) {
		log.debug("error downloading " + downloadURL);
		log.debug("error downloading at " 
				+ new SimpleDateFormat("yyyy-MM-dd HH-mm-ss").format(new Date()));
		
		removeFromFilter (downloadURL);
		
		//TODO ���س�������ߵȴ�
//		try {
//			System.err.println("sleeping for 3 minutes...");
//			Thread.sleep(1000*60*3);	// waiting for 3 minutes 
//		} catch (InterruptedException e1) {
//			e1.printStackTrace();
//		} 
	}
	
	private void removeFromFilter (String downloadURL) {
		for (Filter f : filters) {
			if (f instanceof DuplicationFilter) {
				DuplicationFilter itf = (DuplicationFilter) f;
				itf.remove(downloadURL);
			}
		}
	}
}
