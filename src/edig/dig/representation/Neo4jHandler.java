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

import org.neo4j.graphdb.DynamicRelationshipType;
import org.neo4j.graphdb.GraphDatabaseService;
import org.neo4j.graphdb.Node;
import org.neo4j.graphdb.Relationship;
import org.neo4j.graphdb.RelationshipType;
import org.neo4j.graphdb.Transaction;
import org.neo4j.graphdb.index.Index;
import org.neo4j.kernel.EmbeddedGraphDatabase;

import edig.entites.Document;
import edig.entites.Sentence;
import edig.entites.Word;

public class Neo4jHandler {
	private Neo4jHandler neo4jOperator = null;
	private GraphDatabaseService graphDb = null;
	private Index<Node> nodeIndex = null;

	
	/**
	 * Private Constructor (Singleton)
	 */
	private Neo4jHandler(){};
	
	/**
	 * Get instance of the graph database
	 * @return instance of the graph database
	 */
	public Neo4jHandler getInstance(){
		if(this.neo4jOperator==null){
			this.neo4jOperator = new Neo4jHandler();
			this.graphDb = new EmbeddedGraphDatabase( "/media/disk/master/Master/EDIG_DB" );
			this.nodeIndex =  graphDb.index().forNodes( "nodes" );
			return this.neo4jOperator;
		}
		return this.neo4jOperator;
	}
	
	/**
	 * Method to find node by property (mostly the property used to index the node)
	 * @param property the property
	 * @param value the value
	 * @return the node if found
	 */
	public Node findNodeByProperty(String property, String value){
		return this.nodeIndex.get(property, value).getSingle();
	}
	
	/**
	 * Insert and index node in Neo4j
	 * @param node 
	 * @throws ClassNotFoundException 
	 * @throws IOException 
	 */
	public void insertAndIndexNode(Neo4jNode node) throws IOException, ClassNotFoundException{
		 Node graphNode = this.graphDb.createNode();
	   graphNode.setProperty( Neo4jNode.WORD_PROPERTY, node.getWord() );
	   graphNode.setProperty(Neo4jNode.CLUSTER_IMPORTANCE, serializeObject(node.getClusterImportanceHash()));
	   graphNode.setProperty(Neo4jNode.DOCUMENT_TABLE, serializeObject(node.getDocumentTable()));
	   this.nodeIndex.add( graphNode, Neo4jNode.WORD_PROPERTY, node.getWord());
	}
	
	/**
	 * Create relationship between two nodes
	 * @param source the source node
	 * @param Destination the destination node
	 * @param relationName the relationship name
	 * @return the created relationship
	 */
	public Relationship createRelationship(Node source, Node Destination, String relationName){
		RelationshipType type = DynamicRelationshipType.withName( relationName );
		Relationship relationship = source.createRelationshipTo(Destination,type);
		return relationship;
	}
	
	/**
	 * Convert from node to Neo4j node 
	 * @param node node
	 * @return Neo4j object
	 * @throws IOException
	 * @throws ClassNotFoundException
	 */
	public Neo4jNode convertToNeo4jNode(Node node) throws IOException, ClassNotFoundException{
		Neo4jNode neo4jNode = new Neo4jNode((String) node.getProperty(Neo4jNode.WORD_PROPERTY));
		neo4jNode.setDocumentTable((Hashtable<String, ArrayList<String>>) deserializeObject((byte[]) node.getProperty(Neo4jNode.DOCUMENT_TABLE)));
		neo4jNode.setClusterImportanceHash((Hashtable<String, Double>) deserializeObject((byte[]) node.getProperty(Neo4jNode.CLUSTER_IMPORTANCE)));
		return neo4jNode;
	}
	
