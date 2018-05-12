package com.example.vikrant.mausam;

import android.content.Context;
import android.inputmethodservice.InputMethodService;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLEncoder;

public class MainActivity extends AppCompatActivity {
    TextView weatherInfo;
    EditText cityName;
    Button searchButton;

    public void findWeather(View view) {

        InputMethodManager mgr = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
        mgr.hideSoftInputFromWindow(cityName.getWindowToken(), 0);

        try {
            String city = URLEncoder.encode(cityName.getText().toString(), "UTF-8");
            Downloader task = new Downloader();
            task.execute("http://api.openweathermap.org/data/2.5/weather?q=" + city
                    + "&appid=5a97ba54194d184469d499d34b22a030");
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
            Toast.makeText(getApplicationContext(), "Weather UnAvailable", Toast.LENGTH_LONG).show();

        }


    }

    public class Downloader extends AsyncTask<String, Void, String> {

        @Override
        protected String doInBackground(String... strings) {
            String result = "";
            try {
                URL url = new URL(strings[0]);
                HttpURLConnection connection = (HttpURLConnection) url.openConnection();
                connection.connect();
                InputStream is = connection.getInputStream();

                InputStreamReader reader = new InputStreamReader(is);

                int data = reader.read();
                while (data != -1) {
                    char ch = (char) data;
                    result += ch;
                    data = reader.read();
                }

                return result;

            } catch (Exception e) {
                return null;
            }

        }

        protected void onPostExecute(String s) {

            super.onPostExecute(s);

            try {
                String message = "";
                JSONObject jsonObject = new JSONObject(s);


                String weather = jsonObject.getString("weather");
                JSONObject maindata = new JSONObject(jsonObject.getString("main"));
                String temperature = maindata.getString("temp");
                double temp = Double.parseDouble(temperature) - 273;
                int x = (int) temp;
                message += "Temperature(in Celsius)= " + String.valueOf(x) + "\n";

                JSONArray arr = new JSONArray(weather);

                for (int i = 0; i < arr.length(); i++) {
                    JSONObject jsonObject1 = arr.getJSONObject(i);

                    String main = "";
                    String desc = "";
                    main = jsonObject1.getString("main");
                    desc = jsonObject1.getString("description");

                    if (main != "" && desc != "") {
                        message += main + ": " + desc + "\n";
                    }

                    if (message != "") {
                        weatherInfo.setText(message);
                    } else {
                        Toast.makeText(getApplicationContext(), "Weather UnAvailable", Toast.LENGTH_LONG).show();
                    }
                }

            } catch (Exception e) {
                Toast.makeText(getApplicationContext(), "Weather UnAvailable", Toast.LENGTH_LONG).show();
            }

        }
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        cityName = (EditText) findViewById(R.id.editText);
        searchButton = (Button) findViewById(R.id.searchButton);
        weatherInfo = (TextView) findViewById(R.id.weatherEditText);

    }
}
