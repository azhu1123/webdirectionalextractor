package com.ycyj.webpage.util;

import java.io.*;
import java.util.regex.*;

import org.apache.commons.io.FileUtils;

public class FindRegex {
	
	static String regex = 
		"(?is)<h1>(.*?)</h1>(?:<h2>(.*?)</h2>)?.*?" +
		"<p class=\"abstracts\">(.*?)</p>\\s*" +
		"<dl id=\"perildical_dl\">\\s*" +
		"<dt>作 者：</dt>\\s*<dd>(.*?)</dd>\\s*" +
		"<dt>作者单位：</dt>\\s*<dd>(.*?)</dd>\\s*" +
		"<dt>刊 名：</dt>\\s*<dd>(.*?)</dd>\\s*" +
		"<dt>英文刊名：</dt>\\s*<dd>.*?>(.*?)<.*?</dd>\\s*" +
		"<dt>年，卷\\(期\\)：</dt>\\s*<dd>.*?>(.*?)<.*?</dd>\\s*" +
		"<dt>分类号：</dt>\\s*<dd>(.*?)</dd>\\s*" +
		"<dt>关键词：</dt>\\s*<dd>(.*?)</dd>\\s*" +
		"<dt>机标分类号：</dt>\\s*<dd>(.*?)</dd>\\s*" +
		"<dt>机标关键词：</dt>\\s*<dd>(.*?)</dd>\\s*" +
		"<dt>基金项目：</dt>\\s*<dd>(.*?)</dd>\\s*" +
		"<dt>DOI：</dt>\\s*<dd>.*?>(.*?)<.*?</dd>\\s*" +
		"(.*?)</dl>";
	static int group = 15;
	
	static Pattern pattern = Pattern.compile(regex);
	
	static void test () throws Exception{
		String html = FileUtils.readFileToString(new File("./testWanfang/2.HTML"), "gb2312");
		Matcher m = pattern.matcher(html);
		
		if (m.find()) {
			for (int i = 1; i < group; i++) {
				System.out.println(m.group(i));
			}
			System.out.println("/***********************************************************************************************************/\n\n\n\n");
			System.out.println(m.group(group));
		}
		else
			System.out.println("not found");
	}
	
	public static void main(String[] args) throws Exception{
		test();
//		System.out.println(regex);
	}
	
	
}
