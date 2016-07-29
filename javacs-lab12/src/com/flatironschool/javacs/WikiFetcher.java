package com.flatironschool.javacs;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;

import org.jsoup.Connection;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.util.Map;
import java.util.HashMap;
import java.util.List;
import java.util.ArrayList;


public class WikiFetcher {
	private long lastRequestTime = -1;
	private long minInterval = 1000;

	/**
	 * Fetches and parses a URL string, returning a list of paragraph elements.
	 *
	 * @param url
	 * @return
	 * @throws IOException
	 */
	public Elements fetchWikipedia(String url) throws IOException {
		sleepIfNeeded();

		// download and parse the document
		Connection conn = Jsoup.connect(url);
		Document doc = conn.get();

		// select the content text and pull out the paragraphs.
		Element content = doc.getElementById("mw-content-text");

		// TODO: avoid selecting paragraphs from sidebars and boxouts
		Elements paras = content.select("p");
		return paras;
	}

	/**
	 * Reads the contents of a Wikipedia page from src/resources.
	 *
	 * @param url
	 * @return
	 * @throws IOException
	 */
	public Elements readWikipedia(String url) throws IOException {
		URL realURL = new URL(url);

		// assemble the file name
		String slash = File.separator;
		String filename = "resources" + slash + realURL.getHost() + realURL.getPath();
		System.out.println("Filename: " + filename);

		// read the file
		InputStream stream = WikiFetcher.class.getClassLoader().getResourceAsStream(filename);
		Document doc = Jsoup.parse(stream, "UTF-8", filename);

		// TODO: factor out the following repeated code
		Element content = doc.getElementById("mw-content-text");
		Elements paras = content.select("p");
		return paras;
	}


	/**
	 * Reads the contents of ALL Wikipedia page from src/resources.
	 *
	 * @param url
	 * @return
	 * @throws IOException
	 */
	public Map<String, Elements> readAllWikipedia() throws IOException {
		Map<String, Elements> fileMap = new HashMap<String, Elements>();
		List<String> fileParts = new ArrayList<String>();

		File folder = new File("src/resources/en.wikipedia.org/wiki/");
		File[] fileList = folder.listFiles();

		for (int i = 0; i < fileList.length; i++) {
		  if (fileList[i].isFile()) {
					fileParts.add(fileList[i].getName());
			}
		}

		for(String filePart : fileParts) {
			// assemble the file name
			String filename = "resources/en.wikipedia.org/wiki/" + filePart;

			// read the file
			InputStream stream = WikiFetcher.class.getClassLoader().getResourceAsStream(filename);
			Document doc = Jsoup.parse(stream, "UTF-8", filename);

			// TODO: factor out the following repeated code
			Element content = doc.getElementById("mw-content-text");
			Elements paras = content.select("p");

			fileMap.put(filePart, paras);

		}

		return fileMap;
	}

	/**
	 * Rate limits by waiting at least the minimum interval between requests.
	 */
	private void sleepIfNeeded() {
		if (lastRequestTime != -1) {
			long currentTime = System.currentTimeMillis();
			long nextRequestTime = lastRequestTime + minInterval;
			if (currentTime < nextRequestTime) {
				try {
					//System.out.println("Sleeping until " + nextRequestTime);
					Thread.sleep(nextRequestTime - currentTime);
				} catch (InterruptedException e) {
					System.err.println("Warning: sleep interrupted in fetchWikipedia.");
				}
			}
		}
		lastRequestTime = System.currentTimeMillis();
	}
}
