package com.ycyj.webpage.util;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.nio.charset.Charset;
//import java.util.Date;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.zip.GZIPInputStream;

import com.ycyj.webpage.WebPage;

public class StaticDownloader {
	static final int size = 3291456;		// the buffer size is default set as 3M
	static final Pattern charsetPattern = Pattern.compile("(?i)charset=\"?([-_\\w]*)\"?");
	
	// for single thread model 
	static byte[] b = new byte[size];
	
	static int count = 0;
	
	public static WebPage download (String url) 
	throws MalformedURLException, UnsupportedEncodingException,IOException {
		HttpURLConnection conn = getConnection(url);
		if (HttpURLConnection.HTTP_OK != conn.getResponseCode()) {
			return null;
		}

//		long last = conn.getLastModified();
		String content_encoding = conn.getContentEncoding();
		String content_charset = getCharset (conn.getContentType());	
		
		synchronized (b) {
			int off = getResponseContent(conn, b, "gzip"
					.equalsIgnoreCase(content_encoding));

			String content = new String(b, 0, off, content_charset);
			String checkedCharset = doubleCheckCharsetInsideHTML(content,
					content_charset);
			if (checkedCharset != content_charset)
				content = new String(b, 0, off, checkedCharset);

			return new WebPage(url, content, checkedCharset);
		}
//		page.setLastModified(new Date(last));
//		page.setDownloadTime(new Date());
		
		
	}
	
	static void sleep () {
		if (count >= 10000)
			try {
				System.gc();
				Thread.sleep(1000*60*5);
				count = 0 ;
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		
		count++;
	}
	
	
	private static HttpURLConnection getConnection (String strURL)
	throws IOException{
		URL url = new URL(strURL);
		HttpURLConnection conn = (HttpURLConnection)url.openConnection();	
		setProperties (conn);
		conn.setConnectTimeout(3000);
		conn.setReadTimeout(3000);
		
		try{
			conn.connect();
		}catch (java.net.SocketTimeoutException e){
			throw new IOException();
		}
		return conn;
	}
	
	private static void setProperties (HttpURLConnection conn) {
		conn.setRequestProperty("Cookie","PREF=ID=50ecee25e6158be9:U=09591b7451d583a9:FF=1:LD=zh-CN:NW=1:TM=1276692359:LM=1282280648:S=yCeAMql2fQdx1TNf; NID=36=czadLRFDzxz0Gz9U1Mt7H-6jjdEZEFMABSm7T9cxLOTFpMea6ksmtJzUo2ipuaag4Ju9vgp57RwaN7VvK1sxatXE6LUxRxaI8LPAoNpqZWjFIEQH_AzXD6fk_B91CnIw");
		conn.setRequestProperty("Connection","keep-alive");
		conn.setRequestProperty("Accept-Encoding","gzip,deflate");
		conn.setRequestProperty("User-agent","Mozilla/4.0 (compatible; MSIE 8.0; Windows NT 5.1; Trident/4.0;  Embedded Web Browser from: http://bsalsa.com/; .NET CLR 2.0.50727; InfoPath.2; CIBA; .NET CLR 3.0.04506.648; .NET CLR 3.5.21022; .NET CLR 3.0.4506.2152; .NET CLR 3.5.30729; msn OptimizedIE8;ZHCN; AskTB5.6)");
		conn.setRequestProperty("Accept-Charset","GBK,utf-8;q=0.7,*;q=0.3");
		conn.setRequestProperty("Accept-Language","zh-cn");
		conn.setRequestProperty("Accept","application/xml,application/xhtml+xml,text/html;q=0.9,text/plain;q=0.8,image/png,*/*;q=0.5");
		conn.setRequestProperty("Cache-Control","max-age=0");
	}
	
	private static String getCharset (String _c_type) {
		if (_c_type != null) {
			Matcher matcher = charsetPattern.matcher(_c_type);
			if (matcher.find())
				return matcher.group(1);
		}
		return Charset.defaultCharset().name();
	}
	
	private static int getResponseContent (HttpURLConnection conn ,byte[] b, boolean isGzip) throws IOException{
		BufferedInputStream in;
		if (isGzip)
			in = new BufferedInputStream(new GZIPInputStream(conn.getInputStream()));
		else
			in = new BufferedInputStream(conn.getInputStream());
		
		int off = 0;
		int len = 0;
		while ((len = in.read(b, off, b.length - off)) >= 0) {
			off += len;
		}
		in.close();
		return off;
	}
	
	
	private static String doubleCheckCharsetInsideHTML (String content, String charsetInHeader) {
		Matcher matcher = charsetPattern.matcher(content);
		if (matcher.find()) {
			String charset = matcher.group(1);
			if (!charset.equalsIgnoreCase(charsetInHeader)) {
				return charset;
			}
		}
		return charsetInHeader;
	}

	
	
//	public static int fetchOther (String _url, byte[] b) 
//	throws MalformedURLException, UnsupportedEncodingException,IOException {
//		HttpURLConnection conn = getConnection(_url);
//		if (HttpURLConnection.HTTP_OK != conn.getResponseCode()) 
//			return -1;
//		
//		return getResponseContent(conn, b ,false);
//	}
}
