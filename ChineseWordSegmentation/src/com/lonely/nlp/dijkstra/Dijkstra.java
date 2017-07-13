package com.lonely.nlp.dijkstra;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;
import java.util.PriorityQueue;

import com.lonely.nlp.dat.DoubleArrayTrie;
import com.lonely.nlp.dijkstra.att.Graph;
import com.lonely.nlp.dijkstra.att.State;
import com.lonely.nlp.dijkstra.att.Vertex;

public class Dijkstra {
	
	private static DoubleArrayTrie dat;
	
	private Dijkstra() {
		dat = new DoubleArrayTrie();
		try {
			//dat.open("E:/a cuscomed corpus/CoreDictionary.bin");
			dat.open("E:/MyData/2014CoreDictionary.bin");
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	public static Dijkstra getDijkstra() {
		return new Dijkstra();
	}
	
	private List<Vertex>[] generateWordList(String sentence) {
		
		List<Vertex>[] rst = new ArrayList[sentence.length()];
		for(int i=0; i<sentence.length(); i++)
			rst[i] = new ArrayList<Vertex>();
		
		for(int i=0; i<sentence.length(); i++)
			rst[i].add(new Vertex(sentence.charAt(i) + ""));
		
		for(int i=0; i<sentence.length(); i++) {
			
			
			
			//逆向最大匹配，如果查询到有这个词存在，则连接一条边
			for(int j=i+1; j<i+10; j++) {
				
				if(j>=sentence.length())
					break;
				
				String tmp = sentence.substring(i, j+1);
				if(dat.exactMatchSearch(tmp) >= 0) {
					rst[i].add(new Vertex(tmp));
				}
			}
			
		}
		
		return rst;
	}
	
	private Graph generateGraph(List<Vertex>[] sentence) {
		
		Graph graph = new Graph();
		
		Vertex[] vertexs = new Vertex[sentence.length];
		for(int i=0; i<sentence.length; i++)
			vertexs[i] = sentence[i].get(0);
		
		List<Integer>[] edges = new ArrayList[sentence.length + 1];
		for(int i=0; i<edges.length; i++)
			edges[i] = new ArrayList<Integer>();
		
		//这里的sentence包含最后结尾的一个空值
		for(int i=0; i<sentence.length; i++) {
			
			List<Vertex> vs = sentence[i];
			for(Vertex vertex : vs) {
				edges[i].add(i + vertex.realWord.length());
			}
		}
		
		graph.setVertexs(vertexs);
		graph.setEdges(edges);
		
		return graph;
	}
	
	public List<Vertex> dijkstra(String sentence) {
		
		List<Vertex>[] list = generateWordList(sentence);
		Graph graph = generateGraph(list);
		
		List<Vertex> resultList = new LinkedList<Vertex>();
		Vertex[] vertexes = graph.getVertexs();
		List<Integer>[] edges = graph.getEdges();
		int[] d = new int[vertexes.length+1];		//每个顶点的权值，即每个顶点的距离。这里+1是指最后那个空值
		Arrays.fill(d, Integer.MAX_VALUE);
		d[0] = 0;									//d[起点] = 0，作为“已经确定的、用来作为出发点的顶点”
		int[] path = new int[vertexes.length+1];	//存储回溯出来的路径
		Arrays.fill(path, -1);						//初始化path为-1
		PriorityQueue<State> que = new PriorityQueue<State>();
		que.add(new State(0, 0));
		while (!que.isEmpty()) {
			State p = que.poll();
			if (d[p.vertex] < p.cost) continue;
			int tmp = 0;
			for (int endPoint : edges[p.vertex]) {
				if (d[endPoint] > d[p.vertex] + 1) {
					d[endPoint] = d[p.vertex] + 1;
					tmp = endPoint;
					path[p.vertex] = endPoint;
				}
			}
			
			if(edges[p.vertex].size() == 0)
				break;
			
			que.add((new State(d[tmp], tmp)));
		}
		
		for (int t = 0; t != -1; t = path[t])
			resultList.add(new Vertex(graph.getSubString(t, path[t])));
		resultList.remove(resultList.size() - 1);
		
		return resultList;
	}
	
	public static void main(String[] args) throws IOException {
		Dijkstra app = new Dijkstra();
		
//System.out.println(app.dat.exactMatchSearch("10001"));		

		String[] sentence = {
				"马小东和邓超在划水。",
				"张浩和胡健康复员回家了",
				"龚学平等领导说,邓颖超生前杜绝超生",
				"长春市长春药店出售狗皮膏药，不卖狗头，店长叫张耀飞，写过一本单词书。",
				"团购网站的本质是什么？",
				"加快完成都市化",
				"用户来电表示电信10001在这两天凌晨都给用户下发短信",
				"工信处女干事观看了比赛。"
		};
		
		List<Vertex>  v;
		for(String str : sentence) {
			v = app.dijkstra(str);
			for(int i=0; i<v.size(); i++)
				System.out.print(v.get(i) + "   ");
			System.out.println();
		}
	}
}