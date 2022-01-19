/*
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements. See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License. You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
 
 
 
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
	private MongoDataStore dataStore = null;
	private MongoCollection<Document> collection = null;
	private MongoCollection<T> tCollection = null;

	public void init(MongoDataStore dataStore)
	{
		this.dataStore = dataStore;
		
		collection = dataStore.getDatabase().getCollection(getTClass().getSimpleName());
		tCollection = dataStore.getDatabase().getCollection(getTClass().getSimpleName(), getTClass());
	}

	protected final void create(T obj) throws DAOException
	{
		try
		{
			tCollection.insertOne(obj);
		}
		catch(Exception e)
		{
			throw new DAOException(e.getMessage(), e);
		}
	}
	
	protected final FindIterable<T> read(Bson filter) throws DAOException
	{
		FindIterable<T> iteratable = null;
		try
		{
			iteratable = tCollection.find(filter); 
		}
		catch(Exception e)
		{
			throw new DAOException(e.getMessage(), e);
		}
		return iteratable;
	}

	protected final T readOne(Bson filter) throws DAOException
	{
		T theChosenOne = null;
		try
		{
			theChosenOne = (T)tCollection.find(filter).first(); 
		}
		catch(Exception e)
		{
			throw new DAOException(e.getMessage(), e);
		}
		
		return theChosenOne;
	}

	protected final void update(Bson filter, T obj) throws DAOException
	{
		try
		{
			tCollection.replaceOne(filter, obj);	
		}
		catch(Exception e)
		{
			throw new DAOException(e.getMessage(), e);
		}
	}

	protected final void upsert(Bson filter, T obj) throws DAOException
	{
		try
		{
			tCollection.replaceOne(filter, obj, new ReplaceOptions().upsert(true));
		}
		catch(Exception e)
		{
			throw new DAOException(e.getMessage(), e);
		}
	}

	protected final void delete(Bson filter) throws DAOException
	{
		try
		{
			tCollection.deleteMany(filter);
		}
		catch(Exception e)
		{
			throw new DAOException(e.getMessage(), e);
		}
	}
	
	protected final MongoDataStore getMongoDataStore()
	{
		return dataStore;
	}

	protected final MongoCollection<T> getTConnection()
	{
		return tCollection;
	}

	protected final MongoCollection<Document> getConnection()
	{
		return collection;
	}

	protected abstract Class<T> getTClass();
}

