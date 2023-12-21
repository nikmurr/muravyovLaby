package com.example.course_work;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class GameDatabaseHelper extends SQLiteOpenHelper {

    private static final String DATABASE_NAME = "GameDatabase";
    private static final int DATABASE_VERSION = 1;

    private static final String TABLE_NAME = "GameStats";
    private static final String COLUMN_ID = "id";
    private static final String COLUMN_TARGET_NUMBER = "target_number";
    private static final String COLUMN_ATTEMPTS = "attempts";
    private static final String COLUMN_SUCCESS = "success";
    private static final String COLUMN_START_TIME = "start_time";
    private static final String COLUMN_ELAPSED_TIME = "elapsed_time";

    public GameDatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        String createTableQuery = "CREATE TABLE " + TABLE_NAME + " (" +
                COLUMN_ID + " INTEGER PRIMARY KEY AUTOINCREMENT," +
                COLUMN_TARGET_NUMBER + " INTEGER," +
                COLUMN_ATTEMPTS + " INTEGER," +
                COLUMN_SUCCESS + " INTEGER," +
                COLUMN_START_TIME + " DATETIME," +
                COLUMN_ELAPSED_TIME + " INTEGER" +
                ")";
        db.execSQL(createTableQuery);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_NAME);
        onCreate(db);
    }

    public void addGameStats(int targetNumber, int attempts, boolean success, String start_time, long elapsed_time) {
        try {
            SQLiteDatabase db = this.getWritableDatabase();
            ContentValues values = new ContentValues();
            values.put(COLUMN_TARGET_NUMBER, targetNumber);
            values.put(COLUMN_ATTEMPTS, attempts);
            values.put(COLUMN_SUCCESS, success ? 1 : 0);
            values.put(COLUMN_START_TIME, start_time);
            values.put(COLUMN_ELAPSED_TIME, elapsed_time);
            db.insert(TABLE_NAME, null, values);
            db.close();
            Log.i("Database", "Data added");
        } catch (Exception e) {
            Log.i("Database", "Error while add data");
            throw new RuntimeException(e);
        }
    }

    public void deleteAllGameStats() {
        SQLiteDatabase db = this.getWritableDatabase();
        db.delete(TABLE_NAME, null, null);
        db.close();
    }

    public List<GameStatModel> getAllGameStats() {
        List<GameStatModel> gameStatsList = new ArrayList<>();
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery("SELECT * FROM " + TABLE_NAME, null);

        if (cursor != null && cursor.getCount() > 0) {
            int idColumnIndex = cursor.getColumnIndex(COLUMN_ID);
            int targetNumberColumnIndex = cursor.getColumnIndex(COLUMN_TARGET_NUMBER);
            int attemptsColumnIndex = cursor.getColumnIndex(COLUMN_ATTEMPTS);
            int successColumnIndex = cursor.getColumnIndex(COLUMN_SUCCESS);
            int startTimeColumnIndex = cursor.getColumnIndex(COLUMN_START_TIME);
            int elapsedTimeColumnIndex = cursor.getColumnIndex(COLUMN_ELAPSED_TIME);

            while (cursor.moveToNext()) {
                try {
                    int id = cursor.getInt(idColumnIndex);
                    int targetNumber = cursor.getInt(targetNumberColumnIndex);
                    int attempts = cursor.getInt(attemptsColumnIndex);
                    boolean success = cursor.getInt(successColumnIndex) == 1;
                    String startTime = cursor.getString(startTimeColumnIndex);
                    long elapsedTime = cursor.getLong(elapsedTimeColumnIndex);

                    // Создание экземпляра GameStatModel и добавление его в список
                    String elapsedTimeString = getFormattedElapsedTime(elapsedTime);
                    GameStatModel gameStat = new GameStatModel(id, targetNumber, attempts, success, startTime, elapsedTime, elapsedTimeString);
                    gameStatsList.add(gameStat);
                } catch (Exception e) {
                    Log.e("DBCursor", "Error while compiling List<>");
                    throw new RuntimeException(e);
                }
            }
        }

        cursor.close();
        db.close();
        Collections.reverse(gameStatsList);
        return gameStatsList;
    }

    public String getFormattedElapsedTime(long totalElapsedTime) {
        // Предельное значение для минут и секунд (59:59)
        long limit = 3599000L; // 59 минут 59 секунд в миллисекундах

        // Если значение больше предельного, устанавливаем его равным предельному
        totalElapsedTime = Math.min(totalElapsedTime, limit);

        // Преобразование миллисекунд в минуты и секунды
        long seconds = totalElapsedTime / 1000;
        long minutes = seconds / 60;
        seconds = seconds % 60;

        // Форматирование времени в виде минут:секунды
        return String.format("%02d:%02d", minutes, seconds);
    }
}
