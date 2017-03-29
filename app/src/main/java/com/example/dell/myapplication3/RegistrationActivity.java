package com.example.dell.myapplication3;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.MainThread;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
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

public class RegistrationActivity extends AppCompatActivity {

    private EditText editText_FullName,editText_EmailId,editText_Password;
    private Button btn_Register,btn_AlreadyRegister;
    private ProgressBar progressBar;

    private FirebaseAuth mAuth;
    private ChatMessage chatMessage;



    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_registration);

        //Initialize View Instance

        editText_FullName = (EditText) findViewById(R.id.name);
        editText_EmailId = (EditText) findViewById(R.id.email);
        editText_Password = (EditText) findViewById(R.id.password);

        btn_Register = (Button) findViewById(R.id.btnRegister);
        btn_AlreadyRegister = (Button) findViewById(R.id.btnLinkToLoginScreen);

        progressBar = (ProgressBar) findViewById(R.id.progressBar2);

        //Get Firebase Auth instance
        mAuth = FirebaseAuth.getInstance();

        btn_Register.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                final String name,email,password;
                name = editText_FullName.getText().toString().trim();
                email = editText_EmailId.getText().toString().trim();
                password = editText_Password.getText().toString().trim();

                if(TextUtils.isEmpty(name)){
                    Toast.makeText(getApplicationContext(),"Enter Name",Toast.LENGTH_LONG).show();
                    editText_FullName.setError("Enter name");
                    return;
                }
                if(TextUtils.isEmpty(email)){
                    Toast.makeText(getApplicationContext(),"Enter EmailId",Toast.LENGTH_LONG).show();
                    editText_EmailId.setError("Enter EmailId");
                    editText_Password.setError("Password empty");
                    return;
                }
                if (TextUtils.isEmpty(password)) {
                    Toast.makeText(getApplicationContext(), "Enter password!", Toast.LENGTH_SHORT).show();
                    return;
                }
                if(password.length()<6){
                    Toast.makeText(getApplicationContext(),"Password is too short",Toast.LENGTH_LONG).show();
                    editText_Password.setError("Password is too short");
                    return;
                }

                progressBar.setVisibility(View.VISIBLE);

                //Create user

                Intent intent = new Intent(RegistrationActivity.this, MainActivity.class);
                intent.putExtra("user_name",name);

                Toast.makeText(getApplication(),name,Toast.LENGTH_LONG).show();



                mAuth.createUserWithEmailAndPassword(email,password).addOnCompleteListener(RegistrationActivity.this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {

                        Toast.makeText(RegistrationActivity.this, "createUserWithEmail:onComplete:" + task.isSuccessful(), Toast.LENGTH_SHORT).show();
                        progressBar.setVisibility(View.GONE);

                        // If sign in fails, display a message to the user. If sign in succeeds
                        // the auth state listener will be notified and logic to handle the
                        // signed in user can be handled in the listener.
                        if (!task.isSuccessful()) {
                            Toast.makeText(RegistrationActivity.this, "Authentication failed." + task.getException(),
                                    Toast.LENGTH_SHORT).show();
                        } else {
                            startActivity(new Intent(RegistrationActivity.this, MainActivity.class));
                            finish();
                        }
                    }
                });
            }
        });

        btn_AlreadyRegister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(RegistrationActivity.this,LoginActivity.class));
                finish();
            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        progressBar.setVisibility(View.GONE);
    }
}
