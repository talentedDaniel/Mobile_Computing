import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.sql.SQLException;

import group34.ezdatasender.DataEncapsulator;

public class Server {
	
	private static final String imagePath = "I:\\Image\\";
	private static final String audioPath = "I:\\Audio\\";
	
	private static String imageName = "";
	private static String audioName = "";
	
	
	public static void main(String[] args) throws IOException 
	{
		DBManage dbManage = new DBManage();
		try 
		{
			dbManage.CreateDatabase();
			dbManage.CreateTable();
		}
		catch (SQLException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
		
		new Thread() {
		    public void run() 
		    {
		        try 
		        {
					ObjectListener();
				} 
		        catch (IOException | ClassNotFoundException e) 
		        {
					// TODO Auto-generated catch block
					e.printStackTrace();
				} 
		        catch (SQLException e) 
		        {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
		    }
	    }.start();
	}
	
	private static void WriteByteArrayToFile(byte[] fileStream, String fileName)
	{
		try
		{
			FileOutputStream fos = new FileOutputStream(fileName);
			fos.write(fileStream);
			fos.flush();
			fos.close();
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
	}
	
	private static void ObjectListener() throws IOException, ClassNotFoundException, SQLException
	{
        System.out.println("Waiting...");
        
        ServerSocket serverSocket = new ServerSocket(1369);
        
        while(true)
    	{
        	Socket socket = serverSocket.accept();

    		ObjectInputStream ois = new ObjectInputStream(socket.getInputStream());
    		DataEncapsulator dataEncapsulator = (DataEncapsulator)ois.readObject();
    		
    		try
    		{
        		if(dataEncapsulator.getType().equals("Image"))
        		{
        			imageName = imagePath + dataEncapsulator.getUsername() + "\\" + dataEncapsulator.getFileName();
            		DBManage.InsertValues(
            				dataEncapsulator.getUsername(),
            				dataEncapsulator.getDeviceId(), 
            				dataEncapsulator.getLatitude(), 
            				dataEncapsulator.getLongitude(), 
            				dataEncapsulator.getDateTime(), 
            				dataEncapsulator.getAccelerator(), 
            				dataEncapsulator.getGyroscope(), 
            				imageName,
            				"Image");
            		
            		WriteByteArrayToFile(dataEncapsulator.getFile(), imageName);
        		}
        		else if(dataEncapsulator.getType().equals("Audio"))
        		{
        			audioName = audioPath + dataEncapsulator.getUsername() + "\\" + dataEncapsulator.getFileName();
        			
            		DBManage.InsertValues(
            				dataEncapsulator.getUsername(),
            				dataEncapsulator.getDeviceId(), 
            				dataEncapsulator.getLatitude(), 
            				dataEncapsulator.getLongitude(), 
            				dataEncapsulator.getDateTime(), 
            				dataEncapsulator.getAccelerator(), 
            				dataEncapsulator.getGyroscope(), 
            				audioName,
            				"Audio");
            		
            		WriteByteArrayToFile(dataEncapsulator.getFile(), audioName);
        		}
        		else
        		{
            		DBManage.InsertValues(
            				dataEncapsulator.getUsername(),
            				dataEncapsulator.getDeviceId(), 
            				dataEncapsulator.getLatitude(), 
            				dataEncapsulator.getLongitude(), 
            				dataEncapsulator.getDateTime(), 
            				dataEncapsulator.getAccelerator(), 
            				dataEncapsulator.getGyroscope(), 
            				dataEncapsulator.getFileName(),
            				"Location");
        		}
    			ois.close();
    		}
    		catch(Exception e)
    		{   
    			socket.close();
    			serverSocket.close();
    			e.printStackTrace();
    		}
    	}
	}
}
