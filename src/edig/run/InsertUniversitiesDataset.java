package edig.run;

import java.util.Enumeration;
import java.util.Hashtable;

import edig.datasets.DatasetLoader;
import edig.datasets.UWCANDataset;
import edig.datasets.UniversitesDataset;
import edig.dig.representation.Neo4jHandler;
import edig.entites.Document;

public class InsertUniversitiesDataset {
	public static void main(String[] args) throws Exception {
		Neo4jHandler neo4jHandler = Neo4jHandler.getInstance("/media/disk/master/Noe4j/universities");
		UniversitesDataset datasetHandler = new UniversitesDataset("/media/disk/master/Master/datasets/four_universites_mod");
		Hashtable<String, Document> docsHash = datasetHandler.loadDocuments();
		Enumeration e = docsHash.keys();
		while (e.hasMoreElements()) {
			Document doc = (Document) docsHash.get(e.nextElement());
			System.out.println("Inserting document "+ doc.getId() +" from "+ doc.getOrginalCluster());
			neo4jHandler.insertAndIndexDocument(doc);
		}
		neo4jHandler.registerShutdownHook();

	}
}
