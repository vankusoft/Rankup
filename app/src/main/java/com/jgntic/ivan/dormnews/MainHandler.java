package com.jgntic.ivan.dormnews;

import android.app.Dialog;
import android.content.Intent;
import android.graphics.Point;
import android.os.Bundle;
import android.os.Parcelable;
import android.os.PersistableBundle;
import android.provider.ContactsContract;
import android.support.annotation.NonNull;
import android.support.design.internal.ParcelableSparseArray;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.Display;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.crashlytics.android.Crashlytics;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.google.firebase.FirebaseApp;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;

public class MainHandler extends AppCompatActivity{

    public RecyclerView recyclerView;
    LayoutManager layoutManager;
    DatabaseReference databaseReference,dbUser;

    FirebaseRecyclerAdapter<CardViewInfo,CardViewHolder> FBRA;

    public FirebaseAuth.AuthStateListener authStateListener;
    public FirebaseAuth mAuth;

    private boolean processLike=false;
    private boolean totalLike=false;

    private boolean commentBool=false;
    private boolean showComments=false;

    Values valuesClass;

    Dialog commentsDialog;
    Display display;
    Point size;

    ImageView postCommentsImage,profilePicturePostComments;
    TextView postCommentsTitle;
    EditText postCommentsEditText;
    Button postCommentButton;

    ListView list_view;
    ArrayAdapter<String> array_adapter;
    List<String> list_name,list_comment,list_profileImage;

    public void create()
    {
        layoutManager = new LayoutManager(this);
        layoutManager.setReverseLayout(true);
        layoutManager.setStackFromEnd(true);

        recyclerView=(RecyclerView)findViewById(R.id.recycleView);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setSaveEnabled(true);

        databaseReference= FirebaseDatabase.getInstance().getReference().child("DormNews");
        dbUser=FirebaseDatabase.getInstance().getReference().child("Users");

        mAuth=FirebaseAuth.getInstance();
        checkIfUserIsLoginIn();

        valuesClass =new Values();

        dialogAttributes();
    }

    @Override
    protected void onResume() {
        super.onResume();

        recyclerView.getRecycledViewPool().clear();
        FBRA.notifyDataSetChanged();
    }

    private void dialogAttributes()
    {
        display=getWindowManager().getDefaultDisplay();
        size=new Point();
        display.getSize(size);
        int width=size.x;
        int height=size.y;

        commentsDialog=new Dialog(MainHandler.this);
        commentsDialog.setContentView(R.layout.post_comments);
        commentsDialog.getWindow().setLayout(width,height);

        postCommentsImage=(ImageView)commentsDialog.findViewById(R.id.postCommentsImage);
        profilePicturePostComments=(ImageView)commentsDialog.findViewById(R.id.profilePicturePostComments);
        postCommentsTitle=(TextView)commentsDialog.findViewById(R.id.postCommentsTitle);
        postCommentsEditText=(EditText)commentsDialog.findViewById(R.id.postCommentsEditText);
        postCommentButton=(Button)commentsDialog.findViewById(R.id.postCommentButton);

        list_view=(ListView)commentsDialog.findViewById(R.id.commentsListView);
        list_name=new ArrayList<>();
        list_comment=new ArrayList<>();
        list_profileImage=new ArrayList<>();
    }

