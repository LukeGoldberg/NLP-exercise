package com.lonely.nlp.corpus;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.util.logging.Logger;

public class TransMatrixAndStartpMaker {
	
	private BufferedReader br;
	public static Logger logger = Logger.getLogger("Znlp");
	private double totalFrequency;
	private double[] total;
	private double[] startp;
	private String[] tags;
	
	private TransMatrixAndStartpMaker(String path) {
		try {
			br = new BufferedReader(new InputStreamReader(new FileInputStream(path), "UTF-8"));
		} catch (UnsupportedEncodingException e) {
			logger.info("请检查存储转移矩阵txt文件的编码，默认为UTF-8编码");
			e.printStackTrace();
		} catch (FileNotFoundException e) {
			logger.info("文件不存在");
			e.printStackTrace();
		}
	}
	
	public static TransMatrixAndStartpMaker getTransMatrixMaker(String path) {
		return new TransMatrixAndStartpMaker(path);
	}
	
	/**
	 * 获取转移矩阵
	 * 
	 * @param path 存储转移矩阵的txt文件的路径
	 * @return 转移矩阵，每个值都已取负对数
	 */
	public double[][] getTrans() {
		double[][] rst = null;
		
		try {
			String line = br.readLine();//line: space,A,B,C,D,E,F,G,K,L,M,S,U,V,X,Z

			String[] charArray = line.split(",");
			tags = new String[charArray.length - 1];//标签的数量可能不一样;
			for(int i=1; i<charArray.length; i++) {
				tags[i-1] = charArray[i];
			}

			int length = tags.length;
			startp = new double[length];
			total = new double[length];
			rst = new double[length][length];
			double[] tmp;
			totalFrequency = 0.0;
			int ids = 0;
			
			while((line = br.readLine()) != null) {
				tmp = new double[length];
				String str[] = line.split(",");
				for(int i=1; i<str.length; i++) {
					tmp[i-1] = Double.parseDouble(str[i]);
				}
				rst[ids++] = tmp;
			}
			
			for(int j = 0; j<length; ++j) {
	            total[j] = 0;
	            for (int i = 0; i<length; ++i) {
	                total[j] += rst[j][i];
	            }
	        }
			
			for(int i=0; i<length; i++)
				total[i] += rst[i][i];
			
			for(int j=0; j<length; j++)
				totalFrequency += total[j];
			
			for(int i=0; i<length; i++) {
				for(int j=0; j<length; j++) {
					double frequency = rst[i][j] + 1e-8;
					rst[i][j] = - Math.log(frequency / totalFrequency);
				}
			}
			
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		if(rst != null)
			return rst;
		else {
			logger.info("生成转移矩阵出错");
			return null;
		}
			
	}
	
	public double[] getStartP() {
		for(int i=0; i<startp.length; i++) {
			double frequency = total[i] + 1e-8;
			startp[i] = - Math.log(frequency / totalFrequency);
		}
		
		return startp;
	}
	
	public double getTotalFrequency(String tag) {
		return total[tag2id(tag)];
	}
	
	public double[] getTotal() {
		return total;
	}
	
	public String[] getTags() {
		return tags;
	}
	
	private int tag2id(String tag) {
		for(int i=0; i<tags.length; i++)
			if(tag.equals(tags[i]))
				return i;
		logger.info("转换标签出现错误:	" + tag);
		return -1;
	}
}