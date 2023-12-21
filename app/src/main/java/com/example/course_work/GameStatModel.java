package com.example.course_work;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Locale;
import java.util.Date;

public class GameStatModel {
    private int id;
    private int targetNumber;
    private String targetNumberString;
    private int attempts;
    private boolean success;
    private String startTime;
    private long elapsedTime;
    private String elapsedTimeString;

    public GameStatModel(int id, int targetNumber, int attempts, boolean success, String startTime, long elapsedTime, String elapsedTimeString) {
        this.id = id;
        this.targetNumber = targetNumber;
        setTargetNumberString(targetNumber);
        this.attempts = attempts;
        this.success = success;
        setStartTime(startTime);
        this.elapsedTime = elapsedTime;
        this.elapsedTimeString = elapsedTimeString;
    }

    // Геттеры и сеттеры для каждого поля данных

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getTargetNumber() {
        return targetNumber;
    }

    public void setTargetNumber(int targetNumber) {
        this.targetNumber = targetNumber;
    }

    public String getTargetNumberString() {
        return targetNumberString;
    }

    public void setTargetNumberString(int targetNumber) {
        this.targetNumberString = String.format(Locale.getDefault(), "%,d", targetNumber).replace(',', '\u202F');;
    }

    public int getAttempts() {
        return attempts;
    }

    public void setAttempts(int attempts) {
        this.attempts = attempts;
    }

    public boolean isSuccess() {
        return success;
    }

    public void setSuccess(boolean success) {
        this.success = success;
    }

    public String getStartTime() {
        return startTime;
    }

    public void setStartTime(String startTime) {
        SimpleDateFormat inputFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault());
        SimpleDateFormat outputFormat = new SimpleDateFormat("dd.MM.yyyy в HH:mm", Locale.getDefault());
        String formattedStartTime = startTime;
        try {
            Date date = inputFormat.parse(startTime);
           formattedStartTime = outputFormat.format(date);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        this.startTime = formattedStartTime;
    }

    public long getElapsedTime() {
        return elapsedTime;
    }

    public void setElapsedTime(long elapsedTime) {
        this.elapsedTime = elapsedTime;
    }

    public String getElapsedTimeString() {
        return elapsedTimeString;
    }

    public void setElapsedTimeString(String elapsedTimeString) {
        this.elapsedTimeString = elapsedTimeString;
    }
}
