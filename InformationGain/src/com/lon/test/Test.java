package com.lon.test;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.util.List;

import com.lon.word.WordSegment;

public class Test {
	
	private void deal(String path) {
		try(BufferedReader br = new BufferedReader(new InputStreamReader(new FileInputStream(path), "utf-8"));) {
			StringBuilder sb = new StringBuilder();
			String line;
			while((line = br.readLine()) != null)
				sb.append(line);
			String doc = sb.toString();
			WordSegment ws = new WordSegment.Builder(doc).maxWordLen(3).minAggregation(5.8).minEntropy(1.6).build();
			ws.printWords("D:\\搜狗高速下载\\红楼梦keyword.txt");
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public static void main(String[] args) {
		// TODO Auto-generated method stub
		Test application = new Test();
		application.deal("D:\\搜狗高速下载\\红楼梦.txt");

	}

}
