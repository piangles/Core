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

