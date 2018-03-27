package com.jgntic.ivan.dormnews;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.provider.ContactsContract;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;
import com.squareup.picasso.Target;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class HotOrNot extends AppCompatActivity{

    ImageView profilePicture;
    TextView userName;
    ImageButton hotButton, notButton;

    DatabaseReference databaseReference;
    FirebaseAuth firebaseAuth;

    boolean male;
    boolean search;
    List<String> nameList,profilePicList,userIdList,userLikesList;

    int index=0;
    int index_get=0;
    int children_index=0;

    String current_user;
    String user_id;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_hot_or_not);

        profilePicture=(ImageView) findViewById(R.id.hotImageView);
        userName=(TextView)findViewById(R.id.hotTextView);
        hotButton=(ImageButton) findViewById(R.id.hotHotBtn);
        notButton=(ImageButton) findViewById(R.id.hotNotBtn);
        hotButton.setEnabled(false);
        notButton.setEnabled(false);

        search=true;

        databaseReference= FirebaseDatabase.getInstance().getReference().child("Users");
        firebaseAuth=FirebaseAuth.getInstance();

        current_user=firebaseAuth.getCurrentUser().getUid();

        nameList=new ArrayList<>();
        profilePicList=new ArrayList<>();
        userIdList=new ArrayList<>();
        userLikesList=new ArrayList<>();

        currentUserGender();

        getUsersInfo();
    }

    private void currentUserGender()
    {

        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                if(dataSnapshot.child(current_user).child("Gender").exists())
                {
                    String current_gender=dataSnapshot.child(current_user).child("Gender").getValue().toString();

                    if(current_gender.contentEquals("Male")){
                        male=true;
                    }else{
                        male=false;
                    }
                }

                databaseReference.removeEventListener(this);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    private void getUsersInfo()
    {
        databaseReference.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {

                    if(search)
                    {
                        for(DataSnapshot postSnapshot:dataSnapshot.getChildren())
                        {
                            if(male)
                            {
                                if((postSnapshot.child("Gender").getValue().toString()).contentEquals("Female")
                                        && postSnapshot.child("UsersVoted").exists() &&
                                        !postSnapshot.child("UsersVoted").child(current_user).exists())
                                {
                                    addToList(postSnapshot);
                                }
                                else if((postSnapshot.child("Gender").getValue().toString()).contentEquals("Female")
                                        && !postSnapshot.child("UsersVoted").exists())
                                {
                                    addToList(postSnapshot);
                                }
                            }else
                            {
                                if((postSnapshot.child("Gender").getValue().toString()).contentEquals("Male")
                                        && postSnapshot.child("UsersVoted").exists() &&
                                        !postSnapshot.child("UsersVoted").child(current_user).exists())
                                {
                                    addToList(postSnapshot);
                                }
                                else if((postSnapshot.child("Gender").getValue().toString()).contentEquals("Male")
                                        && !postSnapshot.child("UsersVoted").exists())
                                {
                                    addToList(postSnapshot);
                                }

                            }

                            children_index+=1;

                            if(nameList.size()>=1 && dataSnapshot.getChildrenCount()==children_index) {
                                setUserInfo();
                                hotButton.setEnabled(true);
                                notButton.setEnabled(true);
                            }else
                            {
                                userName.setText("No more users!");
                            }
                        }

                        search=false;
                    }
                    databaseReference.removeEventListener(this);
                }

                @Override
                public void onCancelled(DatabaseError databaseError) {

                }
            });
    }

    private void addToList(DataSnapshot postSnapshot)
    {
        nameList.add(index,postSnapshot.child("Name").getValue().toString());
        userIdList.add(index,postSnapshot.getKey());

        if(postSnapshot.child("UserLikes").exists())
        {
            String likes=postSnapshot.child("UserLikes").getValue().toString();
            userLikesList.add(index,likes);
        }else
        {
            userLikesList.add(index,"0");
        }

        if(postSnapshot.child("profileImage").exists()) {
            profilePicList.add(index,postSnapshot.child("profileImage").getValue().toString());
        }
        else{
            profilePicList.add("No");
        }

        index+=1;
    }

    private void setUserInfo()
    {
            String user_name=nameList.get(index_get);
            String profile_picture=profilePicList.get(index_get);
            user_id=userIdList.get(index_get);

            userName.setText(user_name);

            try {
                if(!profilePicList.get(index_get).contentEquals("No"))
                {
                    Picasso.with(getApplicationContext())
                            .load(profile_picture)
                            .resize(600,600)
                            .onlyScaleDown()
                            .centerCrop()
                            .into(profilePicture);

                }

            }catch (Exception e)
            {
                e.printStackTrace();
            }
    }

    public void hotBtnOnClick(View view)
    {
        if(userLikesList.size()>=index_get+1)
        {
            String userlikes=userLikesList.get(index_get);
            int likesInt=Integer.parseInt(userlikes);
            likesInt+=1;
            databaseReference.child(user_id).child("UserLikes").setValue(Integer.toString(likesInt));
        }

            databaseReference.child(user_id).child("UsersVoted").child(current_user).setValue("1");

        index_get+=1;

        if(nameList.size()<=index_get)
        {
            Toast.makeText(this,"No more users!",Toast.LENGTH_SHORT).show();
        }else{
            setUserInfo();
        }
    }

    public void notBtnOnClick(View view)
    {
        databaseReference.child(user_id).child("UsersVoted").child(current_user).setValue("0");

        index_get+=1;

        if(nameList.size()<=index_get)
        {
            Toast.makeText(this,"No more users!",Toast.LENGTH_SHORT).show();
        }else{
            setUserInfo();
        }
    }

    public void topGirlsButton(View view)
    {
        Intent topGirlsIntent=new Intent(HotOrNot.this,TopGirls.class);
        startActivity(topGirlsIntent);
    }

    public void topBoysButton(View view)
    {
        Intent topBoysIntent=new Intent(HotOrNot.this,TopBoys.class);
        startActivity(topBoysIntent);
    }
}
