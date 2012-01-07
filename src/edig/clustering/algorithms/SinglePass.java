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
import edig.entites.Document;

public class SinglePass {
	
	public Hashtable<String,Neo4jCluster> perform(DatasetLoader datasetHandler, Neo4jHandler neo4jHandler) throws Exception {
		Hashtable<String,Neo4jCluster> clustersList = new Hashtable<String,Neo4jCluster>();
		Hashtable<String, Document> docsHash = datasetHandler.loadDocuments();
		Enumeration e = docsHash.keys();
		while (e.hasMoreElements()) {
			String documentID = (String) e.nextElement();
			Document document = docsHash.get(documentID);
			ArrayList<Neo4jDocument> similarDocuments = getSimilarDocuments(document, neo4jHandler, datasetHandler);
			
		}

		return clustersList;
	}
	
	private ArrayList<Neo4jDocument> getSimilarDocuments(Document doc, Neo4jHandler neo4jHandler, DatasetLoader datasetHandler) throws IOException, ClassNotFoundException{
		ArrayList<Neo4jDocument> similarDocument = new ArrayList<Neo4jDocument>();
		Hashtable<String, Neo4jDocument> similarDocumentHash = new Hashtable<String, Neo4jDocument>();
		Neo4jDocument neo4jDocument = neo4jHandler.loadDocument(doc);
		ArrayList<Neo4jNode> nodes = neo4jDocument.getNodesList();
		for (Iterator iterator = nodes.iterator(); iterator.hasNext();) {
			Neo4jNode neo4jNode = (Neo4jNode) iterator.next();
			Hashtable<String, ArrayList<String>> documentTable = neo4jNode.getDocumentTable();
			Enumeration e = documentTable.keys();
			while (e.hasMoreElements()) {
				String simDocumentID = (String) e.nextElement();
				if(!simDocumentID.equalsIgnoreCase(doc.getId()) && !similarDocumentHash.containsKey(simDocumentID)){
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
