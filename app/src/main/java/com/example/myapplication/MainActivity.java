package com.example.myapplication;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatButton;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;

import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class MainActivity extends AppCompatActivity {

    public static final String TAG = "MyApplication";
    DatabaseReference reference;
    FirebaseDatabase database;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        setButton();
        setFirebase();
    }

    private void setFirebase() {
        database = FirebaseDatabase.getInstance();
        reference = database.getReference();

        reference.addValueEventListener(new ValueEventListener() {

            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                User value = dataSnapshot.child("User").getValue(User.class);
                if(value!=null){
                    TextInputLayout view1 = findViewById(R.id.name);
                    view1.getEditText().setText(value.name);
                    TextInputLayout view2 = findViewById(R.id.email);
                    view2.getEditText().setText(value.email);
                }
                Log.d(TAG, "Value is: " + value.name + " " + value.email);
            }

            @Override
            public void onCancelled(DatabaseError error) {
                // Failed to read value
                Log.w(TAG, "Failed to read value.", error.toException());
            }
        });
    }

    private void setButton() {
        AppCompatButton button = findViewById(R.id.button);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                TextInputLayout viewName = findViewById(R.id.name);
                String name = String.valueOf(viewName.getEditText().getText());
                TextInputLayout viewEmail = findViewById(R.id.email);
                String email = String.valueOf(viewEmail.getEditText().getText());
                reference.child("User").setValue(new User(name,email));
                Intent intent = new Intent(MainActivity.this, CameraActivity.class);
                MainActivity.this.startActivity(intent);
            }
        });
    }
}

