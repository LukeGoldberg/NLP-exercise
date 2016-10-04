package com.lon.word;

import java.util.List;
import java.util.ArrayList;

public class Sequence {
	
	public static List<List<String>> genSubParts(String str) {
		List<List<String>> rst = new ArrayList<List<String>>();
		List<String> list = new ArrayList<String>();
		for(int i=1; i<str.length(); i++) {
			list.add(str.substring(0, i));
			list.add(str.substring(i, str.length()));
			rst.add(list);
		}
		return rst;
	}
	
}
