package edig.datasets;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;

import org.jsoup.Jsoup;

import edig.entites.Document;
import edig.entites.DocumentManager;

public class UWCANDataset implements DatasetLoader {
	private ArrayList<Document> documents ;
	private String datasetPath;
	
	public UWCANDataset(String path) {
		this.datasetPath = path;
		this.documents = new ArrayList<Document>();
	}
	
	
	@Override
	public ArrayList<Document> loadDocument() throws Exception {
		File dir = new File(this.datasetPath);
    String[] categories = dir.list();
    for (int i = 0; i < categories.length; i++) {
    	String categoryName = categories[i];
  		String[] docsInCategory = new File(this.datasetPath+"/"+categoryName).list();
    	for (int j = 0; j < docsInCategory.length; j++) {
    		String documentName = docsInCategory[j];
    		File input = new File(this.datasetPath+"/"+categoryName+"/"+documentName);
    		org.jsoup.nodes.Document doc = Jsoup.parse(input, "UTF-8", "");
				String title = doc.head().text();
				String body = doc.body().text();
				System.out.println(categoryName+"  "+ documentName);
				Document stemmedDocument = DocumentManager.createDocument(documentName, title, body);
				stemmedDocument.setOrginalCluster(categoryName);
				this.documents.add(stemmedDocument);
			}// end loop over documents
		}// end loop over categories
		return this.documents;
	}
	
	public static void main(String[] args) throws Exception {
		UWCANDataset dataset = new UWCANDataset("/media/disk/master/Master/datasets/WU-CAN/webdata");
		ArrayList<Document> docs = dataset.loadDocument();
	//	System.out.println(docs.get(0).getSentences().get(0));
	}

}
