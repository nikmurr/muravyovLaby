package com.example.course_work;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.content.DialogInterface;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import com.google.android.material.bottomnavigation.BottomNavigationView;

import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

import android.view.LayoutInflater;
import android.widget.TextView;

public class MainActivity3 extends AppCompatActivity {

    GameDatabaseHelper dbHelper;
    GameStatAdapter adapter;
    List<GameStatModel> gameStats;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main3);
        RecyclerView recyclerView = findViewById(R.id.recyclerView);
        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(layoutManager);

        dbHelper = new GameDatabaseHelper(this);
        gameStats = dbHelper.getAllGameStats();
        adapter =  new GameStatAdapter(this, gameStats);
        recyclerView.setAdapter(adapter);
        adapter.notifyDataSetChanged();

        BottomNavigationView bottomNavigationView = findViewById(R.id.bottomNavigationView1);
        BottomNavItemSelectedListener listener = new BottomNavItemSelectedListener(this);
        bottomNavigationView.setOnNavigationItemSelectedListener(listener);

        Button buttonUpdate = findViewById(R.id.buttonUpdate);
        Button buttonDestroy = findViewById(R.id.buttonDestroy);

        buttonUpdate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                UpdateRecyclerView();
            }
        });

        buttonDestroy.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showConfirmationDialog();
            }
        });
    }

    private void DestroyDataBaseData() {
        if (dbHelper != null) {
            dbHelper.deleteAllGameStats();
            UpdateRecyclerView();
        }
    }

    private void UpdateRecyclerView() {
        if (adapter != null) {
            gameStats = dbHelper.getAllGameStats();
            adapter.setGameStatsList(gameStats);
        }
    }

    private void showConfirmationDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this, R.style.MyAlertDialogStyle);
        LayoutInflater inflater = this.getLayoutInflater();
        View view = inflater.inflate(R.layout.alert_dialog_text, null);
        TextView textView = view.findViewById(R.id.textView);
        textView.setText("Все данные о прошлых партиях будут удалены без возможности восстановления.");
        builder.setTitle("Очистка статистики")
                .setView(view)
                .setPositiveButton("Очистить", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        DestroyDataBaseData();
                    }
                })
                .setNegativeButton("Отмена", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        dialog.cancel();
                    }
                });

        AlertDialog alertDialog = builder.create();
        alertDialog.show();

        Button negativeButton = alertDialog.getButton(DialogInterface.BUTTON_NEGATIVE);
        negativeButton.setTextColor(ContextCompat.getColor(this, R.color.base_white));
        Button positiveButton = alertDialog.getButton(DialogInterface.BUTTON_POSITIVE);
        positiveButton.setTextColor(ContextCompat.getColor(this, R.color.extra_pink));
    }
}