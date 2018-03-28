package com.jgntic.ivan.dormnews;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class TopActivity extends RankListsHandler implements AdapterView.OnItemClickListener{

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_top);

        createMethod();

        getData();

        listView.setOnItemClickListener(this);
    }

    private void getData()
    {
        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                clearLists();

                for(DataSnapshot postSnapshot: dataSnapshot.getChildren())
                {
                    listName.add(postSnapshot.child("Name").getValue().toString());
                    userIdList.add(postSnapshot.getKey());

                    if(postSnapshot.child("total_votes").exists()) {
                        listVote.add(postSnapshot.child("total_votes").getValue().toString());
                    }else {
                        databaseReference.child(postSnapshot.getKey()).child("total_votes").setValue("0");
                        listVote.add("0");
                    }
                    if(postSnapshot.child("profileImage").exists()) {
                        profilePicturesList.add(postSnapshot.child("profileImage").getValue().toString());
                    }else{
                        profilePicturesList.add("null");
                    }
                }
                sort();

                arrayAdapter=new CustomListLayout(TopActivity.this,
                        listName, listVote, profilePicturesList,getApplicationContext(),
                        R.layout.custom_list_layout, R.id.topListText,R.id.topListVote,R.id.topListImage);

                listView.setAdapter(arrayAdapter);

                arrayAdapter.notifyDataSetChanged();

                databaseReference.removeEventListener(this);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        Intent someUserIntent=new Intent(TopActivity.this,SomeUser.class);
        someUserIntent.putExtra("id", userIdList.get(position));
        startActivity(someUserIntent);
    }

}
