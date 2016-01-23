package group34.ezdatasender;

import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.BitmapFactory;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.provider.Settings.Secure;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.widget.AdapterView.OnItemClickListener;
import android.text.format.DateFormat;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TabHost;
import android.widget.TabHost.TabSpec;
import android.widget.TextView;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.io.OutputStream;
import java.net.InetAddress;
import java.net.Socket;
import java.util.Date;

public class MainActivity extends AppCompatActivity implements View.OnClickListener{
    TabHost tabHost;
    Button bLogout;
    UserLocalStore userLocalStore;

    // Data encapsulation class
    private static DataEncapsulator dataEncapsulator = null;	// Network socket
    Socket socket;

    // Information
    private DataTracker dataTracker;
    private Button bSendGpsButton;
    private Button bGetPhotoButton;
    private Button bUploadPhotoButton;
    private Button bGetAudioButton;
    private Button bUploadAudioButton;

    // Image
    private static int RESULT_LOAD_IMAGE = 1;
    private static String imagePath = "";

    // Audio
    ListView musicList;
    Cursor musicCursor;
    int musicColumnIndex;
    int count;
    MediaPlayer mMediaPlayer;
    private static String audioPath = "";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        bLogout = (Button)findViewById(R.id.bLogout);
        bSendGpsButton = (Button)findViewById(R.id.bSendLocation);
        bGetPhotoButton = (Button)findViewById(R.id.button2);
        bUploadPhotoButton = (Button)findViewById(R.id.bUploadPhoto);
        bGetAudioButton = (Button)findViewById(R.id.bSelectAudio);
        bUploadAudioButton = (Button)findViewById(R.id.bUploadAudio);

        userLocalStore = new UserLocalStore(this);

        bLogout.setOnClickListener(this);
        bSendGpsButton.setOnClickListener(this);
        bGetPhotoButton.setOnClickListener(this);
        bUploadPhotoButton.setOnClickListener(this);
        bGetAudioButton.setOnClickListener(this);
        bUploadAudioButton.setOnClickListener(this);

        tabHost = (TabHost)findViewById(R.id.tabHost);
        tabHost.setup();

        // Create tabs for tab container.
        TabSpec spec1 = tabHost.newTabSpec("TAB 1");
        spec1.setContent(R.id.tab1);
        spec1.setIndicator("TAB 1");

        TabSpec spec2 = tabHost.newTabSpec("TAB 2");
        spec2.setContent(R.id.tab2);
        spec2.setIndicator("TAB 2");

        TabSpec spec3 = tabHost.newTabSpec("TAB 3");
        spec3.setContent(R.id.tab3);
        spec3.setIndicator("TAB 3");

