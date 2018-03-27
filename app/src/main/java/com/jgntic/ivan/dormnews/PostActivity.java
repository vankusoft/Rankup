package com.jgntic.ivan.dormnews;

import android.app.Application;
import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.soundcloud.android.crop.Crop;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.UUID;

public class PostActivity extends AppCompatActivity {

    public Uri uri;
    private ImageButton imageButton;
    private EditText etDesc;

    private StorageReference storageReference;
    private DatabaseReference databaseReference, databasereferenceUsers;
    FirebaseAuth userAuth;

    boolean postCounter=false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_post);

        etDesc=(EditText)findViewById(R.id.etDescription);
        imageButton=(ImageButton) findViewById(R.id.imageButton);

        storageReference=FirebaseStorage.getInstance().getReference();
        databaseReference=FirebaseDatabase.getInstance().getReference().child("DormNews");

        userAuth=FirebaseAuth.getInstance();
        databasereferenceUsers=FirebaseDatabase.getInstance().getReference().child("Users").child(userAuth.getCurrentUser().getUid());
    }

    public void imageButtonClicked(View view)
    {
        Crop.pickImage(PostActivity.this);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if(requestCode==Crop.REQUEST_PICK && resultCode== RESULT_OK)
        {
            beginCrop(data.getData());
        }
        else if(requestCode==Crop.REQUEST_CROP)
        {
            handleCrop(resultCode,data);
        }
    }

    private void beginCrop(Uri data)
    {
        Uri destination= Uri.fromFile(new File(getCacheDir(),"cropped"));
        Crop.of(data,destination).asSquare().withMaxSize(500,500).start(this);
    }

    private void handleCrop(int resultCode,Intent data)
    {
        if(resultCode==RESULT_OK)
        {
            uri=Crop.getOutput(data);

            imageButton.setImageURI(uri);

        }else if (resultCode==Crop.RESULT_ERROR)
        {
            Toast.makeText(this,Crop.getError(data).getMessage(),Toast.LENGTH_SHORT).show();
        }
    }

    public void submitButtonClicked(View view)
    {
        final ProgressDialog progress = new ProgressDialog(this);
        progress.setTitle("Loading");
        progress.setMessage("Uploading picture...");
        progress.setCancelable(false);

        postCounter=true;

        final String descValue=etDesc.getText().toString().trim();

           if(imageButton.getBackground()!=null && !TextUtils.isEmpty(descValue))
           {
               progress.show();

               StorageReference filepath=storageReference.child("Images").child(UUID.randomUUID().toString());

               filepath.putFile(uri).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>()
               {
                   @Override
                   public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {

                       final String downloadUrl= taskSnapshot.getDownloadUrl().toString();
                       final  DatabaseReference newPost=databaseReference.push();
                       final String user_id=userAuth.getCurrentUser().getUid();

                       //Database ValueEventListener
                       handleValueListener(newPost,descValue,downloadUrl,user_id);

                       Toast.makeText(PostActivity.this,"Upload Success", Toast.LENGTH_SHORT).show();

                       progress.dismiss();

                       Intent mainIntent=new Intent(PostActivity.this,MainActivity.class);
                       mainIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                       startActivity(mainIntent);

                   }
               }).addOnFailureListener(new OnFailureListener() {
                   @Override
                   public void onFailure(@NonNull Exception e) {

                       progress.dismiss();
                       Toast.makeText(PostActivity.this, "Failed "+e.getMessage(), Toast.LENGTH_SHORT).show();
                   }
               });
           }else
           {
               Toast.makeText(PostActivity.this,"Choose image and fill the field!",Toast.LENGTH_LONG).show();
           }


    }

    public void handleValueListener(final DatabaseReference newPost,final String descValue,final String downloadUrl,
                                    final String user_id)
    {
        databasereferenceUsers.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                newPost.child("title").setValue(dataSnapshot.child("Name").getValue());
                newPost.child("description").setValue(descValue);
                newPost.child("image").setValue(downloadUrl);
                newPost.child("userId").setValue(user_id);
                newPost.child("votes").setValue("0");

                if(dataSnapshot.child("profileImage").exists())
                {
                    newPost.child("profileImage").setValue(dataSnapshot.child("profileImage").getValue());
                }
                //PostCounter
                if(dataSnapshot.child("Posts").exists())
                {
                    if (postCounter)
                    {
                        String posts=dataSnapshot.child("Posts").getValue().toString().trim();
                        int post_value=Integer.parseInt(posts);
                        post_value+=1;
                        posts=Integer.toString(post_value);

                        databasereferenceUsers.child("Posts").setValue(posts);

                        postCounter=false;
                    }

                }else{
                    databasereferenceUsers.child("Posts").setValue("1");
                }

                databasereferenceUsers.removeEventListener(this);
            }
            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

}


