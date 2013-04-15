mongo-jndi-plugin
=================

Pure java version of my Mongodb JNDI plugin, based in mongo-java-driver 2.11.0.

Resource to include in your context.xml:
<Resource name="mongodb/MongoClientJndi" auth="Container"
	type="com.mongodb.MongoClient" factory="com.mongodb.jndi.MongoClientJNDIFactory"
 mongoClientURI="mongodb://username:password@yourdomain.com:27017,username:password@yourdomain.com:27017,yourdomain:27017/collection?waitqueuemultiple=1500&amp;w=1&amp;maxpoolsize=40&amp;safe=true" />

See http://api.mongodb.org/java/2.10.1/com/mongodb/MongoClientURI.html for more information on how to construct a MongoClientURI correctly.


This is a sample on how to integrate with spring 3.X:
In your applicationContext.xml (or whatever spring config file you use):		
	<jee:jndi-lookup 
		lazy-init="true" 
		jndi-name="mongodb/MongoClientJndi" 
		id="mongoClient"/>
	
	
In your java service or dao class:
	@Autowired
	@Qualifier("mongoClient")
	MongoClient mongoClient;

Enjoy

