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
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthException;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class UserRegistration extends AppCompatActivity {

    EditText regName,regEmail,regGender,regPassword,regPasswordConfirm;
    Button regButton;

    FirebaseAuth mAuth;
    DatabaseReference databaseReference;

    ProgressDialog progress;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_registration);

        regName=(EditText)findViewById(R.id.regName);
        regEmail=(EditText)findViewById(R.id.regEmail);
        regGender=(EditText)findViewById(R.id.regGender);
        regPassword=(EditText)findViewById(R.id.regPassword);
        regPasswordConfirm=(EditText)findViewById(R.id.regPasswordConfirm);
        regButton=(Button)findViewById(R.id.regButton);

        mAuth=FirebaseAuth.getInstance();
        databaseReference=FirebaseDatabase.getInstance().getReference().child("Users");

        progress = new ProgressDialog(this);
        progress.setTitle("Loading");
        progress.setMessage("Registering user...");
        progress.setCancelable(false);

    }

    public void registerOnClick(View view)
    {
        final String name =regName.getText().toString().trim();
        final String email =regEmail.getText().toString().trim();
        final String password =regPassword.getText().toString().trim();
        final String passwordConfirm =regPasswordConfirm.getText().toString().trim();
        String gender=regGender.getText().toString().trim();

         if (!TextUtils.isEmpty(name)
                 && !TextUtils.isEmpty(email)
                 && !TextUtils.isEmpty(gender)
                 && !TextUtils.isEmpty(password)
                 && !TextUtils.isEmpty(passwordConfirm)) {

             final String gender_up=gender.substring(0,1).toUpperCase()+gender.substring(1);

             if(password.contentEquals(passwordConfirm)) {

                 if (gender_up.contentEquals("Male") || gender_up.contentEquals("Female")) {

                     progress.show();

                     mAuth.createUserWithEmailAndPassword(email, passwordConfirm).addOnCompleteListener(new OnCompleteListener<AuthResult>() {

                         @Override
                         public void onComplete(@NonNull Task<AuthResult> task) {
                             if (task.isSuccessful()) {
                                 progress.dismiss();

                                 String user_id = mAuth.getCurrentUser().getUid();
                                 DatabaseReference current_user = databaseReference.child(user_id);

                                 current_user.child("Name").setValue(name);
                                 current_user.child("Gender").setValue(gender_up);

                                 Toast.makeText(UserRegistration.this, "Registration successful!", Toast.LENGTH_SHORT).show();

                                 Intent mainIntent = new Intent(UserRegistration.this, UserProfile.class);
                                 mainIntent.putExtra("news_feed","1");
                                 mainIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                                 startActivity(mainIntent);
                             }
                             else
                             {
                                 progress.dismiss();

                                 Exception e = task.getException();
                                 Toast.makeText(UserRegistration.this, "Registration Failed:" + e.getMessage(), Toast.LENGTH_SHORT).show();
                             }
                         }
                     });

                 }
                 else
                 {
                     Toast.makeText(UserRegistration.this, "Gender must be 'Male' or 'Female'!", Toast.LENGTH_SHORT).show();
                 }
             }
             else
             {
                 Toast.makeText(UserRegistration.this, "Passwords don't match!", Toast.LENGTH_SHORT).show();
             }
         }
         else
         {
             Toast.makeText(UserRegistration.this, "Fill all information!", Toast.LENGTH_SHORT).show();
         }

    }

    public void regLoginOnClick(View view)
    {
        Intent loginIntent=new Intent(UserRegistration.this,LoginActivity.class);
        startActivity(loginIntent);
    }
}
