package com.lon.seg.oov;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

public class LoadMatrix {

	private Map<String, String> emitMap = new HashMap<String, String>();
	private double trans[][] = new double[4][4];
	//private final double totalNum = 1841656.0;
	
	protected void loadEmit(String path) {
		try {
			BufferedReader br = new BufferedReader(new FileReader(path));
			String line = br.readLine();
			while(line != null) {
				String []s = line.split(" ");
				String c = s[0];
				
				emitMap.put(c, line);

				line = br.readLine();
			}
			br.close();
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	//p(column|raw)
	protected void loadTrans(String path) {
		BufferedReader br;
		try {
			br = new BufferedReader(new FileReader(path));
			String line = br.readLine();
			line = br.readLine();
			int ids = 0;
			while(line != null) {
				String s[] = line.split(",");
				//这里 i 要从 0 开始，保证trans矩阵从 0 开始共 4 列
				for(int i=0; i<s.length-1; i++) {
					double tmp = Double.parseDouble(s[i+1]);
						trans[ids][i] = tmp;
				}
				ids ++;
				line = br.readLine();
			}
			br.close();
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	private double[] getEmitPro(Character c) {
		String str = c.toString();
		String rst = emitMap.get(str);
//System.out.println(rst);
		double emit[] = new double[4];
		if(rst != null) {
			String s[] = rst.split(" ");
			
			int ids = 0;
			for(int i=0; i<4; i++) {
				emit[i] = Double.parseDouble(s[ids+2]);
				ids += 2;
			}
				
			return emit;
		} else {
			for(int i=0; i<4; i++)
				emit[i] = 0.0;
			return emit;
		}
	}	
	/**
	 * 行数为 4 ，列数为sentence.length()
	 * 
	 * @param sentence
	 * @return emit_p[][]
	 */
	protected double[][] getEmitMatrix(String sentence) {
		//直接将转移矩阵的行数设为4
		double[][] emitMatrix = new double[4][sentence.length()];
		
		char str[] = sentence.toCharArray();
		for(int i=0; i<sentence.length(); i++) {
			double tmp[] = getEmitPro(str[i]);
			for(int k=0; k<4; k++)
				//if(tmp[k] == 0)
				//	emitMatrix[k][i] = Math.log(0.9999999);
				//else
					emitMatrix[k][i] = Math.log(tmp[k]);
		}
		
		return emitMatrix;
	}

	/**
	 * 4 × 4 的矩阵
	 * 
	 * @return trans_p[][]
	 */
	protected double[][] getTrans() {
		return trans;
	}
}