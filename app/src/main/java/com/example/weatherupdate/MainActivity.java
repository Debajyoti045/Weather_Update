package com.example.weatherupdate;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
public class MainActivity extends AppCompatActivity {
    public class downloadweather extends AsyncTask <String,Void,String>{
        @Override
        protected String doInBackground(String... strings) {
            String result = "";
            URL url;
            HttpURLConnection httpURLConnection = null;
            try {
                url = new URL(strings[0]);
                httpURLConnection = (HttpURLConnection) url.openConnection();
                InputStream in = httpURLConnection.getInputStream();
                InputStreamReader reader = new InputStreamReader(in);
                BufferedReader br = new BufferedReader(reader);
                String initString;
                while((initString=br.readLine())!=null) result+=initString;
                in.close();
                return result;
            } catch (Exception e) {
               e.printStackTrace();
               return "Failed";
            } finally {
                if(httpURLConnection!=null) httpURLConnection.disconnect();
            }
        }

        @Override
        protected void onPostExecute(String s) {
            Log.i("JSON:",s);
            if (s.equals("Failed")) {
                 setWeather.setText("Failed To find!!");
            } else {
                String first = "";
                String second = "";
                try {
                    JSONObject json = new JSONObject(s);
                    JSONArray arr = new JSONArray(json.getString("weather"));
                    JSONObject jsontemp = json.getJSONObject("main");
                    for (int i = 0; i < arr.length(); i++) {
                        JSONObject jsonPart = arr.getJSONObject(i);
                        first += jsonPart.getString("main");
                        second += jsonPart.getString("description");
                    }
                    Double temp = jsontemp.getDouble("temp") - 273.15;
                    String formattedTemperature = String.format("%.1f", temp);
                    String formattedFellsLike = String.format("%.1f",jsontemp.getDouble("feels_like")- 273.15);
                    String Temp_max = String.format("%.1f",jsontemp.getDouble("temp_max")- 273.15);
                    String temp_min = String.format("%.1f",jsontemp.getDouble("temp_min")- 273.15);
                    String Pressure = String.format("%.1f",jsontemp.getDouble("pressure"));
                    String Humidity = String.format("%.1f",jsontemp.getDouble("humidity"));
                    setWeather.setText(first + ": " + second + "\n\r" + "\nTemparature:" + formattedTemperature + "\n\nFeels Like:" +
                            formattedFellsLike + "\n\nMax Temp:" + Temp_max + "\n\nMin Temp:" + temp_min + "\n\nPressure:" + Pressure
                    + "\n\nHumidity:" + Humidity);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
    }
    EditText city;
    TextView setWeather;
    public void getWeather(View view){
        setWeather.setText("");
        downloadweather task = new downloadweather();
        task.execute("https://api.openweathermap.org/data/2.5/weather?q=" + city.getText().toString() + "&appid=47d0631088d8f4060551806daaa3c276");
        InputMethodManager mr = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
        mr.hideSoftInputFromWindow(setWeather.getWindowToken(),0);
    }
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        city = findViewById(R.id.cityEditText);
        setWeather = findViewById(R.id.setTextView);
    }
}
