package com.example.myapp3;

import android.Manifest;
import android.content.Context;
import android.content.DialogInterface;
import android.content.pm.PackageManager;
import android.database.sqlite.SQLiteDatabase;
import android.net.ConnectivityManager;
import android.net.NetworkCapabilities;
import android.net.NetworkInfo;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.io.File;
import java.util.List;

public class MainActivity extends AppCompatActivity implements DataUpdateListener, StopCatcherCaller, StartCatcherCaller {
    public ServerPollingManager serverPollingManager;
    public MyAdapter adapter;
    private DatabaseHelper dbHelper;
    public List<MyDataModel> dataList;
    public Button buttonClick;
    public Button buttonStop;
    public boolean isServerPollingRunning = false;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        dbHelper = new DatabaseHelper(this);
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        dbHelper.ClearDB(db);
        dataList = dbHelper.getAllData();
        serverPollingManager = new ServerPollingManager(this, dbHelper);
        RecyclerView recyclerView = findViewById(R.id.recycler_view);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        serverPollingManager.setDataUpdateListener(this);
        serverPollingManager.setStopCatcherCaller(this);
        serverPollingManager.setStartCatcherCaller(this);

        buttonClick = findViewById(R.id.button);
        buttonStop = findViewById(R.id.button2);
        buttonStop.setEnabled(false);
        buttonStop.setAlpha(0.5F);


        adapter = new MyAdapter(dataList); // Передайте список данных сюда
        recyclerView.setAdapter(adapter);


        buttonClick.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                checkInternetConnection();
            }
        });

        buttonStop.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    serverPollingManager.stopPolling();
                } catch (Exception e) {
                    Log.e("Error", "При нажатии на кнопку СТОП произошла ошибка");
                    throw new RuntimeException(e);
                }
            }
        });
    }

    @Override
    public void onDataUpdated() {
        dataList = dbHelper.getAllData();
        adapter = new MyAdapter(dataList); // Передайте список данных сюда
        RecyclerView recyclerView = findViewById(R.id.recycler_view);
        recyclerView.setAdapter(adapter);
        Log.e("DataUpdated", "Success");
    }
    @Override
    public void StopCatcher() {
        ButtonStateManager(buttonClick, buttonStop);
    }
    @Override
    public void StartCatcher() {
        ButtonStateManager(buttonStop, buttonClick);
    }

    private void checkInternetConnection() {
        ConnectivityManager connectivityManager = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        if (connectivityManager != null) {
            NetworkInfo activeNetwork = connectivityManager.getActiveNetworkInfo();
            boolean isConnected = activeNetwork != null && activeNetwork.isConnectedOrConnecting();

            if (!isConnected) {
                showNoInternetDialog();
                isServerPollingRunning = false;
            }
            else {
                Toast.makeText(this, "Подключение установлено", Toast.LENGTH_SHORT).show();
                isServerPollingRunning = true;
                serverPollingManager.startPolling();
            }
        }
    }

    private void showNoInternetDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Отсутствует подключение к Интернету")
                .setMessage("Для работы приложения необходим доступ к Интернету.")
                .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        // Кнопка "OK" нажата
                        dialog.dismiss(); // Закрыть диалоговое окно
                    }
                })
                .setCancelable(false); // Делаем окно невызываемым кнопкой "Назад"

        AlertDialog dialog = builder.create();
        dialog.show();
    }

    public void ButtonStateManager (Button buttonToOn, Button buttonToOff) {
        if (buttonToOn != null && buttonToOff != null) {
            buttonToOff.setEnabled(false);
            buttonToOff.setAlpha(0.5F);
            buttonToOn.setEnabled(true);
            buttonToOn.setAlpha(1F);
        }
    }
}

