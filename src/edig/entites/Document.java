package edig.entites;

import java.util.ArrayList;
/**
 * Class representing a document	
 * @author ahmad
 *
 */
public class Document {
	private String id;
	private String orginalCluster;
	private ArrayList<String> predictedClusters;
	private ArrayList<Sentence> sentences;
	
	/**
	 * Constructor
	 */
	public Document(String id) {
		this.id = id;
		this.predictedClusters = new ArrayList<String>();
		this.sentences = new ArrayList<Sentence>();
	}

	/**
	 * Set document id
	 * @param id document id
	 */
	public void setId(String id) {
		this.id = id;
	}
	
	/**
	 * Get document id
	 * @return document id
	 */
	public String getId() {
		return id;
	}

	/**
	 * Add sentence to the document
	 * @param s sentence
	 */
	public void addSentence(Sentence s){
		this.sentences.add(s);
	}
	
	/**
	 * Get all sentence in the document
	 * @return all sentences
	 */
	public ArrayList<Sentence> getSentences(){
		return this.sentences;
	}
	
	/**
	 * Get original cluster
	 * @return original cluster id
	 */
	public String getOrginalCluster() {
		return orginalCluster;
	}
	
	/**
	 * Set original cluster
	 * @param orginalCluster orginal cluster id
	 */
	public void setOrginalCluster(String orginalCluster) {
		this.orginalCluster = orginalCluster;
	}
	
	/**
	 * Add predicted cluster
	 * @param clusterID predicted cluster id
	 */
	public void addPredictedCluster(String clusterID){
		this.predictedClusters.add(clusterID);
	}
	
	/**
	 * Get predicted clusters
	 * @return predicted clusters
	 */
	public ArrayList<String> getPredictedClusters() {
		return predictedClusters;
	}
	
	
}
