package com.pkg.luceneapp;

import java.io.File;
import java.nio.file.Path;
import java.nio.file.Paths;

import org.apache.lucene.analysis.core.SimpleAnalyzer;
import org.apache.lucene.document.Document;
import org.apache.lucene.index.DirectoryReader;
import org.apache.lucene.index.IndexReader;
import org.apache.lucene.queryparser.classic.QueryParser;
import org.apache.lucene.search.IndexSearcher;
import org.apache.lucene.search.Query;
import org.apache.lucene.search.ScoreDoc;
import org.apache.lucene.search.TopDocs;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.FSDirectory;
import org.apache.lucene.util.Version;

public class SimpleSearcher {

	public SimpleSearcher() {
		// TODO Auto-generated constructor stub
	}

	public static void main2(String[] args) throws Exception {

		File indexDir = new File("/Users/abhinavjha/Documents/index/");
		Path path=Paths.get("/Users/abhinavjha/Documents/index/");
		String query = "Car";
		int hits = 100;
		SimpleSearcher searcher = new SimpleSearcher();
		searcher.searchIndex(path, query, hits, new SimpleAnalyzer());
		
	}
	
	public static void main(String[] args) throws Exception {
		Path path=Paths.get("/Users/abhinavjha/Documents/index/");
		String query = "App";
		int hits = 100;
		SimpleSearcher searcher = new SimpleSearcher();
		searcher.searchIndexFromDB(path, query, hits, new SimpleAnalyzer());
		
	}
	
	
	private void searchIndexFromDB(Path indexDir, String queryStr, int maxHits,SimpleAnalyzer analyzer) 
            throws Exception {
        
        Directory directory = FSDirectory.open(indexDir);
        IndexReader ireader=DirectoryReader.open(directory);
        IndexSearcher searcher = new IndexSearcher(ireader);
        QueryParser parser = new QueryParser("appname",analyzer);
        Query query = parser.parse(queryStr);
        
        TopDocs topDocs = searcher.search(query, maxHits);
        
        ScoreDoc[] hits = topDocs.scoreDocs;
        for (int i = 0; i < hits.length; i++) {
            int docId = hits[i].doc;
            Document d = searcher.doc(docId);
            System.out.println(d.get("appname"));
        }
        
        System.out.println("Found " + hits.length);
        
    }
	

	private void searchIndex(Path indexDir, String queryStr, int maxHits,SimpleAnalyzer analyzer) 
            throws Exception {
        
        Directory directory = FSDirectory.open(indexDir);
        IndexReader ireader=DirectoryReader.open(directory);
        IndexSearcher searcher = new IndexSearcher(ireader);
        QueryParser parser = new QueryParser("content",analyzer);
        Query query = parser.parse(queryStr);
        
        TopDocs topDocs = searcher.search(query, maxHits);
        
        ScoreDoc[] hits = topDocs.scoreDocs;
        for (int i = 0; i < hits.length; i++) {
            int docId = hits[i].doc;
            Document d = searcher.doc(docId);
            System.out.println(d.get("filename"));
        }
        
        System.out.println("Found " + hits.length);
        
    }
}
