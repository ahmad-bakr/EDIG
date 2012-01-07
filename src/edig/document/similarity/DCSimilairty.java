package edig.document.similarity;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.Hashtable;
import java.util.Iterator;

import edig.datasets.DatasetLoader;
import edig.dig.representation.Neo4jCluster;
import edig.dig.representation.Neo4jDocument;
import edig.dig.representation.Neo4jHandler;
import edig.dig.representation.Neo4jNode;

public class DCSimilairty implements DCSimIF{
	private int numberOfDocuments ;

	@Override
	public double calculateSimilairty(Neo4jDocument document, Neo4jCluster cluster, int numberOfDocuments, Neo4jHandler neo4jHandler, DatasetLoader datasetHandler) throws IOException, ClassNotFoundException {
		this.numberOfDocuments = numberOfDocuments;
		Hashtable<String, Double> documentHash = convertToHash(document);
		Hashtable<String, Double> clusterHash = cluster.getRepresentative(datasetHandler, neo4jHandler);
		double documentMag = calculateMagnitude(documentHash);
		double clusterMag = calculateMagnitude(clusterHash);
		Enumeration e = documentHash.keys();
		double dotProduct = 0.0;
		while(e.hasMoreElements()){
      String word = (String) e.nextElement();
      if(clusterHash.containsKey(word)){
      	dotProduct+= documentHash.get(word)*clusterHash.get(word);
      }
		}
		if (documentMag<0.001) documentMag = 0.001;
		if (clusterMag<0.001) clusterMag = 0.001;
		
		double similarity= dotProduct / (Math.sqrt(documentMag) * Math.sqrt(clusterMag));
		return similarity;
	}
	
	private double calculateMagnitude(Hashtable<String, Double> words){
		double mag = 0.0;
		Enumeration e = words.elements();
    while(e.hasMoreElements()){
      mag+= Math.pow((Double) e.nextElement(), 2);
  	}
		return mag;
	}
	
	private Hashtable<String, Double> convertToHash(Neo4jDocument document){
		Hashtable<String, Double> terms = new Hashtable<String, Double>();
		String documentID = document.getDocumentID();
		ArrayList<Neo4jNode> words = document.getNodesList();
		for (Iterator iterator = words.iterator(); iterator.hasNext();) {
			Neo4jNode neo4jNode = (Neo4jNode) iterator.next();
			int documentTableSize = neo4jNode.getDocumentTable().size();
			ArrayList<String> documentEntityInTable = neo4jNode.getDocumentEntity(documentID);
			double termFrequency = Double.parseDouble(documentEntityInTable.get(0));
			double wordWeight =0;
			if(isTitleWord(documentEntityInTable.get(1))){
				 wordWeight = 0.2 * (termFrequency/document.getNumberOfTitleWords()) * Math.log((documentTableSize*1.0)/this.numberOfDocuments);
			}else{
				 wordWeight = (termFrequency/document.getNumberOfBodyWords()) * Math.log((documentTableSize*1.0)/this.numberOfDocuments);
			}
			terms.put(neo4jNode.getWord(), wordWeight);
		}
		return terms;
	}
	
	private boolean isTitleWord(String firstOccur){
		String first = firstOccur.split("_")[0];
		if(first.equalsIgnoreCase("0")) return true;
		return false;
	}


}
