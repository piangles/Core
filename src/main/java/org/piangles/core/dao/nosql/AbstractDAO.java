package org.piangles.core.dao.nosql;

import org.bson.conversions.Bson;
import org.piangles.core.dao.DAOException;
import org.piangles.core.resources.MongoDataStore;

import com.mongodb.client.FindIterable;
import com.mongodb.client.MongoCollection;

public abstract class AbstractDAO<T>
{
	private MongoCollection<T> collection = null;

	public void init(MongoDataStore dataStore)
	{
		collection = dataStore.getDatabase().getCollection(getTClass().getSimpleName(), getTClass());
	}

	protected final void create(T obj) throws DAOException
	{
		collection.insertOne(obj);
	}
	
	protected final FindIterable<T> read(Bson filter) throws DAOException
	{
		return collection.find(filter);
	}

	protected final T readOne(Bson filter) throws DAOException
	{
		return (T)collection.find(filter).first();
	}

	protected final void update(Bson filter, T obj) throws DAOException
	{
		collection.replaceOne(filter, obj);
	}

	protected final void delete(Bson filter) throws DAOException
	{
		collection.deleteMany(filter);
	}

	protected abstract Class<T> getTClass();
}

