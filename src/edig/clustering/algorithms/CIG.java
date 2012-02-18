package edig.clustering.algorithms;

import java.io.BufferedOutputStream;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
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
import edig.datasets.NewsGroupDataset;
import edig.datasets.UWCANDataset;
import edig.dig.representation.Neo4jCluster;
import edig.dig.representation.Neo4jDocument;
import edig.dig.representation.Neo4jHandler;
import edig.dig.representation.Neo4jNode;
import edig.entites.Document;
import edig.entites.DocumentManager;
import edig.entites.Sentence;
import edig.entites.Word;
import edig.evaluations.CIGMeasure;

public class CIG {

	private  GraphDatabaseService graphDb;
	private  Index<Node> nodeIndex;
	private Index<Relationship> edgesIndex;
	private DatasetLoader datasetHandler;
	private int clusterCounter ;
	private double similarityThreshold ;
	private double alpha ;
	Hashtable<String,Neo4jCluster> clustersList;
	
	public CIG(double alpha, double simThreshold, DatasetLoader dataset) {
		this.alpha = alpha;
		this.similarityThreshold = simThreshold;
		this.clustersList = new Hashtable<String,Neo4jCluster>();
		this.clusterCounter = 1;
		this.graphDb = new EmbeddedGraphDatabase("/media/disk/master/Noe4j/EDIG");
		this.nodeIndex = graphDb.index().forNodes("nodes");
		this.edgesIndex = graphDb.index().forRelationships("relationships");
		this.datasetHandler = dataset;// = new UWCANDataset("/media/disk/master/Master/datasets/WU-CAN/webdata");
	}
	
	public double getAlpha() {
		return alpha;
	}
	
	public double getSimilarityThreshold() {
		return similarityThreshold;
	}
	
	public Hashtable<String, Neo4jCluster> getClustersList() {
		return clustersList;
	}
	public DatasetLoader getDatasetHandler() {
		return datasetHandler;
	}
	
	public void registerShutdownHook() {
		Runtime.getRuntime().addShutdownHook(new Thread() {
			@Override
			public void run() {
				graphDb.shutdown();
			}
		});
	}

	public double calculateWordValue(Document doc, Word word){
		double wordValue = 0;
		if (word.getIsTitle()){
			wordValue = (1.0/doc.getNumberOfTitleWords())*0.5;
		}else{
			wordValue = 1.0/doc.getNumberOfBodyWords();
		}
		return wordValue;
	}
	
	public void updateWordsClusterImportanceTable(Hashtable<String, Double> clusterSimilairtyTableForWords, Node nodeInTheGraph, double wordValue) throws Exception{
		Hashtable<String, Double> clusterImportanceTable = (Hashtable<String, Double>) deserializeObject((byte[]) nodeInTheGraph.getProperty(Neo4jNode.CLUSTER_IMPORTANCE)); 
		Enumeration clustersIDs = clusterImportanceTable.keys();
		//loop for all clusters in the node and update the cluster similarity table for the document
		while (clustersIDs.hasMoreElements()) {
			String clusterID = (String) clustersIDs.nextElement();
			double wordValueForTheCluster = clusterImportanceTable.get(clusterID) * wordValue;
			if(clusterSimilairtyTableForWords.containsKey(clusterID)){
				clusterSimilairtyTableForWords.put(clusterID, wordValueForTheCluster+clusterSimilairtyTableForWords.get(clusterID));
			}else{
				clusterSimilairtyTableForWords.put(clusterID, wordValueForTheCluster);
			}
		}// end loop for clusters at the matched node
	}
	
