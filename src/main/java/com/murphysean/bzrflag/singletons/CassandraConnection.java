package com.murphysean.bzrflag.singletons;

import java.util.Arrays;
import java.util.List;
import java.util.logging.Logger;

import com.datastax.driver.core.Cluster;
import com.datastax.driver.core.Cluster.Builder;
import com.datastax.driver.core.Session;

public class CassandraConnection {
	private Logger logger = Logger.getLogger(this.getClass().getName());
	private static CassandraConnection instance = new CassandraConnection("murphysean.com", "bzrflag", "cassandra", "cassandra");
	protected String contactPoints;
	protected String keyspace;
	protected String username;
	protected String password;

	protected Cluster cluster;
	protected Session session;
	
	private CassandraConnection(){
		logger.info("CassandraConnection Instantiating");
	}
	
	private CassandraConnection(String contactPoints, String keyspace, String username, String password){
		logger.info("CassandraConnection Instantiating");
		this.contactPoints = contactPoints;
		this.keyspace = keyspace;
		this.username = username;
		this.password = password;
		connect();
	}

	public static CassandraConnection getInstance(){
		return instance;
	}
	
	protected void connect(){
		logger.info("CassandraConnection Connecting");
		List<String> contactPointsList = Arrays.asList(contactPoints.split(","));
		Builder cb = cluster.builder();
		for(String s: contactPointsList){
			cb.addContactPoint(s);
		}
		cluster = cb
				.withCredentials(username, password)
				.build();
		session = cluster.connect(keyspace);
	}
	
	public void close(){
		logger.info("CassandraConnection Shutting Down");
		cluster.shutdown();
	}

	public String getContactPoints() {
		return contactPoints;
	}

	public void setContactPoints(String contactPoints) {
		this.contactPoints = contactPoints;
	}

	public String getKeyspace() {
		return keyspace;
	}

	public void setKeyspace(String keyspace) {
		this.keyspace = keyspace;
	}

	public String getUsername() {
		return username;
	}

	public void setUsername(String username) {
		this.username = username;
	}

	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
	}

	public Cluster getCluster() {
		if(cluster == null)
			connect();
		return cluster;
	}

	public void setCluster(Cluster cluster) {
		//this.cluster = cluster;
	}

	public Session getSession() {
		if(session == null)
			connect();
		return session;
	}

	public void setSession(Session session) {
		//this.session = session;
	}
}
