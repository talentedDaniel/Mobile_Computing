package group34.ezdatareceiver;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import group34.ezdatasender.R;

public class Register extends AppCompatActivity implements View.OnClickListener{

    Button bRegister, bBackToLogin;
    EditText etName, etAge, etUsername, etPassword, etVerify;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        etName = (EditText)findViewById(R.id.etName);
        etAge = (EditText)findViewById(R.id.etAge);
        etUsername = (EditText)findViewById(R.id.etUsername);
        etPassword = (EditText)findViewById(R.id.etPassword);
        etVerify = (EditText)findViewById(R.id.etVerify);
        bRegister = (Button)findViewById(R.id.bRegister);
        bBackToLogin = (Button)findViewById(R.id.bBackToLogin);

        bRegister.setOnClickListener(this);
        bBackToLogin.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.bRegister:
                String name = etName.getText().toString();
                String username = etUsername.getText().toString();
                String password = etPassword.getText().toString();
                int age = Integer.parseInt(etAge.getText().toString());
                String verify = etVerify.getText().toString();

                if(!password.equals(verify)){
                    showMessage("Password and verify does not match.");
                    return;
                }

                User user = new User(name, age, username, password);

                registerUser(user);
                break;

            case R.id.bBackToLogin:
                startActivity(new Intent(this, Login.class));
                break;
        }
    }

    private void registerUser(User user){
        ServerRequests serverRequests = new ServerRequests(this);
        serverRequests.storeUserDataInBackground(user, new GetUserCallback() {
            @Override
            public void done(User returnedUser) {
                showMessage("Congraduations! The user has been created successfully.");
                startActivity(new Intent(Register.this, Login.class));
            }
        });
    }

    private void showMessage(String message){
        AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(Register.this);
        dialogBuilder.setMessage(message);
        dialogBuilder.setPositiveButton("Ok", null);
        dialogBuilder.show();
    }
}
