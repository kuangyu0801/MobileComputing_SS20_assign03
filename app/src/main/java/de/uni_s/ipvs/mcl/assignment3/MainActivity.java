package de.uni_s.ipvs.mcl.assignment3;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.Date;

public class MainActivity extends AppCompatActivity {

    final private static String TAG = MainActivity.class.getCanonicalName();
    private static String PATH =  "http://api.openweathermap.org/data/2.5/weather?q=";
    String APIKEY = "1e83ffabaf397f346a91ccd92b796694";
    int TEAM_NUMBER = 8;

    TextView tv_city, tv_temp;
    EditText edt_writeCity, edt_readCity;
    Button bt_write, bt_read;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        tv_city = (TextView) findViewById(R.id.tv_city);
        tv_temp = (TextView) findViewById(R.id.tv_temperature);
        edt_writeCity = (EditText) findViewById(R.id.edt_write_city);
        edt_readCity = (EditText) findViewById(R.id.edt_read_city);
        bt_write = (Button) findViewById(R.id.bt_writeToDatabase);
        bt_read = (Button) findViewById(R.id.bt_readFromDatabase);

        bt_write.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String writeCity = edt_writeCity.getText().toString();
                MyTask task = new MyTask(); // 创建AsyncTask实例
                task.execute(new String[]{writeCity, APIKEY}); // 触发AsyncTask。This method must be invoked on the UI thread.
            }
        });

        bt_read.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String readCity = edt_readCity.getText().toString();
                readFromDatabase(readCity);
            }
        });

    }

    //Reference: https://www.spaceotechnologies.com/implement-openweathermap-api-android-app-tutorial/
    private class MyTask extends AsyncTask <String, Void, String[]>{
        @Override
        protected String[] doInBackground(String...params) {
            Log.i(TAG, "doInBackground");
            int temperature = 0;
            String weather_data = getCurrentWeatherData(params[0],params[1]); // get weather JSON file
            try {
                temperature = (int) Math.round(getTemperature(weather_data) - 273.15); // extract temperature value
                params[0] = getCity(weather_data);
            } catch (JSONException e) {
                e.printStackTrace();
            }
            String[] result = {params[0],Integer.toString(temperature)};
            return result;
        }

        @SuppressLint("SetTextI18n")
        @Override
        protected void onPostExecute(String[] result) {
            Log.i(TAG, "onPostExecute");
            writeToDatabase(result[0],result[1]);
        }

    }


    public String getCurrentWeatherData(String city, String APIKey)  {
        Log.i(TAG, "get current weather data from openweathermap");
        HttpURLConnection connection = null ;
        InputStream inputstream = null;
        try {
            URL url = new URL(PATH + city +"&appid="+ APIKey);
            connection = (HttpURLConnection) url.openConnection();
            connection.setRequestMethod("GET");
            connection.setDoInput(true);
            connection.setDoOutput(true);
            connection.connect();

            StringBuffer current_weather_data = new StringBuffer();
            inputstream = connection.getInputStream();
            BufferedReader br = new BufferedReader(new InputStreamReader(inputstream));
            String line = null;
            while (  (line = br.readLine()) != null ) {
                current_weather_data.append(line + "\r\n");
            }

            inputstream.close();
            connection.disconnect();
            return current_weather_data.toString();

        } catch (Throwable t) {
            t.printStackTrace();
        } finally {
            try { inputstream.close(); } catch(Throwable t) {}
            try { connection.disconnect(); } catch(Throwable t) {}
        }
        return null;
    }

    // parse weather data in JSON format and extract temperature value
    // "main":{"temp":280.32,"pressure":1012,"humidity":81,"temp_min":279.15,"temp_max":281.15}
    public Double getTemperature(String weather_data) throws JSONException {
        Log.i(TAG, "extract temperature value from JSON file");
        //create a JSON object from weather data
        JSONObject obj = new JSONObject(weather_data);
        JSONObject subObj = obj.getJSONObject("main");
        Double temp_value = subObj.getDouble("temp");
        return temp_value;
    }

    public String getCity(String weather_data) throws JSONException {
        Log.i(TAG, "extract city name from JSON file");
        JSONObject obj = new JSONObject(weather_data);
        String city = obj.getString("name");
        return city;
    }


    public void writeToDatabase(String city, String temperature){
        Log.i(TAG,"write to realtime database");
        tv_city.setText("write city:   "+ city);
        tv_temp.setText("write temperature:   "+ temperature);
        DatabaseReference myRef = FirebaseDatabase.getInstance().getReference("teams/"+TEAM_NUMBER);
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        String date = sdf.format(new Date());
        myRef.child("location/"+city+"/"+date).child(""+System.currentTimeMillis()).setValue(Integer.valueOf(temperature));
    }

    public void readFromDatabase(String readCity){
        Log.i(TAG,"read from realtime database");
        DatabaseReference myRef = FirebaseDatabase.getInstance().getReference("/teams/8/location/"+readCity);
        myRef.orderByValue().limitToLast(1).addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
                Log.i(TAG,"The key is:"+ dataSnapshot.getKey()+"\nThe Value is:"+dataSnapshot.getValue());
                long bigMillis = 0;
                int latestTemp = 0;
                for(DataSnapshot dayTemp : dataSnapshot.getChildren()){
                    String keyMillis = dayTemp.getKey();
                    Long currentMillis = Long.parseLong(keyMillis);
                    Log.i(TAG, "Integer Millis is:"+currentMillis);
                    if (currentMillis>bigMillis){
                        bigMillis = currentMillis;
                        latestTemp = dayTemp.getValue(Integer.class);
                    }
                }
                Log.i(TAG,"the last update is:"+bigMillis+"\nthe temperature is:"+latestTemp);
                SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                String date = sdf.format(new Date(bigMillis));
                tv_city.setText("read time:   "+ date);
                tv_temp.setText("read temperature:   "+ latestTemp);

            }

            @Override
            public void onChildChanged(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {

            }

            @Override
            public void onChildRemoved(@NonNull DataSnapshot dataSnapshot) {

            }

            @Override
            public void onChildMoved(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    @Override
    protected void onDestroy() {
        Log.i(TAG, "onDestroy");
        super.onDestroy();
    }

}
