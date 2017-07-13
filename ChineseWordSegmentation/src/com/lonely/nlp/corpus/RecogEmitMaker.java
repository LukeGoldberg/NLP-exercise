package com.lonely.nlp.corpus;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Logger;

import com.lonely.nlp.dijkstra.att.Vertex;

public class RecogEmitMaker {

	protected BufferedReader br;
	protected Map<String, BasicAttribute> map;
	protected String[] tags;//用来存储各个标签
	protected double[] total;//用来存储各个标签的频率
	protected double totalTagFrequency;//
	protected int size;//tags' number
	public static Logger logger = Logger.getLogger("Znlp");
	
	protected RecogEmitMaker(String path, String[] tags, double[] total) {

		try {
			br = new BufferedReader(new InputStreamReader(new FileInputStream(path), "UTF-8"));
		} catch (UnsupportedEncodingException e) {
			logger.info("请检查存储转移矩阵txt文件的编码，默认为UTF-8编码");
			e.printStackTrace();
		} catch (FileNotFoundException e) {
			logger.info("文件不存在");
			e.printStackTrace();
		}

		this.tags = tags;
		this.total = total;
		
		this.size = tags.length;
		
		map = new HashMap<String, BasicAttribute>();
		
		totalTagFrequency = 0.0;
		
		load();
	}
	
	public static RecogEmitMaker getRecogEmitMaker(String Emitpath, String[] tags, double[] total) {
		return new RecogEmitMaker(Emitpath, tags, total);
	}
	
	protected void load() {
		
		String line;
		BasicAttribute att;
		try {
			while((line=br.readLine()) != null) {
				att = new BasicAttribute(size);
				//word tag1 fre1 tag2 fre2......所以要从i=1开始
				String[] str = line.split(" ");
				for(int i=1; i<str.length; i++) {
					att.frequency[tag2id(str[i])] = Integer.parseInt(str[++i]);
					att.totalFrequency += Integer.parseInt(str[i]);
				}
				map.put(str[0], att);
			}
			
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	public double[][] getEmitMatrix(List<Vertex> sentence) {
		String[] str = new String[sentence.size()];
		int ids = 0;
		for(Vertex tmp : sentence)
			str[ids++] = tmp.word;
		return getEmitMatrix(str);
	}
	
	private double[][] getEmitMatrix(String[] sentence) {
		double[][] rst = new double[size][sentence.length];
		
		for(int i=0; i<total.length; i++)
			totalTagFrequency += total[i];
		
		double tmp;
		BasicAttribute att;
		for(int i=0; i<sentence.length; i++) {
			att = map.get(sentence[i]);
			
			if(att == null) {
				rst[0][i] = - Math.log((total[0] + 1e-8) / totalTagFrequency);
				for(int j=1; j<size; j++)
					rst[j][i] = - Math.log((0.0 + 1e-8) / total[j]);
				continue ;
			}
			
			for(int j=0; j<size; j++) {
				tmp = - Math.log((att.frequency[j] + 1e-8) / total[j]);
				rst[j][i] = tmp;
			}
			
		}
		
		return rst;
	}
	
	public void setSize(int size) {
		this.size = size;
	}
	
	public void setTotal(double[] total) {
		this.total = total;
	}
	
	public void setTags(String[] tags) {
		this.tags = tags;
	}
	
	protected int tag2id(String tag) {
		for(int i=0; i<tags.length; i++)
			if(tag.equals(tags[i]))
				return i;
		System.out.println("+++++++++++++++++++++++++++++\t" + tag);
		return -1;
	}

}