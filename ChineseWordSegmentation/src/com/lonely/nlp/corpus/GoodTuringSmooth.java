package com.lonely.nlp.corpus;

import java.util.HashMap;
import java.util.Map;
import java.util.Random;
import java.util.Set;

public class GoodTuringSmooth {
	
	Map<Double, Integer> map = new HashMap<Double, Integer>();
	Map<Integer, Double> n = new HashMap<Integer, Double>();
	double total;
	
	private void goodTuring(double[][] matrix) {
		
		total = matrix.length * matrix[0].length;
		
		int tmp;
		for(int i=0; i<matrix.length; i++)
			for(int j=0; j<matrix[0].length; j++) {
				tmp = 1;
				if(map.containsKey(matrix[i][j])) {
					tmp = map.get(matrix[i][j]);
					tmp++;
				}
				map.put(matrix[i][j], tmp);
			}
		
		//calculate N[c]
		int key;
		double t;
		Set<Map.Entry<Double, Integer>> entry =  map.entrySet();
		for(Map.Entry<Double, Integer> me : entry) {
			key = me.getValue();
			t = 1.0;
			if(n.containsKey(key)) {
				t = n.get(key);
				t++;
			}
			n.put(key, t);
		}
		
		//calculate c*
		for(int i=0; i<matrix.length; i++)
			for(int j=0; j<matrix[0].length; j++)
				if(n.containsKey(map.get(matrix[i][j]) + 1) && n.containsKey(map.get(matrix[i][j])))
					n.put(map.get(matrix[i][j]), (map.get(matrix[i][j])+1) * n.get(map.get(matrix[i][j]) + 1) / n.get(map.get(matrix[i][j])));
				
		
		if(!n.containsKey(1))
			n.put(1, 1e-8);
		
		for(int i=0; i<matrix.length; i++)
			for(int j=0; j<matrix[0].length; j++)
				if(n.containsKey(map.get(matrix[i][j])))
					matrix[i][j] = n.get(map.get(matrix[i][j])) / total;
				else
					matrix[i][j] = n.get(1) / total;
		
		for(int i=0; i<matrix.length; i++) {
			for(int j=0; j<matrix[0].length; j++)
				System.out.print(matrix[i][j] + "\t");
			System.out.println();
		}
			
	}

	public static void main(String[] args) {

		double[][] matrix = new double[5][5];
		Random r = new Random();
		for(int i=0; i<5; i++)
			for(int j=0; j<5; j++)
				matrix[i][j] = r.nextInt(3) + 1;
		
		GoodTuringSmooth app = new GoodTuringSmooth();
		app.goodTuring(matrix);
		
	}

}
