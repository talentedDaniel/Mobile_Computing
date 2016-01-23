package group34.ezdatasender;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Environment;
import android.provider.MediaStore;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Date;

@SuppressLint("SimpleDateFormat")
public class ImageTracker {
	
	public final static int REQUEST_CAMERA = 1;
    public final static int REQUEST_OTHER = 2;
    private Uri cameraImageUri;

    public void TakeFromCamera(Activity activity, String title)
    {
        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        File cameraImageOutputFile = new File(
                Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES),
                CreateCameraImageFileName());
        cameraImageUri = Uri.fromFile(cameraImageOutputFile);
        intent.putExtra(MediaStore.EXTRA_OUTPUT, cameraImageUri);
        activity.startActivityForResult(Intent.createChooser(intent, title), REQUEST_CAMERA);
    }
    
    public void TakeFromOther(Activity activity, String title)
    {
        Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
        intent.setType("image/*");
        activity.startActivityForResult(Intent.createChooser(intent, title), REQUEST_OTHER);
    }

    public Uri RetrievePicture(Activity activity, int requestCode, int resultCode, Intent data)
    {
        Uri result = null;

        if (resultCode == Activity.RESULT_OK) 
        {
            if (requestCode == REQUEST_OTHER) 
            {
                result = data.getData();
            }
            else if (requestCode == REQUEST_CAMERA) 
            {
                result = cameraImageUri;
            }
        }
        return result;
    }

    private String CreateCameraImageFileName() 
    {
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        return timeStamp + ".jpg";
    }
}
