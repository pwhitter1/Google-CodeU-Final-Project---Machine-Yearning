/**
 *
 */
package com.flatironschool.javacs;

import static org.junit.Assert.*;
import static org.hamcrest.CoreMatchers.*;

import java.io.IOException;
import java.util.Map;
import java.util.Set;

import org.jsoup.select.Elements;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import redis.clients.jedis.Jedis;

/**
 * @author downey
 *
 */
public class JedisIndexTest {

	private static String url1, url2;
	private Jedis jedis;
	private JedisIndex index;

	/**
	 * @throws java.lang.Exception
	 */
	@Before
	public void setUp() throws Exception {
		jedis = JedisMaker.make();
		index = new JedisIndex(jedis);

		loadIndex(index);
	}

	/**
	 * Loads the index with two pages read from files.
	 *
	 * @return
	 * @throws IOException
	 */
	private static void loadIndex(JedisIndex index) throws IOException {
		WikiFetcher wf = new WikiFetcher();

		url1 = "https://en.wikipedia.org/wiki/Java_(programming_language)";
		Elements paragraphs = wf.readWikipedia(url1);
		index.indexPage(url1, paragraphs);

		url2 = "https://en.wikipedia.org/wiki/Programming_language";
		paragraphs = wf.readWikipedia(url2);
		index.indexPage(url2, paragraphs);
	}

	/**
	 * @throws java.lang.Exception
	 */
	@After
	public void tearDown() throws Exception {
		jedis.close();
	}

	/**
	 * Test method for {@link com.flatironschool.javacs.JedisIndex#getUrls(java.lang.String)}.
	 */
	@Test
	public void testGetURLs() {
		//get the urls that contain the indicator word (only input one-word string)
		Set<String> set1 = index.getURLs("the");
		Set<String> set2 = index.getURLs("java");
		Set<String> set3 = index.getURLs("programming");
		Set<String> set4 = index.getURLs("pie");
		Set<String> set5 = index.getURLs("yoga");

		Set<String> set6 = index.getURLs("tape");
		Set<String> set7 = index.getURLs("dependencies");


		//check that the url sets contain the correct urls
		assertThat(set1, hasItems("https://en.wikipedia.org/wiki/Java_(programming_language)", "https://en.wikipedia.org/wiki/Programming_language"));
		assertThat(set2, hasItems("https://en.wikipedia.org/wiki/Java_(programming_language)", "https://en.wikipedia.org/wiki/Programming_language"));
		assertThat(set3, hasItems("https://en.wikipedia.org/wiki/Java_(programming_language)", "https://en.wikipedia.org/wiki/Programming_language"));
		assertThat(set4, not(hasItems("https://en.wikipedia.org/wiki/Java_(programming_language)", "https://en.wikipedia.org/wiki/Programming_language")));
		assertThat(set5, not(hasItems("https://en.wikipedia.org/wiki/Java_(programming_language)", "https://en.wikipedia.org/wiki/Programming_language")));

		assertThat(set6, hasItem("https://en.wikipedia.org/wiki/Programming_language"));
		assertThat(set6, not(hasItem("https://en.wikipedia.org/wiki/Java_(programming_language)")));
		assertThat(set7, hasItem("https://en.wikipedia.org/wiki/Java_(programming_language)"));
		assertThat(set7, not(hasItem("https://en.wikipedia.org/wiki/Programming_language")));
	}

	/**
	 * Test method for {@link com.flatironschool.javacs.JedisIndex#getCounts(java.lang.String)}.
	 */
	@Test
	public void testGetCounts() {
		Map<String, Integer> map = index.getCounts("the");
		assertThat(map.get(url1), is(339));
		assertThat(map.get(url2), is(264));
	}
}
