package com.jgntic.ivan.dormnews;

import android.content.Context;
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
import java.util.Collection;
import java.util.Collections;
import java.util.List;

public class TopHandler extends RankListsHandler{

   public void create()
   {
       createMethod();
   }

    public void getData(final Context context,final String gender)
    {
        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                clearLists();

                for(DataSnapshot postSnapshot: dataSnapshot.getChildren())
                {
                    if(postSnapshot.child("Gender").exists())
                    {
                        if((postSnapshot.child("Gender").getValue().toString()).contentEquals(gender))
                        {
                            listName.add(postSnapshot.child("Name").getValue().toString());
                            userIdList.add(postSnapshot.getKey());

                            if(postSnapshot.child("UserLikes").exists()) {
                                listVote.add(postSnapshot.child("UserLikes").getValue().toString());
                            }else {
                                listVote.add("0");
                            }

                            if(postSnapshot.child("profileImage").exists()) {
                                profilePicturesList.add(postSnapshot.child("profileImage").getValue().toString());
                            }else
                            {
                                profilePicturesList.add("null");
                            }
                        }
                    }
                }

                sort();

                arrayAdapter=new CustomListLayout(context, listName,listVote, profilePicturesList,getApplicationContext(),
                        R.layout.custom_list_layout_hot_or_not,R.id.hotListText,R.id.hotListVote,R.id.hotListImage);
                listView.setAdapter(arrayAdapter);

                arrayAdapter.notifyDataSetChanged();

                databaseReference.removeEventListener(this);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

    }

}
