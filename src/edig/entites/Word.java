package edig.entites;

/**
 * Class representing a word
 * @author ahmad
 *
 */
public class Word {

	private String content;
	private int termFrequency =1;

	/**
	 * Constructor of word class
	 * @param word the word
	 * @param tf the term frequency
	 */
	public Word(String word, int tf) {
		this.content = word;
		this.termFrequency = tf;
	}	
	
	/**
	 * Constructor of word class
	 * @param word the word, the term frequency is 1
	 */
	public Word(String word) {
		this.content = word;
	}
	
	/**
	 * the content of the word
	 * @return the content
	 */
	
	public String getContent() {
		return content;
	}
	
	/**
	 * the term frequency of the word
	 * @return the term frequency
	 */
	public int getTermFrequency() {
		return termFrequency;
	}
	
	/**
	 * set the content of the word
	 * @param content the content
	 */
	public void setContent(String content) {
		this.content = content;
	}
	
	/**
	 * set the term frequency of the word
	 * @param termFrequency term frequency
	 */
	public void setTermFrequency(int termFrequency) {
		this.termFrequency = termFrequency;
	}

}
