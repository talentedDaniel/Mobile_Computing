package group34.ezdatasender;

import java.io.DataOutputStream;
import java.io.FileInputStream;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;

public class DataSender {
	private static String serverUrl = "http://localhost:8080"; // url of Server
	
	private String GetFileName(String filePath)
	{
		String fileName = "";
		//String[] splitStrings = filePath.split("\\");
		String splitString = filePath.replace("\\", "/");
		String[] splitStrings = splitString.split("/");

		for(int i=0; i<splitStrings.length; i++)
		{
			fileName = splitStrings[splitStrings.length - 1];
		}
		return fileName;
	}
	
	// Send file(image, audio, txt etc.) to server
	private void UploadFile(String path)
    {	
		String end = "\r\n";
	    String twoHyphens = "--";
	    String boundary = "*****";
	    String fileName = GetFileName(path);
	    
	    try 
	    {
		    URL url = new URL(serverUrl);
		    HttpURLConnection httpURLConnection = (HttpURLConnection) url.openConnection();
		    
		    httpURLConnection.setDoInput(true);
		    httpURLConnection.setDoOutput(true);
		    httpURLConnection.setUseCaches(false);
		    httpURLConnection.setRequestMethod("POST");
		    httpURLConnection.setRequestProperty("Connection", "Keep-Alive");
		    httpURLConnection.setRequestProperty("Accept", "text/*");
		    httpURLConnection.setRequestProperty("Content-Type", "multipart/form-data; boundary=" + boundary);
		    
		    DataOutputStream dataOutputStream = new DataOutputStream(httpURLConnection.getOutputStream());
		    dataOutputStream.writeBytes(twoHyphens + boundary + end);
		    dataOutputStream.writeBytes("Content-Disposition: form-data;" + "name=\"folder\"" + end + end);
		    dataOutputStream.write(path.getBytes("UTF-8"));
		    dataOutputStream.writeBytes(end);
		    dataOutputStream.writeBytes(twoHyphens + boundary + end);
		    dataOutputStream.writeBytes("Content-Disposition: form-data;" + "name=\"Filedata\"; filename=\"");
		    dataOutputStream.write(fileName.getBytes("UTF-8"));
		    dataOutputStream.writeBytes("\"" + end);
		    dataOutputStream.writeBytes(end);
		    
		    FileInputStream fileInputStream = new FileInputStream(path);
		    int bufferSize = 1024;
		    byte[] buffer = new byte[bufferSize];
		    int length = -1;
		    while((length = fileInputStream.read(buffer)) != -1) 
		    {
		    	dataOutputStream.write(buffer, 0, length);
		    }
		    
		    dataOutputStream.writeBytes(end);
		    dataOutputStream.writeBytes(twoHyphens + boundary + twoHyphens + end);
		    fileInputStream.close();
		    dataOutputStream.flush();
		    
		    InputStream inputStream = httpURLConnection.getInputStream();
		    int ch;
		    StringBuffer b = new StringBuffer();
		    
		    while((ch = inputStream.read()) != -1) 
		    {
		    	b.append((char)ch);
		    }
		    
		    dataOutputStream.close();
	    }
	    catch(Exception e) 
	    {
	    	e.printStackTrace();
	    }
    }
}
