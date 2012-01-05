package edig.datasets;

import java.util.ArrayList;

import edig.entites.Document;

public class UWCANDataset implements DatasetLoader {
	private ArrayList<Document> documents ;
	
	public UWCANDataset() {
		this.documents = new ArrayList<Document>();
	}
	
	@Override
	public ArrayList<Document> loadDocument() {

		return this.documents;
	}

}
