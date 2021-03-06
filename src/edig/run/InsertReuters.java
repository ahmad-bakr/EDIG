package edig.run;

import java.util.Enumeration;
import java.util.Hashtable;

import edig.datasets.NewsGroupDataset;
import edig.datasets.ReutersDataset;
import edig.dig.representation.Neo4jHandler;
import edig.entites.Document;

public class InsertReuters {
	public static void main(String[] args) throws Exception {
		Neo4jHandler neo4jHandler = Neo4jHandler.getInstance("/media/disk/master/Noe4j/reuters");
		ReutersDataset datasetHandler = new ReutersDataset("/media/disk/master/Noe4j/datasets/reuters_mod");
		Hashtable<String, Document> docsHash = datasetHandler.loadDocuments();
		Enumeration e = docsHash.keys();
		long startTime = System.currentTimeMillis();

		while (e.hasMoreElements()) {
			Document doc = (Document) docsHash.get(e.nextElement());
			System.out.println("Inserting document "+ doc.getId() +" from "+ doc.getOrginalCluster());
			neo4jHandler.insertAndIndexDocument(doc);
		}
		long endTime = System.currentTimeMillis();
		System.out.println("Total elapsed time in execution  is :"+ (endTime-startTime));
		System.out.println(datasetHandler.numberOfDocuments() + " documents inserted");
		neo4jHandler.registerShutdownHook();

	}
}
