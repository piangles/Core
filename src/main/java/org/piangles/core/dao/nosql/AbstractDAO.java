package org.piangles.core.dao.nosql;

import org.bson.Document;
import org.piangles.core.dao.DAOException;
import org.piangles.core.resources.MongoDataStore;

import com.google.gson.Gson;
import com.mongodb.client.MongoCollection;

public abstract class AbstractDAO<T>
{
	private MongoDataStore dataStore = null;

	public void init(MongoDataStore dataStore)
	{
		this.dataStore = dataStore;
	}

	protected final void create(String message, T obj) throws DAOException
	{
		Document document = new Document();
		Gson gson = new Gson();
		MongoCollection<Document> collection = dataStore.getDatabase().getCollection("Logs");
		document.put(message, gson.toJson(obj));
		collection.insertOne(document);
	}

//	protected final void update() throws DAOException;
//	protected final void read() throws DAOException;
//	protected final void delete() throws DAOException;
}
