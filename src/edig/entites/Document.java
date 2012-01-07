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
	private int numberOfTitleWords;
	private int numberOfBodyWords;
	
	/**
	 * Constructor
	 */
	public Document(String id) {
		this.id = id;
		this.numberOfBodyWords=0;
		this.numberOfTitleWords=0;
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
	 * set number of body words
	 * @param numberOfBodyWords
	 */
	public void setNumberOfBodyWords(int numberOfBodyWords) {
		this.numberOfBodyWords = numberOfBodyWords;
	}
	
	/**
	 * set number of title words
	 * @param numberOfTitleWords
	 */
	public void setNumberOfTitleWords(int numberOfTitleWords) {
		this.numberOfTitleWords = numberOfTitleWords;
	}
	
	
}
