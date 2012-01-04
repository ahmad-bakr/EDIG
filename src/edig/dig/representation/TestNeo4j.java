package edig.dig.representation;

import org.neo4j.graphdb.Node;

import edig.entites.Document;
import edig.entites.DocumentManager;

public class TestNeo4j {
	public static void main(String[] args) throws Exception {
//		String title1 = "Document1 Titl1";
//		String body1 ="Document1 Body1";
//		String title2 = "Document2 Titl2";
//		String body2 ="Document1 Body1";
//
//		Document doc1 = DocumentManager.createDocument("doc1" ,title1, body1);
//		Document doc2 = DocumentManager.createDocument("doc2", title2, body2);
		
		Neo4jHandler handler = Neo4jHandler.getInstance();
		Node node = handler.findNodeByProperty(Neo4jNode.WORD_PROPERTY, "Document1");
		Neo4jNode convertedNode = handler.convertToNeo4jNode(node);
		System.out.println(convertedNode.getDocumentTable().toString());
//		handler.InsertAndIndexDocument(doc1);
//		handler.InsertAndIndexDocument(doc2);
		
	}
}
