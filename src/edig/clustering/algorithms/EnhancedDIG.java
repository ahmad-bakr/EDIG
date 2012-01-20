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

import org.neo4j.graphdb.Direction;
import org.neo4j.graphdb.DynamicRelationshipType;
import org.neo4j.graphdb.GraphDatabaseService;
import org.neo4j.graphdb.Node;
import org.neo4j.graphdb.Relationship;
import org.neo4j.graphdb.RelationshipType;
import org.neo4j.graphdb.Transaction;
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
		// these tables will be used to calculate the similarity between the new document and existing cluster
		Hashtable<String, Double> clusterSimilairtyTableForWords = new Hashtable<String, Double>();
		Hashtable<String, Double> clusterSimilairtyTableForEdges = new Hashtable<String, Double>();
		//
		//Loop for each sentence of the document
		for (int sentenceIndex = 0; sentenceIndex < sentencesList.size(); sentenceIndex++) {
			Sentence currentSentence = sentencesList.get(sentenceIndex);
			ArrayList<Word> currentSentenceWords = currentSentence.getWords();
			//Loop for the words of the current sentence
			Word previousWord = null;
			Word currentWord = null;
			Node previousNodeInTheGraph = null;
			Node currentNodeInGraph = null;
			for (int wordIndex = 0; wordIndex < currentSentenceWords.size(); wordIndex++) {
			  currentWord = currentSentenceWords.get(wordIndex);
			  currentNodeInGraph = nodeIndex.get(Neo4jNode.WORD_PROPERTY, currentWord.getContent()).getSingle();				
				double wordValueForTheDocument = calculateWordValue(doc, currentWord);
				// start handling the word
				if(currentNodeInGraph != null){ // currentWord exists in the graph
					updateWordsClusterImportanceTable(clusterSimilairtyTableForWords, currentNodeInGraph, wordValueForTheDocument);
				}else{ // currentWord is a new word 
					currentNodeInGraph = createNewWord(currentWord);
				}
				// done handling the nodes
				// start handling the edges
				if((previousNodeInTheGraph != null) && (currentNodeInGraph != null)){
					String edgeID = previousWord.getContent()+"_"+currentWord.getContent();
					Relationship edge = edgesIndex.get("edge", edgeID).getSingle();
					if(edge !=  null){ //edge exists
						updateEdgesClusterImportanceTable(clusterSimilairtyTableForEdges, edge, 1);
					}else{ // create new edge
						createNewEdge(previousNodeInTheGraph, currentNodeInGraph, edgeID);
					}
				}
				// done handling the edges
				previousNodeInTheGraph = currentNodeInGraph;
				previousWord = currentWord;
			}// end loop for words of the current sentence
		}// end loop of sentence of the document
		
		//Now we need to calculate the similarity to the clusters collected and take the decision
		
	}
	
	public void updateTheGraph(Document doc, String clusterID) throws Exception{
		Transaction tx = graphDb.beginTx();
		try {

			ArrayList<Sentence> sentencesList = doc.getSentences();
			for (int sentenceIndex = 0; sentenceIndex < sentencesList.size(); sentenceIndex++) {
				Sentence currentSentence = sentencesList.get(sentenceIndex);
				ArrayList<Word> currentSentenceWords = currentSentence.getWords();
				//Loop for the words of the current sentence
				Word previousWord = null;
				Word currentWord = null;
				Node previousNodeInTheGraph = null;
				Node currentNodeInGraph = null;
				for (int wordIndex = 0; wordIndex < currentSentenceWords.size(); wordIndex++) {
				  currentWord = currentSentenceWords.get(wordIndex);
				  currentNodeInGraph = nodeIndex.get(Neo4jNode.WORD_PROPERTY, currentWord.getContent()).getSingle();				
					double wordValueForTheDocument = calculateWordValue(doc, currentWord);
					// update the cluster similarity table for the nodes
					Hashtable<String, Double> clusterImportanceTable = (Hashtable<String, Double>) deserializeObject((byte[]) currentNodeInGraph.getProperty(Neo4jNode.CLUSTER_IMPORTANCE)); 
					if (clusterImportanceTable.containsKey(clusterID)){
						clusterImportanceTable.put(clusterID, clusterImportanceTable.get(clusterID)+wordValueForTheDocument);
					}else{
						clusterImportanceTable.put(clusterID, wordValueForTheDocument);
					}
					currentNodeInGraph.setProperty(Neo4jNode.CLUSTER_IMPORTANCE, clusterImportanceTable);
					nodeIndex.add(currentNodeInGraph, Neo4jNode.WORD_PROPERTY, currentWord.getContent());
					// end updating cluster similarity table for the nodes
					
					
					previousNodeInTheGraph = currentNodeInGraph;
				} // end loop for the words
			}// end loop for the sentences

			
			
			
			tx.success();
		} finally {
			tx.finish();
		}
	}
	
	public double calculateWordValue(Document doc, Word word){
		double wordValue = 0;
		if (word.getIsTitle()){
			wordValue = 1/doc.getNumberOfTitleWords();
		}else{
			wordValue = 1/doc.getNumberOfBodyWords();
		}
		return wordValue;
	}
	
	public void updateWordsClusterImportanceTable(Hashtable<String, Double> clusterSimilairtyTableForWords, Node nodeInTheGraph, double wordValue) throws Exception{
		Hashtable<String, Double> clusterImportanceTable = (Hashtable<String, Double>) deserializeObject((byte[]) nodeInTheGraph.getProperty(Neo4jNode.CLUSTER_IMPORTANCE)); 
		Enumeration clustersIDs = clusterImportanceTable.keys();
		//loop for all clusters in the node and update the cluster similarity table for the document
		while (clustersIDs.hasMoreElements()) {
			String clusterID = (String) clustersIDs.nextElement();
			if(clusterSimilairtyTableForWords.containsKey(clusterID)){
				double wordValueForTheCluster = clusterImportanceTable.get(clusterID);
				clusterSimilairtyTableForWords.put(clusterID, wordValueForTheCluster+wordValue);
			}else{
				clusterSimilairtyTableForWords.put(clusterID, wordValue);
			}
		}// end loop for clusters at the matched node
	}
	
	public void updateEdgesClusterImportanceTable(Hashtable<String, Double> clusterSimilairtyTableForEdges, Relationship edge, double edgeValueInTheDocument ) throws Exception{
		Hashtable<String, Double> clusterImportanceTable = (Hashtable<String, Double>) deserializeObject((byte[]) edge.getProperty("cluster_table"));
		Enumeration clustersIDs = clusterImportanceTable.keys();
		//loop for all clusters in the node and update the cluster similarity table for the document
		while (clustersIDs.hasMoreElements()) {
			String clusterID = (String) clustersIDs.nextElement();
			if(clusterSimilairtyTableForEdges.containsKey(clusterID)){
				double edgeValueForTheCluster = clusterImportanceTable.get(clusterID);
				clusterSimilairtyTableForEdges.put(clusterID, edgeValueForTheCluster + edgeValueInTheDocument);
			}else{
				clusterSimilairtyTableForEdges.put(clusterID, edgeValueInTheDocument);
			}
		}// end loop for clusters at the matched node
	}
	
	public void createNewEdge(Node previousWord, Node currentWord, String edgeID) throws Exception{
		RelationshipType type = DynamicRelationshipType.withName(edgeID);
		Relationship relationship = previousWord.createRelationshipTo(currentWord, type);
		relationship.setProperty("edge", edgeID);
		Hashtable<String, Double> clusterImportanceTable = new Hashtable<String, Double>();
		relationship.setProperty("cluster_table", serializeObject(clusterImportanceTable));
		edgesIndex.add(relationship, "edge", edgeID);
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
