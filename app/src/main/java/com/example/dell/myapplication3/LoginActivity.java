package com.example.dell.myapplication3;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;

/**
 * Created by dell on 3/27/17.
 */

public class LoginActivity extends AppCompatActivity {

    private EditText editText_EmailId,editText_Password;
    private Button btn_LogIn,btn_ForRegister;
    private ProgressBar progressBar;

    private FirebaseAuth mAuth;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        //Initialize Views
        editText_EmailId = (EditText) findViewById(R.id.email_1);
        editText_Password = (EditText) findViewById(R.id.password_1);

        btn_LogIn = (Button) findViewById(R.id.btnLogin);
        btn_ForRegister = (Button) findViewById(R.id.btnLinkToRegisterScreen);

        progressBar = (ProgressBar) findViewById(R.id.progressBar3);

        //Get firebase Auth instance
        mAuth = FirebaseAuth.getInstance();

        if (mAuth.getCurrentUser() != null) {
            startActivity(new Intent(LoginActivity.this, MainActivity.class));
            finish();
        }

        btn_ForRegister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(LoginActivity.this,RegistrationActivity.class));
                finish();
            }
        });

        btn_LogIn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final String inputEmail,inputPassword;
                inputEmail = editText_EmailId.getText().toString().trim();
                inputPassword = editText_Password.getText().toString().trim();

                progressBar.setVisibility(View.VISIBLE);

                try{
                    //Auntheticate user
                    mAuth.signInWithEmailAndPassword(inputEmail,inputPassword).addOnCompleteListener(LoginActivity.this, new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {

                            // If sign in fails, display a message to the user. If sign in succeeds
                            // the auth state listener will be notified and logic to handle the
                            // signed in user can be handled in the listener.
                            progressBar.setVisibility(View.GONE);
                            if (!task.isSuccessful()) {
                                // there was an error
                                if (inputPassword.length() < 6 || inputPassword.isEmpty()) {
                                    editText_Password.setError("Password is incorrect");
                                }
                                else if(inputEmail.isEmpty()){
                                    editText_EmailId.setError("Please Enter Email id");
                                }
                                else {
                                    Toast.makeText(LoginActivity.this,"LogIn is Failed", Toast.LENGTH_LONG).show();
                                }
                            } else {
                                Intent intent = new Intent(LoginActivity.this, MainActivity.class);
                                startActivity(intent);
                                finish();
                            }

                        }
                    });
                }
                catch(Exception e){
                    editText_EmailId.setError("Please enter valid EmailId");
                    editText_Password.setError("Please Enter correct password");
                    progressBar.setVisibility(View.GONE);
                }


            }
        });

    }
}