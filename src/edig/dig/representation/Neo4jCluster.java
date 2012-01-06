package edig.dig.representation;

import java.util.ArrayList;

public class Neo4jCluster {
	ArrayList<String> documentIDs;
	
	public Neo4jCluster() {
		this.documentIDs = new ArrayList<String>();
	}
	
	public ArrayList<Neo4jDocument> getDocumentsList(){
		ArrayList<Neo4jDocument> list = new ArrayList<Neo4jDocument>();
		
		return list;
	}
	
	/**
	 * Add document to cluster
	 * @param docID document id
	 */
	public void addDcoument(String docID){
		this.documentIDs.add(docID);
	}
	
	/**
	 * check if cluster has document 
	 * @param docID document id
	 * @return true if the cluster has the document
	 */
	public boolean hasDoc(String docID){
		return this.documentIDs.contains(docID);
	}
	
	/**
	 * get list of document of the cluster
	 * @return list of documents
	 */
	public ArrayList<String> getDocumentIDs() {
		return documentIDs;
	}
}
