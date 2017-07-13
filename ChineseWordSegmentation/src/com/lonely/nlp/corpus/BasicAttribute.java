package com.lonely.nlp.corpus;

public class BasicAttribute {

	/**
	 * 词性对应的词频
	 */
	public int[] frequency;
	/**
	 * 所有词频的总和
	 */
	public int totalFrequency;
	
	public BasicAttribute(int size) {
		frequency = new int[size];
	}
	
}
