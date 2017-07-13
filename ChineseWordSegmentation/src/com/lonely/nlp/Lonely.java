package com.lonely.nlp;

public class Lonely {
	
	public static final String root = "E:/data/";
	
	//Dijkstra分词的：仅有单词：的语料库
	public static String dijkstraSegWordPath = root + "dictionary/dijkstra/wordDic.bin";
	//姓的词库
	public static String familyNamesPath = root + "dictionary/recognition/nr/names.txt";
	//中国人名识别的转移矩阵
	public static String nrTransPath = root + "dictionary/recognition/nr/nr.tr.txt";
	//中国人名识别的放射矩阵
	public static String nrEmitpath = root + "dictionary/recognition/nr/nr.txt";
	//核心词典转移矩阵
	public static String CoreDictionaryTransformMatrixDictionaryPath = root + "dictionary/CoreNatureDictionary.tr.txt";
}
