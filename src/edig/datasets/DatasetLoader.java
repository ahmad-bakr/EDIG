package edig.datasets;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Hashtable;

import edig.entites.Document;

public interface DatasetLoader {
	
	public Hashtable<String, Document> loadDocuments() throws Exception; 
	public Document getDocument(String documentID);
	public int numberOfDocuments();
	public int getNumberOfDocumentsInClass(String className);
	public ArrayList<String> getOriginalClasses();

}
