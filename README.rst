mongo-jndi-plugin
=================

This is a fork of the `mongo-jndi-plugin
<https://github.com/juanlmelo/mongo-jndi-plugin>`_ by **Juan Luis Melo**.

I renamed a few things, moved it out of the propietary ``com.mongodb``
package and replaced the ``System.out`` logging with a Log4j ``Logger``, so
it would integrate nicely with WildFly's logging.

I also added some step-by-step installation instructions for WildFly 8.x:


Installation Steps for WildFly
------------------------------

The installation of the ``MongoClient`` JNDI plugin consists of the following
phases:

#. Install the MongoDB Java driver as a WildFly module.
#. Install this ``mongo-jndi-plugin`` as a WildFly module.
#. Configure ``standalone.xml`` to load the ``MongoClient`` into JNDI.
#. Use the ``MongoClient`` in your JavaEE app.



Step 1 - Install MongoDB Java driver as a module
~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~

Copy the sample module structure under ``modules/com`` in this repository
into your ``WILDFLY_ROOT/modules`` directory. Download the latest
`MongoDB Java Driver <http://central.maven.org/maven2/org/mongodb/mongo-java-driver/>`_
and place the JAR in ``WILDFLY_ROOT/modules/com/mongodb/driver/main/``, for
example the ``mongo-java-driver-2.12.3.jar``.

Edit ``WILDFLY_ROOT/modules/com/mongodb/driver/main/module.xml`` and match the
referenced JAR in the ``<resource-root ../>`` tag with the version you downloaded.

The directory should now look like this::

        modules/com
        └── mongodb
            └── driver
                └── main
                    ├── module.xml
                    └── mongo-java-driver-2.12.3.jar


Step 2 - Install the ``mongo-jndi-plugin`` as a module
~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~

Copy the sample module structure under ``modules/net`` in this repository
into your ``WILDFLY_ROOT/modules`` directory. Then build the object factory
from this repository::

        $ mvn package

Take the resulting file in ``target/mongo-jndi-plugin.jar`` and copy it to
``WILDFLY_ROOT/modules/net/nigmann/mongodb/jndi/main/``. The ``module.xml``
should not need any adjustments. 

The directory should now look like this::

        modules/net
        └── nigmann
            └── mongodb
                └── jndi
                    └── main
                        ├── module.xml
                        └── mongo-jndi-plugin.jar


Step 3 - Configure ``standalone.xml`` to load the ``MongoClient``
~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~

Find the ``<subsystem xmlns="urn:jboss:domain:naming:2.0">`` block in your
``standalone.xml`` and add the following block for every ``MongoClient``
you would like to add to JNDI::

        <subsystem xmlns="urn:jboss:domain:naming:2.0">
            <bindings>
                <object-factory name="java:global/MongoClient" module="net.nigmann.mongodb.jndi" class="net.nigmann.mongodb.jndi.MongoClientObjectFactory">
                    <environment>
                        <property name="mongoClientURI" value="mongodb://username:password@hostname/auth_db"/>
                    </environment>
                </object-factory>
            </bindings>
            <remote-naming/>
        </subsystem>

The ``mongoClientURI`` parameter follows the standard Mongo URI schema,
as docmented in the `MongoDB Docs <http://docs.mongodb.org/manual/reference/connection-string/>`_.


Step 4 - Use the ``MongoClient`` in your JavaEE app
~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~

Note that since you installed the MongoDB Java driver as a global module
in WildFly, your JavaEE project does not need to package this driver. In
my web app's ``pom.xml`` file, I was able to mark the MongoDB Java driver as
``<scope>provided</scope>``.

When you launch WildFly, the object factory should put your ``MongoClient``
instance into JNDI and log something along these lines::

        16:13:16,106 INFO  [MongoClientObjectFactory] Initializing MongoClientObjectFactory
        16:13:16,122 INFO  [MongoClientObjectFactory] Connecting to MongoDB. Hosts: [myhost], User: myuser
        16:13:16,182 INFO  [MongoClientObjectFactory] MongoClient successfully created. Storing in java:global/MongoClient
        
From there, you can then inject the resource into your JavaEE classes::

        @Resource(lookup = "java:global/MongoClient")
        private MongoClient mongoClient;
        

Disclaimer
----------

This fork and the documentation was mostly created for my own personal use,
so I could find it again later and breeze through the setup.

Please feel free to comment or improve. :-)
