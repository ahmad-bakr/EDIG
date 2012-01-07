package edig.clustering.algorithms;

import java.io.IOException;

import org.neo4j.graphdb.Node;

import edig.dig.representation.Neo4jHandler;
import edig.dig.representation.Neo4jNode;

public class SinglePass {
	
	public static void main(String[] args) throws IOException, ClassNotFoundException {
		Neo4jHandler neo4jHandler = Neo4jHandler.getInstance("/media/disk/master/Noe4j/UWCAN");
		Node node = neo4jHandler.findNodeByProperty(Neo4jNode.WORD_PROPERTY, "bear");
		Neo4jNode neo4jNode = neo4jHandler.convertToNeo4jNode(node);
		System.out.println(neo4jNode.getWord());
		System.out.println(neo4jNode.getDocumentTable().keySet().toString());
		neo4jHandler.registerShutdownHook();	
	}

}
