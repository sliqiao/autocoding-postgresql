package com.autocoding.hutool;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

import org.junit.Test;
import org.openjdk.jmh.annotations.Benchmark;
import org.openjdk.jmh.annotations.BenchmarkMode;
import org.openjdk.jmh.annotations.Mode;
import org.openjdk.jmh.annotations.OutputTimeUnit;
import org.openjdk.jmh.annotations.Scope;
import org.openjdk.jmh.annotations.State;
import org.openjdk.jmh.runner.Runner;
import org.openjdk.jmh.runner.options.Options;
import org.openjdk.jmh.runner.options.OptionsBuilder;

@BenchmarkMode(value = { Mode.AverageTime })
@OutputTimeUnit(TimeUnit.MILLISECONDS)
@State(Scope.Thread)
public class 普通关键字过滤Test {

	private static List<String> keywords = new ArrayList<String>();
	static {
		keywords.add("先帝");
		keywords.add("大业");
		keywords.add("三国");
		keywords.add("艰难");
		keywords.add("国家");
		keywords.add("国家危急");
		keywords.add("现在天下分为三国，指蜀汉国力薄弱，处境艰难。这确实是国家危急存亡的时期啊");
		keywords.add("现在天下分为三国，指蜀汉国力薄弱，处境艰难。这确实是国家危急存亡的时期啊1");
		keywords.add("现在天下分为三国，指蜀汉国力薄弱，处境艰难。这确实是国家危急存亡的时期啊2");
		keywords.add("现在天下分为三国，指蜀汉国力薄弱，处境艰难。这确实是国家危急存亡的时期啊3");
	}

	public static void main(String[] args) throws Exception {
		String name = 普通关键字过滤Test.class.getName();
		Options options = new OptionsBuilder().include(name).forks(1).measurementIterations(3).warmupIterations(1)
				.build();
		new Runner(options).run();
	}

	@Benchmark
	@Test
	public void 普通测试() {
		String text = "先帝开创的大业未完成一半却中途去世了。现在天下分为三国，指蜀汉国力薄弱，处境艰难。这确实是国家危急存亡的时期啊。不过宫廷里侍从护卫的官员不懈怠，战场上忠诚有志的将士们奋不顾身，大概是他们追念先帝对他们的特别的知遇之恩（作战的原因），想要报答在陛下您身上。（陛下）你实在应该扩大圣明的听闻，来发扬光大先帝遗留下来的美德，振奋有远大志向的人的志气，不应当随便看轻自己，说不恰当的话，以致于堵塞人们忠心地进行规劝的言路。皇宫中和朝廷里的大臣，本都是一个整体，奖惩功过，不应有所不同。如有作恶违法的人，或行为忠善的人，都应该交给主管官吏评定对他们的惩奖，以显示陛下处理国事的公正严明,而不应当有偏袒和私心，使宫内和朝廷奖罚方法不同。侍中、侍郎郭攸之、费祎、董允等人，都是善良诚实、心志忠贞纯洁的人，他们的志向和心思忠诚无二。因此先帝选拔他们留给陛下。我认为宫中之事，无论事情大小，都拿来跟他商讨，这样以后再去实施，一定能够弥补缺点和疏漏之处。得到更多的好处。将军向宠，性格和品行善良公正，精通军事，从前任用时，先帝称赞他很有才能，因此众人商议推举他做中部督。我认为禁军营中的事，都拿来跟他商讨，就一定能使军队团结一心，不同才能的人各得其所。亲近贤臣，疏远小人，这是前汉所以兴盛的原因；亲近小人，疏远贤臣，这是后汉之所以衰败的原因。先帝在世的时候，每逢跟我谈论这些事情，未尝不叹息而痛恨桓帝、灵帝时期的腐败。侍中、尚书、长史、参军，这些人都是忠贞善良、守节不逾的大臣，望陛下亲近他们，信任他们，那么汉朝的复兴，就指日可待了。我本来是平民，在南阳亲自耕田，在乱世中苟且保全性命，不奢求在诸侯之中出名。先帝不因为我身份卑微，屈尊下驾来看我，三次去我的茅庐拜访我，征询我对时局大事的意见，我因此十分感动，就答应为先帝奔走效劳。后来遇到兵败，在兵败的时候接受任务，形势危急之时奉命出使，从这以来二十一年了。先帝知道我做事小心谨慎，所以临终时把国家大事托付给我。接受遗命以来，我日夜忧虑叹息，只怕先帝托付给我的大任不能实现，以致损伤先帝的知人之明，所以我五月渡过泸水，深入到人烟稀少的地方。现在南方已经平定，兵员装备已经充足，应当激励将领士兵，平定中原，希望用尽我平庸的才能，铲除奸邪凶恶的敌人，兴复汉室的任务托付给我，返还旧都.这是我用以报答先帝尽忠陛下的职责。至于处理事务，斟酌情理，毫无保留地贡献忠言，那是郭攸之、费祎、董允的责任。希望陛下能够把讨伐曹魏，兴复汉室的任务托付给我，若不能完成，就治我的罪，（从而）用来告慰先帝的在天之灵。如果没有振兴圣德的建议，那就责备郭攸之、费祎、董允等人的怠慢。来揭示他们的过失；陛下也应自行谋划，毫无保留地进献忠诚的建议，采纳正确的言论，深切追念先帝临终留下的教诲。我感激不尽。今天(我)将要告别陛下远行了，面对这份奏表禁不住热泪纵横，也不知说了些什么。";
		List<String> hitList = new ArrayList<>();
		for (String keyword : keywords) {
			if (text.contains(keyword)) {
				hitList.add(keyword);
			}
		}
		System.err.println(hitList);

	}

}
