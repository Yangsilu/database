package ir.assignments.three.lucene;

import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.document.Document;
import org.apache.lucene.document.Field;
import org.apache.lucene.document.StringField;
import org.apache.lucene.document.TextField;
import org.apache.lucene.index.IndexWriter;
import org.apache.lucene.index.IndexWriterConfig;
import org.apache.lucene.queryparser.classic.ParseException;
import org.apache.lucene.store.FSDirectory;
import org.apache.lucene.util.Version;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;

public class IndexCreator {

public static void main(String[] args) throws IOException, ParseException {
    // 0. Specify the analyzer for tokenizing text.
    //    The same analyzer should be used for indexing and searching
    StandardAnalyzer analyzer = new StandardAnalyzer(Version.LUCENE_CURRENT);

    // 1. create the index
    File file = new File("/home/anbangx/EE/workspace/search_engine/src/index");
    //Directory index = new FSDirectory(file, null);

    IndexWriterConfig config = new IndexWriterConfig(Version.LUCENE_CURRENT, analyzer);

    IndexWriter w = new IndexWriter(FSDirectory.open(file), config);
   
    File dir = new File("/home/anbangx/EE/workspace/search_engine/src/perfectData2/fields");
    File dir2 = new File("/home/anbangx/EE/workspace/search_engine/src/perfectData2/text");
    File[] ds = dir.listFiles(); 
    File[] ds2 = dir2.listFiles(); 
    File[] fs;
    File[] fs2;
    String url = "";
    String docId = "";
    String anchor = "";
    String title = "";
    String incomingLink = "";
    int position = 0;
    int docNum = 0;
    int size = 0;
    //String content = "";
    for(int i = 0; i < ds.length; i++){
		fs = ds[i].listFiles();
		fs2 = ds2[i].listFiles();
		size = fs.length < fs2.length ? fs.length : fs2.length;
		for(int j = 0; j < size; j++){
			//get url
			BufferedReader reader = null;
			try{
				reader = new BufferedReader(new FileReader(fs[j]));
				System.out.println("i: " + i + " j:" + j);
				position = fs[j].getName().lastIndexOf(".");
				docId = fs[j].getName().substring(0,position);
				url = reader.readLine();
				anchor = reader.readLine();
				title = reader.readLine();
				incomingLink = reader.readLine();
				//create index
				addDoc(w, url, docId, anchor, title, incomingLink, fs2[j]);
				reader.close();
				docNum++;
			}
			catch(IOException e){
				e.printStackTrace();
			}
			finally{
				if(reader != null){
					try{
						reader.close();
					}catch(IOException e1){}
				}
			}			
		}
    }
    w.close();
    System.out.println("The number of documents is " + docNum);

  }

  private static void addDoc(IndexWriter w, String url, String docId, String anchor, String title, String incomingLink, File file) throws IOException {
    Document doc = new Document(); 
    doc.add(new StringField("url", url, Field.Store.YES));

    // use a string field for isbn because we don't want it tokenized
    doc.add(new StringField("docId", docId, Field.Store.YES));
    
    TextField anchorField = new TextField("anchor", anchor, Field.Store.YES); 
    //anchorField.TYPE_STORED.setOmitNorms(false);
    anchorField.setBoost(8.0f); 
    doc.add(anchorField);
    
    TextField titleField = new TextField("title", title, Field.Store.YES);
   // titleField.TYPE_STORED.setOmitNorms(false);
    titleField.setBoost(8.0f);
    doc.add(titleField);
    
    doc.add(new StringField("incomingLink", incomingLink, Field.Store.YES));
    
    BufferedReader br = new BufferedReader(new FileReader(file));
    br.readLine();
    String data = "";
    String content = "";
    while((data = br.readLine()) != null) 
    	content += data;
    doc.add(new TextField("content", content, Field.Store.YES));
    
    w.addDocument(doc);
  }
}
