package edig.clustering.algorithms;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.Hashtable;
import java.util.Iterator;

import org.neo4j.graphdb.Node;

import edig.datasets.DatasetLoader;
import edig.dig.representation.Neo4jCluster;
import edig.dig.representation.Neo4jDocument;
import edig.dig.representation.Neo4jHandler;
import edig.dig.representation.Neo4jNode;
import edig.document.similarity.DDSimIF;
import edig.document.similarity.DDSimilairty;
import edig.entites.Document;

public class SinglePass {
	
	public Hashtable<String,Neo4jCluster> perform(DatasetLoader datasetHandler, Neo4jHandler neo4jHandler, double similairtyThreshold, int softClusteringThreshold) throws Exception {
		Hashtable<String,Neo4jCluster> clustersList = new Hashtable<String,Neo4jCluster>();
		Hashtable<String, Document> docsHash = datasetHandler.loadDocuments();
		DDSimIF similarityCalculator = new DDSimilairty();
		Enumeration e = docsHash.keys();
		int numberOfClusters = 0;
		while (e.hasMoreElements()) {
			String documentID = (String) e.nextElement();
			Document document = docsHash.get(documentID);
			Neo4jDocument neo4jDocument = neo4jHandler.loadDocument(document);
			boolean clusteredYet = false;
			ArrayList<Neo4jDocument> similarDocuments = getSimilarDocuments(neo4jDocument, neo4jHandler, datasetHandler);
			for (Iterator iterator = similarDocuments.iterator(); iterator.hasNext();) {
				Neo4jDocument neo4jSimilarDocument = (Neo4jDocument) iterator.next();
				if(neo4jSimilarDocument.getClustersHash().isEmpty()) continue;
				// check if the distance to the document is greater than the threshold
				if(similarityCalculator.calculateSimilarity(neo4jDocument, neo4jSimilarDocument, datasetHandler.numberOfDocuments()) > similairtyThreshold ){
				  ArrayList<String> candidateDocumentClustersIDs = neo4jSimilarDocument.getClusterIDsList();
				 for (Iterator iterator2 = candidateDocumentClustersIDs.iterator(); iterator2.hasNext();) { //loop for candidate clusters
					String candidateClusterID = (String) iterator2.next();
					Neo4jCluster candidateNeo4jCluster = clustersList.get(candidateClusterID);
					double averageSimilairtyToCluster = calculateAvgSimilairtyToCluster(neo4jDocument, candidateNeo4jCluster, datasetHandler, neo4jHandler);
					if(averageSimilairtyToCluster > similairtyThreshold){
						clusteredYet = true;
						candidateNeo4jCluster.addDcoument(neo4jDocument.getDocumentID());
						neo4jDocument.addCluster(candidateClusterID, averageSimilairtyToCluster);
					}// end if adding document to the cluster
				 }//end loop for candidate clusters
				  
				  
				}//end if [checking if the distance to the candidate document is less than the threshold] 
			}//end looping for similar documents
			
			if(!clusteredYet){ // create new cluster
				numberOfClusters++;
				Neo4jCluster newCluster = new Neo4jCluster(String.valueOf(numberOfClusters));
				newCluster.addDcoument(documentID);
				neo4jDocument.addCluster(newCluster.getId(), 0);
			}
			
		} // end loop for all documents in the data set

		return clustersList;
	}
	
	/**
	 * Calculate the average similarity to the cluster
	 * @param document the target document
	 * @param cluster the target cluster
	 * @param datasetHandler dataset handler
	 * @param neo4jHandler neo4j handler
	 * @return the average similarity
	 * @throws IOException
	 * @throws ClassNotFoundException
	 */
	private double calculateAvgSimilairtyToCluster(Neo4jDocument document, Neo4jCluster cluster, DatasetLoader datasetHandler, Neo4jHandler neo4jHandler) throws IOException, ClassNotFoundException{
		double similairty =0 ;
		DDSimIF similairtyCalculator = new DDSimilairty();
		ArrayList<Neo4jDocument> clusterDocuments = cluster.getDocumentsList(datasetHandler, neo4jHandler);
		for (Iterator iterator = clusterDocuments.iterator(); iterator.hasNext();) {
			Neo4jDocument neo4jDocument = (Neo4jDocument) iterator.next();
			similairty += similairtyCalculator.calculateSimilarity(document, neo4jDocument, datasetHandler.numberOfDocuments());
		}
		return similairty/clusterDocuments.size();
	}
	
	/**
	 * Get similar documents to the current document by matching the nodes
	 * @param doc target document
	 * @param neo4jHandler neo4j handler
	 * @param datasetHandler dataset handler
	 * @return arraylist of matching document
	 * @throws IOException
	 * @throws ClassNotFoundException
	 */
	private ArrayList<Neo4jDocument> getSimilarDocuments(Neo4jDocument doc, Neo4jHandler neo4jHandler, DatasetLoader datasetHandler) throws IOException, ClassNotFoundException{
		ArrayList<Neo4jDocument> similarDocument = new ArrayList<Neo4jDocument>();
		Hashtable<String, Neo4jDocument> similarDocumentHash = new Hashtable<String, Neo4jDocument>();
		Neo4jDocument neo4jDocument = doc;
		ArrayList<Neo4jNode> nodes = neo4jDocument.getNodesList();
		for (Iterator iterator = nodes.iterator(); iterator.hasNext();) {
			Neo4jNode neo4jNode = (Neo4jNode) iterator.next();
			Hashtable<String, ArrayList<String>> documentTable = neo4jNode.getDocumentTable();
			Enumeration e = documentTable.keys();
			while (e.hasMoreElements()) {
				String simDocumentID = (String) e.nextElement();
				if(!simDocumentID.equalsIgnoreCase(doc.getDocumentID()) && !similarDocumentHash.containsKey(simDocumentID)){
					Document d = datasetHandler.getDocument(simDocumentID);
					Neo4jDocument nd = neo4jHandler.loadDocument(d);
					similarDocumentHash.put(simDocumentID, nd);
					similarDocument.add(nd);
				}//end if
			}//end looping for document at document table in the current node
			
		}// end looping for the node
		return similarDocument;
	}
	
	public static void main(String[] args) throws IOException, ClassNotFoundException {
		Neo4jHandler neo4jHandler = Neo4jHandler.getInstance("/media/disk/master/Noe4j/UWCAN");
		Node node = neo4jHandler.findNodeByProperty(Neo4jNode.WORD_PROPERTY, "bear");
		Neo4jNode neo4jNode = neo4jHandler.convertToNeo4jNode(node);
		System.out.println(neo4jNode.getWord());
		System.out.println(neo4jNode.getDocumentTable().keySet().toString());
		neo4jHandler.registerShutdownHook();	
	}

}
