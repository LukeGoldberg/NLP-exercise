package com.lon.word;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.StringTokenizer;

public class WordSegment {
	
	private final int L = 1;
	private final int S = 1;
	private final int ALL = 2;
	
	private String doc;
	private int maxWordLen;
	private double minFreq;
	private double minEntropy;
	private double minAggregation;
	
	private int wordCount;
	private List<WordInfo> wordInfos;
	private double avgLen;
	private double avgFreq;
	private double avgLeftEntropy;
	private double avgRightEntropy;
	private double avgAggregation;
	
	private List<String> wordWithFreq;
	private List<String> words;
	
	public static class Builder {
		private String doc;
		
		private int maxWordLen 		=	5;
		private double minFreq 		=	0.00005;
		private double minEntropy 	= 	2.0;
		private double minAggregation 	= 	50;
		
		public Builder(String doc) {
			this.doc = doc;
		}
		
		public Builder maxWordLen(int maxWordLen) {
			this.maxWordLen = maxWordLen;
			return this;
		}
		
		public Builder minFreq(double minFreq) {
			this.minFreq = minFreq;
			return this;
		}
		
		public Builder minEntropy(double minEntropy) {
			this.minEntropy = minEntropy;
			return this;
		}
		
		public Builder minAggregation(double minAggregation) {
			this.minAggregation = minAggregation;
			return this;
		}
		
		public WordSegment build() {
			return new WordSegment(this);
		}
	}
	
	private WordSegment(Builder builder) {
		this.doc = builder.doc;
		this.maxWordLen = builder.maxWordLen;
		this.minFreq = builder.minFreq;
		this.minEntropy = builder.minEntropy;
		this.minAggregation = builder.minAggregation;
		wordInfos = genWords(doc);
		wordCount = wordInfos.size();
		double sumLength = 0.0;
		double sumFreq = 0.0;
		double sumLeftEntropy = 0.0;
		double sumRightEntropy = 0.0;
		double sumAggregation = 0.0;
		for(WordInfo wi : wordInfos) {
			sumLength += wi.text.length();
			sumFreq += wi.freq;
			sumLeftEntropy += wi.leftEntropy;
			sumRightEntropy += wi.rightEntropy;
			sumAggregation += wi.aggregation;
		}
		avgLen = sumLength / wordCount;
		avgFreq = sumFreq / wordCount;
		avgLeftEntropy = sumLeftEntropy / wordCount;
		avgRightEntropy = sumRightEntropy / wordCount;
		avgAggregation = sumAggregation / wordCount;
		
		wordWithFreq = new ArrayList<String>();
		words = new ArrayList<String>();
		
		//debug wordInfos
		/*
		System.out.println("text,freq,leftEntropy,rightEntropy,aggregation");
		for(WordInfo wi : wordInfos)
			System.out.println(wi);
		*/
		
		for(WordInfo wi : wordInfos)
			if(filterFunc(wi)) {
				wordWithFreq.add(wi.text);
				wordWithFreq.add(String.valueOf(wi.freq));
				
				words.add(wi.text);
			}
	}
	
	private boolean filterFunc(WordInfo wi) {
		return wi.text.length()>1 && wi.aggregation>minAggregation && wi.freq>minFreq && wi.leftEntropy>minEntropy && wi.rightEntropy>minEntropy;
	}
	
	private List<List<Integer>> indexOfSortedSuffix(String doc, int maxWordLen) {
		List<List<Integer>> indexes = new ArrayList<List<Integer>>();
		List<Integer> list;
		for(int i=0; i<doc.length(); i++) {
			list = new ArrayList<Integer>();
			for(int j=i+1; j<Math.min(i+1+maxWordLen, doc.length()+1); j++) {
				list.add(i);
				list.add(j);
				indexes.add(list);
				list = new ArrayList<Integer>();
			}
		}
		MyComparator mc = new MyComparator(doc);
		Collections.sort(indexes, mc);
		return indexes;
	}
	
