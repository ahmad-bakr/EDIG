package edig.dig.representation;

import java.util.ArrayList;
import java.util.Enumeration;
import java.util.Hashtable;
import java.util.Iterator;



public class Neo4jDocument {
	private String documentID;
	private ArrayList<Neo4jNode> nodesList;
	private Hashtable<String,Double> clustersHash;
	private int numberOfTitleWords ;
	private int numberOfBodyWords ;
	
	/**
	 * Neo4j Document Constructor
	 * @param id document id
	 */
	public Neo4jDocument(String id){
		this.clustersHash = new Hashtable<String, Double>();
		this.documentID = id;
		this.nodesList = new ArrayList<Neo4jNode>();
	}
	
	/**
	 * Get clusters where document belongs to
	 * @return list of clusters
	 */
	public Hashtable<String, Double> getClustersHash() {
		return this.clustersHash;
	}
	
	/**
	 * Add cluster to the document
	 * @param clusterID cluster id
	 * @param similairty document similarity to the cluster
	 */
	public void addCluster(String clusterID, double similarity){
		this.clustersHash.put(clusterID, similarity);
	}
	
	/**
	 * Get the nodes list
	 * @return nodes list of Neo4j nodes
	 */
	public ArrayList<Neo4jNode> getNodesList() {
		return nodesList;
	}
	/**
	 * Get document id
	 * @return document id
	 */
	public String getDocumentID() {
		return documentID;
	}
	
	/**
	 * Set document id
	 * @param documentID document id
	 */
	public void setDocumentID(String documentID) {
		this.documentID = documentID;
	}
	
	/**
	 * Add node to the document list
	 * @param node node
	 */
	public void addNode(Neo4jNode node){
		this.nodesList.add(node);
	}
	
	/**
	 * Set number of body words
	 * @param numberOfBodyWords
	 */
	public void setNumberOfBodyWords(int numberOfBodyWords) {
		this.numberOfBodyWords = numberOfBodyWords;
	}
	
	/**
	 * Set number of title words
	 * @param numberOfTitleWords
	 */
	public void setNumberOfTitleWords(int numberOfTitleWords) {
		this.numberOfTitleWords = numberOfTitleWords;
	}
	
	/**
	 * Get number of body words
	 * @return number of body words
	 */
	public int getNumberOfBodyWords() {
		return numberOfBodyWords;
	}
	
	/**
	 * Get number of title words
	 * @return number of title words
	 */
	public int getNumberOfTitleWords() {
		return numberOfTitleWords;
	}
	
	/**
	 * Get list of cluster ids
	 * @return list of clusters ids
	 */
	public ArrayList<String> getClusterIDsList(){
		ArrayList<String> list = new ArrayList<String>();
		Enumeration keys = this.clustersHash.keys();
		while (keys.hasMoreElements()) {
			String id = (String) keys.nextElement();
			list.add(id);
		}
		
		return list;
	}

	
}
