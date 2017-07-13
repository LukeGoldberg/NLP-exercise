package test;

import java.io.IOException;
import java.util.List;

import com.lonely.nlp.corpus.WordNet;
import com.lonely.nlp.dijkstra.Dijkstra;
import com.lonely.nlp.dijkstra.att.Vertex;
import com.lonely.nlp.recognition.nr.PersonRecognition;

public class TestSegment {
	
	static Dijkstra dijkstra = Dijkstra.getDijkstra();
	
	public static void seg(String str) throws IOException {
		List<Vertex> v = dijkstra.dijkstra(str);
		WordNet wordNet = new WordNet(str.toCharArray(), v);
		v = PersonRecognition.parsePattern(v, wordNet);
		wordNet = new WordNet(str.toCharArray(), v);
		System.out.println(v);
	}

	public static void main(String[] args) throws IOException {
		
		String[] sentence = {
				"马小东和邓超在划水。",
				"张浩和胡健康复员回家了",
				"长春市长春药店出售狗皮膏药，不卖狗头，店长叫张耀飞，写过一本单词书。",
				"团购网站的本质是什么？",
				"加快完成都市化",
				"用户来电表示电信10001在这两天凌晨都给用户下发短信",
				"龚学平等领导观看了比赛",
				"我爱北京天安门，天安门上红旗飘。伟大领袖毛主席，指引我们向前进。",
				"中科院预测科学研究中心学术委员会",
				"你在一汽马自达汽车销售有限公司上班吧",
				"张克智与潍坊地铁建设工程公司",
				"工信处女干事来到这里参加活动。",
				"陈膺奥部长和张婧",
				"济南杨铭宇餐饮管理有限公司是由杨先生创办的餐饮企业",
				"曾幻想过，若干年后的我就是这个样子的吗",
				"江西鄱阳湖干枯，中国最大淡水湖变成大草原",
				"王总和小丽结婚了。",
				"让我们一块乐享4G吧",

				"抗战末期，一群溃败下来的国民党士兵聚集在西南小镇禅达的收容所里，他们被几年来国土渐次沦丧弄得毫无斗志，只想苟且偷生。而日本人此时已经逼近国界，打算切断中国与外界的联系。",
				"收容所里聚集了各色人物：孟烦了、迷龙、不辣、郝兽医、阿译等等。他们混日子，他们不愿面对自己内心存有的梦，那就是再跟日本人打一仗，打败日本人。因为他们已经不抱有任何希望了。他们活得像人渣，活着跟死了也差不多。",
				"师长虞啸卿出现了，他要重建川军团。但真正燃起这群人斗志的是嬉笑怒骂、不惜使用下三滥手段的龙文章。龙文章成了他们的团长，让这群人渣重燃斗志，变成勇于赴死之人。",
				"这些人从一开始就知道，自己的命运就是炮灰的命运，他们面对的是一场几乎必死无疑的战争。",
				"这是迄今为止，唯一一部对得起“中国远征军”这五个字的中国远征军题材的小说。",
				"列宁在评价高尔基的《母亲》时说：“这是一本及时的书。”今时今日，在尤其需要我们对未来抱有信心的时候，本书也当得起这一评价。",
				
				"续梅表示，教育部领导对公开信“很重视”。袁贵仁部长前一段就社会各界对中国教育提出的意件和建议有个回应：“非常受教育，非常受启发，也非常受鼓舞”，续梅说，“我想其中也包括安徽11名教授的公开信。”  意见",
		};
		
		for(String str : sentence) {
			seg(str);
		}
		
	}
	
}
