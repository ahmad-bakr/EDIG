package edig.datasets;

import java.util.ArrayList;
import java.util.Hashtable;

import edig.entites.Document;

public class NewsGroupDataset extends Dataset{
	
	String originalDatasetPath = "/media/disk/master/Master/datasets/20_newsgroups";
	String modifiedDatasetPath = "/media/disk/master/Noe4j/datasets/20_newsgroups";

	@Override
	public Hashtable<String, Document> loadDocuments() throws Exception {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Document getDocument(String documentID) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public int numberOfDocuments() {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public ArrayList<String> getDocumentsIDS() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public int getNumberOfDocumentsInClass(String className) {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public ArrayList<String> getOriginalClasses() {
		// TODO Auto-generated method stub
		return null;
	}

	public static void main(String[] args) {

	}

}
