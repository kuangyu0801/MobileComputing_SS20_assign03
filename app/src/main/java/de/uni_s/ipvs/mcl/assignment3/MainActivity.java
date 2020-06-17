package de.uni_s.ipvs.mcl.assignment3;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
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
import com.google.firebase.database.ChildEventListener;
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
    private Button mSubscribeButton;
    private Button mAverageButton;
    private TextView mLocationTextView;
    private TextView mTemperatureTextView;
    private TextView mLastUpdateTimeTextView;
    private TextView mSubsLocationTextView;
    private TextView mSubsTemperatureTextView;
    private TextView mAverageTemperatureTextView;

    private String lastLocation;
    private String lastDate;


    // Firebase instance variables
    private FirebaseDatabase mFirebaseDatabase;
    private DatabaseReference mTemperatureDatabaseReference;
    private DatabaseReference mSubscribeTemperatureDatabaseReference;
    private ChildEventListener mChildEventListener;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Log.i(TAG, "Activity started");


        // Initialize Firebase components
        mFirebaseDatabase = FirebaseDatabase.getInstance();
        mTemperatureDatabaseReference = mFirebaseDatabase.getReference().child("location");

        // Initialize references to views
        mSendButton = (Button) findViewById(R.id.sendButton);
        mGetButton = (Button) findViewById(R.id.getButton);
        mSubscribeButton = (Button) findViewById(R.id.subscribeButton);
        mAverageButton = (Button) findViewById(R.id.getAverageButton);
        mLocationEditText = (EditText) findViewById(R.id.locationEditText);
        mTemperatureEditText = (EditText) findViewById(R.id.temperatureEditText);
        mLocationTextView = (TextView) findViewById(R.id.locationTextView);
        mTemperatureTextView = (TextView) findViewById(R.id.temperatureTextView);
        mLastUpdateTimeTextView = (TextView) findViewById(R.id.lastUpdateTextView);
        mSubsLocationTextView = (TextView) findViewById(R.id.subLocationTextView);
        mSubsTemperatureTextView = (TextView) findViewById(R.id.subsTempTextView);
        mAverageTemperatureTextView = (TextView) findViewById(R.id.avgTextView);

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
                    mAverageButton.setEnabled(true);
                    mSubscribeButton.setEnabled(true);
                } else {
                    mSendButton.setEnabled(false);
                    mGetButton.setEnabled(false);
                    mAverageButton.setEnabled(false);
                    mSubscribeButton.setEnabled(false);
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
                DatabaseReference getNode = mTemperatureDatabaseReference.child(lastLocation);
                Log.i(TAG, "Get button clicked");
                // read from data base only once
                getNode.limitToLast(1).addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        DataSnapshot latestDate = dataSnapshot.getChildren().iterator().next();
                        Log.i(TAG, "Date count:" + Long.toString(dataSnapshot.getChildrenCount()));

                        if (latestDate != null) {
                            Iterator<DataSnapshot> millisIterator = latestDate.getChildren().iterator();
                            DataSnapshot latestMillis = null;
                            while (millisIterator.hasNext()) {
                                latestMillis = millisIterator.next();
                                Log.i(TAG, "iterating on Millis:" + latestMillis.getKey() );
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
                    public void onCancelled(@NonNull DatabaseError databaseError) {
                    }
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
                // DONE: Send messages on click
                Integer temperatureInt = Integer.parseInt(mTemperatureEditText.getText().toString());
                lastLocation = mLocationEditText.getText().toString();
                lastDate = LocalDate.now().toString();
                Long timestamp = new Long(System.currentTimeMillis());
                String millis = timestamp.toString();
                Log.i(TAG, "send clicked: " + lastDate + "," + lastLocation + "," + millis);
                DatabaseReference node = mTemperatureDatabaseReference.child(lastLocation).child(lastDate);
                node.child(millis).setValue(temperatureInt)
                        .addOnSuccessListener(new OnSuccessListener<Void>() {
                            @Override
                            public void onSuccess(Void aVoid) {
                                Toast.makeText(MainActivity.this, "Temperature successfully updated", Toast.LENGTH_SHORT).show();
                            }
                        })
                        .addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                Toast.makeText(MainActivity.this, "Temperature update failed", Toast.LENGTH_SHORT).show();
                            }
                        });

                // Clear input box
                mLocationEditText.setText("");
                mTemperatureEditText.setText("0");
            }
        });

        // DONE: Task 2.1
        /**
         * Continuously display the latest temperature value of a city
         * */

        mSubscribeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                lastLocation = mLocationEditText.getText().toString();
                mSubscribeTemperatureDatabaseReference = mTemperatureDatabaseReference.child(lastLocation);
                Log.i(TAG, "subscribed button clicked");
                mChildEventListener = new ChildEventListener() {

                    @Override
                    public void onChildAdded(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
                        Log.i(TAG, "child added");
                        Iterator<DataSnapshot> millisIterator = dataSnapshot.getChildren().iterator();
                        DataSnapshot latestMillis = null;
                        while (millisIterator.hasNext()) {
                            latestMillis = millisIterator.next();
                            Log.i(TAG, "iterating on Millis:" + latestMillis.getKey() );
                        }
                        if (latestMillis != null) {
                            Integer latestTemperature = latestMillis.getValue(Integer.class);
                            String latestTime = millisTimeConvert(latestMillis.getKey());
                            mSubsLocationTextView.setText(lastLocation);
                            mSubsTemperatureTextView.setText(latestTemperature.toString()+ "\n" + latestTime);
                        } else {
                            Log.i(TAG, "latestMillis is null");
                        }
                    }

                    @Override
                    public void onChildChanged(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {

                        Log.i(TAG, "child changed");
                        Iterator<DataSnapshot> millisIterator = dataSnapshot.getChildren().iterator();
                        DataSnapshot latestMillis = null;
                        while (millisIterator.hasNext()) {
                            latestMillis = millisIterator.next();
                            Log.i(TAG, "iterating on Millis:" + latestMillis.getKey() );
                        }
                        if (latestMillis != null) {
                            Integer latestTemperature = latestMillis.getValue(Integer.class);
                            String latestTime = millisTimeConvert(latestMillis.getKey());
                            mSubsLocationTextView.setText(lastLocation);
                            mSubsTemperatureTextView.setText(latestTemperature.toString()+ "\n" + latestTime);
                        } else {
                            Log.i(TAG, "latestMillis is null");
                        }
                    }

                    @Override
                    public void onChildRemoved(@NonNull DataSnapshot dataSnapshot) {}

                    @Override
                    public void onChildMoved(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {}

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {}
                };

                mSubscribeTemperatureDatabaseReference.addChildEventListener(mChildEventListener);
            }
        });

        // DONE: Task 2.2
        /**
         * Show todays average from one location
         * */
        mAverageButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                lastLocation = mLocationEditText.getText().toString();
                DatabaseReference getNode = mTemperatureDatabaseReference.child(lastLocation);
                Log.i(TAG, "Get Average");
                // read from data base only once
                getNode.addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                        DataSnapshot latestDate = null;
                        Iterator<DataSnapshot> dateIterator = dataSnapshot.getChildren().iterator();
                        while(dateIterator.hasNext()) {
                            latestDate = dateIterator.next();
                            Log.i(TAG, "iterating date: " + latestDate.getKey());
                        }
                        if (latestDate != null) {
                            Log.i(TAG, "latest date: " + latestDate.getKey());
                            Iterator<DataSnapshot> millisIterator = latestDate.getChildren().iterator();
                            DataSnapshot latestMillis = null;
                            long dataCount = latestDate.getChildrenCount();
                            long temperatureSum = 0;
                            while (millisIterator.hasNext()) {
                                latestMillis = millisIterator.next();
                                Integer latestTemperature = latestMillis.getValue(Integer.class);
                                temperatureSum += latestTemperature;
                            }
                            Long averageTemperature = temperatureSum / dataCount;
                            mAverageTemperatureTextView.setText(new String(averageTemperature.toString() + "\non " + latestDate.getKey()));
                        }
                    }
                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {
                    }
                });
            }
        });
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
