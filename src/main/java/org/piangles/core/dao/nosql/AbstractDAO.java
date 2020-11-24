package org.piangles.core.dao.nosql;

import org.bson.Document;
import org.bson.conversions.Bson;
import org.piangles.core.dao.DAOException;
import org.piangles.core.resources.MongoDataStore;

import com.mongodb.client.FindIterable;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.model.ReplaceOptions;

public abstract class AbstractDAO<T>
{
	private MongoCollection<Document> collection = null;
	private MongoCollection<T> tCollection = null;

	public void init(MongoDataStore dataStore)
	{
		collection = dataStore.getDatabase().getCollection(getTClass().getSimpleName());
		tCollection = dataStore.getDatabase().getCollection(getTClass().getSimpleName(), getTClass());
	}

	protected final void create(T obj) throws DAOException
	{
		tCollection.insertOne(obj);
	}
	
	protected final FindIterable<T> read(Bson filter) throws DAOException
	{
		return tCollection.find(filter);
	}

	protected final T readOne(Bson filter) throws DAOException
	{
		return (T)tCollection.find(filter).first();
	}

	protected final void update(Bson filter, T obj) throws DAOException
	{
		tCollection.replaceOne(filter, obj);
	}

	protected final void upsert(Bson filter, T obj) throws DAOException
	{
		tCollection.replaceOne(filter, obj, new ReplaceOptions().upsert(true));
	}

	protected final void delete(Bson filter) throws DAOException
	{
		tCollection.deleteMany(filter);
	}
	
	protected final MongoCollection<Document> getConnection()
	{
		return collection;
	}

	protected abstract Class<T> getTClass();
}

