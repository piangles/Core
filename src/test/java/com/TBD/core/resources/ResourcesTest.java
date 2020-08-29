package com.TBD.core.resources;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

import com.TBD.core.util.central.CentralConfigProvider;

public class ResourcesTest {

	public static void main(String[] args)
	{
		try {
			RDBMSDataStore dataStore = ResourceManager.getInstance().getRDBMSDataStore(new CentralConfigProvider("14fe64ea-d15a-4c8b-af2f-f2c7efe1943b"));
			Statement stm = dataStore.getConnection().createStatement();
			ResultSet rs = stm.executeQuery("select * from BackboneTier2.Config where ComponentId='14fe64ea-d15a-4c8b-af2f-f2c7efe1943b'");
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
