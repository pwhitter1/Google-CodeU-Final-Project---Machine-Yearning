package com.flatironschool.javacs;

import java.io.IOException;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Arrays;

import redis.clients.jedis.Jedis;
import org.jsoup.select.Elements;


/**
 * Represents the results of a search query.
 *
 */
public class WikiSearch {

	// map from URLs that contain the term(s) to relevance score
	private Map<String, Integer> map;

	/**
	 * Constructor.
	 *
	 * @param map
	 */
	public WikiSearch(Map<String, Integer> map) {
		this.map = map;
	}

	/**
	 * Looks up the relevance of a given URL.
	 *
	 * @param url
	 * @return
	 */
	public Integer getRelevance(String url) {
		Integer relevance = map.get(url);
		return relevance==null ? 0: relevance;
	}

	/**
	 * Prints the contents in order of term frequency.
	 *
	 * @param map
	 */
	private  void print() {
		List<Entry<String, Integer>> entries = sort();
		for (Entry<String, Integer> entry: entries) {
			System.out.println(entry);
		}
	}


	/**
	 * Prints the contents in (descending) order of term frequency.
	 *
	 * @param map
	 */
	private  void printInOrder() {
		//Url || Term Score
		List<Entry<String, Integer>> entries = sort();
		Collections.reverse(entries);
		for (Entry<String, Integer> entry: entries) {
			System.out.println("URL: https://en.wikipedia.org/wiki/" + entry.getKey() + "  ||  TF-IDF: " + entry.getValue());
		}
	}

	/**
	 * Computes the union of two search results.
	 *
	 * @param that
	 * @return New WikiSearch object.
	 */
	public WikiSearch or(WikiSearch that) {
		int relevance;
		Map<String, Integer> unionMap = new HashMap<String, Integer>(that.map);
		for(String key : this.map.keySet()) {
			relevance = totalRelevance(this.getRelevance(key), that.getRelevance(key));
			unionMap.put(key, relevance);
		}
		return new WikiSearch(unionMap);
	}

	/**
	 * Computes the intersection of two search results.
	 *
	 * @param that
	 * @return New WikiSearch object.
	 */
	public WikiSearch and(WikiSearch that) {
		int relevance;
		Map<String, Integer> intersectMap = new HashMap<String, Integer>();
		for(String key : this.map.keySet()) {
			if(that.map.containsKey(key)) {
					relevance = totalRelevance(this.getRelevance(key), that.getRelevance(key));
					intersectMap.put(key, relevance);
			}
		}
		return new WikiSearch(intersectMap);
	}

	/**
	 * Computes the difference of two search results.
	 *
	 * @param that
	 * @return New WikiSearch object.
	 */
	public WikiSearch minus(WikiSearch that) {
		int relevance;
		Map<String, Integer> differenceMap = new HashMap<String, Integer>();
		for(String key : this.map.keySet()) {
				if(!that.map.containsKey(key)) {
					differenceMap.put(key, this.getRelevance(key));
				}
		}
		return new WikiSearch(differenceMap);
	}

	/**
	 * Computes the relevance of a search with multiple terms.
	 *
	 * @param rel1: relevance score for the first search
	 * @param rel2: relevance score for the second search
	 * @return
	 */
	protected int totalRelevance(Integer rel1, Integer rel2) {
		// simple starting place: relevance is the sum of the term frequencies.
		return rel1 + rel2;
	}

	/**
	 * Sort the results by relevance.
	 *
	 * @return List of entries with URL and relevance.
	 */
	public List<Entry<String, Integer>> sort() {
		List<Entry<String, Integer>> entries = new ArrayList<Entry<String, Integer>>(map.entrySet());

		Comparator<Entry<String, Integer>> comparator = new Comparator<Entry<String, Integer>>() {
				@Override
				public int compare(Entry<String, Integer> ent1, Entry<String, Integer> ent2) {

				 		if (ent1.getValue() < ent2.getValue()) {
				 			return -1;
				 		}
				 		if (ent1.getValue() > ent2.getValue()) {
				 			return 1;
				 		}

				 		return 0;
				 }
			};


		Collections.sort(entries, comparator);
		return entries;

	}


	/**
	 * Performs a search and makes a WikiSearch object.
	 *
	 * @param term
	 * @param index
	 * @return
	 */
	public static WikiSearch search(String term, JedisIndex index) {
		Map<String, Integer> map = index.getCounts(term);
		return new WikiSearch(map);
	}

	public static WikiSearch searchTFIDF(String term, JedisIndex index) {
		if(index.getCountsTFIDF(term) == null) { return null; }
		Map<String, Integer> map = index.getCountsTFIDF(term);
		return new WikiSearch(map);
	}


	/* Index all of the pages locally stored in the resources folder
	    	- only need to index once */
	public static void indexResourcesFolder(JedisIndex index) throws IOException {
		/*index.deleteURLSets();
		index.deleteAllKeys();
		index.deleteTermCounters();*/

		WikiFetcher wf = new WikiFetcher();
		for(Entry<String, Elements> entry : wf.readAllWikipedia().entrySet()) {
			index.indexPage(entry.getKey(), entry.getValue());
		}
	}



	public static void main(String[] args) throws IOException {

		// make a JedisIndex
		Jedis jedis = JedisMaker.make();
		JedisIndex index = new JedisIndex(jedis);



		/* Index all of the wiki pages in the resource folder */
		//indexResourcesFolder(index);



		/* TF - IDF */
		List<String> queryList = new ArrayList<String>();
		queryList.addAll(Arrays.asList("java", "and", "the", "philosophy", "currently"));

			for(String query : queryList) {
				/*If the query has a document with a negative TF-IDF score (searchTFIDF() returns null),
					cease searching on that query - that word is a stop word */
				if(searchTFIDF(query, index) == null) {
					System.out.println("Ignored Query: " + query);
					continue;
				} else {
					System.out.println("Query: " + query);
					WikiSearch search = searchTFIDF(query, index);
					search.printInOrder();
				}
			}


		//TF-IDF
		/*String term4 = "java";
		System.out.println("Query: " + term4);
		WikiSearch search4 = searchTFIDF(term4, index);
		search4.printInOrder();

		String term5 = "and";
		System.out.println("Query: " + term5);
		WikiSearch search5 = searchTFIDF(term5, index);
		search5.printInOrder();

		String term6 = "the";
		System.out.println("Query: " + term6);
		WikiSearch search6 = searchTFIDF(term6, index);
		search6.printInOrder();

		String term7 = "philosophy";
		System.out.println("Query: " + term7);
		WikiSearch search7 = searchTFIDF(term7, index);
		search7.printInOrder();*/


		//TF (old)
		/* // search for the first term
		String term1 = "java";
		System.out.println("Query: " + term1);
		WikiSearch search1 = search(term1, index);
		search1.print();

		// search for the second term
		String term2 = "programming";
		System.out.println("Query: " + term2);
		WikiSearch search2 = search(term2, index);
		search2.print();

		// compute the intersection of the searches
		System.out.println("Query: " + term1 + " AND " + term2);
		WikiSearch intersection = search1.and(search2);
		intersection.print(); */
	}
}
