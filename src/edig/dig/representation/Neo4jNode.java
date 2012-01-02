package edig.dig.representation;

import java.util.ArrayList;
import java.util.Hashtable;


public class Neo4jNode {
	// Don't forget when you creating new property to add it to the insertAndIndex method
	public static final String WORD_PROPERTY = "word";
	public static final String CLUSTER_IMPORTANCE = "cluster_importance";
	public static final String DOCUMENT_TABLE = "document_table";
	private String word;
	private Hashtable<String, Double> clusterImportanceHash;
	private Hashtable<String, ArrayList<String>> documentTable ;

	
	/**
	 * Constructor
	 * @param word word 
	 */
	public Neo4jNode(String word){
		this.word = word;
		this.clusterImportanceHash = new Hashtable<String, Double>();
		this.documentTable = new Hashtable<String, ArrayList<String>>();
	}
	
	/**
	 * Method to get word of the node
	 * @return the word of the node
	 */
	public String getWord() {
		return word;
	}
	
	/**
	 * Method to set the word of the node
	 * @param word the word of the node
	 */
	public void setWord(String word) {
		this.word = word;
	}
	
	/**
	 * Add cluster to the word
	 * @param clusterID cluster id
	 * @param importance the importance of this word to the cluster
	 */
	public void addCluster(String clusterID, double importance){
		this.clusterImportanceHash.put(clusterID, importance);
	}
	
	/**
	 * Return true if this word appear in the given cluster
	 * @param clusterID cluster id
	 * @return true if this cluster has this word, otherwise false
	 */
	public boolean isInCluster(String clusterID){
		return this.clusterImportanceHash.containsKey(clusterID);
	}
	
	/**
	 * Return the importance of this word to the cluster
	 * @param clusterID cluster id
	 * @return the importance of this word to this cluster
	 */
	public double getClusterImportance(String clusterID){
		return this.clusterImportanceHash.get(clusterID);
	}
	
	/**
	 * Return true if the document exists in the document table of this node
	 * @param documentID document id
	 * @return true if the document exists in the document table, otherwise false
	 */
	public boolean isInDocumentTable(String documentID){
		return this.documentTable.containsKey(documentID);
	}
	
	/**
	 * Return the entity in the document table corresponding to the document id
	 * @param documentID document id
	 * @return document entity in the document table corresponding to document id
	 */
	public ArrayList<String> getDocumentEntity(String documentID){
		return this.documentTable.get(documentID);
	}

}
