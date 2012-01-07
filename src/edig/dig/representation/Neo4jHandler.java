package edig.dig.representation;

import java.io.BufferedOutputStream;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectInput;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.ArrayList;
import java.util.Hashtable;
import java.util.Iterator;

import org.neo4j.graphdb.Direction;
import org.neo4j.graphdb.DynamicRelationshipType;
import org.neo4j.graphdb.GraphDatabaseService;
import org.neo4j.graphdb.Node;
import org.neo4j.graphdb.Relationship;
import org.neo4j.graphdb.RelationshipType;
import org.neo4j.graphdb.ReturnableEvaluator;
import org.neo4j.graphdb.StopEvaluator;
import org.neo4j.graphdb.Transaction;
import org.neo4j.graphdb.Traverser;
import org.neo4j.graphdb.Traverser.Order;
import org.neo4j.graphdb.index.Index;
import org.neo4j.kernel.EmbeddedGraphDatabase;

import edig.datasets.DatasetLoader;
import edig.datasets.UWCANDataset;
import edig.entites.Document;
import edig.entites.DocumentManager;
import edig.entites.Sentence;
import edig.entites.Word;

public class Neo4jHandler {
	private static Neo4jHandler neo4jOperator = null;
	private static GraphDatabaseService graphDb = null;
	private static Index<Node> nodeIndex = null;
	private static Hashtable<String, Neo4jDocument> documentsCache;
	/**
	 * Private Constructor (Singleton)
	 */
	private Neo4jHandler() {
	};
	
	/**
	 * Get graph database service 
	 * @return graph database service
	 */
	public static GraphDatabaseService getGraphDb() {
		return graphDb;
	}

	/**
	 * Get instance of the graph database
	 * 
	 * @return instance of the graph database
	 */
	public static Neo4jHandler getInstance(String databasePath) {
		if (neo4jOperator == null) {
			documentsCache = new Hashtable<String, Neo4jDocument>();
			neo4jOperator = new Neo4jHandler();
			graphDb = new EmbeddedGraphDatabase(databasePath);
			nodeIndex = graphDb.index().forNodes("nodes");
			return neo4jOperator;
		}
		return neo4jOperator;
	}

	/**
	 * Method to find node by property (mostly the property used to index the
	 * node)
	 * 
	 * @param property
	 *          the property
	 * @param value
	 *          the value
	 * @return the node if found
	 */
	public Node findNodeByProperty(String property, String value) {
		return nodeIndex.get(property, value).getSingle();
	}

	/**
	 * Open transaction to neo4j
	 * @return transaction
	 */
	public Transaction getTransaction() {
		return graphDb.beginTx();
	}

	/**
	 * Insert and index node in Neo4j
	 * 
	 * @param node
	 * @throws ClassNotFoundException
	 * @throws IOException
	 */
	public void insertAndIndexNode(Neo4jNode node) throws IOException,
			ClassNotFoundException {
		Node graphNode = graphDb.createNode();
		graphNode.setProperty(Neo4jNode.WORD_PROPERTY, node.getWord());
		graphNode.setProperty(Neo4jNode.CLUSTER_IMPORTANCE,
				serializeObject(node.getClusterImportanceHash()));
		graphNode.setProperty(Neo4jNode.DOCUMENT_TABLE,
				serializeObject(node.getDocumentTable()));
		nodeIndex.add(graphNode, Neo4jNode.WORD_PROPERTY, node.getWord());
	}

	/**
	 * Modify existing node in neo4j
	 * @param existingNode existing node
	 * @param nodeToBeModified the node to be modified in neo4j
	 * @throws IOException
	 * @throws ClassNotFoundException
	 */
	public void modifyAndIndexNode(Neo4jNode existingNode, Node nodeToBeModified) throws IOException, ClassNotFoundException{
		nodeToBeModified.setProperty(Neo4jNode.CLUSTER_IMPORTANCE,
				serializeObject(existingNode.getClusterImportanceHash()));
		nodeToBeModified.setProperty(Neo4jNode.DOCUMENT_TABLE,
				serializeObject(existingNode.getDocumentTable()));
		nodeIndex.add(nodeToBeModified, Neo4jNode.WORD_PROPERTY, existingNode.getWord());
		
	}
	/**
	 * Create relationship between two nodes
	 * 
	 * @param source
	 *          the source node
	 * @param Destination
	 *          the destination node
	 * @param relationName
	 *          the relationship name
	 * @return the created relationship
	 */
	public Relationship createRelationship(Node source, Node Destination,
			String relationName) {
		RelationshipType type = DynamicRelationshipType.withName(relationName);
		Relationship relationship = source.createRelationshipTo(Destination, type);
		return relationship;
	}
	
	
	/**
	 * Convert from node to Neo4j node
	 * 
	 * @param node
	 *          node
	 * @return Neo4j object
	 * @throws IOException
	 * @throws ClassNotFoundException
	 */
	public Neo4jNode convertToNeo4jNode(Node node) throws IOException,
			ClassNotFoundException {
		Neo4jNode neo4jNode = new Neo4jNode(
				(String) node.getProperty(Neo4jNode.WORD_PROPERTY));
		neo4jNode
				.setDocumentTable((Hashtable<String, ArrayList<String>>) deserializeObject((byte[]) node
						.getProperty(Neo4jNode.DOCUMENT_TABLE)));
		neo4jNode
				.setClusterImportanceHash((Hashtable<String, Double>) deserializeObject((byte[]) node
						.getProperty(Neo4jNode.CLUSTER_IMPORTANCE)));
		return neo4jNode;
	}
	
