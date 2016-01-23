package group34.ezdatasender;
import java.io.Serializable;


public class DataEncapsulator implements Serializable{

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	private String type = "";
	private String deviceId = "";
	private double latitude = 0.0;
	private double longitude = 0.0;
	private String dateTime = "";
	private String fileName = "";
	private String accelerator = "";
	private String gyroscope = "";
	private String username = "";
	private byte[] file;

	public String getUsername(){return username;}

	public void setUsername(String username){ this.username = username; }
	
	public byte[] getFile() {
		return file;
	}

	public void setFile(byte[] file) {
		this.file = file;
	}

	public String getAccelerator() {
		return accelerator;
	}

	public void setAccelerator(String accelerator) {
		this.accelerator = accelerator;
	}

	public String getGyroscope() {
		return gyroscope;
	}

	public String getDeviceId() {
		return deviceId;
	}

	public void setDeviceId(String deviceId) {
		this.deviceId = deviceId;
	}

	public double getLatitude() {
		return latitude;
	}

	public void setLatitude(double latitude) {
		this.latitude = latitude;
	}

	public double getLongitude() {
		return longitude;
	}

	public void setLongitude(double longitude) {
		this.longitude = longitude;
	}

	public String getDateTime() {
		return dateTime;
	}

	public void setDateTime(String dateTime) {
		this.dateTime = dateTime;
	}

	public String getFileName() {
		return fileName;
	}

	public void setFileName(String fileName) {
		this.fileName = fileName;
	}

	public static long getSerialversionuid() {
		return serialVersionUID;
	}

	public void setGyroscope(String gyroscope) {
		this.gyroscope = gyroscope;
	}
	
	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

	public DataEncapsulator(){}
	
	public DataEncapsulator(String type, String deviceId, long latitude, long longitude, String dateTime, String fileName, byte[] file, String username)
	{
		this.type = type;
		this.deviceId = deviceId;
		this.latitude = latitude;
		this.longitude = longitude;
		this.dateTime = dateTime;
		this.fileName = fileName;
		this.file = file;
		this.username = username;
	}
}

