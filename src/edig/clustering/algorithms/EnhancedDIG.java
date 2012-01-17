package edig.clustering.algorithms;

import java.io.BufferedOutputStream;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectInput;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.ArrayList;
import java.util.Hashtable;

import org.neo4j.graphdb.GraphDatabaseService;
import org.neo4j.graphdb.Node;
import org.neo4j.graphdb.Relationship;
import org.neo4j.graphdb.index.Index;
import org.neo4j.kernel.EmbeddedGraphDatabase;

import edig.datasets.DatasetLoader;
import edig.datasets.UWCANDataset;
import edig.dig.representation.Neo4jDocument;
import edig.dig.representation.Neo4jHandler;
import edig.dig.representation.Neo4jNode;
import edig.entites.Document;
import edig.entites.Sentence;
import edig.entites.Word;

public class EnhancedDIG {

	private  GraphDatabaseService graphDb;
	private  Index<Node> nodeIndex;
	private Index<Relationship> edgesIndex;
	private DatasetLoader datasetHandler;
	
	public EnhancedDIG() {
		this.graphDb = new EmbeddedGraphDatabase("/media/disk/master/Noe4j/EDIG");
		this.nodeIndex = graphDb.index().forNodes("nodes");
		this.edgesIndex = graphDb.index().forRelationships("relationships");
		this.datasetHandler = new UWCANDataset("/media/disk/master/Master/datasets/WU-CAN/webdata");
	}
	
	public void clusterDocument(Document doc) throws Exception{
		ArrayList<Sentence> sentencesList = doc.getSentences();
		int numberOfWords = 0;
		//Loop for each sentence of the document
		for (int sentenceIndex = 0; sentenceIndex < sentencesList.size(); sentenceIndex++) {
			Sentence currentSentence = sentencesList.get(sentenceIndex);
			ArrayList<Word> currentSentenceWords = currentSentence.getWords();
			//Loop for the words of the current sentence
			Word previousWord = null;
			numberOfWords += currentSentenceWords.size();
			for (int wordIndex = 0; wordIndex < currentSentenceWords.size(); wordIndex++) {
				Word currentWord = currentSentenceWords.get(wordIndex);
				Node currentNodeInGraph = nodeIndex.get(Neo4jNode.WORD_PROPERTY, currentWord.getContent()).getSingle();

				if(currentNodeInGraph != null){ // currentWord exists in the graph
					
				}else{ // currentWord is a new word 
					Node graphNode = graphDb.createNode();
					graphNode.setProperty(Neo4jNode.WORD_PROPERTY, currentWord.getContent());
					Hashtable<String, Double> wordImportanceTable = new Hashtable<String, Double>();
					graphNode.setProperty(Neo4jNode.CLUSTER_IMPORTANCE, serializeObject(wordImportanceTable));
					nodeIndex.add(graphNode, Neo4jNode.WORD_PROPERTY, currentWord.getContent());
				}
				
				
				
				previousWord = currentWord;
			}// end loop for words of the current sentence
		}// end loop of sentence of the document
	}
	
	
	public byte[] serializeObject(Object obj) throws IOException, ClassNotFoundException {
		ObjectOutputStream os = null;
		ByteArrayOutputStream byteStream = new ByteArrayOutputStream();
		os = new ObjectOutputStream(new BufferedOutputStream(byteStream));
		os.flush();
		os.writeObject(obj);
		os.flush();
		byte[] sendBuf = byteStream.toByteArray();
		os.close();
		return sendBuf;
	}
	
	public Object deserializeObject(byte[] buffer) throws IOException, ClassNotFoundException {
		ByteArrayInputStream is = new ByteArrayInputStream(buffer);
		ObjectInput oi = new ObjectInputStream(is);
		Object newObj = oi.readObject();
		oi.close();
		return newObj;
}


	
}
