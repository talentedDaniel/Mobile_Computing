import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Vector;


public class ConnectionPool implements Runnable{
	private int initialConnectionCount = 20;
	private Vector<Connection> availableConnections = new Vector<Connection>();
	private Vector<Connection> usedConnections = new Vector<Connection>();
	private String urlString = "";
	private String userName = "";
	private String password = "";
	private Thread cleanupThread = null;
	
	// Constructor
	public ConnectionPool(String url, String user, String passwd) throws SQLException
	{
		urlString = url;
		userName = user;
		password = passwd;
		
		for(int cnt=0; cnt<initialConnectionCount; cnt++)
		{
			availableConnections.addElement(getConnection());
		}
		
		cleanupThread = new Thread(this);
		cleanupThread.start();
	}
	
	private Connection getConnection() throws SQLException
	{
		return DriverManager.getConnection(urlString, userName, password);
	}
	
	public synchronized Connection checkout() throws SQLException
	{
		Connection newConnxn = null;
		if(availableConnections.size() == 0)
		{
			newConnxn = getConnection();
			usedConnections.addElement(newConnxn);
		}
		else
		{
			newConnxn = (Connection)availableConnections.lastElement();
			availableConnections.removeElement(newConnxn);
			usedConnections.addElement(newConnxn);
		}
		
		return newConnxn;
	}
	
	public synchronized void checkin(Connection c)
	{
		if(c != null)
		{
			usedConnections.removeElement(c);
			availableConnections.addElement(c);
		}
	}
	
	public int availableCount()
	{
		return availableConnections.size();
	}

	// If there is not enough connection in the pool, server will create more connections temporary.
	// Periodically, thread in server will cleanup the later created connections.
	@Override
	public void run() 
	{
		// TODO Auto-generated method stub
		try
		{
			while(true)
			{
				synchronized(this)
				{
					while(availableConnections.size() > initialConnectionCount)
					{
						Connection c = (Connection)availableConnections.lastElement();
						availableConnections.removeElement(c);
						
						c.close();
					}
				}
				System.out.println("Cleanup: Available Connections:" + availableCount());
				
				Thread.sleep(60*1000);
			}
		}
		catch(SQLException sqle)
		{
			sqle.printStackTrace();
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
	}
}
