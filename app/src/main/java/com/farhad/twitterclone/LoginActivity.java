package com.farhad.twitterclone;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.parse.LogInCallback;
import com.parse.ParseException;
import com.parse.ParseUser;
import com.shashank.sony.fancytoastlib.FancyToast;

public class LoginActivity extends AppCompatActivity implements View.OnClickListener{

    private EditText edtLoginEmail, edtLoginPassword;
    private Button btnLoginActivity, btnSignUpActivity;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        setTitle("Log in");

        edtLoginEmail = findViewById(R.id.edtEmailLogin);
        edtLoginPassword = findViewById(R.id.edtPasswordLogin);
        edtLoginPassword.setOnKeyListener(new View.OnKeyListener() {
            @Override
            public boolean onKey(View v, int keyCode, KeyEvent event) {
                if(keyCode == KeyEvent.KEYCODE_ENTER && event.getAction() == KeyEvent.ACTION_DOWN){
                    onClick(btnLoginActivity);
                }
                return false;
            }
        });

        btnSignUpActivity = findViewById(R.id.btnSignUpLoginActivity);
        btnLoginActivity = findViewById(R.id.btnLoginActivity);

        btnSignUpActivity.setOnClickListener(this);
        btnLoginActivity.setOnClickListener(this);

        if(ParseUser.getCurrentUser() != null){
            transitionSocialMediaActivity();
//            ParseUser.logOut();
        }

    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.btnLoginActivity:
                if(edtLoginEmail.getText().toString().equals("") ||
                        edtLoginPassword.getText().toString().equals("")){
                    FancyToast.makeText(this, "Email, Password is required!", Toast.LENGTH_SHORT, FancyToast.ERROR, false ).show();

                } else {
                    final ProgressDialog progressDialog = new ProgressDialog(this);
                    progressDialog.setMessage(edtLoginEmail.getText().toString() + " is signing up");
                    progressDialog.show();
                    ParseUser.logInInBackground(edtLoginEmail.getText().toString(), edtLoginPassword.getText().toString(),
                            new LogInCallback() {
                                @Override
                                public void done(ParseUser user, ParseException e) {
                                    if (user != null && e == null) {
                                        FancyToast.makeText(LoginActivity.this, user.getUsername() + " is logged in", Toast.LENGTH_SHORT, FancyToast.SUCCESS, false).show();
                                        transitionSocialMediaActivity();
                                    }
                                    progressDialog.dismiss();
                                }
                            });
                }
                break;
            case R.id.btnSignUpLoginActivity:
                Intent intent = new Intent(this, SignUp.class);
                startActivity(intent);
                break;
        }
    }
    private void transitionSocialMediaActivity(){
        Intent intent = new Intent(this, TwitterUsers.class);
        startActivity(intent);
        finish();
    }
}
