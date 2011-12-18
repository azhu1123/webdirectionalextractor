package com.ycyj.webpage.filter;

import java.util.*;
import java.util.regex.*;

public class ConfiguredFilterFactory {
	
	enum Type {
		intercept,
		replace,
		classPath,
	}
	
	private static Map<String, Filter> filters = new HashMap<String, Filter>();
	
	public static Filter product (String type,String replaceReg, String replacement, String group, String classPath) {
		switch (Type.valueOf(type)) {
		case intercept:
			return new InterceptFilterImpl(replaceReg);
		
		case replace:
			return new ReplacementFilterImpl(replaceReg, replacement, Integer.parseInt(group));
		
		case classPath:
			try {
				if (filters.containsKey(classPath))
					return filters.get(classPath);
				
				Object filterObject = Class.forName(classPath).newInstance();
				if (filterObject instanceof Filter) {
					Filter filter = (Filter)filterObject;
					filters.put(classPath, filter);
					return filter;
				}
				
				else
					throw new RuntimeException("wrong class path : " + classPath);
			} catch (Exception e) {
				e.printStackTrace();
				throw new RuntimeException("wrong class path : " + classPath);
			}
		default:
			throw new UnsupportedOperationException();
		}
	}
	
	static class InterceptFilterImpl implements InterceptFilter {
		Pattern p;
		
		public InterceptFilterImpl (String r) {
			p = Pattern.compile(r);
		}

		public Boolean filter(String input) {
			return !p.matcher(input).matches();
		}

		public boolean remove(String input) {
			return true;
		}
	}
	
	static class ReplacementFilterImpl implements ReplaceFilter{
		int group;
		String replaceReg;
		String replacement;
		 
		public ReplacementFilterImpl (String r, String replacement, int g) {
			replaceReg = r;
			this.replacement = replacement;
			setGroup(g);
		}

		public String filter(String input) {
			return input.replaceAll(replaceReg, replacement);
		}

		public int getGroup() {
			return group;
		}

		public void setGroup(int g) {
			group = g;
		}

	}
}
