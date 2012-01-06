package edig.dig.representation;

import java.util.ArrayList;


public class Neo4jDocument {
	private String documentID;
	private ArrayList<Neo4jNode> nodesList;
	
	/**
	 * Neo4j Document Constructor
	 * @param id document id
	 */
	public Neo4jDocument(String id){
		this.documentID = id;
		this.nodesList = new ArrayList<Neo4jNode>();
	}
	
	/**
	 * Get the nodes list
	 * @return nodes list of Neo4j nodes
	 */
	public ArrayList<Neo4jNode> getNodesList() {
		return nodesList;
	}
	/**
	 * Get document id
	 * @return document id
	 */
	public String getDocumentID() {
		return documentID;
	}
	
	/**
	 * Set document id
	 * @param documentID document id
	 */
	public void setDocumentID(String documentID) {
		this.documentID = documentID;
	}
	
	/**
	 * Add node to the document list
	 * @param node node
	 */
	public void addNode(Neo4jNode node){
		this.nodesList.add(node);
	}
}
