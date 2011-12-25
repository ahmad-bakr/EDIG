package edig.dig.representation;

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
	 * @param args
	 */
	public static void main(String[] args) {
		// TODO Auto-generated method stub
		GraphDatabaseService graphDb = new EmbeddedGraphDatabase( "/media/disk/master/Master/EDIG_DB" );
		// indexing .. http://wiki.neo4j.org/content/Indexing_with_IndexService
		Transaction tx = graphDb.beginTx();
		try
		{
			Node firstNode = graphDb.createNode();
			Node secondNode = graphDb.createNode();
			RelationshipType type = DynamicRelationshipType.withName( "rel" );
			Relationship relationship = firstNode.createRelationshipTo(secondNode,type);
			
			firstNode.setProperty( "message", "Hello, " );
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
