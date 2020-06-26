package com.example.covid_19helpme;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {

    EditText name;
    DatabaseReference dref;
    User newUser;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        name= findViewById(R.id.editText);
        dref = FirebaseDatabase.getInstance().getReference("Users");
    }

    public void next(View view){
        name = findViewById(R.id.editText);
        String user_name = name.getText().toString();
        String id = dref.push().getKey();
        newUser = new User(user_name,id);

        Log.i("Data inputted","Hi,"+user_name);

        Intent intent = new Intent(getApplicationContext(), MapsActivity.class);

        intent.putExtra("name", newUser);
        startActivity(intent);
    }
}
