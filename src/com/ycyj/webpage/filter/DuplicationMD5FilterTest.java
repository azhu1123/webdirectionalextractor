package com.ycyj.webpage.filter;

public class DuplicationMD5FilterTest {
	
	public static void main(String[] args) {
		DuplicationMD5Filter f = new DuplicationMD5Filter();
		
		System.out.println(f.filter("abc"));
		System.out.println(!f.filter("abc"));
		
		f.remove("abc");
		System.out.println(f.filter("abc"));
		
	}
	
	

}
