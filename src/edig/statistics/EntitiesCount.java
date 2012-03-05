package edig.statistics;

import java.util.ArrayList;
import java.util.Enumeration;
import java.util.Hashtable;

import edig.datasets.DatasetLoader;
import edig.datasets.ReutersDataset;
import edig.entites.Document;
import edig.entites.Sentence;
import edig.entites.Word;

public class EntitiesCount {
	
	private Hashtable<String, Integer> wordsCount;
	private Hashtable<String, Integer> edgesCount;
	private int sentencesCount;
	
	public EntitiesCount() {
		this.wordsCount = new Hashtable<String, Integer>();
		this.edgesCount = new Hashtable<String, Integer>();
		this.sentencesCount = 0;
	}
	
	public void calculateEntitesCount(DatasetLoader datasetHandler) throws Exception{
		Hashtable<String, Document> documentsHash =	datasetHandler.loadDocuments();
		int numberOfUniqueWords = 0;
		int numberOfEdges = 0;
		long startTime = System.currentTimeMillis();
		Enumeration ids = documentsHash.keys();
		ArrayList<String> documentIDs = datasetHandler.getDocumentsIDS();
		for (int i = 0; i < documentIDs.size(); i++) {
			Document doc = documentsHash.get(documentIDs.get(i));
			System.out.println("Processing Document " + doc.getId() + "From class " + doc.getOrginalCluster() );
			ArrayList<Sentence> sentencesList = doc.getSentences();
			sentencesCount += sentencesList.size();
			for (int sentenceIndex = 0; sentenceIndex < sentencesList.size(); sentenceIndex++) {
				Sentence currentSentence = sentencesList.get(sentenceIndex);
				ArrayList<Word> currentSentenceWords = currentSentence.getWords();
				Word previousWord = null;
				Word currentWord = null;
				for (int wordIndex = 0; wordIndex < currentSentenceWords.size(); wordIndex++) {
				  currentWord = currentSentenceWords.get(wordIndex);
				  if (wordsCount.containsKey(currentWord.getContent())){
				  	wordsCount.put(currentWord.getContent(), wordsCount.get(currentWord.getContent())+1);
				  }else{
				  	numberOfUniqueWords++;
				  	wordsCount.put(currentWord.getContent(),1);
				  }
				  
				  if((previousWord != null) && (currentWord != null)){
				  	String edgeID = previousWord.getContent()+"_"+currentWord.getContent();
				  	if(edgesCount.containsKey(edgeID)){
				  		edgesCount.put(edgeID, edgesCount.get(edgeID)+1);
				  	}else{
				  		numberOfEdges++;
				  		edgesCount.put(edgeID, 1);
				  	}
				  }
				  previousWord = currentWord;
				}//end looping for words

			}//end looping for sentences


		}//end looping for documents
		System.out.println("********************************");
		System.out.println("Number of sentences = " + this.sentencesCount);
		System.out.println("Number of words = "+ numberOfUniqueWords);
		System.out.println("Number of edges = " + numberOfEdges);
		System.out.println("*********************************");
	}
	
	public static void main(String[] args) throws Exception {
			ReutersDataset dataset = new ReutersDataset("/media/disk/master/Noe4j/datasets/reuters_mod");
			EntitiesCount en = new EntitiesCount();
			en.calculateEntitesCount(dataset);
	}
	
}