	/**
	 * Return true if there is a relation between the source and destinatio
	 * @param source source node
	 * @param destination destination node
	 * @param type relationship type
	 * @return true if exists
	 */
	public boolean doesRelationsExist(Node source, Node destination, String type){
		boolean exists = false;
		for ( Relationship rel : source.getRelationships(Direction.OUTGOING) )
		{
			if(((int) rel.getEndNode().getId() == (int)destination.getId() ) && rel.getType().toString().equalsIgnoreCase(type)) return true;
		}
		return exists;
	}

	/**
	 * Insert document to Neo4j
	 * 
	 * @param doc
	 *          document object
	 * @throws IOException
	 * @throws ClassNotFoundException
	 */
	public void insertAndIndexDocument(Document doc) throws IOException,
			ClassNotFoundException {
		Node currentNode = null;
		Node previousNode = null;
		int sentenceNumber = 0;
		int wordNumber = 0;
		Transaction tx = graphDb.beginTx();
		try {
			ArrayList<Sentence> sentences = doc.getSentences();
			for (Sentence sentence : sentences) { // iterate over sentences
				wordNumber = 0;
				ArrayList<Word> words = sentence.getWords();
				for (Word word : words) { // iterate over words of sentence
					currentNode = findNodeByProperty(Neo4jNode.WORD_PROPERTY, word.getContent());
					if (currentNode == null) { // word not found in the index (new word)
						Neo4jNode newNode = new Neo4jNode(word.getContent());
						ArrayList<String> documentEntity = new ArrayList<String>();
						documentEntity.add("1");
						documentEntity.add(String.valueOf(sentenceNumber) + "_" + String.valueOf(wordNumber));
						newNode.addToDocumentTable(doc.getId(), documentEntity);
						insertAndIndexNode(newNode);
						currentNode = findNodeByProperty(Neo4jNode.WORD_PROPERTY,word.getContent());
					} else { // word exists
						Neo4jNode existingNode = convertToNeo4jNode(currentNode);
						if (existingNode.isInDocumentTable(doc.getId())) { // if the word was seen before in the current document
							ArrayList<String> documentEntity = existingNode.getDocumentEntity(doc.getId());
							int tf = Integer.parseInt(documentEntity.get(0)) + 1;
							documentEntity.set(0, String.valueOf(tf));
							documentEntity.add(String.valueOf(sentenceNumber) + "_" + String.valueOf(wordNumber));
							existingNode.addToDocumentTable(doc.getId(), documentEntity);
						} else { // if it's the first time to see the current word in the current document
							ArrayList<String> documentEntity = new ArrayList<String>();
							documentEntity.add("1");
							documentEntity.add(String.valueOf(sentenceNumber) + "_"	+ String.valueOf(wordNumber));
							existingNode.addToDocumentTable(doc.getId(), documentEntity);
						}// end if
						modifyAndIndexNode(existingNode, currentNode);
						currentNode = findNodeByProperty(Neo4jNode.WORD_PROPERTY, 	word.getContent());
					} // end if
					if (currentNode != null && previousNode != null && !doesRelationsExist(previousNode, currentNode, "document_" + doc.getId() ) ) {
						createRelationship(previousNode, currentNode, "document_" + doc.getId());
					}
					previousNode = currentNode;
					wordNumber++;
				}// end loop for words
				sentenceNumber++;
			}// end loop for sentences
			tx.success();
		} finally {
			tx.finish();
		}
	}

