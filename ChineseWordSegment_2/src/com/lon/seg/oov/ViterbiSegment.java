package com.lon.seg.oov;

import java.util.Scanner;

public class ViterbiSegment {

	private static final String emitPath = NLP.emitPath;
	private static final String transPath = NLP.transpath;
	private Viterbi vt;
	private LoadMatrix lm;
	private double[][] emit_p;
	private double trans_p[][];
	private StartP sp;
	
	public ViterbiSegment() {
		
		vt = new Viterbi();
		
		sp = new StartP();
		
		lm = new LoadMatrix();
		lm.loadEmit(emitPath);
		lm.loadTrans(transPath);
		
	}
	
	private String getTag(int i) {
		switch(i) {
			case 0 :
				return "b";
			case 1 :
				return "m";
			case 2 :
				return "e";
			case 3 :
				return "s";
			default :
				return "n";
		}
	}
	
	private String printSeg(String sentence, String tags) {
		char[] c = sentence.toCharArray();
		char[] tag = tags.toCharArray();
		
		String rst = "";
		for(int i=0; i<tags.length(); ) {
			String tmp = "";
			int f = i;
			
			switch(tag[i]) {
				case 'b' :
					if(i == tags.length()-1) {
						tmp += String.valueOf(c[f]);
						rst += tmp;
					}
					
					while(f!=tags.length() && tag[f] != 'e') {
						tmp += String.valueOf(c[f++]);
					}
					
					if(f == tags.length()) {
						i = f;
						break;
					}
					
					tmp += String.valueOf(c[f]);
					rst += tmp + "/";
					i = ++f;
					break;
				default :
					tmp += String.valueOf(c[i]);
					rst += tmp + "/";
					i ++;
					break;
			}
		}
		
		return rst;
	}
	
	/**
	 * Viterbi分词
	 * @author LonelySoul
	 */
	public String seg(String sentence) {
		
		trans_p = lm.getTrans();
		emit_p = lm.getEmitMatrix(sentence);
		
		int l = sentence.length();
		int obs[] = new int[l];
		for(int i=0; i<l; i++)
			obs[i] = i;
		int status[] = new int[4];
		for(int i=0; i<4; i++)
			status[i] = i;
		
		double start_p[] = sp.getStart_p();
		
		int path[] = Viterbi.compute(obs, status, start_p, trans_p, emit_p);
		
		String tags = "";
		for(int i=0; i<path.length; i++)
			tags += getTag(path[i]);
		
		String rst = printSeg(sentence, tags);
		return rst;
	}
	
}