package com.jgntic.ivan.dormnews;

import android.app.ProgressDialog;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.FirebaseException;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthActionCodeException;
import com.google.firebase.auth.FirebaseAuthEmailException;
import com.google.firebase.auth.FirebaseAuthException;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class LoginActivity extends AppCompatActivity {

    EditText emailLogin,passwordLogin;
    Button loginButton;

    private DatabaseReference db;
    private FirebaseAuth firebaseAuth;

    ProgressDialog progress;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        emailLogin=(EditText)findViewById(R.id.emailLogin);
        passwordLogin=(EditText)findViewById(R.id.passwordLogin);
        loginButton=(Button)findViewById(R.id.loginButton);

        firebaseAuth=FirebaseAuth.getInstance();
        db = FirebaseDatabase.getInstance().getReference().child("Users");

        progress = new ProgressDialog(this);
        progress.setTitle("Loading");
        progress.setMessage("Checking for user...");
        progress.setCancelable(false);

    }

    public void loginOnClick(View view)
    {
        String email=emailLogin.getText().toString().trim();
        String password=passwordLogin.getText().toString().trim();

        if(!TextUtils.isEmpty(email) && !TextUtils.isEmpty(password))
        {
            progress.show();

            firebaseAuth.signInWithEmailAndPassword(email,password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                @Override
                public void onComplete(@NonNull Task<AuthResult> task) {

                        if(task.isSuccessful()) {
                            checkUserExistence();
                        }
                        else {
                            progress.dismiss();

                            Toast.makeText(LoginActivity.this,"Login failed! No such a user!",
                                    Toast.LENGTH_SHORT).show();
                        }
                }
            });
        }else
        {
            Toast.makeText(LoginActivity.this,"Type e-mail and password!",Toast.LENGTH_SHORT).show();
        }

    }

    public void checkUserExistence()
    {
        final String user_id=firebaseAuth.getCurrentUser().getUid();

        db.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                    if(dataSnapshot.hasChild(user_id))
                    {
                        progress.dismiss();
                        Toast.makeText(LoginActivity.this,"Login successful",Toast.LENGTH_SHORT).show();

                        Intent loginIntent=new Intent(LoginActivity.this,MainActivity.class);
                        loginIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                        startActivity(loginIntent);

                        db.removeEventListener(this);
                    }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }
}
