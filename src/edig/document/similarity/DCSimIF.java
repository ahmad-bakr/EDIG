package edig.document.similarity;

import java.io.IOException;

import edig.datasets.DatasetLoader;
import edig.dig.representation.Neo4jCluster;
import edig.dig.representation.Neo4jDocument;
import edig.dig.representation.Neo4jHandler;

public interface DCSimIF {
	
	public double calculateSimilairty(Neo4jDocument document, Neo4jCluster cluster,
			int numberOfDocuments, Neo4jHandler neo4jHandler,
			DatasetLoader datasetHandler) throws IOException, ClassNotFoundException;
}
