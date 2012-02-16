package edig.dig.representation;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Hashtable;
import java.util.Iterator;

import scala.collection.mutable.HashTable;

import edig.datasets.DatasetLoader;
import edig.entites.Document;

public class Neo4jCluster {
	ArrayList<String> documentIDs;
	String id;
	double magnitude;
	double edgesMagnitude;
	double length;
	
	public Neo4jCluster(String id) {
		this.id = id;
		this.documentIDs = new ArrayList<String>();
		this.magnitude =0;
		this.length =0;
		this.edgesMagnitude =0;
	}
	
	public String getId() {
		return id;
	}
	
	public void incrementEdgesMagnitude(double value){
		this.edgesMagnitude+= value;
	}
	
	
	public void setId(String id) {
		this.id = id;
	}
	
	public void incrementMagnitude(double value){
		this.magnitude += value;
	}
	
	public void incrementLength(double value){
		this.length += value;
	}
	
	public double getLength() {
		return length;
	}
	
	public double getMagnitude() {
		return magnitude;
	}
	
	/**
	 * Get the cluster representative
	 * @param datasetHandler dataset handler
	 * @param neo4jHandler neo4j Hander
	 * @return hash of word-weight for cluster representative
	 * @throws IOException
	 * @throws ClassNotFoundException
	 */
	public Hashtable<String, Double> getRepresentative(DatasetLoader datasetHandler, Neo4jHandler neo4jHandler ) throws IOException, ClassNotFoundException{
		Hashtable<String, Double> representative = new Hashtable<String,Double>();
		ArrayList<Neo4jDocument> documents = getDocumentsList(datasetHandler, neo4jHandler);
		for (Iterator iterator = documents.iterator(); iterator.hasNext();) {
			Neo4jDocument neo4jDocument = (Neo4jDocument) iterator.next();
			ArrayList<Neo4jNode> nodesList = neo4jDocument.getNodesList();
			for (Iterator iterator2 = nodesList.iterator(); iterator2.hasNext();) {
				Neo4jNode neo4jNode = (Neo4jNode) iterator2.next();
				ArrayList<String> documentEntityInTable = neo4jNode.getDocumentEntity(neo4jDocument.getDocumentID());
				double termFrequency = Double.parseDouble(documentEntityInTable.get(0));
				double wordWeight =0;
				if(isTitleWord(documentEntityInTable.get(1))){
					 wordWeight = 0.2 * (termFrequency/neo4jDocument.getNumberOfTitleWords());
				}else{
					 wordWeight = (termFrequency/neo4jDocument.getNumberOfBodyWords());
				}// end if
				
				if(representative.containsKey(neo4jNode.getWord())){
					representative.put(neo4jNode.getWord(), representative.get(neo4jNode.getWord()) + wordWeight );
				}else{
					representative.put(neo4jNode.getWord(), wordWeight);
				}
				
			}//end loop for nodes of the document
		}// end loop for documents
		return representative;
	}
	
	
	private boolean isTitleWord(String firstOccur){
		String first = firstOccur.split("_")[0];
		if(first.equalsIgnoreCase("0")) return true;
		return false;
	}

	/**
	 * Get document List from neo4j
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
		handler.registerShutdownHook();

	}
}
