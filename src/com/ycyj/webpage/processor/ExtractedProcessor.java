package com.ycyj.webpage.processor;

import java.util.*;

public interface ExtractedProcessor {
	
	public void process(List<String> data) ;
	
	public void init (Map<String, String> p);
}
