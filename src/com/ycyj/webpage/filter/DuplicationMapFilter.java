package com.ycyj.webpage.filter;

import java.util.*;

import org.apache.log4j.Logger;
import org.apache.commons.codec.digest.DigestUtils;


/**
 * ʹ��java.util.Set��ʵ�ִ����ص��ַ�����ȥ��
 * @author ����
 *
 */
public class DuplicationMapFilter implements DuplicationFilter{
	
	static Logger logger = Logger.getLogger(DuplicationMapFilter.class);
	
	Map<byte[], Date> map = new TreeMap<byte[], Date>();
	
	public Boolean filter(String input) {
		if (input == null)
			return false;
		String str = input.trim();
		if (str.length() == 0)
			return false;
		
		boolean result = true;
		if (map.containsKey(str)) {
			result =  false;
			logger.debug("find a duplicated one : "+ str);
		}
		
		map.put(DigestUtils.md5(str), new Date());
		return result;
	}


	public boolean remove(String input) {
		map.remove(DigestUtils.md5(input));
		return true;
	}
	
	

}
