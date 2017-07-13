package com.lonely.nlp.dijkstra.att;

import java.util.List;

public class Graph {

	private Vertex[] vertexs;
	
	private List<Integer>[] edges;	//数组的下标是起点，List里存储的是终点。

	public String getSubString(int start, int end) {
		StringBuilder sb = new StringBuilder();
		for(int i=start; i<end; i++)
			sb.append(vertexs[i].realWord);
		return sb.toString();
	}
	
	public Vertex[] getVertexs() {
		return vertexs;
	}

	public List<Integer>[] getEdges() {
		return edges;
	}

	public void setVertexs(Vertex[] vertexs) {
		this.vertexs = vertexs;
	}

	public void setEdges(List<Integer>[] edges) {
		this.edges = edges;
	}
	
}
