package org.piangles.core.dao.nosql;

import org.piangles.core.dao.DAOException;
import org.piangles.core.resources.MongoDataStore;

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

	protected abstract Class<T> getTClass();
//	protected final void update() throws DAOException;
//	protected final void read() throws DAOException;
//	protected final void delete() throws DAOException;
}
