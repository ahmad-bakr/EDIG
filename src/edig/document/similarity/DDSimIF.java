package edig.document.similarity;

import edig.datasets.DatasetLoader;
import edig.dig.representation.Neo4jDocument;

public interface DDSimIF {
	public double calculateSimilarity(Neo4jDocument document1, Neo4jDocument document2, int numberOfDocuments);
}
