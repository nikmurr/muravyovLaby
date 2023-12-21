package com.example.course_work;

import android.content.Context;
import android.util.Log;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.Random;
import java.util.TimeZone;
import java.util.Timer;
import java.util.TimerTask;

public class GameLogic {

    private OnTimerTickListener onTimerTickListener;
    private Context context;
    private int targetNumber;
    private int attempts;
    private boolean isGameWon;
    private boolean isBeenVeryHot;
    private long startTime; // Добавленная переменная для хранения времени начала игры
    private long totalElapsedTime;
    private Timer timer; // Добавленный таймер


    public void setOnTimerTickListener(OnTimerTickListener listener) {
        this.onTimerTickListener = listener;
    }


    public GameLogic() {
        initializeGame();
        startTime = System.currentTimeMillis(); // Записываем время начала игры при инициализации объекта GameLogic
        startTimer(); // Запускаем таймер для отслеживания времени игры
    }

    public void initializeGame() {
        Random random = new Random();
        targetNumber = random.nextInt(1000000) + 1; // Генерация случайного числа от 1 до 1000000
        attempts = 0;
        isGameWon = false;
        isBeenVeryHot = false;
    }

    public void setPreferences(int targetNumber, int attempts, boolean isGameWon, boolean isBeenVeryHot, long startTime, long totalElapsedTime) {
        this.targetNumber = targetNumber;
        this.attempts = attempts;
        this.isGameWon = isGameWon;
        this.isBeenVeryHot = isBeenVeryHot;
        this.startTime = startTime;
        this.totalElapsedTime = totalElapsedTime;
        startTimer();
    }

    public int compareNumbers(Integer guessedNumber, Integer oldGuessedNumber) {
        attempts++;
        int largerNumber = Math.max(targetNumber, guessedNumber);
        int smallerNumber = Math.min(targetNumber, guessedNumber);
        int difference = largerNumber - smallerNumber;
        int isCloser = isCloser(guessedNumber, oldGuessedNumber);

        if (guessedNumber == targetNumber) {
            isGameWon = true;
            StopGame();
            return 0;
        } else if (guessedNumber.equals(oldGuessedNumber) || isCloser == 3) {
            return 50;
        } else if ((difference <= 1000 && difference >= 10 && isCloser == 0) || (((difference <= 10 && isBeenVeryHot) || (difference >= 10)) && isCloser == 1)) {
            if (isCloser == 1) return 20;
            else return 21;
        } else if (difference >= 1000 && largerNumber/smallerNumber >= 10 && isCloser == 0) {
            return 40;
        } else if (difference <= 10 && !isBeenVeryHot) {
            isBeenVeryHot = true;
            return  10;
        } else {
            if (isCloser == 2) return 30;
            else return 31;
        }
    }

    private int isCloser(int guessedNumber, Integer oldGuessedNumber) {
        try {
            int difference = Math.abs(targetNumber - guessedNumber);
            int differenceOld = Math.abs(targetNumber - oldGuessedNumber);
            if (difference < differenceOld) return 1;
            else if (difference > differenceOld) return 2;
            else return 3;
        } catch (Exception e) {
            return 0;
        }

    }

    public void RollBack() {
        attempts++;
    }
    public int getAttempts() {
        return attempts;
    }
    public long getStartTime() {
        return startTime;
    }
    public long getTotalElapsedTime() {
        return totalElapsedTime;
    }
    public int getTargetNumber() {
        return targetNumber;
    }
    public boolean isBeenVeryHot() { return isBeenVeryHot; }
    public boolean isGameWon() {
        return isGameWon;
    }

    public void ResumeGame() {
        startTimer();
    }

    public void StopGame() {
        stopTimer();
    }

    private void startTimer() {
        if (timer == null) {
            timer = new Timer();
            timer.scheduleAtFixedRate(new TimerTask() {
                @Override
                public void run() {
                    // Вычисляем время, прошедшее с начала игры
                    long currentTime = System.currentTimeMillis();
                    totalElapsedTime = currentTime - startTime;
                    Log.i("Timer", "Tick");
                    if (onTimerTickListener != null) {
                        onTimerTickListener.OnTimerTick();
                    }
                }
            }, 0, 1000);
        }
    }

    private void stopTimer() {
        if (timer != null) {
            timer.cancel();
        }
    }

    public String getFormattedElapsedTime() {
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

    public String getFormattedStartTime() {
        TimeZone timeZone = TimeZone.getTimeZone("Europe/Moscow");
        Date startDate = new Date(startTime);
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault());
        sdf.setTimeZone(timeZone);
        return sdf.format(startDate);
    }


}
