package com.lonely.nlp.dijkstra.att;

public class Vertex {

	public String word;
	
	public String realWord;
	
	public int length;
	
	public Vertex(String word, String realWord, int length) {
		this.word = word;
		this.realWord = realWord;
		this.length = length;
	}
	
	public Vertex(String word, String realWord) {
		this.word = word;
		this.realWord = realWord;
		if(word.equals("未##数"))
			this.length = 1;
		else
			this.length = realWord.length();
	}
	
	public Vertex(String realWord) {
		this.word = realWord;
		this.realWord = realWord;
		this.length = realWord.length();
	}
	
	public String getSubstring(int start, int end) {
		return realWord.substring(start, end);
	}
	
	public String getSubstring(int start) {
		return realWord.substring(start);
	}
	
	public String toString() {
		//return word + "\t" + realWord + "\t" + length;
		return realWord;
	}
}
