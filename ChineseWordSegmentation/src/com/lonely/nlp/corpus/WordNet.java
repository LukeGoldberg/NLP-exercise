package com.lonely.nlp.corpus;

import java.util.ArrayList;
import java.util.List;

import com.lonely.nlp.dijkstra.att.Vertex;

public class WordNet {

	private List<Vertex>[] vertexs;
	
	char[] charArray;
	
	public WordNet(int size) {
		vertexs = new ArrayList[size];
		for(int i=0; i<size; i++)
			vertexs[i] = new ArrayList<Vertex>();
	}
	
	/*public WordNet(List<Vertex> sentence) {
		
		this(sentence.size());
		
		Vertex tmp;
		for(int i=0; i<sentence.size(); i++) {
			tmp = sentence.get(i);
			if(tmp.word.contains("##"))
				vertexs[i].add(new Vertex(tmp.word));
			else {
				vertexs[i].add(new Vertex(tmp.realWord));
			}
		}
	}*/
	
	public WordNet(char[] charArray)
    {
        this.charArray = charArray;
        vertexs = new ArrayList[charArray.length];
        for (int i = 0; i < vertexs.length; ++i)
        {
            vertexs[i] = new ArrayList<Vertex>();
            vertexs[i].add(new Vertex(charArray[i] + ""));
        }
    }
	
	public WordNet(char[] charArray, List<Vertex> vertexList)
    {
        this.charArray = charArray;
        vertexs = new ArrayList[charArray.length];
        for (int i = 0; i < vertexs.length; ++i)
        {
            vertexs[i] = new ArrayList<Vertex>();
            vertexs[i].add(new Vertex(charArray[i] + ""));
        }
        int i = 0;
        for (Vertex vertex : vertexList)
        {
        	if(vertex.realWord.length() == 1)
        		i += vertex.realWord.length();
        	else {
        		vertexs[i].add(vertex);
        		i += vertex.realWord.length();
        	}
        }
    }
	
	public void add(int line, Vertex vertex) {
		vertexs[line].add(vertex);
	}
	
	public List<Vertex>[] getVertexs() {
		return vertexs;
	}
	
	public String toString() {
		StringBuilder sb = new StringBuilder();
		for(List<Vertex> list : vertexs) {
			for(Vertex str : list) {
				sb.append(str.realWord + "\t");
			}
			sb.append("\n");
		}
		sb.append("---------------------");
		return sb.toString();
	}
	
	public String getLongestWord(int position) {
		return vertexs[position].get(vertexs[position].size()-1).realWord;
	}
	
	public List<Vertex> getResult() {
		List<Vertex> list = new ArrayList<Vertex>();
		//StringBuilder sb = new StringBuilder();
		for(int i=0; i<vertexs.length; ) {
			String str = vertexs[i].get(vertexs[i].size()-1).realWord;
			//sb.append(str + "/");
			list.add(new Vertex(str));
			i += str.length();
		}
		//return sb.toString();
		return list;
	}
	
}
