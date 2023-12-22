package com.example.myapp3;

import android.app.AlertDialog;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import com.example.myapp3.DatabaseHelper;

public class DatabaseViewer {

    private Context context;
    private DatabaseHelper dbHelper;

    public DatabaseViewer(Context context, DatabaseHelper dbHelper) {
        this.context = context;
        this.dbHelper = dbHelper;
    }

    public void showDatabaseContent() {
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        Cursor cursor = null;
        try {
            cursor = db.query(DatabaseHelper.TABLE_SONGS,
                    null,
                    null,
                    null,
                    null,
                    null,
                    null);

            StringBuilder data = new StringBuilder(); // Создаем экземпляр StringBuilder для накопления строк

            if (cursor != null && cursor.moveToFirst()) {
                int idIndex = cursor.getColumnIndex(DatabaseHelper.COLUMN_ID);
                int artistIndex = cursor.getColumnIndex(DatabaseHelper.COLUMN_ARTIST);
                int titleIndex = cursor.getColumnIndex(DatabaseHelper.COLUMN_TRACK_TITLE);
                int entryTimeIndex = cursor.getColumnIndex(DatabaseHelper.COLUMN_ENTRY_TIME);

                if (idIndex >= 0 && artistIndex >= 0 && titleIndex >= 0 && entryTimeIndex >= 0) {
                    do {
                        int id = cursor.getInt(idIndex);
                        String artist = cursor.getString(artistIndex);
                        String title = cursor.getString(titleIndex);
                        String entryTime = cursor.getString(entryTimeIndex);

                        // Формирование строк для каждой записи в БД
                        String row = id + " | " + title + " | " + artist + " | " + entryTime + "\n";
                        data.append(row);
                    } while (cursor.moveToNext());
                    showDataInAlertDialog(data.toString());
                } else {
                    Log.d("DatabaseViewer", "Invalid column index.");
                }

            } else {
                Log.d("DatabaseViewer", "No data found in the database.");
            }
        } finally {
            if (cursor != null) {
                cursor.close();
            }
        }
    }

    private void showDataInAlertDialog(String data) {
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setTitle("Содержимое базы данных")
                .setMessage(data)
                .setPositiveButton("OK", null) // Добавление кнопки "ОК"
                .show();
    }
}
