package com.example.weatherapp;

import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.content.Context;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.InetSocketAddress;
import java.net.MalformedURLException;
import java.net.Socket;
import java.net.SocketAddress;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;
import java.util.TimeZone;

public class MainActivity extends AppCompatActivity {

    private EditText user_field;
    private Button main_button;
    private TextView today;

    private TextView temperature;
    private TextView feels_like;
    private TextView humidity;
    private TextView pressure;
    private TextView wind_speed;

    private View borders[];// Разделители между полей для вывода информации

    public boolean isOnline() {
        try {
            int timeoutMs = 1500;
            Socket sock = new Socket();
            SocketAddress sockaddr = new InetSocketAddress("8.8.8.8", 53);

            sock.connect(sockaddr, timeoutMs);
            sock.close();

            return true;
        } catch (IOException e){
            return false;
        }
    }

    @SuppressLint("SetTextI18n")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        user_field = findViewById(R.id.user_field);
        main_button = findViewById(R.id.main_button);
        today = findViewById(R.id.today);

        temperature = findViewById(R.id.temperature);
        feels_like = findViewById(R.id.feels_like);
        humidity = findViewById(R.id.humidity);
        pressure = findViewById(R.id.pressure);
        wind_speed = findViewById(R.id.wind_speed);

        borders = new View[5];
        borders[0]=findViewById(R.id.border1);
        borders[1]=findViewById(R.id.border2);
        borders[2]=findViewById(R.id.border3);
        borders[3]=findViewById(R.id.border4);
        borders[4]=findViewById(R.id.border5);

        // Выставление текущего дня недели и даты
        Calendar calendar = Calendar.getInstance(TimeZone.getDefault());
        Date date = calendar.getTime();
        today.setText(new SimpleDateFormat("EEEE", new Locale("ru")).format(date.getTime()).substring(0,1).toUpperCase()+
                new SimpleDateFormat("EEEE", new Locale("ru")).format(date.getTime()).substring(1)+", "+
                String.format(new Locale("ru"),"%1$te %1$tB", date));

        main_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(user_field.getText().toString().trim().equals(""))
                    Toast.makeText(MainActivity.this, R.string.No_user_input, Toast.LENGTH_LONG).show();// Всплывающее 3х-секундное сообщение "Введите текст"
                else
                {
                    String city = user_field.getText().toString();
                    String key = "4941241ed8b1fe54a6c246a56c4938e9"; // API ключ для доступа к сранице с данными
                    String url = "https://api.openweathermap.org/data/2.5/weather?q="+city+"&appid="+key+"&units=metric&lang=ru";

                    new GetURLData().execute(url);
                    // Check if no view has focus
                    // Скрытие клавиатуры после ввода информации и нажатия на кнопку
                    if (view != null) {
                        InputMethodManager imm = (InputMethodManager)getSystemService(Context.INPUT_METHOD_SERVICE);
                        imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
                    }
                }
            }
        });
    }

    private class GetURLData extends AsyncTask<String, String, String >
    {
        protected void onPreExecute(){// Блок выполняется перед доступом к сайту и получением информации
            super.onPreExecute();

            for (View itVar:borders)
                itVar.setVisibility(View.INVISIBLE);

            temperature.setText("");
            feels_like.setText("");
            humidity.setText("");
            pressure.setText("");
            wind_speed.setText("");

            temperature.setText("Ожидание...");
        }

        @SuppressLint("SetTextI18n")
        protected void onPostExecute(String result){// Блок выполняется после доступа к сайту и получения информации
            super.onPostExecute(result);

            if(result.equals("Не удалось установить соединение с сервером.") ||
                    result.equals("Город не найден.")) {
                temperature.setText(result);
                return;
            }
            try {
                for (View itVar:borders)
                    itVar.setVisibility(View.VISIBLE);

                JSONObject jsonObject =new JSONObject(result);
                temperature.setText("Температура "+ jsonObject.getJSONObject("main").getDouble("temp")+" ℃");
                feels_like.setText("Ощущается как "+jsonObject.getJSONObject("main").getDouble("feels_like")+" ℃");
                humidity.setText("Влажность "+jsonObject.getJSONObject("main").getDouble("humidity")+" %");
                pressure.setText("Давление "+jsonObject.getJSONObject("main").getDouble("pressure")+" мм.рт.ст.");
                wind_speed.setText("Ветер "+jsonObject.getJSONObject("wind").getDouble("speed")+" м/с");
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }

        @Override
        protected String doInBackground(String... strings) {
            HttpURLConnection connection =null;
            BufferedReader reader = null;

            try {
                if(!isOnline())
                    return "Не удалось установить соединение с сервером.";

                URL url = new URL(strings[0]);
                connection=(HttpURLConnection)url.openConnection();
                connection.connect();

                InputStream stream = connection.getInputStream();
                reader = new BufferedReader(new InputStreamReader(stream));

                StringBuffer buffer = new StringBuffer();

                String line = "";

                while((line = reader.readLine())!=null)
                    buffer.append(line).append("\n");

                return buffer.toString();

            } catch (MalformedURLException e) {
                e.printStackTrace();
            }
            catch (IOException e) {
                e.printStackTrace();
                return "Город не найден.";
            }
            finally
            {
                if(connection!=null)
                    connection.disconnect();
                try
                {
                    if(reader != null)
                        reader.close();
                }
                catch (IOException e)
                {
                    e.printStackTrace();
                }
            }
            return null;
        }
    }
}