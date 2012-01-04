package edig.entites;

import java.util.ArrayList;

import edig.document.parser.ParserFactory;
import edig.document.parser.ParserIF;
/**
 * Class to create document object given title and body
 * It uses the specified parser to extract, stem and remove stop words from the sentences
 * @author ahmad
 *
 */
public class DocumentManager {
	private static ParserIF parser = ParserFactory.getInstance("lingpipe");

	/**
	 * Create document object from title and body
	 * @param title String title
	 * @param body String body
	 * @return Document object
	 * @throws Exception
	 */
	public static Document createDocument(String id,String title, String body) throws Exception{
		Sentence titleSentence = flatten(parser.parseText(title));
		ArrayList<Sentence> bodySentences = parser.parseText(body);
		Document doc = new Document(id);
		doc.addSentence(titleSentence);
		for (int i = 0; i < bodySentences.size(); i++) {
			doc.addSentence(bodySentences.get(i));
		}
		return doc;
	}
	
	private static Sentence flatten(ArrayList<Sentence> sentences){
		for (int i = 1; i < sentences.size(); i++) {
			for (int j = 0; j < sentences.get(i).getWords().size(); j++) {
				sentences.get(0).addWord(sentences.get(i).getWords().get(j));
			}
		}
		return sentences.get(0);
	}
	
	public static void main(String[] args) throws Exception {
		String title = "Hello, This is title. Ahmad Bakr";
		String body ="Hello, This is body. How is going?";
		Document doc = DocumentManager.createDocument("doc1" ,title, body);
		for (int i = 0; i < doc.getSentences().size(); i++) {
			for (int j = 0; j < doc.getSentences().get(i).getWords().size(); j++) {
				System.out.print(doc.getSentences().get(i).getWords().get(j).getContent() + " ");
			}
			System.out.println("");
		} 
	}
}
