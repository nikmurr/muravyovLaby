package com.example.myapp3;

import android.content.ContentValues;
import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.os.AsyncTask;
import android.os.Handler;
import android.util.Log;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;

import com.example.myapp3.DatabaseHelper;
import com.google.gson.annotations.SerializedName;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Date;
import java.util.Locale;
import java.util.Objects;
import java.util.TimeZone;

public class ServerPollingManager {

    private boolean isFirstExecution = true;
    private boolean isPollingEnabled = false; // Флаг для управления поллингом
    private Context context;
    private DatabaseHelper dbHelper;
    private final Handler handler = new Handler();
    private final Runnable pollingTask = new Runnable() {
        @Override
        public void run() {
            if (isPollingEnabled) {
                new PollServerTask().execute(); // Выполнение запроса в фоновом потоке
                handler.postDelayed(this, 20000); // Запуск задачи через 20 секунд снова
            }
        }
    };
    private DataUpdateListener listener;
    private StopCatcherCaller listener2;
    private StartCatcherCaller listener3;

    public void setDataUpdateListener(DataUpdateListener listener) {
        this.listener = listener;
    }
    public void setStopCatcherCaller(StopCatcherCaller listener2) {
        this.listener2 = listener2;
    }
    public void setStartCatcherCaller(StartCatcherCaller listener3) {
        this.listener3 = listener3;
    }

    public ServerPollingManager(Context context, DatabaseHelper dbHelper) {
        this.context = context;
        this.dbHelper = dbHelper;
    }

    public void startPolling() {
        isPollingEnabled = true;
        if (listener3 != null) {
            listener3.StartCatcher();
        }
        handler.postDelayed(pollingTask, 0);
    }

    public void stopPolling() {
        if(isPollingEnabled) {
            isPollingEnabled = false; // Устанавливаем флаг поллинга в false для остановки
            handler.removeCallbacks(pollingTask); // Останавливаем задачу
            Toast.makeText(context, "Опрос сервера остановлен...", Toast.LENGTH_SHORT).show();
            if (listener2 != null) {
                listener2.StopCatcher();
            }
        }

    }

    public void resumePolling() {
        isPollingEnabled = true;
        if (listener3 != null) {
            listener3.StartCatcher();
        }
        handler.postDelayed(pollingTask, 0);
    }

    private class PollServerTask extends AsyncTask<Void, Void, String> {

        @Override
        protected String doInBackground(Void... voids) {
            // Выполнение запроса в фоновом потоке
            String response = "";
            try {
                URL url = new URL("https://media.ifmo.ru/api_get_current_song/?login=4707login&password=4707pass.php");
                HttpURLConnection conn = (HttpURLConnection) url.openConnection();
                conn.setReadTimeout(10000);
                conn.setConnectTimeout(15000);
                conn.setRequestMethod("GET");

                int responseCode = conn.getResponseCode();
                if (responseCode == HttpURLConnection.HTTP_OK) {
                    BufferedReader in = new BufferedReader(new InputStreamReader(conn.getInputStream()));
                    StringBuilder stringBuilder = new StringBuilder();
                    String line;
                    while ((line = in.readLine()) != null) {
                        stringBuilder.append(line);
                    }
                    in.close();

                    response = stringBuilder.toString();
                } else {
                    response = "Error";
                    Log.e("Polling", "Unsuccessful request: " + responseCode);
                }
            } catch (IOException e) {
                e.printStackTrace();
                response = "Error";
                Log.e("Polling", "Failed request: " + e.getMessage());
            }
            return response;
        }

        @Override
        protected void onPostExecute(String response) {
            handleServerResponse(response);
        }
    }

    private void handleServerResponse(String responseBody) {
        if (!Objects.equals(responseBody, "Error")) {
            if (isFirstExecution) {
                showFirstExecutionDialog();
                isFirstExecution = false;
            }
            else {
                Toast.makeText(context, "Опрос сервера...", Toast.LENGTH_SHORT).show();
            }

        /*try {
            JSONObject jsonObject = new JSONObject(responseBody);
            String result = jsonObject.optString("result");
            if (result.equals("success")) {
                String info = jsonObject.optString("info");
                writeToDatabase(info);
            } else {
                String errorInfo = jsonObject.optString("info");
                Log.d("Polling", "Error from server: " + errorInfo);
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }*/

            writeToDatabase(RandomTextGenerator.generateRandomText());
        } else {
            Toast.makeText(context, "Ошибка соединения. Подключение приостановлено.", Toast.LENGTH_SHORT).show();
            stopPolling();
        }
    }

    private void writeToDatabase(String trackInfo) {
        SQLiteDatabase db = dbHelper.getWritableDatabase();

        String[] parts = trackInfo.split(" – ");
        String artist = parts[0];
        String trackTitle = parts[1];
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault());
        dateFormat.setTimeZone(TimeZone.getTimeZone("Europe/Moscow"));
        String currentTime = dateFormat.format(new Date());

        ContentValues values = new ContentValues();
        values.put(DatabaseHelper.COLUMN_ARTIST, artist);
        values.put(DatabaseHelper.COLUMN_TRACK_TITLE, trackTitle);
        values.put(DatabaseHelper.COLUMN_ENTRY_TIME, currentTime);

        long newRowId = db.insert(DatabaseHelper.TABLE_SONGS, null, values);

        if (newRowId != -1) {
            Log.d("Database", "New row inserted with ID: " + newRowId);
            if (listener != null) {
                listener.onDataUpdated();
            }
        } else {
            Log.e("Database", "Error inserting new row");
        }

        db.close();
    }

    private void showFirstExecutionDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setTitle("Сервер косячит!");
        builder.setMessage("Т.к. текст возвращает не корректный JSON-файл, а какую-то билиберду, " +
                "которая не преобразовывается, дальше используются фейковые значения. " +
                "Если вдруг окажется, что эт окак-то можно преобразовать, я перепишу код. Но пока так");

        builder.setPositiveButton("OK((", (dialog, which) -> dialog.dismiss());

        AlertDialog alertDialog = builder.create();
        alertDialog.show();
    }

}