    public void checkIfUserIsLoginIn()
    {
        authStateListener=new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                if(firebaseAuth.getCurrentUser()==null) {
                    Intent registerIntent = new Intent(MainHandler.this, UserRegistration.class);
                    registerIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                    startActivity(registerIntent);
                }
            }
        };
    }

    public void startOn(){
        mAuth.addAuthStateListener(authStateListener);

        FBRA =new FirebaseRecyclerAdapter<CardViewInfo, CardViewHolder>
                (CardViewInfo.class,R.layout.carview_layout,CardViewHolder.class,databaseReference)
        {
            @Override
            protected void populateViewHolder(final CardViewHolder viewHolder, CardViewInfo model, int position) {

                final String post_key=getRef(position).getKey();

                viewHolder.setTitle(model.getTitle());
                viewHolder.setDescription(model.getDescription());
                viewHolder.setImage(model.getImage(),getApplicationContext());
                viewHolder.setProfileImage(model.getProfileImage(),getApplicationContext());
                viewHolder.setVotes(model.getVotes());
                viewHolder.setCommentsCount(model.getCommentsCount());

                DatabaseReference db=databaseReference.child(post_key);

                handleVoteButton(viewHolder,db);

                commentsEventListener(viewHolder,db);

                commentsCounter(db);

                showWhoLikedThePost(viewHolder,post_key,position);
            }
        };

        recyclerView.setAdapter(FBRA);
    }

    //PostLikes
    private void showWhoLikedThePost(CardViewHolder viewHolder, final String post_key , final int position)
    {
        viewHolder.mView.findViewById(R.id.postImage).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent i=new Intent(MainHandler.this,PostLikes.class);
                i.putExtra("post_key",post_key);
                i.putExtra("position",position);
                startActivity(i);
            }
        });
    }

    //Comments
    private void commentsEventListener(CardViewHolder viewHolder, final DatabaseReference db)
    {
        viewHolder.mView.findViewById(R.id.commentsIcon).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                showComments=true;

                db.addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {

                           //Setting post info
                           setPostCommentInfo(dataSnapshot);

                           //Post comment at current post
                           postComments(db);

                           //Get all comments at current post
                           if(showComments)
                           {
                               showComments(dataSnapshot);
                               showComments=false;
                           }
                           db.removeEventListener(this);
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {
                    }
                });

                commentsDialog.show();
            }
        });
    }

    private void commentsCounter(final DatabaseReference databaseReference)
    {
       databaseReference.addValueEventListener(new ValueEventListener() {
           @Override
           public void onDataChange(DataSnapshot dataSnapshot) {

                   if(dataSnapshot.child("Comments").exists())
                   {
                       long comments=dataSnapshot.child("Comments").getChildrenCount();

                       databaseReference.child("CommentsCount").setValue(String.valueOf(comments));
                   }
                   databaseReference.removeEventListener(this);
           }

           @Override
           public void onCancelled(DatabaseError databaseError) {

           }
       });
    }

    private void postComments(final DatabaseReference db)
    {
        postCommentButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                final DatabaseReference dbComment=db.child("Comments").push();
                commentBool=true;

                dbUser.child(mAuth.getCurrentUser().getUid()).addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {

                        String comment=postCommentsEditText.getText().toString();

                        if(commentBool && !TextUtils.isEmpty(comment))
                        {
                            dbComment.child("CommentComment").setValue(comment);
                            if(dataSnapshot.child("Name").exists())
                            {
                                dbComment.child("CommentName").setValue(dataSnapshot.child("Name").getValue().toString());
                            }
                            if(dataSnapshot.child("profileImage").exists())
                            {
                                dbComment.child("CommentProfileImage").setValue(dataSnapshot.child("profileImage").getValue().toString());
                            }
                            postCommentsEditText.setText("");

                            Toast.makeText(MainHandler.this,"Comment added.",Toast.LENGTH_SHORT).show();

                            commentsDialog.dismiss();

                            commentBool=false;
                        }

                        dbUser.removeEventListener(this);
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {

                    }

                });
            }
        });
    }

    private void setPostCommentInfo(DataSnapshot dataSnapshot)
    {
        postCommentsTitle.setText(dataSnapshot.child("title").getValue().toString());

        if(dataSnapshot.child("image").exists())
        {
            Picasso.with(getApplicationContext())
                    .load(dataSnapshot.child("image").getValue().toString())
                    .resize(500,500)
                    .onlyScaleDown()
                    .centerCrop()
                    .into(postCommentsImage);
        }

        if(dataSnapshot.child("profileImage").exists())
        {
            Picasso.with(getApplicationContext())
                    .load(dataSnapshot.child("profileImage").getValue().toString())
                    .resize(60,60)
                    .onlyScaleDown()
                    .centerCrop()
                    .into(profilePicturePostComments);
        }
    }

    private void showComments(final DataSnapshot dataSnapshot)
    {
            list_name.clear();
            list_comment.clear();
            list_profileImage.clear();

            if(dataSnapshot.child("Comments").exists())
            {
                for(DataSnapshot postSnapshot:dataSnapshot.child("Comments").getChildren())
                {
                    if(postSnapshot.child("CommentName").exists())
                    {
                        list_name.add(postSnapshot.child("CommentName").getValue().toString());
                    }else{
                        list_name.add("null");
                    }
                    if(postSnapshot.child("CommentComment").exists())
                    {
                        list_comment.add(postSnapshot.child("CommentComment").getValue().toString());
                    }else{
                        list_comment.add("null");
                    }
                    if(postSnapshot.child("CommentProfileImage").exists())
                    {
                        list_profileImage.add(postSnapshot.child("CommentProfileImage").getValue().toString());
                    }else
                    {
                        list_profileImage.add("null");
                    }
                }
            }

            array_adapter =new CustomListLayout(MainHandler.this,
                    list_name,list_comment,list_profileImage,getApplicationContext(),
                    R.layout.custom_list_layout_post_comments,R.id.customPostCommentsName,R.id.customPostCommentsComment,
                    R.id.customPostCommentsImage);

            list_view.setAdapter(array_adapter);

            array_adapter.notifyDataSetChanged();
    }

    //Votes
    private void handleVoteButton(CardViewHolder viewHolder,final DatabaseReference db)
    {
        viewHolder.mView.findViewById(R.id.voteButton).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                processLike=true;
                totalLike=true;

                //Handling votes
                handleVotes(db);

                //Handling user's total vote counter
                handleTotalVotes();
            }
        });
    }

    private void handleVotes(final DatabaseReference db)
    {
        db.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                if(processLike)
                {
                    //Handling posts like/unlike function and postVotes count
                    valuesClass.setUserId(dataSnapshot.child("userId").getValue().toString());

                    String value=dataSnapshot.child("votes").getValue().toString();
                    int value_int=Integer.parseInt(value);

                    int totalValue=0;

                    if(dataSnapshot.child("users_voted").exists()) {

                        if(dataSnapshot.child("users_voted").hasChild(mAuth.getCurrentUser().getUid())) {

                            if(dataSnapshot.child("users_voted").child(mAuth.getCurrentUser().getUid()).getValue().toString()
                                    .contentEquals("1"))
                            {
                                db.child("users_voted").child(mAuth.getCurrentUser().getUid()).setValue("0");
                                value_int-=1;
                                totalValue-=1;
                            }
                            else if(dataSnapshot.child("users_voted").child(mAuth.getCurrentUser().getUid()).getValue().toString()
                                    .contentEquals("0"))
                            {
                                db.child("users_voted").child(mAuth.getCurrentUser().getUid()).setValue("1");
                                value_int+=1;
                                totalValue+=1;
                            }
                        }else {
                            value_int+=1;
                            totalValue+=1;
                            db.child("users_voted").child(mAuth.getCurrentUser().getUid()).setValue("1");
                        }
                    }
                    else {
                        db.child("users_voted").child(mAuth.getCurrentUser().getUid()).setValue("1");
                        value_int+=1;
                        totalValue+=1;
                    }

                    valuesClass.setValueInt(totalValue);
                    value=Integer.toString(value_int);
                    db.child("votes").setValue(value);

                    processLike=false;
                }
                db.removeEventListener(this);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

    }

    private void handleTotalVotes()
    {
        dbUser.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if(totalLike)
                {
                    String userID= valuesClass.getUser_id();

                    if(dataSnapshot.child(userID).hasChild("total_votes"))
                    {
                        String totalUserVotes=dataSnapshot.child(userID).child("total_votes").getValue().toString();
                        int total=Integer.parseInt(totalUserVotes);

                        total+= valuesClass.getValueInt();
                        totalUserVotes=Integer.toString(total);
                        dbUser.child(userID).child("total_votes").setValue(totalUserVotes);
                    }else
                    {
                        dbUser.child(userID).child("total_votes").setValue("1");
                    }

                    totalLike=false;
                }

                dbUser.removeEventListener(this);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

    }
}
