package com.jgntic.ivan.dormnews;

import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.soundcloud.android.crop.Crop;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.squareup.picasso.Picasso;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.util.UUID;

public class UserProfile extends AppCompatActivity {

    ImageButton profilePicture;
    TextView name, gender, posts, postVotes, userLikes;
    Button updateProfileBtn;

    FirebaseAuth auth;
    DatabaseReference databaseReference;
    StorageReference storageReference;

    Uri uri;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_profile);

        setLayoutStuff();

        auth = FirebaseAuth.getInstance();

        databaseReference = FirebaseDatabase.getInstance().getReference().child("Users");
        storageReference = FirebaseStorage.getInstance().getReference();

        setUserInfo();
    }

    private void setLayoutStuff() {
        profilePicture = (ImageButton) findViewById(R.id.profilePicture);
        name = (TextView) findViewById(R.id.nameTextView);
        gender = (TextView) findViewById(R.id.genderTextView);
        posts = (TextView) findViewById(R.id.postsTextView);
        postVotes = (TextView) findViewById(R.id.votesTextView);
        userLikes = (TextView) findViewById(R.id.ratingTextView);
        updateProfileBtn = (Button) findViewById(R.id.profileUpdateButton);
        updateProfileBtn.setEnabled(false);
    }


    private void setUserInfo() {
        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                String current_user = auth.getCurrentUser().getUid();

                if ((dataSnapshot.child(current_user).child("Name").exists())
                        && (dataSnapshot.child(current_user).child("Gender").exists())) {
                    name.setText(dataSnapshot.child(current_user).child("Name").getValue().toString());
                    gender.setText(dataSnapshot.child(current_user).child("Gender").getValue().toString());

                    if (dataSnapshot.child(current_user).child("profileImage").exists()) {
                        Picasso.with(getApplicationContext())
                                .load((String) (dataSnapshot.child(current_user).child("profileImage")
                                        .getValue()))
                                .resize(700, 700)
                                .onlyScaleDown()
                                .centerCrop()
                                .into(profilePicture);
                    }
                    if (dataSnapshot.child(current_user).child("Posts").exists()) {
                        posts.setText(dataSnapshot.child(current_user).child("Posts").getValue().toString());
                    }
                    if (dataSnapshot.child(current_user).child("total_votes").exists()) {
                        postVotes.setText(dataSnapshot.child(current_user).child("total_votes").getValue().toString());
                    }
                    if (dataSnapshot.child(current_user).child("UserLikes").exists()) {

                        String likes = dataSnapshot.child(current_user).child("UserLikes").getValue().toString();
                        userLikes.setText(likes);
                    }
                }
                databaseReference.removeEventListener(this);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

    }

    public void profilePicOnClick(View view) {
        Crop.pickImage(this);
        updateProfileBtn.setEnabled(true);
    }

    public void profileNewsFeedOnClick(View view) {
        Intent newsFeedIntent = new Intent(UserProfile.this, MainActivity.class);
        startActivity(newsFeedIntent);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == Crop.REQUEST_PICK && resultCode == RESULT_OK) {
            beginCrop(data.getData());

        } else if (requestCode == Crop.REQUEST_CROP) {
            handleCrop(resultCode, data);
        }
    }

    private void beginCrop(Uri data) {
        Uri destination = Uri.fromFile(new File(getCacheDir(), "cropped"));
        Crop.of(data, destination).asSquare().withMaxSize(500, 500).start(this);
    }

    private void handleCrop(int resultCode, Intent data) {
        if (resultCode == RESULT_OK) {
            uri = Crop.getOutput(data);

            profilePicture.setImageURI(uri);

        } else if (resultCode == Crop.RESULT_ERROR) {
            Toast.makeText(this, Crop.getError(data).getMessage(), Toast.LENGTH_SHORT).show();
        }
    }

    public void profileUpdateOnClick(View view) {
        final ProgressDialog progress = new ProgressDialog(this);
        progress.setTitle("Loading");
        progress.setMessage("Setting profile picture...");
        progress.setCancelable(false);
        progress.show();

        StorageReference filepath = storageReference.child("ProfilePictures").child(UUID.randomUUID().toString());
        filepath.putFile(uri).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {

                final String downloadUrl = taskSnapshot.getDownloadUrl().toString();

                String current_user = auth.getCurrentUser().getUid();
                DatabaseReference user = databaseReference.child(current_user);
                user.child("profileImage").setValue(downloadUrl);

                progress.dismiss();
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {

                Toast.makeText(UserProfile.this, "An error occurred! Check your internet connection!"
                        , Toast.LENGTH_SHORT).show();

                progress.dismiss();
            }
        });
    }

    public void viewLikesClick(View view)
    {
        Intent viewLikesIntent=new Intent(UserProfile.this,ShowLikes.class);
        startActivity(viewLikesIntent);
    }

}

