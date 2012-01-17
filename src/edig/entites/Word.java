package edig.entites;

/**
 * Class representing a word
 * @author ahmad
 *
 */
public class Word {

	private String content;
	private int termFrequency =1;
	private boolean isTitle;

	/**
	 * Constructor of word class
	 * @param word the word
	 * @param tf the term frequency
	 */
	public Word(String word, int tf, boolean isTitle) {
		this.content = word;
		this.termFrequency = tf;
		this.isTitle = isTitle;
	}	
	
	public void setIsTitle(boolean isTitle) {
		this.isTitle = isTitle;
	}
	
	public boolean getIsTitle(){
		return this.isTitle;
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
