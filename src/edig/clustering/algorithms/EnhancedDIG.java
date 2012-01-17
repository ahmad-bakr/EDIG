package edig.clustering.algorithms;

import java.io.BufferedOutputStream;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectInput;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.ArrayList;
import java.util.Enumeration;
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
		Hashtable<String, Double> clusterSimilairtyTableForWords = new Hashtable<String, Double>();
		Hashtable<String, Double> clusterSimilairtyTableForEdges = new Hashtable<String, Double>();
		
		//Loop for each sentence of the document
		for (int sentenceIndex = 0; sentenceIndex < sentencesList.size(); sentenceIndex++) {
			Sentence currentSentence = sentencesList.get(sentenceIndex);
			ArrayList<Word> currentSentenceWords = currentSentence.getWords();
			//Loop for the words of the current sentence
			Word previousWord = null;
			for (int wordIndex = 0; wordIndex < currentSentenceWords.size(); wordIndex++) {
				Word currentWord = currentSentenceWords.get(wordIndex);
				Node currentNodeInGraph = nodeIndex.get(Neo4jNode.WORD_PROPERTY, currentWord.getContent()).getSingle();
				
				if(currentNodeInGraph != null){ // currentWord exists in the graph
					updateWordsClusterImportanceTable(clusterSimilairtyTableForWords, currentNodeInGraph, 1);
				}else{ // currentWord is a new word 
					currentNodeInGraph = createNewWord(currentWord);
				}
				
				
				
				previousWord = currentWord;
			}// end loop for words of the current sentence
		}// end loop of sentence of the document
	}
	
	public void updateWordsClusterImportanceTable(Hashtable<String, Double> clusterSimilairtyTableForWords, Node nodeInTheGraph, double wordValue) throws Exception{
		Hashtable<String, Double> clusterImportanceTable = (Hashtable<String, Double>) deserializeObject((byte[]) nodeInTheGraph.getProperty(Neo4jNode.CLUSTER_IMPORTANCE)); 
		Enumeration clustersIDs = clusterImportanceTable.keys();
		//loop for all clusters in the node and update the cluster similarity table for the document
		while (clustersIDs.hasMoreElements()) {
			String clusterID = (String) clustersIDs.nextElement();
			if(clusterSimilairtyTableForWords.containsKey(clusterID)){
				double wordValueForTheCluster = clusterImportanceTable.get(clusterID);
				clusterImportanceTable.put(clusterID, wordValueForTheCluster+wordValue);
			}else{
				clusterImportanceTable.put(clusterID, wordValue);
			}
		}// end loop for clusters at the matched node
	}
	
	public Node createNewWord(Word word) throws Exception{
		Node graphNode = graphDb.createNode();
		graphNode.setProperty(Neo4jNode.WORD_PROPERTY, word.getContent());
		Hashtable<String, Double> clusterImportanceTable = new Hashtable<String, Double>();
		graphNode.setProperty(Neo4jNode.CLUSTER_IMPORTANCE, serializeObject(clusterImportanceTable));
		nodeIndex.add(graphNode, Neo4jNode.WORD_PROPERTY, word.getContent());
		return graphNode;
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
