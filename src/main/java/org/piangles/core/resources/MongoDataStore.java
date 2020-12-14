package org.piangles.core.resources;

//import static com.mongodb.client.model.Filters.eq;
//import static java.util.Collections.singletonList;
import static org.bson.codecs.configuration.CodecRegistries.fromProviders;
import static org.bson.codecs.configuration.CodecRegistries.fromRegistries;

import java.util.Properties;

import org.bson.codecs.configuration.CodecRegistry;
import org.bson.codecs.pojo.PojoCodecProvider;
import org.piangles.core.util.abstractions.Decrypter;

import com.mongodb.ConnectionString;
import com.mongodb.MongoClientSettings;
import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoClients;
import com.mongodb.client.MongoDatabase;

public final class MongoDataStore
{
	private static final String PREFIX = "mongodb";
	private static final String DATABASE = "Database";

	private static final String LOGIN_ID = "LoginId";
	private static final String PASSWORD = "Password";
	private static final String HOST_ADDRESS = "HostAddress";
	
	private static final String DECRYPTER_CLASS_NAME = "DecrypterClassName";
	private static final String DECRYPTER_AUTHZ_ID = "DecrypterAuthorizationId";
	
	private MongoDatabase mongoDb;

	MongoDataStore(String serviceName, Properties dataStoreProps) throws Exception
	{
		String decrypterClassName = dataStoreProps.getProperty(DECRYPTER_CLASS_NAME);
		String decrypterAuthorizationId = dataStoreProps.getProperty(DECRYPTER_AUTHZ_ID);
		
		Decrypter decrypter = (Decrypter)Class.forName(decrypterClassName).getConstructor().newInstance();
		decrypter.init(serviceName, "Configuration", DECRYPTER_AUTHZ_ID, decrypterAuthorizationId);

		String userName = decrypter.decrypt(LOGIN_ID, dataStoreProps.getProperty(LOGIN_ID));
		String password = decrypter.decrypt(PASSWORD, dataStoreProps.getProperty(PASSWORD));
		String hostAddress = dataStoreProps.getProperty(HOST_ADDRESS);
		String database = dataStoreProps.getProperty(DATABASE);

		StringBuilder sb = new StringBuilder();
		sb.append(PREFIX).append("://")
				.append(userName).append(":").append(password)
				.append("@").append(hostAddress).append("/")
				.append(database);

		ConnectionString connectionString = new ConnectionString(sb.toString());
		
		/**
		 * For persisting POJOs
		 * https://www.mongodb.com/blog/post/quick-start-java-and-mongodb--mapping-pojos?utm_campaign=javapojos&utm_source=twitter&utm_medium=organic_social
		 * 
		 * Need to configure the CodecRegistry to include a codec to handle 
		 * the translation to and from BSON for our POJOs.
		 */
		CodecRegistry pojoCodecRegistry = fromProviders(PojoCodecProvider.builder().automatic(true).build());
		/**
		 * Need to add the default codec registry, which contains all the default codecs. 
		 * They can handle all the major types in Java-like Boolean, Double, String, BigDecimal, etc.
		 */
		CodecRegistry codecRegistry = fromRegistries(MongoClientSettings.getDefaultCodecRegistry(), 
                pojoCodecRegistry);
		/**
		 * Now wrap all my settings together using MongoClientSettings.
		 */
		MongoClientSettings clientSettings = MongoClientSettings.builder()
                .applyConnectionString(connectionString)
                .codecRegistry(codecRegistry)
                .build();

		/**
		 * Connection Pooling
		 * https://stackoverflow.com/questions/44785556/how-to-use-mongodb-connecton-pooling-in-java
		 * https://stackoverflow.com/questions/8968125/mongodb-connection-pooling
		 */
		MongoClient mongoClient = MongoClients.create(clientSettings);
		mongoDb = mongoClient.getDatabase(database);
	}
	
	public MongoDatabase getDatabase()
	{
		return mongoDb;
	}
}