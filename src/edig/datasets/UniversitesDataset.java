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

public class UniversitesDataset extends Dataset {
	
	private Hashtable<String, Document> documentHash;
	private String datasetPath;
	private int numberOfDocument;
	private ArrayList<String> originalClasses ;
	private Hashtable<String, Integer> classDocumentCount;
	private ArrayList<String> documentsIDS;
	
 public UniversitesDataset(String path) {
		this.documentsIDS = new ArrayList<String>();
		this.originalClasses = new ArrayList<String>();
		this.datasetPath = path;
		this.documentHash = new Hashtable<String, Document>();
		this.numberOfDocument =0;
		this.classDocumentCount = new Hashtable<String, Integer>();
 }
	
	
		public void prepareDataset(){
			String originalPath = "/media/disk/master/Master/datasets/webkb";
			String modifiedPath = "/media/disk/master/Master/datasets/four_universites";
			File originalDir = new File(originalPath);
	    String[] originalCategories = originalDir.list();
	    for (int i = 0; i < originalCategories.length; i++) {
	    	String orginalCategory = originalCategories[i];
				String subCategoryPath = originalPath+"/"+originalCategories[i];
	    	boolean createNewDir = (new File(modifiedPath+"/"+orginalCategory)).mkdirs();
	  		String[] docsInSubCategory = new File(subCategoryPath).list();
	  		for (int j = 0; j < docsInSubCategory.length; j++) {
					super.copyfile(subCategoryPath+"/"+docsInSubCategory[i], modifiedPath+"/"+orginalCategory+"/"+docsInSubCategory[i]);
				}
	    }
		}
		
		
		public void createNewDataset(){
			super.createDataset("/media/disk/master/Master/datasets/four_universites", "/media/disk/master/Master/datasets/four_universites_mod", 130);
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
	    		File input = new File(this.datasetPath+"/"+categoryName+"/"+documentName);
	    		org.jsoup.nodes.Document doc = Jsoup.parse(input, "UTF-8", "");
	    		String title = categoryName+" "+ doc.title().replaceAll("(\\r|\\n)", ". ");
	    		String body =  getFirstNWords( doc.text().replaceAll("\\s+", " ")); //getFirstNWords(doc.text().replaceAll("(\\r|\\n)", ". ").replaceAll("\\s+", " "));
					Document stemmedDocument = DocumentManager.createDocument(categoryName+title, title, body);
					stemmedDocument.setOrginalCluster(categoryName);
					this.documentHash.put(stemmedDocument.getId(),stemmedDocument);
					this.documentsIDS.add(stemmedDocument.getId());
				}// end loop over documents
			}// end loop over categories
			return this.documentHash;
		}
		
		public static boolean deleteDir(File dir) {
	    if (dir.isDirectory()) {
	        String[] children = dir.list();
	        for (int i=0; i<children.length; i++) {
	            boolean success = deleteDir(new File(dir, children[i]));
	            if (!success) {
	                return false;
	            }
	        }
	    }

	    // The directory is now empty so delete it
	    return dir.delete();
	}	    

		
		public static void main(String[] args) throws Exception {
			UniversitesDataset u = new UniversitesDataset("/media/disk/master/Master/datasets/four_universites");
			UniversitesDataset.deleteDir(new File("/media/disk/master/Master/datasets/four_universites_mod"));
			//	u.loadDocuments();
		//	System.out.println(u.getDocumentsIDS().size());
			u.createNewDataset();
		}
}
