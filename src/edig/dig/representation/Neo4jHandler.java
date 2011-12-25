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
	
	public Node findNodeByProperty(String property, String value){
		return this.nodeIndex.get(property, value).getSingle();
	}
	
	public void insertAndIndexNode(){}
	
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
	
	
	public boolean InsertAndIndexDocument(Document doc){
		
		return true;
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
		ByteArrayOutputStream byteStream = new ByteArrayOutputStream(1000);
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
