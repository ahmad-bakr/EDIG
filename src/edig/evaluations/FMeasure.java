package edig.evaluations;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.Hashtable;

import edig.datasets.DatasetLoader;
import edig.dig.representation.Neo4jCluster;
import edig.dig.representation.Neo4jDocument;
import edig.dig.representation.Neo4jHandler;

public class FMeasure {
	private double precision;
	private double recall;
	private double fmeasure;
	private Hashtable<String, ArrayList<Integer>> confusionMatrix;

	public FMeasure() {
		this.precision = 0;
		this.recall = 0;
		this.fmeasure = 0;
		this.confusionMatrix = new Hashtable<String, ArrayList<Integer>>();
	}
	
	
	public void calculate(Hashtable<String, Neo4jCluster> clusters , DatasetLoader datasetHandler, Neo4jHandler neo4jHandler) throws Exception{
		ArrayList<String> classes = datasetHandler.getOriginalClasses();
		initializeConfusionMartix(classes, clusters.keySet().size());
		Enumeration e = clusters.keys();
		while (e.hasMoreElements()) {
			String clusterID = (String) e.nextElement();
			Neo4jCluster cluster = clusters.get(clusterID);
			processCluster(cluster, datasetHandler, neo4jHandler);
		}
		
		for (int i = 0; i < classes.size(); i++) {
			String className = classes.get(i);
			double values [] = calculateFMeasureForClass(className, datasetHandler, neo4jHandler, clusters);
			this.fmeasure += values[2] * datasetHandler.getNumberOfDocumentsInClass(className);
			this.recall += values[1] * datasetHandler.getNumberOfDocumentsInClass(className) ;
			this.precision += values[0] * datasetHandler.getNumberOfDocumentsInClass(className);
		}
		
		this.fmeasure = (this.fmeasure*1.0)/datasetHandler.numberOfDocuments();
		this.precision = (this.precision*1.0)/datasetHandler.numberOfDocuments();
		this.recall = (this.recall*1.0)/datasetHandler.numberOfDocuments();
		
	}
	
	public double getFmeasure() {
		return fmeasure;
	}
	
	public double getPrecision() {
		return precision;
	}
	
	public double getRecall() {
		return recall;
	}
	
	private double[] calculateFMeasureForClass(String className, DatasetLoader datasetHandler, Neo4jHandler neo4jHandler, Hashtable<String, Neo4jCluster> clusters){
		double values [] = new double[3] ;
		double maxFMeasure = 0;
		double maxPrecision = 0;
		double maxRecall = 0;
		ArrayList<Integer> list = this.confusionMatrix.get(className);
		int classSize = datasetHandler.getNumberOfDocumentsInClass(className);
		for (int i = 0; i < list.size(); i++) {
			String clusterID = String.valueOf(i+1);
			int clustersize = clusters.get(clusterID).getDocumentIDs().size();
			double precision = (list.get(i)*1.0)/clustersize;
			double recall = (list.get(i)*1.0)/classSize;
			double fmeasure = (2*precision*recall)/(precision+recall);
			if(fmeasure>maxFMeasure){
				maxFMeasure = fmeasure;
				maxPrecision = precision;
				maxRecall = recall;
			}
		}
		
		values[0] = maxPrecision;
		values[1] = maxRecall;
		values[2] = maxFMeasure;
		return values;
	}
	
	private void processCluster(Neo4jCluster cluster, DatasetLoader datasetHandler, Neo4jHandler neo4jHandler) throws IOException, ClassNotFoundException{
		ArrayList<Neo4jDocument> documents = cluster.getDocumentsList(datasetHandler, neo4jHandler);
		int clusterIDIndex = Integer.parseInt(cluster.getId()) - 1; 
		for (int i = 0; i < documents.size(); i++) {
			Neo4jDocument d = documents.get(i);
			String originalClass = datasetHandler.getDocument(d.getDocumentID()).getOrginalCluster();
			this.confusionMatrix.get(originalClass).set(clusterIDIndex, this.confusionMatrix.get(originalClass).get(clusterIDIndex) +1);
		}
	}
	
	private void initializeConfusionMartix(ArrayList<String> originalClasses, int numberOfClusters){
		for (int i = 0; i < originalClasses.size(); i++) {
			ArrayList<Integer> list = new ArrayList<Integer>(numberOfClusters);
			for (int j = 0; j < numberOfClusters; j++) {
				list.add(0);
			}
			confusionMatrix.put(originalClasses.get(i),list);
		}
	}
	
	
	
}