	public void updateEdgesClusterImportanceTable(Hashtable<String, Double> clusterSimilairtyTableForEdges, Relationship edge, double edgeValueInTheDocument ) throws Exception{
		Hashtable<String, Double> clusterImportanceTable = (Hashtable<String, Double>) deserializeObject((byte[]) edge.getProperty("cluster_table"));
		Enumeration clustersIDs = clusterImportanceTable.keys();
		//loop for all clusters in the node and update the cluster similarity table for the document
		while (clustersIDs.hasMoreElements()) {
			String clusterID = (String) clustersIDs.nextElement();
			double edgeValueForTheCluster = clusterImportanceTable.get(clusterID) * edgeValueInTheDocument;
			if(clusterSimilairtyTableForEdges.containsKey(clusterID)){
				clusterSimilairtyTableForEdges.put(clusterID, edgeValueForTheCluster + clusterSimilairtyTableForEdges.get(clusterID));
			}else{
				clusterSimilairtyTableForEdges.put(clusterID, edgeValueForTheCluster);
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


	public void updateTheGraph(Document doc, String clusterID) throws Exception{
		  Neo4jCluster cluster = this.clustersList.get(clusterID);
			double numberOfDocumentsInTheCluster = this.clustersList.get(clusterID).getDocumentIDs().size();
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
					double wordValueForTheDocument = calculateWordValue(doc, currentWord) / numberOfDocumentsInTheCluster;
					// update the cluster similarity table for the nodes
					Hashtable<String, Double> clusterImportanceTable = (Hashtable<String, Double>) deserializeObject((byte[]) currentNodeInGraph.getProperty(Neo4jNode.CLUSTER_IMPORTANCE)); 
					if (clusterImportanceTable.containsKey(clusterID)){
						clusterImportanceTable.put(clusterID, clusterImportanceTable.get(clusterID)+wordValueForTheDocument);
					}else{
						clusterImportanceTable.put(clusterID, wordValueForTheDocument);
					}
					currentNodeInGraph.setProperty(Neo4jNode.CLUSTER_IMPORTANCE, serializeObject(clusterImportanceTable));
					nodeIndex.add(currentNodeInGraph, Neo4jNode.WORD_PROPERTY, currentWord.getContent());
					// end updating cluster similarity table for the nodes
					
					// update the edges
					if((previousNodeInTheGraph != null) && (currentNodeInGraph != null)){
						String edgeID = previousWord.getContent()+"_"+currentWord.getContent();
						Relationship edge = edgesIndex.get("edge", edgeID).getSingle();
						Hashtable<String, Double> clusterImportanceTableForEdge  = (Hashtable<String, Double>) deserializeObject((byte[]) edge.getProperty("cluster_table")); 
						if(clusterImportanceTableForEdge.containsKey(clusterID)){
							clusterImportanceTableForEdge.put(clusterID, clusterImportanceTableForEdge.get(clusterID)+1);
						}else{
							clusterImportanceTableForEdge.put(clusterID, 1.0);
							cluster.incrementLength(1);
						}
						edge.setProperty("cluster_table", serializeObject(clusterImportanceTableForEdge));
						edgesIndex.add(edge, "edge", edgeID);
					}
					// end update the edges
					previousNodeInTheGraph = currentNodeInGraph;
					previousWord = currentWord;
				} // end loop for the words
			}// end loop for the sentences
	}
	
	
	public void clusterDocument(Document doc) throws Exception{
		Transaction tx = graphDb.beginTx();
		try {

		ArrayList<Sentence> sentencesList = doc.getSentences();
		// these tables will be used to calculate the similarity between the new document and existing cluster
		Hashtable<String, Double> clusterSimilairtyTableForWords = new Hashtable<String, Double>();
		Hashtable<String, Double> clusterSimilairtyTableForEdges = new Hashtable<String, Double>();
		double documentMagnitude = 0.0;
		double edgesMagnitude = 0.0;
		int numberOfEgdesOfDocument = 0;
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
				documentMagnitude +=  Math.pow(wordValueForTheDocument, 2);
				// start handling the word
				if(currentNodeInGraph != null){ // currentWord exists in the graph
					updateWordsClusterImportanceTable(clusterSimilairtyTableForWords, currentNodeInGraph, wordValueForTheDocument);
				}else{ // currentWord is a new word 
					currentNodeInGraph = createNewWord(currentWord);
				}
				// done handling the nodes
				// start handling the edges
				if((previousNodeInTheGraph != null) && (currentNodeInGraph != null)){
					numberOfEgdesOfDocument++;
					edgesMagnitude++;
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
		
		// Evaluate the document to the matched clusters
		String closestCluster = getClosestCluster(doc, documentMagnitude, numberOfEgdesOfDocument ,clusterSimilairtyTableForWords, clusterSimilairtyTableForEdges);
		if(closestCluster.equalsIgnoreCase("")){ //create new cluster
			closestCluster = String.valueOf(clusterCounter);
			Neo4jCluster c = new Neo4jCluster(closestCluster);
			c.incrementMagnitude(documentMagnitude);
			c.incrementEdgesMagnitude(edgesMagnitude);
			this.clustersList.put(c.getId(), c);
			c.addDcoument(doc.getId());
			this.clusterCounter++;
			updateTheGraph(doc, closestCluster);
		}else{
			Neo4jCluster c = this.clustersList.get(closestCluster);
			c.incrementMagnitude(documentMagnitude);
			c.incrementEdgesMagnitude(edgesMagnitude);
			c.addDcoument(doc.getId());
			updateTheGraph(doc, closestCluster);
		}
		tx.success();
		} finally {
			tx.finish();
		}
	}
	
	public String getClosestCluster(Document doc, double documentMagnitude, int numberOfEdgesOfDocument ,Hashtable<String, Double> clusterSimilarityForWords, Hashtable<String, Double> clusterSimilarityForEdges){
		Enumeration clusterIDs = clusterSimilarityForWords.keys();
		double selectedSimilairty = -1;
		String selectedClusterID = "";
		while (clusterIDs.hasMoreElements()) {
			String clusterID = (String) clusterIDs.nextElement();
			Neo4jCluster cluster = clustersList.get(clusterID);
		//	System.out.println("check document "+doc.getId()+ " to cluster "+ clusterID);	
			double wordsWeight = alpha * (  Math.sqrt(clusterSimilarityForWords.get(clusterID)) / ( Math.sqrt(documentMagnitude) + Math.sqrt(cluster.getMagnitude()))  ); 
		
			double edgesWeight = 0.0;
			if (clusterSimilarityForEdges.containsKey(clusterID)){
				double overlapping = clusterSimilarityForEdges.get(clusterID);
				edgesWeight = overlapping/(numberOfEdgesOfDocument + cluster.getEdgesMagnitude());
			//	System.out.println("edges ="+edgesWeight);
			}
	//		System.out.println("words ="+wordsWeight);
			double similairty = wordsWeight + edgesWeight ; 
	//		System.out.println("Similarity calculated to cluster"+ clusterID +" is = "+similairty);
			if (similairty > similarityThreshold && similairty > selectedSimilairty){
				selectedClusterID = clusterID;
				selectedSimilairty = similairty;
			}
		}
		
		return selectedClusterID;
	}
	

	public static boolean deleteDir(File dir) {
    if (dir.isDirectory()) {
        String[] children = dir.list();
        for (int i=0; i<children.length; i++) {
            boolean success = deleteDir(new File(dir, children[i]));
            if (!success) {
                return false;
            }
        }
    }

    // The directory is now empty so delete it
    return dir.delete();
}

	public static void run(double alpha, double similairtyThreshold) throws Exception{
		CIG.deleteDir(new File("/media/disk/master/Noe4j/EDIG"));
		NewsGroupDataset dataset = new NewsGroupDataset("/media/disk/master/Noe4j/datasets/20_newsgroups");
		CIG cig = new CIG(alpha, similairtyThreshold, dataset);
		DatasetLoader datasetHandler = cig.getDatasetHandler();
		Hashtable<String, Document> documentsHash =	datasetHandler.loadDocuments();
		long startTime = System.currentTimeMillis();
		ArrayList<String> documentsIDs = datasetHandler.getDocumentsIDS();
		Enumeration ids = documentsHash.keys();
		while (ids.hasMoreElements()) {
			String id = (String) ids.nextElement();
			Document d = documentsHash.get(id);
			System.out.println("Processing Document " + d.getId() + "From class " + d.getOrginalCluster() );
			cig.clusterDocument(d);
			
		}		
		long endTime = System.currentTimeMillis();
		cig.registerShutdownHook();
		CIGMeasure measure = new CIGMeasure();
		measure.calculate(cig.getClustersList(), datasetHandler);
		System.out.println("*********************************************************");
		System.out.println("F-Measure = "+ measure.getFmeasure());
		System.out.println("Precision = "+ measure.getPrecision());
		System.out.println("Recall = "+ measure.getRecall());
		System.out.println("Total elapsed time in execution  is :"+ (endTime-startTime) * 2.4);
		System.out.println("Alpha Value = "+ cig.getAlpha());
		System.out.println("Similarity Threshold = " +cig.getSimilarityThreshold());
		System.out.println("*********************************************************");

		
		
	}
	
	
	public static void main(String[] args) throws Exception {
		
		double alpha = 0.0;
		double similairtyThreshold = 0.15;
		CIG.run(alpha, similairtyThreshold);

	}
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
}
