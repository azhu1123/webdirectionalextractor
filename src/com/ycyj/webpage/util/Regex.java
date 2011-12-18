package com.ycyj.webpage.util;

import java.util.*;
import java.util.regex.*;


/**
 * 封装正则表达式的操作，本系统中主要就是抽取文本
 * 
 * 特别是对于多层正则表达式的操作
 * 应该考虑从底层向上构建正则表达式
 * 
 * @author 朱亮
 *
 */
public class Regex {
	
	Pattern curPattern;
	int group;
	
	String input;
	Matcher curMatcher;
	
	Regex lowerlevel;
	
	public Regex (String regex, int group){
		this.curPattern = Pattern.compile(regex);
		this.group = group;
	}
	
	public Regex(String regex, int group, Regex r){
		this.curPattern = Pattern.compile(regex);
		this.group = group;
		this.lowerlevel = r;
	}
	
	public Regex(Regex r, Regex lower) {
		this.curPattern = r.curPattern;
		this.group = r.group;
		this.lowerlevel = lower;
	}
	
	public int getGroup() 	{
		return this.group;
	}
	
	public void addLowerLevelRegex (Regex r) {
		this.lowerlevel = r;	
	}
	
	private int curEnd;
	public void setInput(String in) {
		this.input = in;
		curMatcher = this.curPattern.matcher(input);
		curEnd = 0;
		
	}
	/**
	 * @return
	 */
	public String next () {
		String next;
		if(lowerlevel == null) {		// current is the lowest
			if(curMatcher.find(curEnd)) {
				curEnd = curMatcher.end();
				return curMatcher.group(group);
				
			} else return null;
		} 
		
		if ((lowerlevel.input == null) 
				|| (next = lowerlevel.next()) == null) {	// lower level regex has no more next
			if (!curMatcher.find(curEnd)) {					// current level neither has no more
				return null;
			}
			else {
				curEnd = curMatcher.end();
				lowerlevel.setInput(curMatcher.group(group));
				return lowerlevel.next();
			}
		}
		else return next;
	}
	
	public void next (int group, List<String> rst) {
		rst.clear();
		if (curMatcher.find(curEnd)) {
			for(int i = 1; i <= group; i++)
				rst.add(curMatcher.group(i));
			curEnd = curMatcher.end();
		}
	}
	
	@Override
	public String toString() {
		return this.group + "\t" + this.curPattern.toString();
	}
}
