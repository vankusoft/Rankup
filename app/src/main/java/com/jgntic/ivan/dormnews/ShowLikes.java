package com.jgntic.ivan.dormnews;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;

public class ShowLikes extends RankListsHandler implements AdapterView.OnItemClickListener{

    FirebaseAuth firebaseAuth;
    String current_user;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_top);

        firebaseAuth=FirebaseAuth.getInstance();
        current_user=firebaseAuth.getCurrentUser().getUid();

        createMethod();

        getData();

        listView.setOnItemClickListener(this);
    }

    private void getData(){

        databaseReference.child(current_user).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                if(dataSnapshot.child("UsersVoted").exists()){

                    databaseReference.child(current_user).child("UsersVoted").addValueEventListener(new ValueEventListener() {
                        @Override
                        public void onDataChange(DataSnapshot dataSnapshot) {

                            clearLists();

                            for(DataSnapshot postSnaphot:dataSnapshot.getChildren())
                            {
                                if(postSnaphot.getValue().toString().contentEquals("1"))
                                {
                                    userIdList.add(postSnaphot.getKey());
                                }
                            }

                            databaseReference.removeEventListener(this);
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

        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                for(int i=0;i<userIdList.size();i++) {

                    for (DataSnapshot postSnapshot : dataSnapshot.getChildren()) {

                        if (postSnapshot.getKey().contentEquals(userIdList.get(i))) {
                            listName.add(postSnapshot.child("Name").getValue().toString());
                            profilePicturesList.add(postSnapshot.child("profileImage").getValue().toString());

                        }

                    }
                }

                arrayAdapter=new CustomListLayout(ShowLikes.this,listName,profilePicturesList,getApplicationContext(),
                        R.layout.custom_list_layout_post_likes,R.id.likesListName,R.id.likesListImageView);

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
        Intent someUserIntent=new Intent(ShowLikes.this,SomeUser.class);
        someUserIntent.putExtra("id", userIdList.get(position));
        startActivity(someUserIntent);
    }
}
