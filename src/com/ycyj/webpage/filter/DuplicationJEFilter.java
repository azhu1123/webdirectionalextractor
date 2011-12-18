package com.ycyj.webpage.filter;

import java.io.File;

import org.apache.log4j.Logger;


/**
 * ʹ��Berkeley DBʵ��ȥ��
 * @author ����
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
			for (File fl : f.listFiles())		// ����ļ���
				fl.delete();
		}
		
		
		dao = new DownloadedDAO (dbPath);
	}
	
//	public DuplicationJEFilter (String dbPath) {
		//TODO ��ô���Σ�
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
