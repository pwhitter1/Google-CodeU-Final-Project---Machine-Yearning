package com.flatironschool.javacs;

import java.util.Map;
import java.util.Set;

/*
*   Identifies the stop words of a set of documents
 */
public class StopWords {

    public StopWords() {}

    /* Returns the term frequency */
    public Integer getTermFrequency(JedisIndex index, String url, String term) {
      //System.out.println("TF: " + index.getCount(url, term));

      ///get term count for the document
      return index.getCount(url, term);
    }

    /* Returns the inverse document frequency */
    public Double getInverseDocumentFrequency(JedisIndex index, String term) {
      //Get total number of documents (in resources)
      double docNum = 15.0;

      //Get total number of documents that contain search term
      int docTally = 1; //denominator adjustment so there is no division by zero
      Set<String> urls = index.getURLs(term);
      for (String url : urls) {
        if(index.getCount(url, term) > 0) {
          docTally++;
        }
      }

      return Math.log10(docNum/docTally);

    }

    /* Returns the TF-IDF */
    public Double getTFIDF (JedisIndex index, String url, String term) {
      //System.out.println("TF-IDF: " + getTermFrequency(index, url, term) * getInverseDocumentFrequency(index, term));

      double TFIDF = getTermFrequency(index, url, term) * getInverseDocumentFrequency(index, term);

      //cease calculations if the (pre-) TFIDF for the query of one document is not sufficiently high
      if(TFIDF == 0 || TFIDF < 0) { return null; }
      return TFIDF;
    }

}
