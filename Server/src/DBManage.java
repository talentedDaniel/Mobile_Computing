import java.awt.List;
import java.math.BigInteger;
import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Random;

import group34.ezdatasender.DataEncapsulator;

public class DBManage {
	
		public enum Type{
			All, Location, Photo, Audio
		}
	   // JDBC driver name and database URL
	   static final String JDBC_DRIVER = "com.mysql.jdbc.Driver";  
	   static final String DB_URL = "jdbc:mysql://localhost/students";
	   
	   //  Database credentials
	   static final String USER = "root";
	   static final String PASS = "12345";
	   
	   static ConnectionPool cp = null;
	   static java.sql.PreparedStatement st;
	   static ResultSet rs;
	   // Constructor
	   public DBManage()
	   {   
		   try
		   {
			   Class.forName(JDBC_DRIVER).newInstance();
			   cp = new ConnectionPool(DB_URL, USER, PASS);
		   }
		   catch(Exception e)
		   {
			   e.printStackTrace();
		   }
	   }
	   
	   public void CreateDatabase() throws SQLException, Exception
	   {
		   Class.forName(JDBC_DRIVER);
		   Connection conn = DriverManager.getConnection("jdbc:mysql://localhost/", USER, PASS);
		   Statement stmt = null;
		   
		   try
		   {
			   st = conn.prepareStatement("show databases");
			   rs=st.executeQuery();
			   while(rs.next())
			   {
				   String dbName = rs.getString(1);
				   // If STUDENTS database already exists, skip this method.
				   if(dbName.equals("students"))
				   {
					   conn.close();
					   return;
				   }
			   }
			   
			   // STUDENTS database not exists, create.
			   stmt = conn.createStatement();
			   String sql = "CREATE DATABASE students";
			   stmt.executeUpdate(sql);
		   }
		   finally
		   {
			   if(stmt != null)
				   stmt.close();
			   
			   conn.close();
		   }
	   }
	   
	   public void CreateTable() throws SQLException, Exception
	   {
		   Class.forName(JDBC_DRIVER);
		   Connection conn = cp.checkout();
		   Statement stmt = null;
		   
		   try
		   {
			   DatabaseMetaData md = conn.getMetaData();
			   ResultSet rs = md.getTables(null, null, "sensorsdata", null);
			   if(rs.next())
			   {
				   // Table SENSORSDATA already exists in STUDENT database.
				   System.out.println("Table SENSORSDATA already exists.");
			   }
			   else
			   {
				   // Table SENSORDATA does not exist in STUDENT database, create database.
				   System.out.println("Creating table in given database...");
				   stmt = conn.createStatement();
		      
				   String sql = "CREATE TABLE sensorsdata " +
			                    "(DataRowIndex bigint(20) not NULL, " +
						   		" Username VARCHAR(255) not NULL, " +
			                    " DeviceId VARCHAR(255), " +
			                    " Latitude FLOAT, " + 
			                    " Longitude FLOAT, " + 
			                    " DateTime VARCHAR(255), " +
			                    " Accelerator VARCHAR(255), " +
			                    " Gyroscope VARCHAR(255), " +
			                    " Path VARCHAR(255), " +
			                    " Type VARCHAR(255), " +
			                    " PRIMARY KEY ( DataRowIndex ))";

				   stmt.executeUpdate(sql);
				   
				   if(stmt != null)
					   stmt.close();
			   }
			   
			   cp.checkin(conn);
		   }
		   catch(Exception e)
		   {
			   e.printStackTrace();
		   }
	   }
	   
	   public static void InsertValues(String username, String deviceId, double latitude, double longitude, String dateTime, String accelerator, String gyroscope, String path, String type) throws SQLException
	   {
		   {
			   Connection connection = cp.checkout();
			   Statement statement = null;
			   
			   try
			   {
				   Class.forName(JDBC_DRIVER);
				   
				   System.out.println("Insert row in given database...");
				   statement = connection.createStatement();
				   BigInteger index = new BigInteger(20, new Random());
				   
				   String sql = "insert into sensorsdata values (" + index + ", '" +username +"', '" + deviceId +"', "+ latitude +", "+ longitude +", '"+ dateTime +"', '"+ accelerator +"', '"+ gyroscope +"', '"+ path +"', '" + type +"');";
				   
				   statement.executeUpdate(sql);
				   
				   cp.checkin(connection);
			   }
			   catch(SQLException se)
			   {
				   //Handle errors for JDBC
				   se.printStackTrace();
			   }
			   catch(Exception e)
			   {
				   //Handle errors for Class.forName
				   e.printStackTrace();
			   }
			   finally
			   {
				   //finally block used to close resources
				   try
				   {
					   if(statement != null)
						   statement.close();
				   }
				   catch(SQLException se)
				   {
					   se.printStackTrace();
				   }
			   	}
		   }
	   }
	   
	   public static List RetrieveValues(String username, Type type) throws SQLException
	   {
		   {
			   List results = new List();
			   Connection connection = cp.checkout();
			   Statement statement = null;
			   
			   try
			   {
				   Class.forName(JDBC_DRIVER);
				   
				   System.out.println("Retrieve data from database...");
				   statement = connection.createStatement();
				   
				   String sql = "";
				   
				   switch(type){
				   case All:
					   sql = "select * from sensorsdata where username = '" + username + "';";
					   break;
					   
				   case Location:
					   sql = "select * from sensorsdata where  username = '" + username + "' and type = 'Location';";
					   break;
					   
				   case Photo:
					   sql = "select * from sensorsdata where  username = '" + username + "' and type = 'Photo';";
					   break;
					   
				   case Audio:
					   sql = "select * from sensorsdata where  username = '" + username + "' and type = 'Audio';";
					   break;
				   }
				   
				   rs = statement.executeQuery(sql);
				   
				   while(rs.next())
				   {
					   // format: xxx#xxx#xxx#xxx#xxx...
					   String item = rs.getString("Username") + "#" + rs.getString("DeviceId") + "#" + rs.getLong("Latitude") + "#"
							   + rs.getLong("Longitude") + "#" + rs.getString("DateTime") + "#" + rs.getString("Path") + "#" + 
							   rs.getString("Type");
					   results.add(item);
				   }
				   
				   cp.checkin(connection);
				   return results;
			   }
			   catch(SQLException se)
			   {
				   //Handle errors for JDBC
				   se.printStackTrace();
			   }
			   catch(Exception e)
			   {
				   //Handle errors for Class.forName
				   e.printStackTrace();
			   }
			   finally
			   {
				   //finally block used to close resources
				   try
				   {
					   if(statement != null)
						   statement.close();
				   }
				   catch(SQLException se)
				   {
					   se.printStackTrace();
				   }
			   	}
		   }
		return null;
	   }
}
