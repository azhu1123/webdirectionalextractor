package com.ycyj.webpage.filter;


import java.util.Date;

import com.sleepycat.persist.model.Entity;
import com.sleepycat.persist.model.PrimaryKey;

/**
 * 每次得到一个新的URL，在数据库中搜寻之
 * 
 * 如果查询到，即最近已经下载过它了， 那么就更新其访问时间
 * 
 * 如果没有查询到，就直接插入数据库
 * 
 * 当数据库中的数据量达到一定值，则删除太旧的数据
 * （比较当前时间和最近访问时间，相隔太大的删除之）
 * @author 朱亮
 *
 */

@Entity
public class Downloaded {
	
	private static final long BIGGEST_AGE=1*30*24*3600*1000;	// 一个月
	
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