	private List<WordInfo> genWords(String doc) {
		
		StringBuilder sb = new StringBuilder();
		//这里的正则表达式与原Python里的正则表达式不同，不同点在于在Java里“[”和“]”也是要加上\\的。还有“．”。
		StringTokenizer st = new StringTokenizer(doc, "[．\\s\\d,.<>/?:;\'\"\\[\\]{}()\\|~!@#$%^&*\\-_=+a-zA-Z，。《》、？：；“”‘’｛｝【】（）…￥！—┄－]+");
		while(st.hasMoreTokens())
			sb.append(st.nextToken() + " ");
		sb.deleteCharAt(sb.length()-1);
		
		doc = sb.toString();
		
		List<List<Integer>> suffixIndexes = indexOfSortedSuffix(doc, maxWordLen);
		
		Map<String, WordInfo> wordCands = new HashMap<String, WordInfo>();
		for(List<Integer> suf : suffixIndexes) {
			String word = doc.substring(suf.get(0), suf.get(1));
			WordInfo wi;
			if(!wordCands.containsKey(word)) {
				wi = new WordInfo(word);
				int tmp = suf.get(0)-1;
				if(tmp < 0)	tmp++;
				int tmp2 = suf.get(1) + 1;
				if(tmp2 > doc.length())	tmp2--;
				wi.update(doc.substring(tmp, suf.get(0)), doc.substring(suf.get(1), tmp2));
				wordCands.put(word, wi);
			} else {
				wi = wordCands.get(word);
				int tmp = suf.get(0)-1;
				if(tmp < 0)	tmp++;
				int tmp2 = suf.get(1) + 1;
				if(tmp2 > doc.length())	tmp2--;
				wi.update(doc.substring(tmp, suf.get(0)), doc.substring(suf.get(1), tmp2));
				wordCands.put(word, wi);
			}
		}
		for(String k : wordCands.keySet()) {
			WordInfo wi = wordCands.get(k);
			wi.compute(doc.length());
			wordCands.put(k, wi);
		}
		List<WordInfo> values = new ArrayList<WordInfo>();
		for(Map.Entry<String, WordInfo> entry : wordCands.entrySet())
			values.add(entry.getValue());
		MyComparator2 mc2 = new MyComparator2();
		Collections.sort(values, mc2);
		for(WordInfo wi : values) {
			if(wi.text.length() == 1)	continue;
			wi.computeAggregation(wordCands);
		}
		MyComparator3 mc3 = new MyComparator3();
		Collections.sort(values, mc3);
		Collections.reverse(values);
		return values;
	}
	
	public List<String> segSentence(String sentence) {
		return this.segSentence(sentence, ALL);
	}
	
	private List<String> segSentence(String sentence, int method) {
		int i = 0;
		List<String> res = new LinkedList<String>();
		while(i < sentence.length()) {
			if(method == L || method == S) {
				if(method == L) {
					for(int j=maxWordLen; j>=0; j--) {
						if(j==1 || words.contains(sentence.substring(i, i+j))) {
							res.add(sentence.substring(i, i+j));
							i += j;
							break;
						}
					}
				} else {
					for(int j=0; j<maxWordLen; j+=2) {
						if(j==1 || words.contains(sentence.substring(i, i+j))) {
							res.add(sentence.substring(i, i+j));
							i += j;
							break;
						}
					}
				}
			} else {
				int toInc = 1;
				for(int j=0; j<maxWordLen+1; j+=2) {
					if(i+j<=sentence.length() && words.contains(sentence.substring(i, i+j))) {
						res.add(sentence.substring(i, i+j));
						if(toInc == 1)
							toInc = j;
					}
				}
				if(toInc == 1)
					res.add(sentence.charAt(i) + "");
				i += toInc;
			}
		}
		return res;
	}
	
	public void printWords(String path) {
		try(BufferedWriter bw = new BufferedWriter(new FileWriter(path));) {
			for(String word : words)
				bw.write(word + "\r\n");
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	public static void main(String[] args) {

		String doc = "十四是十四四十是四十，，十四不是四十，，，，四十不是十四";
		WordSegment ws = new WordSegment.Builder(doc).maxWordLen(2).minAggregation(1.2).minEntropy(0.4).build();
		List<String> res = ws.segSentence(doc);
		System.out.println(res);
		System.out.println("average len: " + ws.avgLen);
	    System.out.println("average frequency: " + ws.avgFreq);
	    System.out.println("average left entropy: " + ws.avgLeftEntropy);
	    System.out.println("average right entropy: " + ws.avgRightEntropy);
	    System.out.println("average aggregation: " + ws.avgAggregation);
	}
	
	class MyComparator implements Comparator {
		
		String doc;
		
		public MyComparator(String doc) {
			this.doc = doc;
		}
		
		@Override
		public int compare(Object o1, Object o2) {
			int i, j;
			i = ((List<Integer>)o1).get(0);
			j = ((List<Integer>)o1).get(1);
			String s1 = doc.substring(i, j);
			i = ((List<Integer>)o2).get(0);
			j = ((List<Integer>)o2).get(1);
			String s2 = doc.substring(i, j);
			return s1.compareTo(s2);
		}
		
	}
	
	class MyComparator2 implements Comparator {

		@Override
		public int compare(Object o1, Object o2) {
			return ((WordInfo)o1).text.compareTo(((WordInfo)o2).text);
		}
		
	}
	
	class MyComparator3 implements Comparator {

		@Override
		public int compare(Object o1, Object o2) {
			if(((WordInfo)o1).freq < ((WordInfo)o2).freq)
				return 1;
			else if(((WordInfo)o1).freq == ((WordInfo)o2).freq)
				return 0;
			return -1;
		}
		
	}

}
