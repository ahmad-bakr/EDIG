package edig.dig.representation;

import java.util.ArrayList;


public class Neo4jDocument {
	private int documentID;
	private ArrayList<Neo4jNode> nodesList;
	
	/**
	 * Neo4j Document Constructor
	 * @param id document id
	 */
	public Neo4jDocument(int id){
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
	public int getDocumentID() {
		return documentID;
	}
	
	/**
	 * Set document id
	 * @param documentID document id
	 */
	public void setDocumentID(int documentID) {
		this.documentID = documentID;
	}
}
