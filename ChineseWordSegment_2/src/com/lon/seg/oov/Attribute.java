package com.lon.seg.oov;

public class Attribute {
	
	protected String value;//字符
	protected String tag[];//b, m, e, s
	protected double num[];//相应的数值
	
	public Attribute() {
		tag = new String[]{"b", "m", "e", "s"};
		num = new double[4];
	}
}
