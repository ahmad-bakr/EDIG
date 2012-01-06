package edig.document.similarity;

import edig.dig.representation.Neo4jCluster;
import edig.dig.representation.Neo4jDocument;

public interface DCSimIF {
	public double calculateSimilairty(Neo4jDocument document, Neo4jCluster cluster);
}
