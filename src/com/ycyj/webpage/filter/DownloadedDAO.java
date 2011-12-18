package com.ycyj.webpage.filter;

import java.io.File;
import java.util.Date;

import com.sleepycat.je.Environment;
import com.sleepycat.je.EnvironmentConfig;
import com.sleepycat.je.Transaction;
import com.sleepycat.persist.EntityCursor;
import com.sleepycat.persist.EntityStore;
import com.sleepycat.persist.PrimaryIndex;
import com.sleepycat.persist.StoreConfig;

public class DownloadedDAO {
	EnvironmentConfig envConfig;
	Environment env;
	
	StoreConfig storeConfig;
	EntityStore store;
	
	private PrimaryIndex<String,Downloaded> idx;
	private int size;
	
	private static final int threshold = 100000; // 10Íò
	
	public DownloadedDAO (String name){
		// Open a transactional Berkeley DB engine environment.
		//
		envConfig = new EnvironmentConfig();
		envConfig.setAllowCreate(true);
		envConfig.setTransactional(true);
		env = new Environment(new File(name), envConfig);
		
		
		// Open a transactional entity store.
		//
		storeConfig = new StoreConfig();
		storeConfig.setAllowCreate(true);
		storeConfig.setTransactional(true);
		store = new EntityStore(env, name + "_store", storeConfig);
		
		idx = store.getPrimaryIndex(
	            String.class, Downloaded.class);
	}
	
	
	private void save (String str) {
		idx.put(new Downloaded(str));
	}
	
	
	public synchronized boolean contains (String str) {
		if (str == null || str.trim().length() == 0)
			return true;
		
		Downloaded p = idx.get(str);
		if (p == null) {
			save (str);
			return false;
		}
		
		p.lastVisit =new Date();
		idx.put(p);
		size ++;
		return true;
	}
	
	public synchronized boolean remove (String str) {
		return idx.delete(str);
	}
	
	
	public synchronized void clearOld () {
		if (size < threshold) {
			return;
		}
		
	    Transaction txn = env.beginTransaction(null, null);
		EntityCursor<Downloaded> c =this.idx.entities(txn, null);
		long now = new Date().getTime();
		
		try {
			for (Downloaded p = c.first(); p != null; p = c.next()) {
				if (p.isOld(now)) {
					c.delete();
					size --;
				}
			}
		} finally {
			c.close();
		}
		
		txn.commit();
	}

}
