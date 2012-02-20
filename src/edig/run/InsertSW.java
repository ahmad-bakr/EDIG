package edig.run;

import java.util.Enumeration;
import java.util.Hashtable;

import edig.datasets.DatasetLoader;
import edig.datasets.SWDataset;
import edig.datasets.UWCANDataset;
import edig.dig.representation.Neo4jHandler;
import edig.entites.Document;

public class InsertSW {
	public static void main(String[] args) throws Exception {
		Neo4jHandler neo4jHandler = Neo4jHandler.getInstance("/media/disk/master/Noe4j/SW");
		DatasetLoader datasetHandler = new SWDataset("/media/disk/master/Master/datasets/SW");
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
