/*
 * Author:
 *	Juan Melo	@ 2013	
 *
 */

package com.mongodb.jndi;

import java.util.Enumeration;
import java.util.Hashtable;

import javax.naming.Context;
import javax.naming.Name;
import javax.naming.RefAddr;
import javax.naming.Reference;
import javax.naming.spi.ObjectFactory;

import com.mongodb.MongoClient;
import com.mongodb.MongoClientOptions;
import com.mongodb.MongoClientURI;

/**
 * Mongo JNDI factory, getObjectInstance method will return an object of type {@code com.mongodb.MongoClient}  if everything goes fine.
 * 
 * 
 * @author jmelo
 *
 */
public class MongoClientJNDIFactory implements ObjectFactory {

	private static final String MONGO_CLIENT_URI = "mongoClientURI";

	final String newLine = System.getProperty("line.separator");


	public Object getObjectInstance(Object obj, Name name, Context nameCtx, Hashtable<?, ?> environment) throws Exception {

		String mongoURI = null;
		Enumeration<RefAddr> props = ((Reference) obj).getAll();
		
		while (props.hasMoreElements()) {
			RefAddr addr = (RefAddr) props.nextElement();
			if (addr != null) {
				if (MONGO_CLIENT_URI.equals(addr.getType())) {
					mongoURI = (String) addr.getContent();
				}
			}
		}
		
		
		if (mongoURI == null || mongoURI.isEmpty()) {
			throw new RuntimeException(MONGO_CLIENT_URI + " resource property is empty");
		}
		
		/* Instantiate MongoClientURI	*/
		MongoClientURI uri = new MongoClientURI(mongoURI);
		
		/* Mongo client options			*/
		 MongoClientOptions o = uri.getOptions();

		/* Show hosts and options		*/
		StringBuilder s = new StringBuilder("*************** Mongo Client JNDI Factory ***************");
		s.append(newLine);
		for (String host: uri.getHosts()) {
			s.append("Mongo host: ["); s.append(host); s.append("]"); s.append(newLine); 	
		}
		
		if (o != null) {
			s.append("Mongo options set "); s.append(o.toString()); s.append(newLine);
		}
		/* Create mongoClient 			*/
		MongoClient mongoClient = new MongoClient(uri);
		
		System.out.println( s );
		
		return mongoClient;
	}

}
