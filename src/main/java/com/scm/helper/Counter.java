package com.scm.helper;

public class Counter {
	
	private int count;
	
	public Counter() {}
	
	public Counter(int count) {
		this.count = count;
	}

	public int getCount() {
		return count;
	}

	public void setCount(int count) {
		this.count = count;
	}
	
	public int incrementAndGetCount() {
		return count++;
	}
}
