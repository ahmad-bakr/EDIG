package edig.datasets;

import java.io.File;
import java.util.Enumeration;
import java.util.Hashtable;
import org.jsoup.Jsoup;
import edig.entites.Document;
import edig.entites.DocumentManager;

public class UWCANDataset implements DatasetLoader {
	private Hashtable<String, Document> documentHash;
	private String datasetPath;
	private int numberOfDocument;
	
	public UWCANDataset(String path) {
		this.datasetPath = path;
		this.documentHash = new Hashtable<String, Document>();
		this.numberOfDocument =0;
	}
	
	
	@Override
	public Hashtable<String, Document> loadDocument() throws Exception {
		File dir = new File(this.datasetPath);
    String[] categories = dir.list();
    for (int i = 0; i < categories.length; i++) {
    	String categoryName = categories[i];
  		String[] docsInCategory = new File(this.datasetPath+"/"+categoryName).list();
    	for (int j = 0; j < docsInCategory.length; j++) {
    		this.numberOfDocument++;
    		String documentName = docsInCategory[j];
    		File input = new File(this.datasetPath+"/"+categoryName+"/"+documentName);
    		org.jsoup.nodes.Document doc = Jsoup.parse(input, "UTF-8", "");
    		String title = doc.head().text().replaceAll("(\\r|\\n)", ". ");;
				String body = doc.body().text().replaceAll("(\\r|\\n)", ". ");;
				Document stemmedDocument = DocumentManager.createDocument(documentName, title, body);
				stemmedDocument.setOrginalCluster(categoryName);
				this.documentHash.put(stemmedDocument.getId(),stemmedDocument);
			}// end loop over documents
		}// end loop over categories
		return this.documentHash;
	}
	
	@Override
	public Document getDocument(String documentID) {
		return this.documentHash.get(documentID);
	}
	
	@Override
	public int numberOfDocuments() {
		return this.numberOfDocument;
	}


	public static void main(String[] args) throws Exception {
		UWCANDataset dataset = new UWCANDataset("/media/disk/master/Master/datasets/WU-CAN/webdata");
		Hashtable<String, Document> docsHash = dataset.loadDocument();
		Enumeration e = docsHash.keys();
		Document doc = docsHash.get(e.nextElement());
		System.out.println(doc.getOrginalCluster()+ "  "+ doc.getId());
		System.out.println("********************");
		for (int i = 0; i < doc.getSentences().size(); i++) {
			for (int j = 0; j < doc.getSentences().get(i).getWords().size(); j++) {
				System.out.print(doc.getSentences().get(i).getWords().get(j).getContent() + " ");
			}
			System.out.println("");
		} 


	}





}
