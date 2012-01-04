package edig.entites;

import java.util.ArrayList;
/**
 * Class representing a document	
 * @author ahmad
 *
 */
public class Document {
	private String id;
	private ArrayList<Sentence> sentences;
	
	/**
	 * Constructor
	 */
	public Document() {
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
	
	
}