	/**
	 * Insert document to Neo4j
	 * @param doc document object
	 * @throws IOException
	 * @throws ClassNotFoundException
	 */
	public void InsertAndIndexDocument(Document doc) throws IOException, ClassNotFoundException{
		Node currentNode = null;
		Node previousNode = null;
		int sentenceNumber = 0;
		int wordNumber = 0;
		Transaction tx = this.graphDb.beginTx();
		try
		{
			ArrayList<Sentence> sentences = doc.getSentences();
			for (Sentence sentence : sentences) { //iterate over sentences
				ArrayList<Word> words = sentence.getWords();
				for (Word word : words) { //iterate over words of sentence
				  currentNode =findNodeByProperty(Neo4jNode.WORD_PROPERTY, word.getContent());
					if(currentNode == null) { //word not found in the index (new word)
						Neo4jNode newNode = new Neo4jNode(word.getContent());
						ArrayList<String> documentEntity = new ArrayList<String>();
						documentEntity.add("1");
						documentEntity.add(String.valueOf(sentenceNumber)+"_"+String.valueOf(wordNumber));
						newNode.addToDocumentTable(doc.getId(), documentEntity);
						insertAndIndexNode(newNode);
						currentNode = findNodeByProperty(Neo4jNode.WORD_PROPERTY, word.getContent());
					}else{ //word exists
						Neo4jNode existingNode = convertToNeo4jNode(currentNode);
						if(existingNode.isInDocumentTable(doc.getId())){ // if the word was seen before in the current document
							ArrayList<String> documentEntity = existingNode.getDocumentEntity(doc.getId());
							int tf= Integer.parseInt(documentEntity.get(0)) + 1;
							documentEntity.set(0, String.valueOf(tf));
							documentEntity.add(String.valueOf(sentenceNumber)+"_"+String.valueOf(wordNumber));
							existingNode.addToDocumentTable(doc.getId(), documentEntity);
						}else{ //if it's the first time to see the current word in the current document
							ArrayList<String> documentEntity = new ArrayList<String>();
							documentEntity.add("1");
							documentEntity.add(String.valueOf(sentenceNumber)+"_"+String.valueOf(wordNumber));
							existingNode.addToDocumentTable(doc.getId(), documentEntity);
						}// end if
						insertAndIndexNode(existingNode);
						currentNode = findNodeByProperty(Neo4jNode.WORD_PROPERTY, word.getContent());
					} //end if
					if(currentNode !=null && previousNode != null){
						createRelationship(previousNode, currentNode, "document_"+doc.getId());
					}
					previousNode = currentNode;
					wordNumber++;
				}//end loop for words
				sentenceNumber++;
			}// end loop for sentences
			tx.success();
		}
		finally
		{
		  tx.finish();
		}
	}
	
	public ArrayList<Neo4jNode> loadDocument(Neo4jNode firstNode){
		ArrayList<Neo4jNode> nodes = new ArrayList<Neo4jNode>();
		
		return nodes;
	}
	
	/**
	 * Method to serialize object to sequence of bytes (to store in neo4j)
	 * @param obj The object to be serialized
	 * @return serialized
	 * @throws IOException
	 * @throws ClassNotFoundException
	 */
	public byte[] serializeObject(Object obj) throws IOException, ClassNotFoundException {
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
	 * @param buffer the buffer
	 * @return object 
	 * @throws IOException
	 * @throws ClassNotFoundException
	 */
	public Object deserializeObject(byte [] buffer) throws IOException, ClassNotFoundException{
		ByteArrayInputStream is = new ByteArrayInputStream(buffer);
		ObjectInput oi = new ObjectInputStream(is);
		Object newObj = oi.readObject();
		oi.close();
		return newObj;
	}
	
	/**
	 * Shutdown the database
	 * @param graphDb graph database instance
	 */
	public void registerShutdownHook( final GraphDatabaseService graphDb )
	{
	    Runtime.getRuntime().addShutdownHook( new Thread()
	    {
	        @Override
	        public void run()
	        {
	            graphDb.shutdown();
	        }
	    } );
	}
	

	/**
	 * @param args
	 * @throws IOException 
	 * @throws ClassNotFoundException 
	 */
	public static void main(String[] args) throws IOException, ClassNotFoundException {
		// TODO Auto-generated method stub
		GraphDatabaseService graphDb = new EmbeddedGraphDatabase( "/media/disk/master/Master/EDIG_DB" );
		Index<Node> nodeIndex2 = graphDb.index().forNodes( "nodes" );
		ArrayList<String> array = new ArrayList<String>();
		array.add("ahmad");
		array.add("Ahmad2");
		// indexing .. http://wiki.neo4j.org/content/Indexing_with_IndexService
		Transaction tx = graphDb.beginTx();
		try
		{
		//	nodeIndex2.get(property, value).getSingle();
				Node node = nodeIndex2.get("message","node3").getSingle();
				if (node == null){
					System.out.println("node not found");
				}
				//System.out.println(node.getProperty("prop2"));
				//node.setProperty("prop2", "prop2Value");
				//nodeIndex2.add( node, "message", "node2");
//			Node firstNode = graphDb.createNode();
//			Node secondNode = graphDb.createNode();
//			RelationshipType type = DynamicRelationshipType.withName( "rel" );
//			Relationship relationship = firstNode.createRelationshipTo(secondNode,type);
//
//			firstNode.setProperty( "message", array.toString());
//			secondNode.setProperty( "message", "world!" );
//			relationship.setProperty( "message", "brave Neo4j " );
//			nodeIndex2.add( firstNode, "message", "node1");
//			nodeIndex2.add( secondNode, "message", "node2");
//			
			 
//			System.out.print( firstNode.getProperty( "message" ) );
//			System.out.print( relationship.getProperty( "message" ) );
//			System.out.print( secondNode.getProperty( "message" ) );
			
		  // let's remove the data
//			for ( Node node : graphDb.getAllNodes() )
//			{
//			    for ( Relationship rel : node.getRelationships() )
//			    {
//			        rel.delete();
//			    }
//			    node.delete();
//			}
		  tx.success();
		}
		finally
		{
		    tx.finish();
		}
		
		int i = 10;
		System.out.println(String.valueOf(i));
	}

}
