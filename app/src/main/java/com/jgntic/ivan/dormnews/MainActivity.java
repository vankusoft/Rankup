package com.jgntic.ivan.dormnews;

import android.content.Intent;
import android.os.Bundle;
import android.os.Parcelable;
import android.os.PersistableBundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.view.Menu;
import android.view.MenuItem;
import android.view.Window;
import android.widget.Toast;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class MainActivity extends MainHandler{

    int savedScrollPosition;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        getIntentExtra(savedInstanceState);

        create();
    }

    private void getIntentExtra(Bundle savedInstanceState)
    {
        if (savedInstanceState == null) {
            Bundle extras = getIntent().getExtras();
            if(extras == null) {
                savedScrollPosition= 0;
            } else {
                savedScrollPosition= extras.getInt("pos");
            }
        } //else {
           // savedScrollPosition=(int) savedInstanceState.getSerializable("pos");
        //}
    }

    @Override
    protected void onStart() {
        super.onStart();

       startOn();
    }

    @Override
    protected void onResume() {
        super.onResume();

        Bundle extras=getIntent().getExtras();
        if(extras!=null)
        {
            layoutManager.smoothScrollToPosition(recyclerView,null,savedScrollPosition);
        }

    }

    public void profileOnClick(View view)
    {
        Intent profileIntent=new Intent(MainActivity.this,UserProfile.class);
        startActivity(profileIntent);
    }

    public void topOnClick(View view)
    {
        Intent topIntent=new Intent(MainActivity.this,TopActivity.class);
        startActivity(topIntent);
    }

    public void hotOrNotOnClick(View view)
    {
        Intent hotOrNotIntent=new Intent(MainActivity.this,HotOrNot.class);
        startActivity(hotOrNotIntent);
    }

    public void mainLogoutOnClick(View view){
        mAuth.signOut();
    }

    public void mainAddPictureOnClick(View view)
    {
        Intent intent=new Intent(this,PostActivity.class);
        startActivity(intent);
    }

}
