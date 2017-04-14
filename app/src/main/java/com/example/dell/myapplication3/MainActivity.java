package com.example.dell.myapplication3;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.text.TextWatcher;
import android.text.method.CharacterPickerDialog;
import android.view.View;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.google.android.gms.auth.api.Auth;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {


    private static final int RC_PHOTO_PICKER = 2;
    private FirebaseAuth mAuth;
    private FirebaseAuth.AuthStateListener mAuthListener;
    private FirebaseDatabase mDatabase;
    private DatabaseReference mDatabaseReference;
    private ChildEventListener childEventListener;
    private GoogleApiClient mGoogleApiClient;
    private FirebaseStorage firebaseStorage;
    private StorageReference chatPhotostorageReference;

    private EditText editText_message;
    private MessageAdapter messageAdapter;
    private ProgressBar progressBar;
    private ListView listView_message;

    public String userName = "ANONYMOUS";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.setLogo(R.drawable.chat);
        setSupportActionBar(toolbar);

        editText_message = (EditText) findViewById(R.id.editText3);
        progressBar = (ProgressBar) findViewById(R.id.progressBar);
        listView_message = (ListView) findViewById(R.id.messageListView);

        Bundle extras = getIntent().getExtras();
        if(extras !=null) {
           userName = extras.getString("user_name");
        }
        Toast.makeText(getApplicationContext(),userName,Toast.LENGTH_LONG).show();




        final FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        FloatingActionButton floatingActionButton_att = (FloatingActionButton) findViewById(R.id.floatingActionButton2);

        // Initialize message ListView and its adapter
        List<ChatMessage> chatMessages = new ArrayList<>();
        messageAdapter = new MessageAdapter(this, R.layout.item_message, chatMessages);
        listView_message.setAdapter(messageAdapter);

        progressBar.setVisibility(View.INVISIBLE);

        fab.setEnabled(false);
        //For message sending
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                //For Save Messgaes
                ChatMessage chatMessage = new ChatMessage(editText_message.getText().toString().trim(),userName,null);
                mDatabaseReference.push().setValue(chatMessage);
                editText_message.setText("");
            }
        });

        //For message write
        editText_message.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (s.toString().trim().length() > 0) {
                    fab.setEnabled(true);
                } else {
                    fab.setEnabled(false);
                }
            }
            @Override
            public void afterTextChanged(Editable s) {}
        });

        //Authentication code.
        //Get Firebase auth Instance

        mAuth = FirebaseAuth.getInstance();

        mAuthListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                FirebaseUser user = firebaseAuth.getCurrentUser();
                if(user != null){
                    //User signed in
                    //Toast.makeText(getApplicationContext(),"User is signed in",Toast.LENGTH_LONG).show();
                    userSignedInInitialize();
                }
                else{
                    //User Signed Out
                    userSignedOutCleanUp();
                    startActivity(new Intent(MainActivity.this,LoginActivity.class));
                    finish();
                }
            }
        };

        //Databse Code
        //Get Databse instance and reference

        mDatabase = FirebaseDatabase.getInstance();
        mDatabaseReference = mDatabase.getReference().child("messages");

        //Storage Code
        //Get Storage Reference and instance

        firebaseStorage = FirebaseStorage.getInstance();
        chatPhotostorageReference = firebaseStorage.getReference().child("chat_photos");

        //For Attachment PickUp
        floatingActionButton_att.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
                intent.setType("image/jpeg");
                intent.putExtra(Intent.EXTRA_LOCAL_ONLY,true);
                startActivityForResult(Intent.createChooser(intent,"Complete action using"),RC_PHOTO_PICKER);

            }
        });

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode == RC_PHOTO_PICKER && resultCode == RESULT_OK && data.getData()!=null){
            Uri selectImageUri = data.getData();
            uploadFile(selectImageUri);
        }
    }

    private void uploadFile(Uri selectImageUri) {

        //Get a reference to store file at chat_photos/<FILENAME>
        StorageReference photoRef = chatPhotostorageReference.child(selectImageUri.getLastPathSegment());

        //Upload File To Firebase Storage
        photoRef.putFile(selectImageUri).addOnSuccessListener(MainActivity.this, new OnSuccessListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {


                ChatMessage chatMessage = new ChatMessage(null,userName,taskSnapshot.getDownloadUrl().toString());
                mDatabaseReference.push().setValue(chatMessage);
            }
        })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {

                    }
                });
    }

    private void userSignedOutCleanUp() {

        if(childEventListener != null)
        {
            mDatabaseReference.removeEventListener(childEventListener);
        }
    }

    private void userSignedInInitialize() {

        if(childEventListener == null) {

            childEventListener = new ChildEventListener() {
                @Override
                public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                    ChatMessage chatMessage = dataSnapshot.getValue(ChatMessage.class);
                    messageAdapter.add(chatMessage);
                }
                @Override
                public void onChildChanged(DataSnapshot dataSnapshot, String s) {}
                @Override
                public void onChildRemoved(DataSnapshot dataSnapshot) {}
                @Override
                public void onChildMoved(DataSnapshot dataSnapshot, String s) {}
                @Override
                public void onCancelled(DatabaseError databaseError) {}
            };
            mDatabaseReference.addChildEventListener(childEventListener);
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
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
            mAuth.signOut();
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onStart() {
        super.onStart();
        mAuth.addAuthStateListener(mAuthListener);
    }

    @Override
    public void onStop() {
        super.onStop();
        if (mAuthListener != null) {
            mAuth.removeAuthStateListener(mAuthListener);
        }
    }
}