	/**
	 * Load document from Neo4j
	 * @param doc document
	 * @return Neo4j document
	 * @throws IOException
	 * @throws ClassNotFoundException
	 */
	public Neo4jDocument loadDocument(Document doc) throws IOException, ClassNotFoundException {
		if (documentsCache.containsKey(doc.getId())) return documentsCache.get(doc.getId());
		Neo4jDocument document = new Neo4jDocument(doc.getId());
		document.setNumberOfTitleWords(doc.getNumberOfTitleWords());
		document.setNumberOfBodyWords(doc.getNumberOfBodyWords());
		String firstWord = doc.getSentences().get(0).getWords().get(0).getContent();
		Node firstNode = findNodeByProperty(Neo4jNode.WORD_PROPERTY, firstWord);
		Traverser nodesChain = firstNode.traverse(Order.DEPTH_FIRST, StopEvaluator.END_OF_GRAPH, ReturnableEvaluator.ALL, DynamicRelationshipType.withName("document_"+doc.getId()), Direction.OUTGOING);
		for ( Node node : nodesChain )
		{
			document.addNode(convertToNeo4jNode(node));
		}
		documentsCache.put(doc.getId(), document);
		return document;
	}

	/**
	 * Method to serialize object to sequence of bytes (to store in neo4j)
	 * 
	 * @param obj
	 *          The object to be serialized
	 * @return serialized
	 * @throws IOException
	 * @throws ClassNotFoundException
	 */
	public byte[] serializeObject(Object obj) throws IOException,
			ClassNotFoundException {
		ObjectOutputStream os = null;
		ByteArrayOutputStream byteStream = new ByteArrayOutputStream();
		os = new ObjectOutputStream(new BufferedOutputStream(byteStream));
		os.flush();
		os.writeObject(obj);
		os.flush();
		byte[] sendBuf = byteStream.toByteArray();
		os.close();
		return sendBuf;
	}

	/**
	 * Method to deserialize sequence of bytes
	 * 
	 * @param buffer
	 *          the buffer
	 * @return object
	 * @throws IOException
	 * @throws ClassNotFoundException
	 */
	public Object deserializeObject(byte[] buffer) throws IOException,
			ClassNotFoundException {
		ByteArrayInputStream is = new ByteArrayInputStream(buffer);
		ObjectInput oi = new ObjectInputStream(is);
		Object newObj = oi.readObject();
		oi.close();
		return newObj;
	}

	/**
	 * Shutdown the database
	 * 
	 * @param graphDb
	 *          graph database instance
	 */
	public void registerShutdownHook() {
		Runtime.getRuntime().addShutdownHook(new Thread() {
			@Override
			public void run() {
				graphDb.shutdown();
			}
		});
	}

	/**
	 * @param args
	 * @throws Exception 
	 */
	public static void main(String[] args) throws Exception {
		// TODO Auto-generated method stub
//		String title = "Hello, This is title. Ahmad Bakr";
//		String body ="Hello, This is body. How is going?";
//		Document doc = DocumentManager.createDocument("doc1" ,title, body);
//		Document doc2 = DocumentManager.createDocument("doc2" ,title, body);		
		
		Neo4jHandler neo4jHandler = Neo4jHandler.getInstance("/media/disk/master/Noe4j/UWCAN");
		DatasetLoader datasetHandler = new UWCANDataset("/media/disk/master/Master/datasets/WU-CAN/webdata");
		Hashtable<String, Document> docsHash = datasetHandler.loadDocuments();
		Document testDocument = docsHash.get("4EnUp.htm");
		Neo4jDocument neo4jDoc = neo4jHandler.loadDocument(testDocument);
		neo4jDoc.addCluster("1", 0.9);
		Neo4jDocument neo4jDoc2 = neo4jHandler.loadDocument(testDocument);
		System.out.println(neo4jDoc2.getClusterIDsList().toString());
		
		neo4jHandler.registerShutdownHook();
//		handler.insertAndIndexDocument(doc);
//		handler.insertAndIndexDocument(doc2);

		
//		Node node = handler.findNodeByProperty(Neo4jNode.WORD_PROPERTY, "Hello");
//		Node node2 = handler.findNodeByProperty(Neo4jNode.WORD_PROPERTY, "This");
//		for ( Relationship rel : node.getRelationships(Direction.OUTGOING) )
//		{
//			System.out.println(rel.getType().toString());
//		}
//		System.out.println(handler.doesRelationsExist(node, node2, "document_doc1"));
// 		Neo4jNode convertedNode = handler.convertToNeo4jNode(node);
//		System.out.println(convertedNode.getDocumentTable().get("doc1").toString());
//		System.out.println(convertedNode.getDocumentTable().get("doc2").toString());

	}

}
