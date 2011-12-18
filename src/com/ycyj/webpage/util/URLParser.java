package com.ycyj.webpage.util;

public class URLParser {
	
	String baseURL;
	
	public URLParser (String base) {
		String end = "";
		if (!base.endsWith("/"))
			end = "/";
		
		this.baseURL = base + end;
	}
	
	public String parse (String original) {
		if (original.startsWith("http://")
				|| original.startsWith(baseURL))
			return original;
		else {
			if (original.startsWith("/"))
				return baseURL + original.substring(1);
			else
				return baseURL + original;
				
		}
			
	}
}
