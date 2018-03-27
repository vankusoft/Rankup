package com.jgntic.ivan.dormnews;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Created by Ivan on 3/26/2018.
 */

public class RankListsHandler extends AppCompatActivity{

    ListView listView;
    ArrayAdapter<String> arrayAdapter;
    List<String> listName,listVote,profilePicturesList,userIdList;
    DatabaseReference databaseReference;

    public void createMethod()
    {
        listName =new ArrayList<>();
        profilePicturesList=new ArrayList<>();
        listVote=new ArrayList<>();
        userIdList=new ArrayList<>();
        listView=(ListView)findViewById(R.id.listView);
        databaseReference= FirebaseDatabase.getInstance().getReference().child("Users");
    }

    public void clearLists()
    {
        listName.clear();
        listVote.clear();
        profilePicturesList.clear();
        userIdList.clear();
    }

   public void sort()
   {
       //Bubble sort
       int n=listVote.size();
       int temp=0;
       String temp_name;
       String temp_profilePic;
       String temp_userId;

       for (int i = 0; i < n; i++) {
           for (int j = 1; j < (n - i); j++) {

               if (Integer.valueOf(listVote.get(j-1)) > Integer.valueOf(listVote.get(j))) {
                   //Vote sort
                   temp = Integer.valueOf(listVote.get(j-1));
                   Integer.valueOf(listVote.set(j-1,listVote.get(j)));
                   listVote.set(j,String.valueOf(temp));
                   //Name sort
                   temp_name=listName.get(j-1);
                   listName.set(j-1,listName.get(j));
                   listName.set(j,temp_name);
                   //Proifile pic sort
                   temp_profilePic=profilePicturesList.get(j-1);
                   profilePicturesList.set(j-1,profilePicturesList.get(j));
                   profilePicturesList.set(j,temp_profilePic);
                   //UserId sort
                   temp_userId=userIdList.get(j-1);
                   userIdList.set(j-1,userIdList.get(j));
                   userIdList.set(j,temp_userId);
               }
           }
       }
       //End of BubbleSort

       Collections.reverse(listName);
       Collections.reverse(listVote);
       Collections.reverse(profilePicturesList);
       Collections.reverse(userIdList);
   }

}
