package com.lon.seg;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.log4j.Logger;

import com.lon.dat.DoubleArrayTrie;
import com.lon.seg.oov.ViterbiSegment;

public class Segment {
	
	private boolean initialized;
	
	private Logger logger;
	
	private DoubleArrayTrie dat;
	
	/**
	 * 词典里词语的总数目
	 */
	private double total = 7223355.0;
	
	/**
	 * 词典里每个词语，以及其对应的在词典里的出现次数
	 */
	private Map<String, Integer> map;
	
	private ViterbiSegment app;
	
	String reg1 = "^(.+?)( [0-9]+)?( [a-z]+)?$";
	String reg2 = "[a-zA-Z0-9]";
	String reg3 = "([\u4E00-\u9FD5a-zA-Z0-9+#&\\._]+)";
	String reg4 = "(\\r\\n|\\s)";
	String reg5 = "([\u4E00-\u9FD5]+)";
	String reg6 = "[^a-zA-Z0-9+#\n]";
	
	Pattern p1 = Pattern.compile(reg3);
	Pattern p2 = Pattern.compile(reg2);
	Pattern cutDefaultP = Pattern.compile(reg3);
	Pattern p4 = Pattern.compile(reg4);
	Pattern cutAllP = Pattern.compile(reg5);
	Pattern p6 = Pattern.compile(reg6);
	
