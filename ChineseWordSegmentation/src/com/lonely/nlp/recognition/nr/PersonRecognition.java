package com.lonely.nlp.recognition.nr;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.DataInputStream;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;
import java.util.Map;

import com.lonely.nlp.Lonely;
import com.lonely.nlp.algorithm.Viterbi;
import com.lonely.nlp.corpus.NR;
import com.lonely.nlp.corpus.RecogEmitMaker;
import com.lonely.nlp.corpus.TransMatrixAndStartpMaker;
import com.lonely.nlp.corpus.WordNet;
import com.lonely.nlp.dat.DoubleArrayTrie;
import com.lonely.nlp.dijkstra.att.Vertex;

public class PersonRecognition {
	
	static String[] tags = new String[]{"A", "B", "C", "D", "E", "F", "G", "K", "L", "M", "S", "U", "V", "X", "Z"};
	 /**
     * AC算法用到的Trie树
     */
    public static DoubleArrayTrie trie;
    
	private static double[][] trans;
	private static double[] startp;
	private static RecogEmitMaker emitMaker;
	
	private static Map<String, Integer> wordMap;
	private static HashSet<String> lastNameSet;
	
	private static int m, l;
	private final static int B = 2;
	
	static {
		trie = new DoubleArrayTrie();
        try {
			trie.open("E:\\workspace\\TestDATWM\\src\\test\\resources\\shift/nr.dat.bin");
		} catch (IOException e) {
			e.printStackTrace();
		}
        
        wordMap = new HashMap<String, Integer>();
        loadWordMap("E:\\workspace\\TestDATWM\\src\\test\\resources\\shift/nr.shift.bin");
        lastNameSet = new HashSet<String>();
        loadLastNameSet("E:\\MyNewData\\dictionary\\recognition\\nr\\names.txt");
        
		TransMatrixAndStartpMaker transMaker = TransMatrixAndStartpMaker.getTransMatrixMaker(Lonely.nrTransPath);
		trans = transMaker.getTrans();
		startp = transMaker.getStartP();
		emitMaker = RecogEmitMaker.getRecogEmitMaker(Lonely.nrEmitpath, transMaker.getTags(), transMaker.getTotal());
	}
	
	
	
