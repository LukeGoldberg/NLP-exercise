package com.lon.word;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class WordInfo {

	/**
	 * 词
	 */
	public String text;
	
	/**
	 * 频率
	 */
	public double freq;
	
	/**
	 * 左邻
	 */
	public List<String> left;
	
	/**
	 * 右邻
	 */
	public List<String> right;
	
	/**
	 * 左熵
	 */
	public double leftEntropy;
	
	/**
	 * 右熵
	 */
	public double rightEntropy;
	
	/**
	 * i do not know what word means here, please tell me if you like to.
	 */
	public double aggregation;
	
	public WordInfo(String text) {
		this.text = text;
		freq = 0.0;
		left = new ArrayList<String>();
		right = new ArrayList<String>();
		aggregation = 0;
	}
	
	protected void update(String leftWord, String rightWord) {
		freq++;
		if(leftWord.length() > 0)	left.add(leftWord);
		if(rightWord.length() > 0)	right.add(rightWord);
	}
	
	protected void compute(int length) {
		freq /= length;
		leftEntropy = entropyOfList(left);
		rightEntropy = entropyOfList(right);
	}
	
	protected void computeAggregation(Map<String, WordInfo> wordsDict) {
		List<List<String>> parts = Sequence.genSubParts(text);
		if(parts.size() > 0) {
			double minAgg = -1;
			for(int i=0; i<parts.size(); i++) {
				List<String> list = parts.get(i);
				double tmp = freq / wordsDict.get(list.get(0)).freq / wordsDict.get(list.get(1)).freq;
				if(tmp > minAgg)
					minAgg = tmp;
			}
			aggregation = minAgg;
		}
	}
	
	private double entropyOfList(List<String> list) {
		Map<String, Integer> map = new HashMap<String, Integer>();
		for(String str : list)
			if(map.containsKey(str))
				map.put(str, map.get(str)+1);
			else
				map.put(str, 1);
		double length = (double)list.size();
		double sum = 0.0;
		for(Map.Entry<String, Integer> entry : map.entrySet()) {
			double tmp = entry.getValue();
			sum += -tmp / length * Math.log(tmp / length);
		}
		return sum;
	}
	
	public String toString() {
		return text + "," + freq + "," + leftEntropy + "," + rightEntropy + "," + aggregation;
	}
	
}