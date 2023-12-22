package com.example.myapp3;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import java.util.ArrayList;
import java.util.List;

public class DatabaseHelper extends SQLiteOpenHelper {
    private static final String TAG = "DatabaseHelper"; // Тег для сообщений LogCat

    private static final String DATABASE_NAME = "SongsDatabase";
    private static final int DATABASE_VERSION = 1;

    public static final String TABLE_SONGS = "songs";
    public static final String COLUMN_ID = "id";
    public static final String COLUMN_ARTIST = "artist";
    public static final String COLUMN_TRACK_TITLE = "track_title";
    public static final String COLUMN_ENTRY_TIME = "entry_time";
    private String createTableQuery = "CREATE TABLE " + TABLE_SONGS +
            " (" + COLUMN_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
            COLUMN_ARTIST + " TEXT, " +
            COLUMN_TRACK_TITLE + " TEXT, " +
            COLUMN_ENTRY_TIME + " DATETIME DEFAULT CURRENT_TIMESTAMP)";

    public DatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(createTableQuery);

        // Добавление сообщения в LogCat после создания таблицы
        Log.d(TAG, "Таблица создана: " + TABLE_SONGS);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_SONGS);
        onCreate(db);

        // Добавление сообщения в LogCat после обновления базы данных
        Log.d(TAG, "База данных обновлена");
    }

    public void ClearDB(SQLiteDatabase db) {
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_SONGS);
        onCreate(db);
    }

    public List<MyDataModel> getAllData() {
        List<MyDataModel> dataList = new ArrayList<>();

        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = null;

        try {
            cursor = db.query(TABLE_SONGS,
                    null,
                    null,
                    null,
                    null,
                    null,
                    null);

            if (cursor != null && cursor.moveToFirst()) {
                do {
                    int idIndex = cursor.getColumnIndex(COLUMN_ID);
                    int artistIndex = cursor.getColumnIndex(COLUMN_ARTIST);
                    int titleIndex = cursor.getColumnIndex(COLUMN_TRACK_TITLE);
                    int entryTimeIndex = cursor.getColumnIndex(COLUMN_ENTRY_TIME);

                    if (idIndex >= 0 && artistIndex >= 0 && titleIndex >= 0 && entryTimeIndex >= 0) {
                        int id = cursor.getInt(idIndex);
                        String artist = cursor.getString(artistIndex);
                        String title = cursor.getString(titleIndex);
                        String entryTime = cursor.getString(entryTimeIndex);

                        MyDataModel data = new MyDataModel(id, artist, title, entryTime);
                        dataList.add(data);
                    }
                } while (cursor.moveToNext());
            }
        } catch (Exception e) {
            Log.e(TAG, "Error while trying to get data from database: " + e.getMessage());
        } finally {
            if (cursor != null) {
                cursor.close();
            }
        }

        return dataList;
    }

}


