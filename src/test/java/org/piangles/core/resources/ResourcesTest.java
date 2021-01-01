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
 
 
 
package org.piangles.core.resources;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

import org.piangles.core.resources.RDBMSDataStore;
import org.piangles.core.resources.ResourceException;
import org.piangles.core.resources.ResourceManager;
import org.piangles.core.util.central.CentralConfigProvider;

public class ResourcesTest {

	public static void main(String[] args)
	{
		try {
			RDBMSDataStore dataStore = ResourceManager.getInstance().getRDBMSDataStore(new CentralConfigProvider("ConfigService", "14fe64ea-d15a-4c8b-af2f-f2c7efe1943b"));
			Statement stm = dataStore.getConnection().createStatement();
			ResultSet rs = stm.executeQuery("select * from Backbone.Config where ComponentId='14fe64ea-d15a-4c8b-af2f-f2c7efe1943b'");
			while(rs.next())
			{
				System.out.println(rs.getString(5));
			}
		} catch (ResourceException | SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
}
