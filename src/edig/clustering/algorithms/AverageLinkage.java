package edig.clustering.algorithms;

import java.util.ArrayList;
import java.util.Enumeration;
import java.util.Hashtable;
import java.util.LinkedList;

import edig.datasets.DatasetLoader;
import edig.dig.representation.Neo4jCluster;
import edig.dig.representation.Neo4jDocument;
import edig.dig.representation.Neo4jHandler;
import edig.document.similarity.DDSimIF;
import edig.document.similarity.DDSimilairty;
import edig.entites.Document;

public class AverageLinkage {
	private DatasetLoader datasetHandler;
	private Neo4jHandler  neo4jHandler;
	private ArrayList<Neo4jCluster> finalClustersList; 
	private int numberOfClusters;
	private int numberOfDocuments;
	private Hashtable<String, Document> docsHash;
	private ArrayList<Boolean> clustersExists;
	private double[][] similairtyMatrix;
	
	public AverageLinkage(DatasetLoader datasetHandler, Neo4jHandler neo4jHandler, int clustersNumber) throws Exception {
		this.datasetHandler = datasetHandler;
		this.neo4jHandler = neo4jHandler;
		this.clustersExists = new ArrayList<Boolean>();
		this.finalClustersList = new ArrayList<Neo4jCluster>();
		this.numberOfClusters = clustersNumber;
	  docsHash = datasetHandler.loadDocuments();
	  numberOfDocuments = datasetHandler.numberOfDocuments();
		this.similairtyMatrix = new double[datasetHandler.numberOfDocuments()][datasetHandler.numberOfDocuments()];
		for (int i = 0; i < datasetHandler.numberOfDocuments(); i++) {
			clustersExists.add(true);
		}
		ArrayList<Neo4jDocument> documents = new ArrayList<Neo4jDocument>();
		Enumeration e = docsHash.keys();
		while (e.hasMoreElements()) {
			Document d = (Document) e.nextElement();
			documents.add(neo4jHandler.loadDocument(d));
		}
		initializeSimilairtyMatrix(documents);
	}
	
	public Hashtable<String, Neo4jCluster> perfrom() throws Exception{
		Hashtable<String,Neo4jCluster> clustersList = new Hashtable<String,Neo4jCluster>();
		while(clustersColumns.size() > numberOfClusters){
			
		}
		return clustersList;
	}
	
	public void initializeSimilairtyMatrix(ArrayList<Neo4jDocument> documents){
		DDSimIF similarityCalculate = new DDSimilairty();
		for (int i = 0; i < documents.size(); i++) {
			for (int j = 0; j < documents.size(); j++) {
				if(i>j) continue;
				if(i==j){
					this.similairtyMatrix[i][j]=0;
				}else{
					this.similairtyMatrix[i][j] = similarityCalculate.calculateSimilarity(documents.get(i), documents.get(j), numberOfDocuments);
				}
			}
		}
	}
	

	
	
	
}
