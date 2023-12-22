package com.example.myapp61;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;

public class DatabaseHelper extends SQLiteOpenHelper {
    private static final String DATABASE_NAME = "RemindersDatabase";
    private static final int DATABASE_VERSION = 1;

    private static final String TABLE_NAME = "reminders";
    private static final String COLUMN_ID = "id";
    private static final String COLUMN_TITLE = "title";
    private static final String COLUMN_TEXT = "text";
    private static final String COLUMN_DATE = "reminder_date";

    // Создание таблицы reminders
    private static final String CREATE_TABLE_REMINDERS = "CREATE TABLE " + TABLE_NAME + "("
            + COLUMN_ID + " INTEGER PRIMARY KEY AUTOINCREMENT,"
            + COLUMN_TITLE + " TEXT,"
            + COLUMN_TEXT + " TEXT,"
            + COLUMN_DATE + " TEXT"
            + ")";

    public DatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    public ArrayList<Reminder> getAllReminders() {
        ArrayList<Reminder> reminderList = new ArrayList<>();

        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = db.rawQuery("SELECT * FROM " + TABLE_NAME, null);

        if (cursor != null && cursor.moveToFirst()) {
            int columnIndexID = cursor.getColumnIndex(COLUMN_ID);
            int columnIndexTitle = cursor.getColumnIndex(COLUMN_TITLE);
            int columnIndexText = cursor.getColumnIndex(COLUMN_TEXT);
            int columnIndexDate = cursor.getColumnIndex(COLUMN_DATE);

            do {
                if (columnIndexID != -1 && columnIndexTitle != -1 && columnIndexText != -1 && columnIndexDate != -1) {
                    int id = cursor.getInt(columnIndexID);
                    String title = cursor.getString(columnIndexTitle);
                    String text = cursor.getString(columnIndexText);
                    String dateString = cursor.getString(columnIndexDate);
                    Log.i("getData", dateString);


                    // Преобразование строки с датой в LocalDateTime (время Москвы)
                    DateTimeFormatter formatter = null;
                    ZonedDateTime zonedDateTime = null;
                    try {
                        formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");
                        LocalDateTime localDateTime = LocalDateTime.parse(dateString, formatter);
                        zonedDateTime = localDateTime.atZone(ZoneId.of("Europe/Moscow"));
                    } catch (Exception e) {
                        Log.e("getAllReminders", "Ошибка парсинга даты");
                        throw new RuntimeException(e);
                    }

                    String dateString2 = zonedDateTime.format(formatter);

                    Log.d("getData", dateString2);

                    Reminder reminder = new Reminder(id, title, text, zonedDateTime);

                    reminderList.add(reminder);
                }
            } while (cursor.moveToNext());
        }

        if (cursor != null) {
            cursor.close();
        }
        db.close();

        return reminderList;
    }

    public void addReminder(String title, String text, ZonedDateTime reminderDateTime) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();

        values.put(COLUMN_TITLE, title);
        values.put(COLUMN_TEXT, text);

        // Форматирование ZonedDateTime в строку для хранения в базе данных
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");
        String dateString = reminderDateTime.format(formatter);
        Log.i("addData", dateString);
        values.put(COLUMN_DATE, dateString);

        db.insert(TABLE_NAME, null, values);
        db.close();
    }

    public void deleteReminder(int id) {
        SQLiteDatabase db = this.getWritableDatabase();
        db.delete(TABLE_NAME, COLUMN_ID + " = ?", new String[]{String.valueOf(id)});
        db.close();
    }

    public void clearTable() {
        SQLiteDatabase db = this.getWritableDatabase();
        db.execSQL("DELETE FROM " + TABLE_NAME);
        db.close();
    }

    public int getCount() {
        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = db.rawQuery("SELECT COUNT(*) FROM " + TABLE_NAME, null);

        if (cursor != null && cursor.moveToFirst()) {
            int count = cursor.getInt(0);
            cursor.close();
            return count;
        } else {
            return 0;
        }
    }

    public int getLastInsertedId() {
        SQLiteDatabase db = this.getReadableDatabase();
        int lastId = 0;

        Cursor cursor = db.rawQuery("SELECT MAX(id) FROM " + TABLE_NAME, null);
        if (cursor != null) {
            if (cursor.moveToFirst()) {
                lastId = cursor.getInt(0);
            }
            cursor.close();
        }
        db.close();
        return lastId;
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(CREATE_TABLE_REMINDERS);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_NAME);
        onCreate(db);
    }
}