	public Segment() {
		logger = Logger.getLogger(Segment.class);
		
		initialized = false;
		
		dat = new DoubleArrayTrie();
		map = new HashMap<String, Integer>();
		try {
			dat.open("E:/MyData/CoreDictionary2000.bin");
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		app = new ViterbiSegment();
	}
	
	private String getAbsolutePath(String fileName) {
		String path = System.getProperty(fileName);
		return path == null ? fileName : path;
	}
	
	private String strdecode(String sentence) throws UnsupportedEncodingException {
		return new String(sentence.getBytes(), "utf-8");
	}
	
	private String switchFunction(int flag, String sentence) {
		switch(flag) {
			case 1 :
				return cutDAG(sentence);
		}
		return "出现异常，断点：Segment-->71行 : switchFunction";
	}
	
	public String cut(String sentence, boolean cutAll, boolean hmm) {
		try {
			sentence = strdecode(sentence);
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		}
		
		Pattern p;
		int flag = 1;
		
		if(cutAll)
			p = cutAllP;
		else
			p = cutDefaultP;
			//使用cutDAG函数
		if(hmm)
			flag = 1;
		
		Matcher m = p.matcher(sentence);
		List<String> blocksString = new ArrayList<String>();//不包含标点符号
		List<String> blocksAll = new ArrayList<String>();//包含sentence的全部（标点符号和字符串）
		while(m.find()) {
			blocksString.add(m.group());
			blocksAll.add(m.group());
			if(m.end() < sentence.length()) {
				blocksAll.add(sentence.charAt(m.end()) + "");
			}
		}
		
		StringBuilder rst = new StringBuilder();
		for(String sen : blocksAll) {
			if(sen.length() == 0)	continue;
			
			if(blocksString.contains(sen))//如果是字符串，不是标点符号
				for(String s : switchFunction(flag, sen).split("/"))
					rst.append(s + "/");
			else {//如果是标点符号
				rst.append(sen + "/");
			}
		}
		
		return rst.toString();
	}
	
	public String finalSegCut(String sentence) {
	
		StringBuilder sb = new StringBuilder();
		
		String r = "([\u4E00-\u9FD5]+)";
		Pattern p = Pattern.compile(r);
		Matcher m = p.matcher(sentence);
		int start;
		int end = 0;
		if(m.find()) {
			start = m.start();
			end = m.end();
			if(start > 0)
				sb.append(sentence.substring(0, start) + "/");
			
			sb.append(app.seg(m.group()) + "/");
		}
		while(m.find()) {
			if(end != m.start()) {
				sb.append(sentence.substring(end, m.start()) + "/");
			}
			
			sb.append(app.seg(m.group()) + "/");
			end = m.end();
		}
		if(end != sentence.length())
			sb.append(sentence.substring(end, sentence.length()));
		
		return sb.toString();
	}
	
	public String cutDAG(String sentence) {
		
		StringBuilder rst = new StringBuilder();
		
		List<List<Integer>> dag = getDAG(sentence);
		
		int[] route = calc(sentence, dag);
		
		int x = 0;
		List<String> hmmTempStr = new ArrayList<String>();
		while(x < sentence.length()) {
			
			int y = route[x];
			String word = sentence.substring(x, y);
			if((y - x) == 1)
				hmmTempStr.add(word);
			else {
				if(hmmTempStr.size() > 0) {
					if(hmmTempStr.size() == 1) {
						rst.append(hmmTempStr.get(0) + "/");
						hmmTempStr.clear();;
					} else {
						String temp = "";
						for(String s : hmmTempStr)
							temp += s;
						if(dat.exactMatchSearch(temp) < 0) {
							for(String s : finalSegCut(temp).split("/"))
								rst.append(s + "/");
						} else {
							for(String s : hmmTempStr)
								rst.append(s + "/");
						}
						hmmTempStr.clear();
					}
				}
				rst.append(word + "/");
			}
			x = y;
		}
		
		if(hmmTempStr.size() > 0) {
			if(hmmTempStr.size() == 1)
				rst.append(hmmTempStr.get(0) + "/");
			else {
				String temp = "";
				for(String s : hmmTempStr)
					temp += s;
				if(dat.exactMatchSearch(temp) < 0) {
					for(String s : finalSegCut(temp).split("/"))
						rst.append(s + "/");
				} else {
					for(String s : hmmTempStr)
						rst.append(s + "/");
				}
			}
		}
		
		return rst.toString();
	}

	private List<List<Integer>> getDAG(String sentence) {
		checkInitialized();
		
		List<List<Integer>> dag = new ArrayList<List<Integer>>();
		for(int i=0; i<sentence.length(); i++) {
			List<Integer> t = new ArrayList<Integer>();
			t.add(i+1);//这个字（词）的下一个下标
			//绝大多数词都不会超过10个，我猜。
			for(int k=i+1; k-i<10 && k<sentence.length(); k++) {
				String c = sentence.substring(i, k+1);
				if(dat.exactMatchSearch(c) >= 0)
					t.add(k+1);
			}
			dag.add(t);
		}
		
		return dag;
	}
	
	private int[] calc(String sentence, List<List<Integer>> dag) {
		int[] route = new int[sentence.length()+1];
		route[sentence.length()] = 0;
		double logtotal = Math.log(total);
		for(int i=sentence.length()-1; i>=0; i--) {
			double max = -Double.MAX_VALUE;
			int maxPosi = 0;
			int p;
			for(p=0; p<dag.get(i).size(); p++) {
				if(map.get(sentence.substring(i, dag.get(i).get(p))) == null) {
					maxPosi = dag.get(i).get(p);
					continue;
				}
				double fre = Math.log(map.get(sentence.substring(i, dag.get(i).get(p)))) - logtotal;
				if(fre > max) {
					max = fre;
					maxPosi = dag.get(i).get(p);
				}
			}
			route[i] = maxPosi;
		}
		return route;
	}
	
	private void checkInitialized() {
		if(!initialized)
			initialize();
	}
	
	private void initialize() {
		
		//同步语句
		logger.info("Building prefix dic from" + "datPath");//dictionary);
		try {
			dat.open("E:\\MyData\\2014CoreDictionary.bin");
		} catch (IOException e) {
			logger.info("data unloaded");
			System.exit(0);
		}
		
		//inital map
		try(BufferedReader br = new BufferedReader(new InputStreamReader(new FileInputStream("E:\\corpus\\人民日报2000语料库\\RMRB2000生语料\\总语料-整理后\\wordFre.txt"), "utf-8"));) {
			String line;
			while((line = br.readLine()) != null) {
				String[] s = line.split("\t");
				map.put(s[0], Integer.parseInt(s[1]));
			}
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		initialized = true;
	}
	
	public String cut(String sentence) {
		return this.cut(sentence, false, true);
	}

}