package com.pkg.luceneapp;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.nio.file.Paths;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import org.apache.lucene.analysis.core.SimpleAnalyzer;
import org.apache.lucene.document.Document;
import org.apache.lucene.document.Field;
import org.apache.lucene.index.IndexWriter;
import org.apache.lucene.index.IndexWriterConfig;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.FSDirectory;

public class SimpleFileIndexer {

	public SimpleFileIndexer() {
		// TODO Auto-generated constructor stub
	}

	public static void main(String[] args) throws SQLException, IOException {
		SimpleFileIndexer sfi = new SimpleFileIndexer();
		SimpleAnalyzer san = new SimpleAnalyzer();
		Directory directory=FSDirectory.open(Paths.get("/Users/abhinavjha/Documents/index"));
		IndexWriterConfig writerConfig=new IndexWriterConfig(san);
		IndexWriter indexWriter=new IndexWriter(directory,writerConfig);
		sfi.createDocument(sfi.populateDatabase(),indexWriter);
		
	}
	
	public static void main2(String[] args) throws IOException {

		File indexDir = new File("/Users/abhinavjha/Documents/index");
		File dataDir = new File("/Users/abhinavjha/Documents/workspace");
		String suffix = "java";
		SimpleFileIndexer sfi = new SimpleFileIndexer();
		SimpleAnalyzer san = new SimpleAnalyzer();
		Directory directory=FSDirectory.open(Paths.get("/Users/abhinavjha/Documents/index"));
		IndexWriterConfig writerConfig=new IndexWriterConfig(san);
		IndexWriter indexWriter=new IndexWriter(directory,writerConfig);

		sfi.indexTheDataDirectory(indexWriter,dataDir,suffix);
		
		System.out.println(indexWriter.maxDoc());
		indexWriter.close();
	}
	// Loop through the data directory and create index for these Documents

	private void indexTheDataDirectory(IndexWriter indexWriter, File dataDir, 
	           String suffix) throws IOException {

	        File[] files = dataDir.listFiles();
	        for (int i = 0; i < files.length; i++) {
	            File f = files[i];
	            if (f.isDirectory()) {
	                indexTheDataDirectory(indexWriter, f, suffix);
	            }
	            else {
	                indexFileWithIndexWriter(indexWriter, f, suffix);
	            }
	        }

	}
	
	
	private ResultSet populateDatabase() throws SQLException{
		ResultSet rs=null;
		try {
			Class.forName("com.mysql.jdbc.Driver");
			Connection con=DriverManager.getConnection("jdbc:mysql://localhost/RiskAssessment","root","root");
			PreparedStatement pstmt=con.prepareStatement("select * from Application");
			rs=pstmt.executeQuery();
		} catch (ClassNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return rs;
	}
	
	//index particular file and check if its type matches the suffix
	private void indexFileWithIndexWriter(IndexWriter indexWriter, File f, 
            String suffix) throws IOException {

        if (f.isHidden() || f.isDirectory() || !f.canRead() || !f.exists()) {
            return;
        }
        if (suffix!=null && !f.getName().endsWith(suffix)) {
            return;
        }
        System.out.println("Indexing file " + f.getCanonicalPath());
        
        
       createDocument(f, indexWriter);

    }
	
	
	private void createDocument(ResultSet rs,IndexWriter indexWriter) throws IOException, SQLException{
		while(rs.next()){
			System.out.println("Creating document");
			Document dbDocument=new Document();
			dbDocument.add(new Field("id",rs.getString("application_id"),org.apache.lucene.document.TextField.TYPE_STORED));
			dbDocument.add(new Field("appname",rs.getString("application_name"),org.apache.lucene.document.TextField.TYPE_STORED));
			//dbDocument.add(new Field("notes",rs.getString("notes"),org.apache.lucene.document.TextField.TYPE_STORED));
			indexWriter.addDocument(dbDocument);
		}
		rs.close();
		indexWriter.close();
		
	}
	
	private void createDocument(File sjp, IndexWriter indexWriter) throws IOException {
	    BufferedReader reader = new BufferedReader(new FileReader(sjp));
	    Document dictionary = new Document();
	    String readLine = null;
	    StringBuffer sb=new StringBuffer();
	    dictionary.add(new Field("filename",sjp.getCanonicalPath(),org.apache.lucene.document.TextField.TYPE_STORED));
	    while((readLine = reader.readLine()) != null) {
	        readLine = readLine.trim();
	        System.out.println(readLine);
	        sb.append(readLine);
	    }
	    dictionary.add(new Field("content", sb.toString(),org.apache.lucene.document.TextField.TYPE_STORED)); 
	    indexWriter.addDocument(dictionary);
	    reader.close();
	}
	
}
