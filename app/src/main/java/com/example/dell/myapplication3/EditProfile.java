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
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

/**
 * Created by dell on 4/17/17.
 */

public class EditProfile extends AppCompatActivity {

    private EditText editTextPassword,editTextEmail;
    private Button buttonEditPassword,buttonEditEmail;
    private ProgressBar progressBar;

    private FirebaseAuth firebaseAuth;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.edit_layout);

        editTextPassword = (EditText) findViewById(R.id.editPassword);
        editTextEmail = (EditText) findViewById(R.id.editTextEmail);
        buttonEditPassword = (Button) findViewById(R.id.editPasswordButton);
        buttonEditEmail = (Button) findViewById(R.id.button_email);
        progressBar = (ProgressBar) findViewById(R.id.edit_progressbar);

        progressBar.setVisibility(View.INVISIBLE);

        firebaseAuth = FirebaseAuth.getInstance();

        //get current user
        final FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();

        buttonEditPassword.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String editPass = editTextPassword.getText().toString().trim();
                progressBar.setVisibility(View.VISIBLE);

                if(user != null && !editPass.equals("")){
                    if(editPass.length() < 6){
                        editTextPassword.setError("Password is too Short , Please enter Valid Password");
                        progressBar.setVisibility(View.GONE);
                    }
                    else{
                        user.updatePassword(editPass).addOnCompleteListener(EditProfile.this, new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task) {

                                if(task != null){
                                    Toast.makeText(getApplicationContext(),"Password Is Changed",Toast.LENGTH_LONG).show();
                                    progressBar.setVisibility(View.GONE);
                                    signOUT();
                                }
                                else {
                                    progressBar.setVisibility(View.GONE);
                                    Toast.makeText(getApplicationContext(),"Password Changed unsuccessful",Toast.LENGTH_LONG).show();
                                }
                            }
                        });
                    }
                }
                else if(editPass.equals("")) {
                    progressBar.setVisibility(View.GONE);
                    editTextPassword.setError("Enter Password");
                }
            }
        });

        buttonEditEmail.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                progressBar.setVisibility(View.VISIBLE);
                String editEmail = editTextEmail.getText().toString().trim();
                if(user != null && !editEmail.equals(""))
                {
                    user.updateEmail(editEmail).addOnCompleteListener(EditProfile.this, new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {

                            if(task != null){
                                progressBar.setVisibility(View.GONE);
                                Toast.makeText(getApplicationContext(),"Email is changed",Toast.LENGTH_LONG).show();
                                signOUT();
                            }
                            else{
                                progressBar.setVisibility(View.GONE);
                                Toast.makeText(getApplicationContext(),"Email changing is unsuccessful",Toast.LENGTH_LONG).show();
                            }
                        }
                    });
                }
                else if(editEmail.equals("")){
                    progressBar.setVisibility(View.GONE);
                    editTextEmail.setError("Please enter New Email id");
                }
            }
        });
    }

    private void signOUT() {
        firebaseAuth.signOut();
        finish();
    }
}