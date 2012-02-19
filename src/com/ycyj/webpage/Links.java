package com.ycyj.webpage;

import java.io.IOException;
import java.lang.reflect.Constructor;
import java.text.SimpleDateFormat;
import java.util.*;
//import java.util.regex.*;

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
	
	public Links (Segment s) {
		super (s);
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
	 * 对抽取出来的一个链接，处理之
	 * 获取其对应的页面，迭代处理其子节点
	 * 如果该页面要分页，则相当于“拉长当前页”
	 */
	protected void processOne () {
		if (!filter ()) 
			return;
		
		String downloadURL = urlParser.parse(resultContent);
		WebPage page = downloadPage (downloadURL);
		
		processGettedPage (page);
		processNextPage (downloadURL, page);
	}
	
	
	protected boolean filter ()  {
		for (Filter f : filters) {
			if (f instanceof InterceptFilter) {
				InterceptFilter itf = (InterceptFilter) f;
				synchronized (itf) {
					if (!itf.filter(resultContent)) {
						return false;
					}
				}
			}
			
			else if (f instanceof ReplaceFilter) {
				ReplaceFilter rf = (ReplaceFilter)f;
				synchronized (rf) {
					resultContent = rf.filter(resultContent);
				}
			}
		}
		
		return true;
	}
	
	/**
	 * 将当前抽取出来的链接，下载到对应的网页
	 * @param downloadURL
	 * @return
	 */
	private WebPage downloadPage (String downloadURL)  {
		WebPage page;
		while (true) {
			try {
				page = WebPage.download(downloadURL);
				if (page != null) {
					log.debug("successfully download web page : " + page.url);
					return page;
				}
				else
					errorDownloading (downloadURL);
			} catch (IOException e) {
				errorDownloading (downloadURL);
			}
		}
	}
	
	private void processGettedPage (WebPage page) {
		
		//TODO : 这样是有线程危险的。。。？
		// 没有吧。。。在同一个配置文件中， 在该站点内深度优先采集，并没有多线程并发
//		page.segments = nextLevelPageSegments;
		
		try {
			page.segments = buildNextLeverSegments (nextLevelPageSegments);
		}catch (Exception e) {
			log.error("building next level segments error", e);
			throw new RuntimeException();
		}
		
		for (Segment s : page.segments)
			s.setWebPage(page);
		
//		log.error("building\t" + getTitle(page.html)+ "\t" + page.url  + "\t");
		page.process();
	}
	
	// TODO:
	// used for testing:
	// get the web page's title, display along with the page's URL
	// to find the duplications or errors
	
//	private String getTitle (String html) {
//		Matcher m = TITLE.matcher(html);
//		if (m.find())
//			return m.group(1);
//		return "";
//	}
//	
//	private static Pattern TITLE = Pattern.compile("(?i)<title>(.*?)</title>");
	
	@SuppressWarnings("unchecked")
	private List<Segment> buildNextLeverSegments (List<Segment> n) throws Exception	 {
		List<Segment> newBuilt = new ArrayList<Segment> (n.size());
		for (Segment s : n) {
			Constructor con = s.getClass().getConstructor(Segment.class);
			newBuilt.add ((Segment)con.newInstance(s));
		}
		
		return newBuilt;
	}
	
	
	private void processNextPage (String downloadURL, WebPage page) {
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
	
	private void errorDownloading (String downloadURL) {
		log.debug("error downloading " + downloadURL);
		log.debug("error downloading at " 
				+ new SimpleDateFormat("yyyy-MM-dd HH-mm-ss").format(new Date()));
		
		removeFromFilter (downloadURL);
		
		//TODO 下载出错的休眠等待
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
