/**
 * 
 */
package net.nigmann.mongodb.jndi;


import java.util.Hashtable;

import javax.naming.Context;
import javax.naming.Name;
import javax.naming.spi.ObjectFactory;

import org.apache.log4j.Logger;

import com.mongodb.MongoClient;
import com.mongodb.MongoClientURI;


/**
 * Object factory for creating a {@link MongoClient}. This module can be installed
 * into a WildFly application server to provide JNDI-based access to MongoDB.
 * 
 * This class is loosely based on a similar class by Juan Luis Melo
 * (https://github.com/juanlmelo/mongo-jndi-plugin)
 * 
 * I merely replaced the <code>System.out</code> calls with a <code>Logger</code>.
 * 
 * @author Bernd Nigmann
 *
 */
public class MongoClientObjectFactory implements ObjectFactory {

    /** Logger instance for some helpful output */
    private static final Logger logger = Logger.getLogger(MongoClientObjectFactory.class);

    /** Single configuration parameter for the object factory */
    private static final String MONGO_CLIENT_URI = "mongoClientURI";



    /**
     * Creates a {@link MongoClient} instance based on a {@link MongoClientURI} passed
     * in via the module environment.
     * 
     */
    public Object getObjectInstance(Object obj, Name name, Context nameCtx,
            Hashtable< ? , ? > environment) throws Exception {

        logger.info("Initializing MongoClientObjectFactory");

        try {
            /* Fetch the MongoClientURI from the environment */
            logger.debug("Fetching mongoClientURI");
            String clientUri = (String)environment.get(MONGO_CLIENT_URI);
            if (clientUri == null) { throw new RuntimeException(
                    "No MongoDB URI given in environment parameter '" + MONGO_CLIENT_URI + "'"); }

            MongoClientURI mUri = new MongoClientURI((String)environment.get(MONGO_CLIENT_URI));

            if (logger.isInfoEnabled()) {
                logger.info("Connecting to MongoDB. Hosts: " + mUri.getHosts() + ", User: "
                        + mUri.getUsername());
            }

            MongoClient mongoClient = new MongoClient(mUri);
            logger.info("MongoClient successfully created. Storing in JNDI as " + obj);
            return mongoClient;

        } catch (Throwable t) {
            logger.fatal("Unable to initialize MongoClient", t);
            throw new RuntimeException(t);
        }
    }


}
