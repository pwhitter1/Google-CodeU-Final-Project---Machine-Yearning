/**
 *
 */
package com.flatironschool.javacs;

import static org.junit.Assert.*;
import static org.hamcrest.CoreMatchers.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.junit.Before;
import org.junit.Test;

import java.io.IOException;
import redis.clients.jedis.Jedis;

/**
 * @author downey
 *
 */
public class WikiSearchTest {

	private WikiSearch search1;
	private WikiSearch search2;

	/**
	 * @throws java.lang.Exception
	 */
	@Before
	public void setUp() throws Exception {
		Map<String, Integer> map1 = new HashMap<String, Integer>();
		map1.put("Page1", 1);
		map1.put("Page2", 2);
		map1.put("Page3", 3);
		search1 = new WikiSearch(map1);

		Map<String, Integer> map2 = new HashMap<String, Integer>();
		map2.put("Page2", 4);
		map2.put("Page3", 5);
		map2.put("Page4", 7);
		search2 = new WikiSearch(map2);

		String query1 = "java";
		String query2 = "and";
		String query3 = "the";
		String query4 = "philosophy";
	}

	//hard to test because we are only counting terms including in paragraph tags
		//need to choose obscure words, so we can check them individually
	@Test
	public void testTF() throws IOException {
			Jedis jedis = JedisMaker.make();
			JedisIndex index = new JedisIndex(jedis);
			StopWords stop = new StopWords();

			assertThat(stop.getTermFrequency(index, "Java_(programming_language)", "currently"), is(2));
					//the java wikipedia page uses the word "currently" twice
	}

	@Test
	public void testIDF() throws IOException {
			Jedis jedis = JedisMaker.make();
			JedisIndex index = new JedisIndex(jedis);
			StopWords stop = new StopWords();

			assertThat(stop.getInverseDocumentFrequency(index, "currently"), is(0.5118833609788743)); 	//log10(13/4) = 0.51188336097
					//there are a total of 13 documents, three of which contain the word currently at least once
							//test with number 4 as opposed to 3 because in getInverseDocumentFrequency() you started counting from
							//one to avoid dividing by zero
	}

	/**
	 * Test method for {@link com.flatironschool.javacs.WikiSearch#or(com.flatironschool.javacs.WikiSearch)}.
	 */
	@Test
	public void testOr() {
		WikiSearch search = search1.or(search2);
		assertThat(search.getRelevance("Page1"), is(1));
		assertThat(search.getRelevance("Page2"), is(6));
		assertThat(search.getRelevance("Page3"), is(8));
		assertThat(search.getRelevance("Page4"), is(7));
		assertThat(search.getRelevance("Page5"), is(0));
	}

	/**
	 * Test method for {@link com.flatironschool.javacs.WikiSearch#and(com.flatironschool.javacs.WikiSearch)}.
	 */
	@Test
	public void testAnd() {
		WikiSearch search = search1.and(search2);
		assertThat(search.getRelevance("Page1"), is(0));
		assertThat(search.getRelevance("Page2"), is(6));
		assertThat(search.getRelevance("Page3"), is(8));
		assertThat(search.getRelevance("Page4"), is(0));
		assertThat(search.getRelevance("Page5"), is(0));
	}

	/**
	 * Test method for {@link com.flatironschool.javacs.WikiSearch#minus(com.flatironschool.javacs.WikiSearch)}.
	 */
	@Test
	public void testMinus() {
		WikiSearch search = search1.minus(search2);
		assertThat(search.getRelevance("Page1"), is(1));
		assertThat(search.getRelevance("Page2"), is(0));
		assertThat(search.getRelevance("Page3"), is(0));
		assertThat(search.getRelevance("Page4"), is(0));
		assertThat(search.getRelevance("Page5"), is(0));
	}

	/**
	 * Test method for {@link com.flatironschool.javacs.WikiSearch#sort()}.
	 */
	@Test
	public void testSort() {
		List<Entry<String, Integer>> list = search2.sort();
		assertThat(list.get(0).getValue(), is(4));
		assertThat(list.get(1).getValue(), is(5));
		assertThat(list.get(2).getValue(), is(7));
	}
}
