package com.jgntic.ivan.dormnews;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;

import com.google.firebase.database.FirebaseDatabase;

/**
 * Created by Ivan on 3/22/2018.
 */

public class FirebasePersistence extends AppCompatActivity {

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        FirebaseDatabase.getInstance().setPersistenceEnabled(true);
    }
}
