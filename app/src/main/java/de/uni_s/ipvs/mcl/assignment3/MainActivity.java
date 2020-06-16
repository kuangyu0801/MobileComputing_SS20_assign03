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
import java.util.Date;
import java.util.Iterator;

public class MainActivity extends AppCompatActivity {
    final private static String TAG = MainActivity.class.getCanonicalName();
    private EditText mLocationEditText;
    private EditText mTemperatureEditText;
    private Button mSendButton;
    private Button mGetButton;
    private Button mPrevButton;
    private TextView mLocationTextView;
    private TextView mTemperatureTextView;
    private TextView mLastUpdateTimeTextView;

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
        mPrevButton = (Button) findViewById(R.id.prevButton);
        mLocationEditText = (EditText) findViewById(R.id.locationEditText);
        mTemperatureEditText = (EditText) findViewById(R.id.temperatureEditText);
        mLocationTextView = (TextView) findViewById(R.id.locationTextView);
        mTemperatureTextView = (TextView) findViewById(R.id.temperatureTextView);
        mLastUpdateTimeTextView = (TextView) findViewById(R.id.lastUpdateTextView);

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

        // DONE: Task1.2
        /**
         * Read Previous value from one city, identified by the city name
         * */
        mGetButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                lastLocation = mLocationEditText.getText().toString();
                DatabaseReference getNode = mMessagesDatabaseReference.child(lastLocation);

                // read from data base only once
                getNode.addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        DataSnapshot latestDate = dataSnapshot.getChildren().iterator().next();
                        if (latestDate != null) {
                            Iterator<DataSnapshot> millisIterator = latestDate.getChildren().iterator();
                            DataSnapshot latestMillis = null;
                            while (millisIterator.hasNext()) {
                                latestMillis = millisIterator.next();
                            }
                            if (latestMillis != null) {
                                String latestTime = millisTimeConvert(latestMillis.getKey());
                                Integer latestTemperature = latestMillis.getValue(Integer.class);
                                mLocationTextView.setText(lastLocation);
                                mTemperatureTextView.setText(latestTemperature.toString());
                                mLastUpdateTimeTextView.setText(latestTime);
                            }
                        }
                    }
                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {}
                });
            }
        });

        // DONE: Task1.1
        /**
         * Add the new value to the database
         * */
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

        // TODO: Task 2.1
        /**
        * Continuously display the latest temperature value of a city
         * */

        // TODO: Task 2.2
        /**
         * Show todays average from one location
         * */

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

    private String millisTimeConvert(String millisStr) {
        Long milliseconds = Long.parseLong(millisStr);
        Date date = new Date(milliseconds);
        return date.toString();
    }
}
