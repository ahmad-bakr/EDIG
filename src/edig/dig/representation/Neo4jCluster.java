package edig.dig.representation;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;

import edig.datasets.DatasetLoader;
import edig.entites.Document;

public class Neo4jCluster {
	ArrayList<String> documentIDs;
	String id;
	
	public Neo4jCluster(String id) {
		this.id = id;
		this.documentIDs = new ArrayList<String>();
	}
	
	/**
	 * Get document List
	 * @return list of documents
	 * @throws ClassNotFoundException 
	 * @throws IOException 
	 */
	public ArrayList<Neo4jDocument> getDocumentsList(DatasetLoader datasetHandler, Neo4jHandler neo4jHandler) throws IOException, ClassNotFoundException{
		ArrayList<Neo4jDocument> list = new ArrayList<Neo4jDocument>();
		for (Iterator iterator = this.documentIDs.iterator(); iterator.hasNext();) {
			String documentID = (String) iterator.next();
			Document doc = datasetHandler.getDocument(documentID);
			Neo4jDocument neo4jDoc = neo4jHandler.loadDocument(doc);
			list.add(neo4jDoc);
		}
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
	
	
	public static void main(String[] args) {
		Neo4jHandler handler = Neo4jHandler.getInstance("/media/disk/master/Master/EDIG_DB");
		Neo4jCluster cluster = new Neo4jCluster("1");
		cluster.addDcoument("doc1");
		handler.registerShutdownHook(handler.getGraphDb());

	}
}
