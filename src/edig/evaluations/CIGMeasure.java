package edig.evaluations;

import java.util.ArrayList;
import java.util.Enumeration;
import java.util.Hashtable;

import edig.datasets.DatasetLoader;
import edig.dig.representation.Neo4jCluster;
import edig.entites.Document;

public class CIGMeasure {

	private double precision;
	private double recall;
	private double fmeasure;
	private Hashtable<String, ArrayList<Integer>> confusionMatrix;
	
	public CIGMeasure() {
		this.precision = 0;
		this.recall = 0;
		this.fmeasure = 0;
		this.confusionMatrix = new Hashtable<String, ArrayList<Integer>>();
	}

	public double getFmeasure() {
		return (2.0*this.precision*this.recall)/(this.precision+this.recall);
	}
	
	public double getPrecision() {
		return precision;
	}
	
	
	public double getRecall() {
		return recall;
	}
	
	
	public void calculate(Hashtable<String, Neo4jCluster> clusters , DatasetLoader datasetHandler){
		ArrayList<String> classes = datasetHandler.getOriginalClasses();
		initializeConfusionMartix(classes, clusters.keySet().size());
		Enumeration e = clusters.keys();
		while (e.hasMoreElements()) {
			String clusterID = (String) e.nextElement();
			Neo4jCluster cluster = clusters.get(clusterID);
			processCluster(cluster, datasetHandler);
		}
		for (int i = 0; i < classes.size(); i++) {
			String className = classes.get(i);
			double values [] = calculateFMeasureForClass(className, datasetHandler, clusters);
			this.fmeasure += values[2] * datasetHandler.getNumberOfDocumentsInClass(className);
			this.recall += values[1] * datasetHandler.getNumberOfDocumentsInClass(className) ;
			this.precision += values[0] * datasetHandler.getNumberOfDocumentsInClass(className);
		}

		this.fmeasure = (this.fmeasure*1.0)/datasetHandler.numberOfDocuments();
		this.precision = (this.precision*1.0)/datasetHandler.numberOfDocuments();
		this.recall = (this.recall*1.0)/datasetHandler.numberOfDocuments();


	}
	
	private double[] calculateFMeasureForClass(String className, DatasetLoader datasetHandler, Hashtable<String, Neo4jCluster> clusters){
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
	
	
	
	private void processCluster(Neo4jCluster cluster, DatasetLoader datasetHandler){
		ArrayList<String> clusterDocumentsIDS = cluster.getDocumentIDs();
		int clusterIDIndex = Integer.parseInt(cluster.getId()) - 1; 
		for (int i = 0; i < clusterDocumentsIDS.size(); i++) {
			Document d = datasetHandler.getDocument(clusterDocumentsIDS.get(i));
			String originalClass = d.getOrginalCluster();
			int count = this.confusionMatrix.get(originalClass).get(clusterIDIndex);
			this.confusionMatrix.get(originalClass).set(clusterIDIndex, count +1);
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
