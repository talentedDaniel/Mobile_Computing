package group34.ezdatasender;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

public class changePassword extends AppCompatActivity implements View.OnClickListener{

    Button bUpdate, bBackToLogin;
    EditText etUsername, etOldPassword, etNewPassword, etVerify;

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.bUpdate:
                String username = etUsername.getText().toString();
                String newPassword = etNewPassword.getText().toString();
                String verify = etVerify.getText().toString();

                if(!newPassword.equals(verify)){
                    showMessage("New password and verify does not match.");
                    return;
                }
                User user = new User(username, newPassword);

                updatePassword(user);
                break;

            case R.id.bBackToLogin:
                startActivity(new Intent(this, Login.class));
                break;
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_change_password);
        bUpdate = (Button)findViewById(R.id.bUpdate);
        bBackToLogin = (Button)findViewById(R.id.bBackToLogin);

        etUsername = (EditText)findViewById(R.id.etUsername);
        etOldPassword = (EditText)findViewById(R.id.etOldPassword);
        etNewPassword = (EditText)findViewById(R.id.etNewPassword);
        etVerify = (EditText)findViewById(R.id.etVerify);

        bUpdate.setOnClickListener(this);
        bUpdate.setOnClickListener(this);
    }

    private void updatePassword(User user){
        ServerRequests serverRequests = new ServerRequests(this);
        serverRequests.updateUserDataInBackground(user, new GetUserCallback() {
            @Override
            public void done(User returnedUser) {
                showMessage("Congraduations! Password has been reset successfully.");
                startActivity(new Intent(changePassword.this, Login.class));
            }
        });
    }

    private void showMessage(String message){
        AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(changePassword.this);
        dialogBuilder.setMessage(message);
        dialogBuilder.setPositiveButton("Ok", null);
        dialogBuilder.show();
    }
}
