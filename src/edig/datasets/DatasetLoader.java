package edig.datasets;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Hashtable;

import edig.entites.Document;

public interface DatasetLoader {
	
	public Hashtable<String, Document> loadDocument() throws Exception; 
	public Document getDocument(String documentID);

}
