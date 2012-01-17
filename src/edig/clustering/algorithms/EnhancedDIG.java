package edig.clustering.algorithms;

import java.util.ArrayList;

import edig.datasets.DatasetLoader;
import edig.datasets.UWCANDataset;
import edig.dig.representation.Neo4jHandler;
import edig.entites.Document;
import edig.entites.Sentence;
import edig.entites.Word;

public class EnhancedDIG {
	
	private Neo4jHandler neo4jHandler;
	private DatasetLoader dataserHandler;
		
	
	public EnhancedDIG() {
		this.neo4jHandler = Neo4jHandler.getInstance("/media/disk/master/Noe4j/EDIG");
		this.dataserHandler = new UWCANDataset("/media/disk/master/Master/datasets/WU-CAN/webdata");
	}
	
	public void clusterDocument(Document doc){
		ArrayList<Sentence> sentencesList = doc.getSentences();
		//Loop for each sentence of the document
		for (int sentenceIndex = 0; sentenceIndex < sentencesList.size(); sentenceIndex++) {
			Sentence currentSentence = sentencesList.get(sentenceIndex);
			ArrayList<Word> currentSentenceWords = currentSentence.getWords();
			//Loop for the words of the current sentence
			for (int wordIndex = 0; wordIndex < currentSentenceWords.size(); wordIndex++) {
				
				
			}// end loop for words of the current sentence
		}// end loop of sentence of the document
	}
}
