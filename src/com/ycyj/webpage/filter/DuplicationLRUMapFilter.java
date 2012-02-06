package com.ycyj.webpage.filter;

import org.apache.commons.codec.digest.DigestUtils;
import org.apache.commons.collections.map.LRUMap;
import org.apache.log4j.Logger;

public class DuplicationLRUMapFilter implements DuplicationFilter{
	private static Logger log = Logger.getLogger(DuplicationLRUMapFilter.class);
	
	LRUMap map = new LRUMap(5000, 0.8f);
	
	public DuplicationLRUMapFilter () {
		log.warn("initializing a LRU Map filter.");
	}

	public boolean remove(String input) {
		synchronized (map) {
			return map.remove(getKey(input)) == null;
		}
	}

	private static final Object VALUE = new Object();
	
	
	public Boolean filter(String input) {
		String key = getKey(input);
		
		synchronized (map) {
			if (map.containsKey(key)) {
				log.debug("duplicated:\t" + input);
				return false;
			}
			else {
				map.put(key, VALUE);
				log.debug("newly download" + input);
//				synchronized (urls) {
//					if (urls.contains(input))	// 应该被过滤掉
//						log.error("error filtered :\t"+input);
//					else
//						urls.add(input);
//				}
				
				return true;
			}
		}
	}
	
//	private static Set<String> urls = new HashSet<String>();
	
	/**
	 * 使用MD5以减少因为字符串不变性导致的内存泄露
	 */
	private String getKey (String input) {
		return DigestUtils.md5Hex(input);
	}
	
	
	public static void main (String[] args) {
		DuplicationLRUMapFilter d = new DuplicationLRUMapFilter();
		
		String in1 = "dabc".substring(1);
		String s1 = d.getKey(in1);
		System.out.println(s1);
		
		String in2 = "abc";
		String s2 = d.getKey(in2);
		System.out.println(s2);
		
		
		System.out.println(d.filter(in1));
		System.out.println(d.filter(in2));
	}

}
