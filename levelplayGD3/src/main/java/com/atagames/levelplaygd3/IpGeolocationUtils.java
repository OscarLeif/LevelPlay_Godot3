package com.atagames.levelplaygd3;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;

import org.json.JSONObject;

public class IpGeolocationUtils {

    //https://ipapi.co/json
    private static final String GEOLOCATION_API_URL = "https://ipapi.co/json";
    private static final ExecutorService executor = Executors.newSingleThreadExecutor();
    private static final Handler handler = new Handler(Looper.getMainLooper());

    public static void getCountryFromIP(Callback callback) {
        executor.execute(() -> {
            String country = null;
            try {
                URL url = new URL(GEOLOCATION_API_URL);
                HttpURLConnection connection = (HttpURLConnection) url.openConnection();
                connection.setRequestMethod("GET");
                connection.setConnectTimeout(5000);
                connection.setReadTimeout(5000);

                BufferedReader in = new BufferedReader(new InputStreamReader(connection.getInputStream()));
                StringBuilder response = new StringBuilder();
                String inputLine;

                while ((inputLine = in.readLine()) != null) {
                    response.append(inputLine);
                }
                in.close();

                JSONObject jsonResponse = new JSONObject(response.toString());
                country = jsonResponse.getString("country_code");

            } catch (Exception e) {
                Log.e("IpGeolocationUtils", "Error fetching IP geolocation: " + e.getMessage(), e);
            }

            String finalCountry = country;
            handler.post(() -> {
                if (callback != null) {
                    callback.onResult(finalCountry);
                }
            });
        });
    }

    public interface Callback {
        void onResult(String country);
    }
}