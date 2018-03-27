package com.jgntic.ivan.dormnews;

import android.media.Image;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

public class SomeUser extends AppCompatActivity {

    String post_key;

    ImageView profilePicture;
    TextView name,gender,posts,votes,userLikes;

    DatabaseReference dbUser;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_some_user);

        getIntentExtra(savedInstanceState);

        setLayoutStuff();

        dbUser=FirebaseDatabase.getInstance().getReference().child("Users").child(post_key);

        setUserInfo();
    }

    private void getIntentExtra(Bundle savedInstanceState)
    {
        if (savedInstanceState == null) {
            Bundle extras = getIntent().getExtras();
            if(extras == null) {
                post_key= null;
            } else {
                post_key= extras.getString("id");
            }
        } else {
            post_key= (String) savedInstanceState.getSerializable("id");
        }
    }

    private void setLayoutStuff()
    {
        profilePicture=(ImageView)findViewById(R.id.suProfilePicture);
        name=(TextView)findViewById(R.id.suNameTextView);
        gender=(TextView)findViewById(R.id.suGenderTextView);
        posts=(TextView)findViewById(R.id.suPostsTextView);
        votes=(TextView)findViewById(R.id.suVotesTextView);
        userLikes=(TextView)findViewById(R.id.suUserLikesTextView);
    }

    private void setUserInfo()
    {
        dbUser.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                name.setText(dataSnapshot.child("Name").getValue().toString());
                gender.setText(dataSnapshot.child("Gender").getValue().toString());

                if(dataSnapshot.child("profileImage").exists())
                {
                    Picasso.with(getApplicationContext())
                            .load((String)(dataSnapshot.child("profileImage").getValue()))
                            .resize(700,700)
                            .onlyScaleDown()
                            .centerCrop()
                            .into(profilePicture);
                }
                if(dataSnapshot.child("Posts").exists())
                {
                    posts.setText(dataSnapshot.child("Posts").getValue().toString());
                }
                if(dataSnapshot.child("total_votes").exists())
                {
                    votes.setText(dataSnapshot.child("total_votes").getValue().toString());
                }
                if(dataSnapshot.child("UserLikes").exists())
                {
                    userLikes.setText(dataSnapshot.child("UserLikes").getValue().toString());
                }
                dbUser.removeEventListener(this);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

    }

}
