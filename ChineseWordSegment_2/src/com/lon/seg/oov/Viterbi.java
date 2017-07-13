package com.lon.seg.oov;

public class Viterbi {

	/**
     * 求解HMM模型，所有概率请提前取对数
     *
     * @param obs     观测序列
     * @param states  隐状态
     * @param start_p 初始概率（隐状态）
     * @param trans_p 转移概率（隐状态）
     * @param emit_p  发射概率 （隐状态表现为显状态的概率）
     * @return 最可能的序列
     */
	public static int[] compute(int []obs, int []states, double []start_p, double [][]trans_p, double [][]emit_p) {
		int rst[] = new int[obs.length];
		
		double v[][] = new double[states.length][obs.length];
		int path[][] = new int[states.length][obs.length];
		
		for(int s : states) {
			v[s][0] = start_p[s] + emit_p[s][0];
			path[s][0] = s;
		}
		
		for(int i=1; i<obs.length; i++) {
			for(int s=0; s<states.length; s++) {
				v[s][i] = v[0][i-1] + trans_p[0][s];
				for(int k=1; k<states.length; k++) {
					double tmp = v[k][i-1] + trans_p[k][s];
					if(tmp > v[s][i]) {
						v[s][i] = tmp;
						path[s][i] = k;
					}
				}
				
				v[s][i] += emit_p[s][i];
				
			}
		}
		
		int q = 0;
		for(int j=0; j<states.length; j++) {
			if(v[j][obs.length-1] > v[q][obs.length-1]) {
//System.out.println("happen");
				q = j;
			}
		}
		
		rst[obs.length-1] = q;
		
		for(int k=obs.length-2; k>=0; k--) {
			q = path[q][k+1];
			rst[k] = q;
		}
		//debug
		
		//print v
		/*System.out.println("********** v:");
		for(int i=0; i<states.length; i++) {
			for(int j=0; j<obs.length; j++)
				System.out.print(v[i][j] + "\t");
			System.out.println();
		}
		//print path
  		System.out.println("**********compeleted path");
  		for(int w=0; w<states.length; w++) {
  			for(int p=0; p<obs.length; p++) {
  				System.out.print(path[w][p] + "   ");
  			}
  			System.out.println();
  		}*/
		
		return rst;
	}
	
}