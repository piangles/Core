package org.piangles.core.resources;

import java.util.Properties;

import org.piangles.core.util.abstractions.Decrypter;

import com.mongodb.ConnectionString;
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
		MongoClient mongoClient = MongoClients.create(connectionString);
		mongoDb = mongoClient.getDatabase(database);
	}
	
	public MongoDatabase getDatabase()
	{
		return mongoDb;
	}
}
