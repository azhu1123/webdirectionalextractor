package com.ycyj.webpage.filter;


import java.util.Date;

import com.sleepycat.persist.model.Entity;
import com.sleepycat.persist.model.PrimaryKey;

/**
 * ÿ�εõ�һ���µ�URL�������ݿ�����Ѱ֮
 * 
 * �����ѯ����������Ѿ����ع����ˣ� ��ô�͸��������ʱ��
 * 
 * ���û�в�ѯ������ֱ�Ӳ������ݿ�
 * 
 * �����ݿ��е��������ﵽһ��ֵ����ɾ��̫�ɵ�����
 * ���Ƚϵ�ǰʱ����������ʱ�䣬���̫���ɾ��֮��
 * @author ����
 *
 */

@Entity
public class Downloaded {
	
	private static final long BIGGEST_AGE=1*30*24*3600*1000;	// һ����
	
	@PrimaryKey
	String content;
	
	Date lastVisit;
	
	@SuppressWarnings("unused")
	private Downloaded() {
	}
	
	public Downloaded (String str){
		content = str;
		lastVisit = new Date();
	}
	
	public boolean isOld (long date) {
		if ((date - lastVisit.getTime()) > BIGGEST_AGE) 
			return true;
		return false;
	}
	
	
	@Override
	public String toString() {
		return this.content +"\t"+ this.lastVisit;
	}
}
