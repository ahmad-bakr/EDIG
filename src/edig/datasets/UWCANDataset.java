package edig.datasets;

import java.io.File;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.Hashtable;
import org.jsoup.Jsoup;
import edig.entites.Document;
import edig.entites.DocumentManager;

public class UWCANDataset implements DatasetLoader {
	private Hashtable<String, Document> documentHash;
	private String datasetPath;
	private int numberOfDocument;
	private ArrayList<String> originalClasses ;
	private Hashtable<String, Integer> classDocumentCount;
	private ArrayList<String> documentsIDS;
	
	public UWCANDataset(String path) {
		this.documentsIDS = new ArrayList<String>();
		this.originalClasses = new ArrayList<String>();
		this.datasetPath = path;
		this.documentHash = new Hashtable<String, Document>();
		this.numberOfDocument =0;
		this.classDocumentCount = new Hashtable<String, Integer>();
	}

	@Override
	public ArrayList<String> getOriginalClasses() {
		return this.originalClasses;
	}
	
	@Override	
	public ArrayList<String> getDocumentsIDS() {
		return documentsIDS;
	}

	@Override
	public int getNumberOfDocumentsInClass(String className) {
		// TODO Auto-generated method stub
		return this.classDocumentCount.get(className);
	}

	
	@Override
	public Hashtable<String, Document> loadDocuments() throws Exception {
		File dir = new File(this.datasetPath);
    String[] categories = dir.list();
    for (int i = 0; i < categories.length; i++) {
    	String categoryName = categories[i];
    	this.originalClasses.add(categoryName);
  		String[] docsInCategory = new File(this.datasetPath+"/"+categoryName).list();
  		this.classDocumentCount.put(categoryName, docsInCategory.length);
    	for (int j = 0; j < docsInCategory.length; j++) {
    		this.numberOfDocument++;
    		String documentName = docsInCategory[j];
    		File input = new File(this.datasetPath+"/"+categoryName+"/"+documentName);
    		org.jsoup.nodes.Document doc = Jsoup.parse(input, "UTF-8", "");
    		String title = categoryName+" "+ doc.head().text().replaceAll("(\\r|\\n)", ". ");
				String body = getFirstNWords(doc.body().text().replaceAll("(\\r|\\n)", ". "));
				System.out.println(title);
				System.out.println(body);
				Document stemmedDocument = DocumentManager.createDocument(documentName, title, body);
				stemmedDocument.setOrginalCluster(categoryName);
				this.documentHash.put(stemmedDocument.getId(),stemmedDocument);
				this.documentsIDS.add(stemmedDocument.getId());
			}// end loop over documents
		}// end loop over categories
		return this.documentHash;
	}
	
	@Override
	public Document getDocument(String documentID) {
		return this.documentHash.get(documentID);
	}
	
	private String getFirstNWords(String s){
		String [] arr = s.split(" ");
		String str = "";
		for (int i = 0; i < 200; i++) {
			if (i >= arr.length) break;
			str += arr[i];
		}
		
		return str;
	}
	
	@Override
	public int numberOfDocuments() {
		return this.numberOfDocument;
	}


	public static void main(String[] args) throws Exception {
		UWCANDataset dataset = new UWCANDataset("/media/disk/master/Master/datasets/WU-CAN/webdata");
		dataset.loadDocuments();
		//		Hashtable<String, Document> docsHash = dataset.loadDocuments();
//		Enumeration e = docsHash.keys();
//		Document doc = docsHash.get(e.nextElement());
//		System.out.println(doc.getOrginalCluster()+ "  "+ doc.getId());
//		System.out.println("********************");
//		for (int i = 0; i < doc.getSentences().size(); i++) {
//			for (int j = 0; j < doc.getSentences().get(i).getWords().size(); j++) {
//				System.out.print(doc.getSentences().get(i).getWords().get(j).getContent() + " ");
//			}
//			System.out.println("");
//		}
		
	}








}
