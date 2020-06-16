package de.uni_s.ipvs.mcl.assignment3;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.time.LocalDate;

public class MainActivity extends AppCompatActivity {
    final private static String TAG = MainActivity.class.getCanonicalName();
    private EditText mLocationEditText;
    private EditText mTemperatureEditText;
    private Button mSendButton;
    private Button mGetButton;
    private TextView mLocationTextView;
    private TextView mTemperatureTextView;

    private String lastLocation;
    private String lastDate;


    // Firebase instance variables
    private FirebaseDatabase mFirebaseDatabase;
    private DatabaseReference mMessagesDatabaseReference;
    private ValueEventListener mValueEventListener;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Log.i(TAG, "Activity started");


        // Initialize Firebase components
        mFirebaseDatabase = FirebaseDatabase.getInstance();
        mMessagesDatabaseReference = mFirebaseDatabase.getReference().child("location");

        // Initialize references to views
        mSendButton = (Button) findViewById(R.id.sendButton);
        mGetButton = (Button) findViewById(R.id.getButton);
        mLocationEditText = (EditText) findViewById(R.id.locationEditText);
        mTemperatureEditText = (EditText) findViewById(R.id.temperatureEditText);
        mLocationTextView = (TextView) findViewById(R.id.locationTextView);
        mTemperatureTextView = (TextView) findViewById(R.id.temperatureTextView);


        // Enable Send button when there's text to send
        mLocationEditText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                if (charSequence.toString().trim().length() > 0) {
                    mSendButton.setEnabled(true);
                    mGetButton.setEnabled(true);
                } else {
                    mSendButton.setEnabled(false);
                    mGetButton.setEnabled(false);
                }
            }

            @Override
            public void afterTextChanged(Editable editable) {
            }
        });

        // Send button sends a message and clears the EditText
        mSendButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // TODO: Send messages on click

                Integer temperatureInt= Integer.parseInt(mTemperatureEditText.getText().toString());
                lastLocation = mLocationEditText.getText().toString();
                lastDate = LocalDate.now().toString();
                Long timestamp = new Long(System.currentTimeMillis());
                String millis = timestamp.toString();
                Log.i(TAG, lastDate);
                Log.i(TAG, lastLocation);
                DatabaseReference node = mMessagesDatabaseReference.child(lastLocation).child(lastDate);
                //mMessagesDatabaseReference.child(location).push().setValue(temperatureDouble);
                node.child(millis).setValue(temperatureInt)
                        .addOnSuccessListener(new OnSuccessListener<Void>() {
                            @Override
                            public void onSuccess(Void aVoid) {
                                Toast.makeText(MainActivity.this,"Temperature successfully updated", Toast.LENGTH_SHORT).show();
                            }
                        })
                        .addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                Toast.makeText(MainActivity.this,"Temperature update failed", Toast.LENGTH_SHORT).show();
                            }
                        });

                // Clear input box
                mLocationEditText.setText("");
                mTemperatureEditText.setText("0");
            }
        });

        // read from data base once
        mGetButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                lastLocation = mLocationEditText.getText().toString();
                DatabaseReference getNode = mMessagesDatabaseReference.child(lastLocation);
                getNode.addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        DataSnapshot latestDate = dataSnapshot.getChildren().iterator().next();
                        DataSnapshot latestMillis = latestDate.getChildren().iterator().next();
                        Integer latestTemperature = latestMillis.getValue(Integer.class);
                        mLocationTextView.setText(lastLocation);
                        Log.i(TAG, latestTemperature.toString());
                        mTemperatureTextView.setText(latestTemperature.toString());
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });
            }
        });

        // TODO: read from database
/*        mValueEventListener = new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                // Integer getTemperature = dataSnapshot.getValue(Integer.class);

                DatabaseReference node = dataSnapshot.getRef();

                String getLocation = node.getParent().getParent().toString();

                mTemperatureTextView.setText(getTemperature);
                mLocationTextView.setText(getLocation);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        };
        mMessagesDatabaseReference.addValueEventListener(mValueEventListener);*/

    }

    @Override
    protected void onDestroy() {
        Log.i(TAG, "Activity destroyed");
        super.onDestroy();
    }
}
