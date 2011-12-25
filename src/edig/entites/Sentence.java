package edig.entites;

import java.util.ArrayList;

/**
 * Class representing a sentence
 * @author ahmad
 *
 */
public class Sentence {
	private	ArrayList<Word> words;
	
	/**
	 * Constructor
	 */
	public Sentence() {
		this.words = new ArrayList<Word>();
	}
	
	/**
	 * Add word to the sentence
	 * @param w word object
	 */
	public void addWord(Word w){
		this.words.add(w);
	}
	
	/**
	 * Remove word from the sentence
	 * @param w word object
	 */
	public void removeWord(Word w){
		this.words.remove(w);
	}
	
	/**
	 * Check if the sentence contains a word
	 * @param w the word
	 * @return true if exists, otherwise false
	 */
	public boolean hasWord(Word w){
		return this.words.contains(w);
	}
	
	
	/**
	 * Get all the words in the sentence
	 * @return the words
	 */
	public ArrayList<Word> getWords(){
		return this.words;
	}
	
	
	
	
}
