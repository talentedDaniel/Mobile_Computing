package group34.ezdatareceiver;

import android.app.AlertDialog;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

import group34.ezdatasender.R;

public class DisplayImageActivity extends AppCompatActivity implements View.OnClickListener{
    public static String TAG = "deb";
    TextView tab1, tab2, tab3;
    UserLocalStore userInfo;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.i(TAG, "I1");
        setContentView(R.layout.activity_display_image);
        Log.i(TAG, "I12");
        tab1 = (TextView) findViewById(R.id.textAddr1);
        tab2 = (TextView) findViewById(R.id.textAddr2);
        tab3 = (TextView) findViewById(R.id.textAddr3);
        Log.i(TAG, "I14");
        String[] forOne ={"Demo1.jpg","Demo3.jpg"};
        String[] forTwo ={"Demo2.jpg"};
        String[] forThree ={"temp.jpg"};
        Log.i(TAG, "I17");
        userInfo = new UserLocalStore(this);
        String user = userInfo.getLoggedInUser().username;
        Log.i(TAG, "I15");
        switch (user){
            case "abc1":
                tab1.setText(forOne[0]);
                tab2.setText(forOne[1]);
                break;
            case "abc2":
                tab1.setText(forTwo[0]);
                break;
            default:
                tab1.setText(forThree[0]);
                break;
        }
//        ArrayAdapter<String> adapter = new ArrayAdapter<String>(getListView().getContext(),android.R.layout.simple_list_item_1,items);
//        getListView().setAdapter(adapter);
        Log.i(TAG, "I16");
        tab1.setOnClickListener(this);
        tab2.setOnClickListener(this);
        tab3.setOnClickListener(this);

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_display_image, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.textAddr1:
            case R.id.textAddr2:
            case R.id.textAddr3:
                showAlert(v);
                break;
//            case R.id.btnImgBack:
//                startActivity(new Intent(this, MainActivity.class));
//                break;
        }
    }

    public void showAlert(View v){
        AlertDialog.Builder error_hint = new AlertDialog.Builder(this);
        error_hint.setMessage("Sorry. File cannot be downloaded currently!").create();
        error_hint.show();
    }

    protected void listFile(String user, TextView tab1, TextView tab2, TextView tab3 ){
        String[] forOne ={"Demo1.jpg","Demo3.jpg"};
        String[] forTwo ={"Demo2.jpg"};
        String[] forThree ={"temp.jpg"};

        switch (user){
            case "abc1":
                tab1.setText(forOne[0]);
                tab2.setText(forOne[1]);
                break;
            case "abc2":
                tab1.setText(forTwo[0]);
                break;
            default:
                tab1.setText(forThree[0]);
                break;
        }
    }
}
