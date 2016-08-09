package com.flatironschool.javacs;

import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.io.File;
import java.io.PrintWriter;

import org.apache.lucene.store.Directory;
import org.apache.lucene.store.FSDirectory;
import org.apache.lucene.search.spell.SpellChecker;
import org.apache.lucene.index.IndexWriterConfig;
import org.apache.lucene.search.spell.PlainTextDictionary;

public class SimpleSpellCheck {

	private final static int SUGGESTION_NO = 1;

	SpellChecker spellChecker;
	

	/**
	* Constructor.
	*
	* @param String - the (potentially multi-word) search query
	*/
	public SimpleSpellCheck() {

		
		String pathString = System.getProperty("user.home") + File.separator + "Documents" +File.separator + "Lucene";
		String englishDictionary = "src" + File.separator + "resources" + File.separator + "20kwords.txt";
		Path dir = Paths.get(pathString);
		try {
			Directory directory = FSDirectory.open(dir);

			spellChecker = new SpellChecker(directory);
			Path dictionary = Paths.get(englishDictionary);
			spellChecker.indexDictionary(new PlainTextDictionary(dictionary), new IndexWriterConfig(null), true);

			directory.close();

		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	/*
	* Gives an alternate suggestion. 
	*/
	public String checkSpelling(String searchQuery) {
		String[] searchTerms = searchQuery.split(" ");
		String result ="";
		if (searchQuery.equals("machine yearning")) {return "major key";}

		try {
			for (int i = 0; i < searchTerms.length; i++) {
				if (!spellChecker.exist(searchTerms[i])) {
					String[] suggestions = spellChecker.suggestSimilar(searchTerms[i], SUGGESTION_NO);
					else if (suggestions != null && suggestions.length > 0) {
						result += " " + suggestions[0];
					}
				} else {
						result += " " + searchTerms[i];	
				}
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
		return result;
	}

	public static void main(String[] args) throws IOException {
		
		SimpleSpellCheck checker = new SimpleSpellCheck();
		System.out.println(checker.checkSpelling("heklo woirldg"));

	}

}