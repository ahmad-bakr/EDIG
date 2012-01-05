package edig.datasets;

import java.io.IOException;
import java.util.ArrayList;

import edig.entites.Document;

public interface DatasetLoader {
	
	public ArrayList<Document> loadDocument() throws Exception; 

}