        // Add tabs to Tab container.
        tabHost.addTab(spec1);
        tabHost.addTab(spec2);
        tabHost.addTab(spec3);
    }

    @Override
    protected void onStart(){
        super.onStart();

        if(authenticate()){
            displayUserDetails();
        }else{
            startActivity(new Intent(MainActivity.this, Login.class));
        }
    }

    private boolean authenticate(){
        return userLocalStore.getUserLoggedIn();
    }

    private void displayUserDetails(){
        User user = userLocalStore.getLoggedInUser();
    }

    @Override
    public void onClick(View v) {
        switch(v.getId()){
            case R.id.bLogout:
                userLocalStore.clearUserData();
                userLocalStore.setUserLoggedIn(false);
                startActivity(new Intent(this, Login.class));
                break;

            case R.id.bSendLocation:
                // Send GPS button is clicked
                dataTracker = new DataTracker(MainActivity.this);
                if(dataTracker.canGetLocation())
                    createGpsDataEncapsulator(dataTracker);
                else
                {
                    dataTracker.showSettingsAlert();
                }
                break;

            case R.id.bUploadAudio:
                // Upload audio.
                if(audioPath.length() == 0)
                    showAlertMessage("No audio", "No audio has been selected!");
                else
                {
                    dataTracker = new DataTracker(MainActivity.this);
                    if(dataTracker.canGetLocation())
                        createFileEncapsulator(dataTracker, "Audio", audioPath);
                    else
                    {
                        dataTracker.showSettingsAlert();
                    }
                }
                break;

            case R.id.bSelectAudio:
                // Get audio data.
                InitialMusicGrid();
                break;

            case R.id.bUploadPhoto:
                // Upload photo data;
                dataTracker = new DataTracker(MainActivity.this);
                if(dataTracker.canGetLocation())
                    createFileEncapsulator(dataTracker, "Image", imagePath);
                else
                {
                    dataTracker.showSettingsAlert();
                }
                break;

            case R.id.button2:
                Intent i = new Intent(
                        Intent.ACTION_PICK,
                        android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                startActivityForResult(i, RESULT_LOAD_IMAGE);
                break;
        }
    }

    // Encapsulating retrieved data for later sending to server.
    private void createGpsDataEncapsulator(DataTracker dataTracker)
    {
        String deviceId = Secure.getString(getBaseContext().getContentResolver(), Secure.ANDROID_ID);
        double latitude = dataTracker.getLatitude();
        double longitude = dataTracker.getLongitude();
        String currentDateTime = (String) DateFormat.format("MM-dd-yyyy hh:mm:ss", new Date());
        dataEncapsulator = new DataEncapsulator("", deviceId, latitude, longitude, currentDateTime, "", null, userLocalStore.getLoggedInUser().username);
        new Thread(new ObjectSocket()).start();
    }

    private void createFileEncapsulator(DataTracker dataTracker, String type, String path)
    {
        String deviceId = Secure.getString(getBaseContext().getContentResolver(), Secure.ANDROID_ID);
        double latitude = gpsTracker.getLatitude();
        double longitude = gpsTracker.getLongitude();
        String currentDateTime = (String) DateFormat.format("MM-dd-yyyy hh:mm:ss", new Date());
        String fileName = GetFileNameWithExtension(path);
        byte[] file = ConvertFileToByteArray(path);
        dataEncapsulator = new DataEncapsulator(type, deviceId, latitude, longitude, currentDateTime, fileName, file, userLocalStore.getLoggedInUser().username);
        new Thread(new ObjectSocket()).start();
    }

    private void showAlertMessage(String title, String message)
    {
        AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
        builder.setMessage(message);
        builder.setTitle(title);
        builder.setPositiveButton("OK", null);
        builder.setCancelable(true);
        AlertDialog alert = builder.create();
        alert.show();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data)
    {
        super.onActivityResult(requestCode, resultCode, data);

        System.out.println("Running onActivityResult.");
        if (requestCode == RESULT_LOAD_IMAGE && resultCode == RESULT_OK && null != data) {
            Uri selectedImage = data.getData();
            String[] filePathColumn = { MediaStore.Images.Media.DATA };

            Cursor cursor = getContentResolver().query(selectedImage,
                    filePathColumn, null, null, null);
            cursor.moveToFirst();

            int columnIndex = cursor.getColumnIndex(filePathColumn[0]);
            imagePath = cursor.getString(columnIndex);
            cursor.close();

            ImageView imageView = (ImageView) findViewById(R.id.imgView);
            imageView.setImageBitmap(BitmapFactory.decodeFile(imagePath));
        }
    }

    private void InitialMusicGrid()
    {
        System.out.println("Running InitialMusicGrid.");
        System.gc();

        String[] proj =
                {
                        MediaStore.Audio.Media._ID,
                        MediaStore.Audio.Media.DATA,
                        MediaStore.Audio.Media.DISPLAY_NAME,
                        MediaStore.Video.Media.SIZE
                };

        musicCursor = managedQuery(MediaStore.Audio.Media.EXTERNAL_CONTENT_URI, proj, null, null, null);
        count = musicCursor.getCount();
        musicList = (ListView) findViewById(R.id.MusicList);
        musicList.setAdapter(new MusicAdapter(getApplicationContext()));

        musicList.setOnItemClickListener(musicgridlistener);
        mMediaPlayer = new MediaPlayer();
    }

    private OnItemClickListener musicgridlistener = new OnItemClickListener()
    {
        public void onItemClick(AdapterView parent, View v, int position, long id)
        {
            System.gc();
            musicColumnIndex = musicCursor.getColumnIndexOrThrow(MediaStore.Audio.Media.DATA);
            musicCursor.moveToPosition(position);
            String fileName = musicCursor.getString(musicColumnIndex);
            audioPath = fileName;

            try
            {
                if (mMediaPlayer.isPlaying())
                {
                    mMediaPlayer.reset();
                }

                mMediaPlayer.setDataSource(fileName);
                mMediaPlayer.prepare();
                mMediaPlayer.start();
            }
            catch (Exception e)
            {
                e.printStackTrace();
            }
        }
    };

    public class MusicAdapter extends BaseAdapter
    {
        private Context mContext;

        public MusicAdapter(Context c)
        {
            mContext = c;
        }

        public int getCount()
        {
            return count;
        }

        public Object getItem(int position)
        {
            return position;
        }

        public long getItemId(int position)
        {
            return position;
        }

        public View getView(int position, View convertView, ViewGroup parent)
        {
            System.gc();
            TextView tv = new TextView(mContext.getApplicationContext());
            String id = null;
            if (convertView == null)
            {
                musicColumnIndex = musicCursor.getColumnIndexOrThrow(MediaStore.Audio.Media.DISPLAY_NAME);
                musicCursor.moveToPosition(position);
                id = musicCursor.getString(musicColumnIndex);
                musicColumnIndex = musicCursor.getColumnIndexOrThrow(MediaStore.Audio.Media.SIZE);
                musicCursor.moveToPosition(position);
                id += " Size(KB):" + musicCursor.getString(musicColumnIndex);
                tv.setText(id);
            }
            else
                tv = (TextView) convertView;

            return tv;
        }
    }

    private BufferedInputStream bufferedInputStream;
    private byte[] ConvertFileToByteArray(String path)
    {
        try
        {
            File file = new File(path);
            byte[] myByteArray = new byte[(int)file.length()];
            FileInputStream fileInputStream;
            fileInputStream = new FileInputStream(file);
            bufferedInputStream = new BufferedInputStream(fileInputStream);
            bufferedInputStream.read(myByteArray, 0, myByteArray.length);

            return myByteArray;
        }
        catch (FileNotFoundException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        return null;
    }

    private void UploadFile(DataEncapsulator dataEncapsulator)
    {
        try
        {
            InetAddress inetAddress = InetAddress.getByName("10.143.74.69");
            Socket s = new Socket(inetAddress, 1369);
            OutputStream os = s.getOutputStream();
            ObjectOutputStream oos = new ObjectOutputStream(os);
            oos.writeObject(dataEncapsulator);

            oos.close();
            os.close();
            s.close(); 			}
        catch(IOException e)
        {
            e.printStackTrace();
        }
    }

    private String GetFileNameWithExtension(String path)
    {
        String[] splitString = path.split("/");

        return splitString[splitString.length - 1];
    }

    class ObjectSocket implements Runnable
    {
        @Override
        public void run() {
            // TODO Auto-generated method stub
            try
            {
                UploadFile(dataEncapsulator);
            }
            catch(Exception e)
            {
                e.printStackTrace();
            }
        }
    }
}
