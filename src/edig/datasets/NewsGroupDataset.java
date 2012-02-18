package edig.datasets;

import java.io.BufferedReader;
import java.io.DataInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Hashtable;

import org.jsoup.Jsoup;

import edig.entites.Document;
import edig.entites.DocumentManager;

public class NewsGroupDataset extends Dataset{
	
	String originalDatasetPath = "/media/disk/master/Master/datasets/20_newsgroups";
	String modifiedDatasetPath = "/media/disk/master/Noe4j/datasets/20_newsgroups";

	private Hashtable<String, Document> documentHash;
	private String datasetPath;
	private int numberOfDocument;
	private ArrayList<String> originalClasses ;
	private Hashtable<String, Integer> classDocumentCount;
	private ArrayList<String> documentsIDS;
	
	public NewsGroupDataset(String path) {
		this.documentsIDS = new ArrayList<String>();
		this.originalClasses = new ArrayList<String>();
		this.datasetPath = path;
		this.documentHash = new Hashtable<String, Document>();
		this.numberOfDocument =0;
		this.classDocumentCount = new Hashtable<String, Integer>();
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
		for (int i = 0; i < 200; i++) {
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
    		String title =  categoryName;
				String body = "";
        while ((str = br.readLine()) != null) {
	        if(str.contains("Subject:")){
	        	title +=  " "+ str.replace("Subject:", "").replace("Re:", "").replaceAll("\\s+", " ");
	        	continue;
	        }
	        else if(str.contains("Newsgroups:") || str.contains("Path:") || str.contains("From:") || str.contains("Message-ID:") || str.contains("Sender:") ||
	        		 str.contains("Lines:") || str.contains("References:") || str.contains("Date:") || str.contains("Article-I.D.:") || str.contains("Expires:") || (str.length() < 4)
	        		 || str.contains("Followup-To:") || str.contains(".com")
	        		 ){
	        	 		continue;
	        }
	        body +=  str.replaceAll(">", "").toLowerCase().replaceAll("(\\r|\\n)", ". ").replaceAll("\\s+", " ");
        }
        in.close();
        System.out.println(title);
        System.out.println(body);
        System.out.println("**************************************************************");
        Document stemmedDocument = DocumentManager.createDocument(documentName, title, body);
				stemmedDocument.setOrginalCluster(categoryName);
				this.documentHash.put(stemmedDocument.getId(),stemmedDocument);
				this.documentsIDS.add(stemmedDocument.getId());
			}// end loop over documents
		}// end loop over categories
		return this.documentHash;
	}
	
	
	public static void main(String[] args) throws Exception {
		DatasetLoader datasetHandler = new NewsGroupDataset("/media/disk/master/Noe4j/datasets/20_newsgroups");
		datasetHandler.loadDocuments();
		String documentID = datasetHandler.getDocumentsIDS().get(0);
		System.out.println("Document count = " + datasetHandler.getDocumentsIDS().size());
		Document d = datasetHandler.getDocument(documentID);
		System.out.println("ID = "+d.getId());
		System.out.println("Title Words Count= "+d.getNumberOfTitleWords());
		System.out.println("Body words Count= "+ d.getNumberOfBodyWords());
	}

}
