package com.ycyj.webpage.filter;

import java.io.File;

import org.apache.log4j.Logger;


/**
 * 使用Berkeley DB实现去重
 * @author 朱亮
 *
 */
public class DuplicationJEFilter implements DuplicationFilter{
	
	static Logger log = Logger.getLogger(DuplicationJEFilter.class);
	
	private DownloadedDAO dao;
	
	public DuplicationJEFilter () {
		String dbPath = "c:/jeTmp/";
		File f = new File (dbPath);
		
		
		if (!f.exists())
			f.mkdir();
		else {
			for (File fl : f.listFiles())		// 清空文件夹
				fl.delete();
		}
		
		
		dao = new DownloadedDAO (dbPath);
	}
	
//	public DuplicationJEFilter (String dbPath) {
		//TODO 怎么传参？
//		
//	}

	public boolean canPass(String input) {
		dao.clearOld();
		if (dao.contains(input)) {
			log.debug("a duplicated : " + input);
			return false;
		}
		
		return true;
	}

	public Boolean filter(String input) {
		dao.clearOld();
		if (dao.contains(input)) {
			log.debug("a duplicated : " + input);
			return false;
		}
		
		return true;
	}

	public boolean remove(String input) {
		return dao.remove(input);
	}

}