	private static void loadWordMap(String filePath) {
		try {
			DataInputStream in = new DataInputStream(new BufferedInputStream(new FileInputStream(filePath)));
			m = in.readInt();
			l = in.readInt();
			int size = in.readInt();
			
			for(int i=0; i<size; i++) {
				wordMap.put(in.readUTF(), in.readInt());
			}
			
			in.close();
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	private static void loadLastNameSet(String filePath) {
		try(BufferedReader br = new BufferedReader(new InputStreamReader(new FileInputStream(filePath), "UTF-8"))) {
			String line;
			line = br.readLine().substring(1);
			lastNameSet.add(line);
			while((line=br.readLine()) != null)
				lastNameSet.add(line);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	public static List<NR> getRoleTagSequence(List<Vertex> sentence) {
		
		double[][] emit = emitMaker.getEmitMatrix(sentence);
		int[] obs = new int[sentence.size()];
		for(int i=0; i<sentence.size(); i++)
			obs[i] = i;
		int[] states = new int[tags.length];
		for(int i=0; i<tags.length; i++)
			states[i] = i;
		
		int[] path = Viterbi.compute(obs, states, startp, trans, emit);
		
		NR[] nrArrayIndex = NR.values();
		List<NR> rst = new ArrayList<NR>();
		for(int i=0; i<path.length; i++) {
			rst.add(nrArrayIndex[path[i]]);
		}
		
		return rst;
	}
	
	public static List<Vertex> parsePattern(List<Vertex> sentence, WordNet wordNetOptimum) throws IOException {
		
		List<NR> tagSequence = getRoleTagSequence(sentence);
		
		//拆分UV
		ListIterator<Vertex> listIterator = sentence.listIterator();
		StringBuilder sbPattern = new StringBuilder(tagSequence.size());
		NR preNR = NR.A;
		boolean backUp = false;
        int index = 0;
        for(NR nr : tagSequence) {
        	++ index;
        	Vertex current = listIterator.next();
        	switch(nr) {
        		case U :
        			if (!backUp)
                    {
                        sentence = new ArrayList<Vertex>(sentence);
                        listIterator = sentence.listIterator(index);
                        backUp = true;
                    }
                    sbPattern.append(NR.K.toString());
                    sbPattern.append(NR.B.toString());
                    preNR = NR.B;
                    listIterator.previous();
                    String nowK = current.getSubstring(0, current.length - 1);
                    String nowB = current.getSubstring(current.length - 1);
                    listIterator.set(new Vertex(nowK, nowK));
                    listIterator.next();
                    listIterator.add(new Vertex(nowB, nowB));
                    continue;
                case V:
                    if (!backUp)
                    {
                        sentence = new ArrayList<Vertex>(sentence);
                        listIterator = sentence.listIterator(index);
                        backUp = true;
                    }
                    if (preNR == NR.B)
                    {
                        sbPattern.append(NR.E.toString());  //BE
                    }
                    else
                    {
                        sbPattern.append(NR.D.toString());  //CD
                    }
                    sbPattern.append(NR.L.toString());
                    // 对串也做一些修改
                    listIterator.previous();
                    String nowED = current.getSubstring(current.length - 1);
                    String nowL = current.getSubstring(0, current.length - 1);
                    listIterator.set(new Vertex(nowED, nowED));
                    listIterator.add(new Vertex(nowL, nowL));
                    listIterator.next();
                    continue;
                default:
                    sbPattern.append(nr.toString());
                    break;
        	}
        	preNR = nr;
        }
        String pattern = sbPattern.toString();
        
        String subStr = "";
		String subStrOfB = "";
		for(int j=0; j<pattern.length(); ) {
			if((m+j) > pattern.length())
				break;
			subStr = pattern.substring(j, m+j);
			subStrOfB = subStr.substring(m-B, m);
			
			if(wordMap.get(subStrOfB) == null) {
				j += 1;
				continue;
			}
			
			int value = wordMap.get(subStrOfB);
			
			if(value == 0) {
				//check from DAT
				//l : (模式串的最大长度)
				for(int position=m+j-2; (m+j-1-position)<=l; position--) {
					if(position < 0)
						break;
					//if(m+j > sentence.size())
					//	break;
					String tmp = pattern.substring(position, m+j);
					int num = trie.exactMatchSearch(tmp);
					if(num >= 0) {

String ssssssss = "";
for(int i=position; i<m+j; i++) {
	ssssssss += sentence.get(i).realWord;
}
int wordNetPosition = 0;
for(int i=0; i<position; i++)
	wordNetPosition += sentence.get(i).realWord.length();

//System.out.println(tmp + "\t" + ssssssss);
						
						if(!checkLastName(ssssssss.charAt(0)))	continue;
						if(!isChinese(ssssssss))
							continue;
	
						wordNetOptimum.add(wordNetPosition, new Vertex(ssssssss));
						continue;
					}
				}
				
				value = 1;
			}
			
			j += value;
		}
        
        return wordNetOptimum.getResult();
	}
	
	public static void saveToTXT() {
		Iterator<Map.Entry<String, Integer>> entries = wordMap.entrySet().iterator();
		try {
			FileWriter fw = new FileWriter("E:\\statistics\\recognition/nr/nr.txt", true);
		
			while(entries.hasNext()) {
				Map.Entry<String, Integer> entry = entries.next();
				fw.write(entry.getKey() + "\t" + entry.getValue() + "\r\n");
			}
			
			fw.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
		
	}

	/**
	 * 输入的字符是否是汉字
	 * @param a char
	 * @return boolean
	 */
	public static boolean isChinese(String str) {
		int flag = 0;
		for(int i=0; i<str.length(); i++)
			if(!((str.charAt(i) >= '\u4e00' && str.charAt(i) <= '\u9fa5') || (str.charAt(i) >= '\uf900' && str.charAt(i) <='\ufa2d'))) {
				flag = 1;
				break;
			}
		if(flag == 1)
			return false;
		return true;
	}
	
	public static boolean checkLastName(char c) {
		if(lastNameSet.contains(c+""))
			return true;
		return false;
	}
	
	public static void add(String word) {
		if(wordMap.containsKey(word)) {
			int tmp = wordMap.get(word);
			tmp ++;
			wordMap.put(word, tmp);
		} else {
			wordMap.put(word, 1);
		}
	}
}
;