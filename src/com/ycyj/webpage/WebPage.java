package com.ycyj.webpage;

import java.io.*;
import java.util.*;

import net.htmlparser.jericho.Source;

import org.apache.commons.io.FileUtils;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.params.AllClientPNames;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.params.CoreProtocolPNames;
import org.apache.http.util.EntityUtils;
import org.apache.log4j.Logger;

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
		
		HttpClient httpclient = new DefaultHttpClient();
		httpclient.getParams().setParameter(CoreProtocolPNames.HTTP_CONTENT_CHARSET,"UTF-8"); 
		httpclient.getParams().setParameter(CoreProtocolPNames.USER_AGENT,"Mozilla/4.0 (compatible; MSIE 8.0; Windows NT 5.1; Trident/4.0;  Embedded Web Browser from: http://bsalsa.com/; .NET CLR 2.0.50727; InfoPath.2; CIBA; .NET CLR 3.0.04506.648; .NET CLR 3.5.21022; .NET CLR 3.0.4506.2152; .NET CLR 3.5.30729; msn OptimizedIE8;ZHCN; AskTB5.6)");
		httpclient.getParams().setParameter(AllClientPNames.SO_TIMEOUT, new Integer(10000));
		httpclient.getParams().setParameter(AllClientPNames.CONNECTION_TIMEOUT, new Integer(10000));
		
        try {
            HttpGet httpget = new HttpGet(url);

            // Execute HTTP request
            logger.debug("executing request " + httpget.getURI());
            HttpResponse response = httpclient.execute(httpget);

            logger.debug("----------------------------------------");
            logger.debug(response.getStatusLine());
            logger.debug("----------------------------------------");

            // Get hold of the response entity
            HttpEntity entity = response.getEntity();

            // If the response does not enclose an entity, there is no need
            // to bother about connection release
            if (entity != null) {
                InputStream instream = entity.getContent();
                try {
                	Source s = new Source(instream);
                	WebPage page = new WebPage(url, s.toString(), s.getEncoding());
        			EntityUtils.consume(entity);
        			return page;
                    // do something useful with the response
                } catch (IOException ex) {
                    // In case of an IOException the connection will be released
                    // back to the connection manager automatically
                    throw ex;
                } catch (RuntimeException ex) {
                    // In case of an unexpected exception you may want to abort
                    // the HTTP request in order to shut down the underlying
                    // connection immediately.
                    httpget.abort();
                    throw ex;
                } finally {
                    // Closing the input stream will trigger connection release
                    try { instream.close(); } catch (Exception ignore) {}
                }
            }
            else
            	return null;

        } finally {
            // When HttpClient instance is no longer needed,
            // shut down the connection manager to ensure
            // immediate deallocation of all system resources
            httpclient.getConnectionManager().shutdown();
        }
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
