package com.ycyj.webpage.filter;

import java.util.*;

import org.apache.commons.codec.digest.DigestUtils;
import org.apache.log4j.Logger;

public class DuplicationMD5Filter  implements DuplicationFilter{
	Logger log = Logger.getLogger(DuplicationMD5Filter.class);
	Set<String> downloaded;
	
	public DuplicationMD5Filter () {
		log.warn("instance a MD5 filter");
		downloaded = new HashSet<String>(1000);
	}

	public synchronized boolean remove(String input) {
		downloaded.remove(DigestUtils.md5Hex(input));
		return true;
	}

	public synchronized Boolean filter(String input) {
		return downloaded.add(DigestUtils.md5Hex(input));
	}
}
