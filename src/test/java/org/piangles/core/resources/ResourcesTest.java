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
