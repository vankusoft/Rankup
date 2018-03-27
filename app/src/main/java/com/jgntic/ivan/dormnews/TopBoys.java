package com.jgntic.ivan.dormnews;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;


public class TopBoys extends TopHandler implements AdapterView.OnItemClickListener{

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_top);

        create();
        getData(this,"Male");

        listView.setOnItemClickListener(this);
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        Intent someUserIntent=new Intent(TopBoys.this,SomeUser.class);
        someUserIntent.putExtra("id",userIdList.get(position));
        startActivity(someUserIntent);
    }
}
