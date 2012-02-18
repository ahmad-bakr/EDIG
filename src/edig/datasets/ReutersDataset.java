package edig.datasets;

import java.io.BufferedReader;
import java.io.DataInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Hashtable;

import edig.entites.Document;
import edig.entites.DocumentManager;

public class ReutersDataset extends Dataset{

	String originalDatasetPath = "/media/disk/master/Master/datasets/reuters_mod";
	String modifiedDatasetPath = "/media/disk/master/Noe4j/datasets/reuters_mod";

	private Hashtable<String, Document> documentHash;
	private String datasetPath;
	private int numberOfDocument;
	private ArrayList<String> originalClasses ;
	private Hashtable<String, Integer> classDocumentCount;
	private ArrayList<String> documentsIDS;
	
	public ReutersDataset(String path) {
		this.documentsIDS = new ArrayList<String>();
		this.originalClasses = new ArrayList<String>();
		this.datasetPath = path;
		this.documentHash = new Hashtable<String, Document>();
		this.numberOfDocument =0;
		this.classDocumentCount = new Hashtable<String, Integer>();

	}
	
	public void createNewDataset(){
		super.createDataset(originalDatasetPath, modifiedDatasetPath, 20);
	}

	
	public void prepareDataset() throws IOException{
		int numberOfDocument = 0;
		String documentsPath = "/media/disk/master/Master/datasets/reuters/adapted_docs";
		String topicsPath = "/media/disk/master/Master/datasets/reuters/adapted_topics";
		String newDatasetPath = "/media/disk/master/Noe4j/datasets/reuters";
		
		String[] topicsList = new File(topicsPath).list();
		for (int i = 0; i < topicsList.length; i++) {
			String topicName = topicsList[i];
			//create folder for topic
			boolean createNewDir = (new File(newDatasetPath+"/"+topicName)).mkdirs();
			//read the documents ids of this topic
			 FileInputStream fstream = new FileInputStream(topicsPath+"/"+topicName);
			 DataInputStream in = new DataInputStream(fstream);
			 BufferedReader br = new BufferedReader(new InputStreamReader(in));
			 String documentId;
			 while ((documentId = br.readLine()) != null)   {
				  // Print the content on the console
				 numberOfDocument++;
				 super.copyfile(documentsPath+"/"+documentId.trim(), newDatasetPath+"/"+topicName+"/"+documentId.trim());
			 }
			in.close();
			
		}
		System.out.println("Number of documents = " + numberOfDocument);
	}
	
	@Override
	public Document getDocument(String documentID) {
		return this.documentHash.get(documentID);
	}

	@Override
	public int numberOfDocuments() {
		return this.numberOfDocument;
	}

	@Override
	public ArrayList<String> getDocumentsIDS() {
		return documentsIDS;
	}

	@Override
	public int getNumberOfDocumentsInClass(String className) {
	 return this.classDocumentCount.get(className);
	}

	@Override
	public ArrayList<String> getOriginalClasses() {
		return this.originalClasses;
	}
	
	
	private String getFirstNWords(String s){
		String [] arr = s.split(" ");
		String str = "";
		for (int i = 0; i < 250; i++) {
			if (i >= arr.length) break;
			str += arr[i];
		}
		
		return str;
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
    		FileInputStream fstream = new FileInputStream(this.datasetPath+"/"+categoryName+"/"+documentName);
        DataInputStream in = new DataInputStream(fstream);
        BufferedReader br = new BufferedReader(new InputStreamReader(in));
        String str;    		
    		String title =  categoryName + " " +  br.readLine().toLowerCase().replaceAll("\\s+", " ");
    		String body = "";
        while ((str = br.readLine()) != null) {
        	body +=  str.replaceAll(">", "").toLowerCase().replaceAll("(\\r|\\n)", ". ").replaceAll("\\s+", " ");
        }
        in.close();
        Document stemmedDocument = DocumentManager.createDocument(documentName, title, getFirstNWords(body));
				stemmedDocument.setOrginalCluster(categoryName);
				this.documentHash.put(stemmedDocument.getId(),stemmedDocument);
				this.documentsIDS.add(stemmedDocument.getId());
			}// end loop over documents
		}// end loop over categories
		return this.documentHash;
	}
	
	
	public static void main(String[] args) throws Exception {
		ReutersDataset r = new ReutersDataset("/media/disk/master/Noe4j/datasets/reuters_mod");
		r.loadDocuments();
		
		//		r.createNewDataset();
	}
	
}
