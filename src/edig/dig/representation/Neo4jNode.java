package edig.dig.representation;

public class Neo4jNode {
	public static final String WORD_PROPERTY = "word";
	private String word;

	
	public Neo4jNode(String word){
		this.word = word;
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

}
