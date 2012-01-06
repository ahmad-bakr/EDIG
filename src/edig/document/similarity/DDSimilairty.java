package edig.document.similarity;

import java.util.ArrayList;
import java.util.Enumeration;
import java.util.Hashtable;
import java.util.Iterator;

import edig.datasets.DatasetLoader;
import edig.dig.representation.Neo4jDocument;
import edig.dig.representation.Neo4jNode;

public class DDSimilairty implements DDSimIF{
	private int numberOfDocuments ;
	
	@Override
	public double calculateSimilarity(Neo4jDocument document1, Neo4jDocument document2, int numberOfDocuments) {
		this.numberOfDocuments = numberOfDocuments;
		Hashtable<String, Double> document1Hash = convertToHash(document1);
		Hashtable<String, Double> document2Hash = convertToHash(document2);
		double document1Mag = calculateMagnitude(document1Hash);
		double document2Mag = calculateMagnitude(document2Hash);
		Enumeration e = document1Hash.keys();
		double dotProduct = 0.0;
		while(e.hasMoreElements()){
      String word = (String) e.nextElement();
      if(document2Hash.containsKey(word)){
      	dotProduct+= document1Hash.get(word)*document1Hash.get(word);
      }
		}
		double similarity= dotProduct / (Math.sqrt(document1Mag) * Math.sqrt(document2Mag));
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
				 wordWeight = 0.2 * termFrequency * Math.log((documentTableSize*1.0)/this.numberOfDocuments);
			}else{
				 wordWeight =  termFrequency * Math.log((documentTableSize*1.0)/this.numberOfDocuments);
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
