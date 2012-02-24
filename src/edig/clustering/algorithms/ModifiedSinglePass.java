package edig.clustering.algorithms;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.Hashtable;
import java.util.Iterator;

import edig.datasets.DatasetLoader;
import edig.datasets.NewsGroupDataset;
import edig.datasets.ReutersDataset;
import edig.datasets.SWDataset;
import edig.datasets.UWCANDataset;
import edig.datasets.UniversitesDataset;
import edig.dig.representation.Neo4jCluster;
import edig.dig.representation.Neo4jDocument;
import edig.dig.representation.Neo4jHandler;
import edig.dig.representation.Neo4jNode;
import edig.document.similarity.DCSimIF;
import edig.document.similarity.DCSimilairty;
import edig.document.similarity.DDSimIF;
import edig.document.similarity.DDSimilairty;
import edig.entites.Document;
import edig.evaluations.FMeasure;

public class ModifiedSinglePass {
	
	public Hashtable<String,Neo4jCluster> perform(DatasetLoader datasetHandler, Neo4jHandler neo4jHandler, double similairtyThreshold, int softClusteringThreshold) throws Exception {
		Hashtable<String,Neo4jCluster> clustersList = new Hashtable<String,Neo4jCluster>();
		Hashtable<String, Document> docsHash = datasetHandler.loadDocuments();
		DDSimIF similarityCalculator = new DDSimilairty();
		Enumeration e = docsHash.keys();
		int numberOfClusters = 0;
		//loop for documents in the dataset
		while (e.hasMoreElements()) {
			Hashtable<String, Double> candidateClustersHash = new Hashtable<String, Double>();
			String documentID = (String) e.nextElement();
			System.out.println("Processing document "+ documentID );
			Document document = docsHash.get(documentID);
			Neo4jDocument neo4jDocument = neo4jHandler.loadDocument(document);
			boolean clusteredYet = false;
			// get similar documents to the document
			ArrayList<Neo4jDocument> similarDocuments = getSimilarDocuments(neo4jDocument, neo4jHandler, datasetHandler);
			// loop over the similar documents
			for (Iterator iterator = similarDocuments.iterator(); iterator.hasNext();) {

				Neo4jDocument neo4jSimilarDocument = (Neo4jDocument) iterator.next();
				// continue if the current similar document has no clusters
				if(neo4jSimilarDocument.getClustersHash().isEmpty()) continue;
				// check if the distance to the document is greater than the threshold
				if(similarityCalculator.calculateSimilarity(neo4jDocument, neo4jSimilarDocument, datasetHandler.numberOfDocuments()) > similairtyThreshold ){
					//get the clusters of the similar document
				 ArrayList<String> candidateDocumentClustersIDs = neo4jSimilarDocument.getClusterIDsList();
					// loop over the clusters of the similar document
				 for (Iterator iterator2 = candidateDocumentClustersIDs.iterator(); iterator2.hasNext();) { //loop for candidate clusters
					String candidateClusterID = (String) iterator2.next();
					// get the cluster
					Neo4jCluster candidateNeo4jCluster = clustersList.get(candidateClusterID);
					// calculate the average similarity to the cluster
					double averageSimilairtyToCluster = calculateAvgSimilairtyToCluster(neo4jDocument, candidateNeo4jCluster, datasetHandler, neo4jHandler);
					// if the average similarity greater the the threshold then the cluster to the candidate clusters
					if(averageSimilairtyToCluster > similairtyThreshold){
						clusteredYet = true;
						candidateClustersHash.put(candidateClusterID, averageSimilairtyToCluster);
					}// end if adding cluster to candidate cluster hash
				 }//end loop for candidate clusters
				}//end if [checking if the distance to the candidate document is less than the threshold] 
			}//end looping for similar documents
			
			if(!clusteredYet){ // create new cluster
				numberOfClusters++;
				Neo4jCluster newCluster = new Neo4jCluster(String.valueOf(numberOfClusters));
				newCluster.addDcoument(documentID);
				neo4jDocument.addCluster(newCluster.getId(), 1);
				clustersList.put(newCluster.getId(), newCluster);
			}else{ // add to the cloeset cluster
				String nearestClusterID = getNearestCluster(candidateClustersHash);
				Neo4jCluster cluster = clustersList.get(nearestClusterID);
				cluster.addDcoument(neo4jDocument.getDocumentID());
				neo4jDocument.addCluster(nearestClusterID, 1);

			}
			
		} // end loop for all documents in the data set

		return clustersList;
	}
	
	private String getNearestCluster(Hashtable<String, Double> clustersHash){
		Enumeration e = clustersHash.keys();
		String closestCluster ="";
		double value = 0.0;
		while (e.hasMoreElements()) {
			String cluster = (String) e.nextElement();
			if(closestCluster.equalsIgnoreCase("")){
				closestCluster = cluster;
				value = clustersHash.get(cluster);
			}else{
				if(value < clustersHash.get(cluster)){
					closestCluster = cluster;
					value = clustersHash.get(cluster);
				}
			}
		}
		return closestCluster;
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
		double similarity =0 ;
		DCSimIF similairtyCalculator = new DCSimilairty();
		similarity = similairtyCalculator.calculateSimilairty(document, cluster, datasetHandler.numberOfDocuments(), neo4jHandler, datasetHandler);
		return similarity;
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
	
	public static void main(String[] args) throws Exception {
		Neo4jHandler neo4jHandler = Neo4jHandler.getInstance("/media/disk/master/Noe4j/universities");
		UniversitesDataset datasetHandler = new UniversitesDataset("/media/disk/master/Master/datasets/four_universites_mod");
		long startTime = System.currentTimeMillis();
		ModifiedSinglePass singlePassAlgorithm = new ModifiedSinglePass();
		double threshold = 0.1;
		Hashtable<String, Neo4jCluster> clusters = singlePassAlgorithm.perform(datasetHandler, neo4jHandler, threshold, 5);
		long endTime = System.currentTimeMillis();
		FMeasure fmeasureCalculate = new FMeasure();
		fmeasureCalculate.calculate(clusters, datasetHandler, neo4jHandler);
		System.out.println("*********************");
		System.out.println("Total elapsed time in execution  is :"+ (endTime-startTime)*12);
		System.out.println("******* For Threshold = " + threshold);
		System.out.println("Fmeasure = " + fmeasureCalculate.getFmeasure());
		System.out.println("Precision = "+ fmeasureCalculate.getPrecision());
		System.out.println("Recall = "+ fmeasureCalculate.getRecall());
		System.out.println("*********************");
		
//		System.out.println("Number of documents = "+ datasetHandler.numberOfDocuments());
//		Enumeration e = clusters.keys();
//		while (e.hasMoreElements()) {
//			String clusterID = (String) e.nextElement();
//			System.out.println("Cluster = " + clusterID + " has number of documents = " + clusters.get(clusterID).getDocumentIDs().size());	
//			ArrayList<Neo4jDocument> documents = clusters.get(clusterID).getDocumentsList(datasetHandler, neo4jHandler); 
//			for (int i = 0; i < documents.size(); i++) {
//				System.out.println(datasetHandler.getDocument(documents.get(i).getDocumentID()).getOrginalCluster());
//			}
//			System.out.println(("*********************************************************************************"));
//		}
		neo4jHandler.registerShutdownHook();	
	}

}
