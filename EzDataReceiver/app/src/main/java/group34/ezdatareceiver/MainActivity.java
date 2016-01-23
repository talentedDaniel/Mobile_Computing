package group34.ezdatareceiver;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TabHost;
import android.widget.TextView;

import group34.ezdatasender.R;

public class MainActivity extends AppCompatActivity implements View.OnClickListener{

    public static String TAG = "deb";

    TabHost tabHost;
    Button bLogout;
    Button bImg;
    Button bVid;
    TextView textName, textStatus;
    UserLocalStore userLocalStore;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        bLogout = (Button)findViewById(R.id.bLogout);
        bLogout.setOnClickListener(this);
        userLocalStore = new UserLocalStore(this);
        bImg = (Button)findViewById(R.id.bImg);
        bImg.setOnClickListener(this);
        bVid = (Button)findViewById(R.id.bVid);
        textName = (TextView) findViewById(R.id.textName);
        textName.setText(userLocalStore.getLoggedInUser().name);
        textStatus = (TextView) findViewById(R.id.textStatus);
        textStatus.setText("Attention! Abnormal sign-in location detected!");
        textStatus.setTextColor(Color.rgb(255,69,0));
        //bVid.setOnClickListener(this);
        //tabHost = (TabHost)findViewById(R.id.tabHost);

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
            case R.id.bImg:
                startActivity(new Intent(this, DisplayImageActivity.class));
                break;
            case R.id.bVid:
                startActivity(new Intent(this, DisplayAudioActivity.class));
                break;
        }
    }
}
