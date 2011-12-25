package edig.dig.representation;

import java.io.BufferedOutputStream;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectInput;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.ArrayList;
import org.neo4j.graphdb.DynamicRelationshipType;
import org.neo4j.graphdb.GraphDatabaseService;
import org.neo4j.graphdb.Node;
import org.neo4j.graphdb.Relationship;
import org.neo4j.graphdb.RelationshipType;
import org.neo4j.graphdb.Transaction;
import org.neo4j.graphdb.index.Index;
import org.neo4j.kernel.EmbeddedGraphDatabase;

import edig.entites.Document;

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
	 */
	public void insertAndIndexNode(Neo4jNode node){
		 Node graphNode = graphDb.createNode();
	   graphNode.setProperty( Neo4jNode.WORD_PROPERTY, node.getWord() );
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
	
	
	public void InsertAndIndexDocument(Document doc){
		
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
			Node firstNode = graphDb.createNode();
			Node secondNode = graphDb.createNode();
			RelationshipType type = DynamicRelationshipType.withName( "rel" );
			Relationship relationship = firstNode.createRelationshipTo(secondNode,type);

			firstNode.setProperty( "message", array.toString());
			secondNode.setProperty( "message", "world!" );
			relationship.setProperty( "message", "brave Neo4j " );
			
			System.out.print( firstNode.getProperty( "message" ) );
			System.out.print( relationship.getProperty( "message" ) );
			System.out.print( secondNode.getProperty( "message" ) );
			
		  // let's remove the data
			for ( Node node : graphDb.getAllNodes() )
			{
			    for ( Relationship rel : node.getRelationships() )
			    {
			        rel.delete();
			    }
			    node.delete();
			}
		  tx.success();
		}
		finally
		{
		    tx.finish();
		}
		
		
	}

}
