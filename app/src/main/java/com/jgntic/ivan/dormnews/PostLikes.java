package com.jgntic.ivan.dormnews;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class PostLikes extends AppCompatActivity implements AdapterView.OnItemClickListener{

    String post_key;
    int position;
    DatabaseReference databaseReference,dbUser;

    ListView listView;
    ArrayAdapter<String> arrayAdapter;
    List<String> listName,profilePicList;
    List<String> userIdList;

    boolean showUsers=false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.likes_activity_list);

        getIntentExtra(savedInstanceState);

        databaseReference= FirebaseDatabase.getInstance().getReference().child("DormNews").child(post_key);
        dbUser=FirebaseDatabase.getInstance().getReference().child("Users");

        showUsers=true;

        setStuff();

        setData();
    }

    private void getIntentExtra(Bundle savedInstanceState)
    {
        if (savedInstanceState == null) {
            Bundle extras = getIntent().getExtras();
            if(extras == null) {
                post_key= null;
                position=0;
            } else {
                post_key= extras.getString("post_key");
                position=extras.getInt("position");
            }
        } else {
            post_key= (String) savedInstanceState.getSerializable("post_key");
            position=(int) savedInstanceState.getSerializable("position");
        }
    }

    private void setStuff()
    {
        listView=(ListView)findViewById(R.id.likesListView);
        listName=new ArrayList<>();
        userIdList=new ArrayList<>();
        profilePicList=new ArrayList<>();
        listView.setOnItemClickListener(this);
    }

    private void setData()
    {
        //GET USERS IDs WHICH LIKED CURRENT POST
        getUsersIds();
        //SET USER INFO
        showUsers();
    }

    private void getUsersIds()
    {
        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if(dataSnapshot.child("users_voted").exists())
                {
                    final DatabaseReference usersLiked=databaseReference.child("users_voted");

                    usersLiked.addValueEventListener(new ValueEventListener() {
                        @Override
                        public void onDataChange(DataSnapshot dataSnapshot) {

                            for(DataSnapshot postSnapshot:dataSnapshot.getChildren())
                            {
                                if((postSnapshot.getValue()).equals("1"))
                                {
                                    userIdList.add(postSnapshot.getKey());
                                }
                            }
                            usersLiked.removeEventListener(this);
                        }

                        @Override
                        public void onCancelled(DatabaseError databaseError) {

                        }
                    });
                }
                    databaseReference.removeEventListener(this);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    private void showUsers()
    {
        dbUser.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                if(showUsers)
                {
                    for(DataSnapshot userSnapshot:dataSnapshot.getChildren()) {

                        for (int i = 0; i < userIdList.size(); i++) {

                            if ((userIdList.get(i)).contentEquals(userSnapshot.getKey())) {

                                listName.add(userSnapshot.child("Name").getValue().toString());

                                if (userSnapshot.child("profileImage").exists()) {
                                    profilePicList.add(userSnapshot.child("profileImage").getValue().toString());
                                } else {
                                    profilePicList.add("null");
                                }
                            }
                        }
                    }

                    arrayAdapter=new CustomListLayoutPostLikes(PostLikes.this,listName,profilePicList,getApplicationContext());

                    listView.setAdapter(arrayAdapter);
                    arrayAdapter.notifyDataSetChanged();

                    showUsers=false;
                }
                dbUser.removeEventListener(this);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

    }

    public void likesActivityButton(View view)
    {
        Intent returnToMain=new Intent(PostLikes.this,MainActivity.class);
        returnToMain.putExtra("pos",position);
        returnToMain.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(returnToMain);
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        Intent someUserIntent=new Intent(PostLikes.this,SomeUser.class);
        someUserIntent.putExtra("id", userIdList.get(position));
        startActivity(someUserIntent);
    }


}
