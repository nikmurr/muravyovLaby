package com.example.course_work;

import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.content.ContextCompat;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;

import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Map;

public class MainActivity extends AppCompatActivity implements OnActivitySwitchListener, OnTimerTickListener {

    final Context context = this;
    GameLogic gamelogic;
    GameDatabaseHelper dbHelper;
    Integer guessedNumber;
    Integer oldGuessedNumber;
    int currentTag = 60;
    List<View> allElements = new ArrayList<>();
    boolean isGameGoing = false;
    SharedPreferences sharedPreferences;
    TextView timeText;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        dbHelper = new GameDatabaseHelper(this);
        sharedPreferences = getSharedPreferences("SavedGame", Context.MODE_PRIVATE);

        ConstraintLayout rootLayout = findViewById(R.id.rootLayout);
        LinearLayout buttonLayout = findViewById(R.id.buttonLayout);
        Button buttonStart = findViewById(R.id.button);
        ImageButton buttonGetAnswer = buttonLayout.findViewById(R.id.imageButton3);
        ImageButton buttonRollBack = buttonLayout.findViewById(R.id.imageButton4);
        ImageButton buttonStop = findViewById(R.id.imageButton5);
        TextView currentNumberText = findViewById(R.id.textView2);
        TextView tagText = findViewById(R.id.textView4);
        TextView attemptsText = findViewById(R.id.textView7);
        timeText = findViewById(R.id.textView5);
        TextInputLayout textInputLayout = findViewById(R.id.textLayout);
        TextInputEditText textInputEditText = textInputLayout.findViewById(R.id.textInput);
        ImageView circle = findViewById(R.id.imageView);
        ImageView attemptsCircle = findViewById(R.id.imageView2);

        BottomNavigationView bottomNavigationView = findViewById(R.id.bottomNavigationView1);
        circle.setColorFilter(ContextCompat.getColor(MainActivity.this, R.color.base_olive), PorterDuff.Mode.SRC_IN);
        attemptsCircle.setColorFilter(ContextCompat.getColor(MainActivity.this, R.color.base_olive), PorterDuff.Mode.SRC_IN);
        attemptsText.setText("0");

        BottomNavItemSelectedListener listener = new BottomNavItemSelectedListener(this);
        bottomNavigationView.setOnNavigationItemSelectedListener(listener);
        listener.setOnActivitySwitchListener(this);



        GetGameState(rootLayout, tagText, textInputLayout, circle, attemptsCircle, currentNumberText, attemptsText);

        buttonStart.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                gamelogic = new GameLogic();
                gamelogic.setOnTimerTickListener(MainActivity.this);
                SetGame(true);
            }
        });

        buttonStop.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                GameOver(gamelogic.isGameWon(),rootLayout, tagText, textInputLayout, circle, attemptsCircle, currentNumberText, attemptsText);
            }
        });

        buttonGetAnswer.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                try {
                    String inputText = String.valueOf(textInputEditText.getText()); // Получение текста из EditText
                    if (!inputText.isEmpty()) {
                        Integer newGuessedNumber = convertStringToInt(inputText);
                        if (newGuessedNumber != null && newGuessedNumber > 0) {
                            try {
                                textInputEditText.setText("");
                                currentTag = gamelogic.compareNumbers(newGuessedNumber, guessedNumber);
                                if (guessedNumber != null) {
                                    oldGuessedNumber = guessedNumber;
                                }
                                guessedNumber = newGuessedNumber;
                                UpdateView(rootLayout, tagText, textInputLayout, circle, attemptsCircle, currentNumberText, attemptsText);
                            } catch (Exception e) {
                                Log.i("Bitch", "Всё нае");
                                throw new RuntimeException(e);
                            }
                        }
                        else {
                            Toast.makeText(MainActivity.this, "Введите натуральное число", Toast.LENGTH_SHORT).show();
                        }

                    } else {
                        Toast.makeText(MainActivity.this, "Введите текст", Toast.LENGTH_SHORT).show();
                    }
                } catch (Exception e) {
                    Log.i("Bitch", "Все пошло попопе");
                    throw new RuntimeException(e);
                }
            }
        });

        buttonRollBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                try {
                    RollBack(rootLayout, tagText, textInputLayout, circle, attemptsCircle, currentNumberText, attemptsText);
                } catch (Exception e) {
                    Log.i("Bitch", "Кнопка сдохла");
                    throw new RuntimeException(e);
                }
            }
        });
    }

    private Integer convertStringToInt(String str) {
        try {
            return Integer.parseInt(str);
        } catch (NumberFormatException e) {
            return null;
        }
    }

    private void RollBack(ConstraintLayout rootLayout, TextView tagText, TextInputLayout textInputLayout, ImageView circle, ImageView attemptsCircle, TextView currentNumberText, TextView attemptsText) {
        if (guessedNumber != null && oldGuessedNumber != null && !guessedNumber.equals(oldGuessedNumber)) {
            Integer tempNumber = guessedNumber;
            guessedNumber = oldGuessedNumber;
            oldGuessedNumber = tempNumber;
            gamelogic.RollBack();
            currentTag = 60;
            UpdateView(rootLayout, tagText, textInputLayout, circle, attemptsCircle, currentNumberText, attemptsText);
        } else {
            Toast.makeText(context, "Недостаточно чисел для отката", Toast.LENGTH_SHORT).show();
        }
    }


    private void GameOver(boolean isWon, ConstraintLayout rootLayout, TextView tagText, TextInputLayout textInputLayout, ImageView circle, ImageView attemptsCircle, TextView currentNumberText, TextView attemptsText) {
        if (gamelogic != null && isGameGoing) {
            gamelogic.StopGame();
            SharedPreferences.Editor editor = sharedPreferences.edit();
            editor.clear();
            editor.apply();
            isGameGoing = false;
            AddGameDataToDatabase(gamelogic);
            if (isWon) {
                Intent intentWin = new Intent(context, ActivityWin.class);
                finish();
                context.startActivity(intentWin);
            }
            else {
                Intent intentLose = new Intent(context, ActivityLose.class);
                finish();
                context.startActivity(intentLose);
            }

            /*gamelogic = null;
            guessedNumber = null;
            oldGuessedNumber = null;
            currentTag = 60;
            isGameGoing = false;
            UpdateView(rootLayout, tagText, textInputLayout, circle, attemptsCircle, currentNumberText, attemptsText);
            SetGame(isGameGoing);*/
        };
    }

    private void UpdateView(ConstraintLayout rootLayout, TextView tagText, TextInputLayout textInputLayout, ImageView circle, ImageView attemptsCircle, TextView currentNumberText, TextView attemptsText) {
        if (isGameGoing) {
            if (guessedNumber != null) {
                String formattedNumber = String.format(Locale.getDefault(), "%,d", guessedNumber).replace(',', '\u202F');;
                currentNumberText.setText(formattedNumber);
                attemptsText.setText(String.valueOf(gamelogic.getAttempts()));
                setTag(context, rootLayout, tagText, currentNumberText, textInputLayout, circle, attemptsCircle, attemptsText);
            }
        } else {
            String formattedNumber = "";
            currentNumberText.setText(formattedNumber);
            attemptsText.setText("0");
            setTag(context, rootLayout, tagText, currentNumberText, textInputLayout, circle, attemptsCircle, attemptsText);
        }
    }

    private void setTag(Context context, ConstraintLayout rootLayout, TextView tagLabel, TextView currentState, TextInputLayout textInputLayout, ImageView circle, ImageView attemptsCircle, TextView attemptsText) {
        String tagText = "";
        int baseColor = ContextCompat.getColor(context, R.color.base_olive);
        int backgroundColor = ContextCompat.getColor(context, R.color.base_background);
        switch (currentTag) {
            case 0:
                GameOver(gamelogic.isGameWon(), rootLayout, tagLabel, textInputLayout, circle, attemptsCircle, currentState, attemptsText);
                break;
            case 10:
                tagText = "Горячо!";
                baseColor = ContextCompat.getColor(context, R.color.hot_base);
                backgroundColor = ContextCompat.getColor(context, R.color.hot_background);
                break;
            case 20:
                tagText = "Теплее";
                baseColor = ContextCompat.getColor(context, R.color.warm_base);
                backgroundColor = ContextCompat.getColor(context, R.color.warm_background);
                break;
            case 21:
                tagText = "Тепло";
                baseColor = ContextCompat.getColor(context, R.color.warm_base);
                backgroundColor = ContextCompat.getColor(context, R.color.warm_background);
                break;
            case 30:
                tagText = "Холоднее";
                baseColor = ContextCompat.getColor(context, R.color.cold_base);
                backgroundColor = ContextCompat.getColor(context, R.color.cold_background);
                break;
            case 31:
                tagText = "Холодно";
                baseColor = ContextCompat.getColor(context, R.color.cold_base);
                backgroundColor = ContextCompat.getColor(context, R.color.cold_background);
                break;
            case 40:
                tagText = "Очень холодно";
                baseColor = ContextCompat.getColor(context, R.color.verycold_base);
                backgroundColor = ContextCompat.getColor(context, R.color.verycold_background);
                break;
            case 50:
                tagText = "Так же";
                baseColor = ContextCompat.getColor(context, R.color.neutral_base);
                backgroundColor = ContextCompat.getColor(context, R.color.neutral_background);
                break;
            case 60:
                tagText = "";
                break;
            default:
                tagText = "Произошла неизвестная ошибка";
                break;
        }

        tagLabel.setText(tagText);
        currentState.setTextColor(baseColor);
        rootLayout.setBackgroundColor(backgroundColor);
        textInputLayout.setBoxBackgroundColor(backgroundColor);
        circle.setColorFilter(baseColor, PorterDuff.Mode.SRC_IN);
        attemptsCircle.setColorFilter(baseColor, PorterDuff.Mode.SRC_IN);
    }

    private void SetGame(boolean startGame) {
        Button buttonStart = findViewById(R.id.button);
        ImageView circle = findViewById(R.id.imageView);
        if (allElements.isEmpty()) {
            LinearLayout buttonLayout = findViewById(R.id.buttonLayout);
            ImageButton buttonGetAnswer = buttonLayout.findViewById(R.id.imageButton3);
            ImageButton buttonRollBack = buttonLayout.findViewById(R.id.imageButton4);
            ImageButton buttonStop = findViewById(R.id.imageButton5);
            TextView currentState = findViewById(R.id.textView2);
            TextInputLayout textInputLayout = findViewById(R.id.textLayout);
            TextInputEditText textInputEditText = textInputLayout.findViewById(R.id.textInput);
            TextView lastNumLabel = findViewById(R.id.textView3);
            TextView tagLabel = findViewById(R.id.textView4);
            TextView timeLabel = findViewById(R.id.textView5);
            TextView attemptsText = findViewById(R.id.textView7);
            ImageView attempts_circle = findViewById(R.id.imageView2);

            allElements.add(buttonLayout);
            allElements.add(buttonGetAnswer);
            allElements.add(buttonRollBack);
            allElements.add(buttonStop);
            allElements.add(currentState);
            allElements.add(textInputLayout);
            allElements.add(textInputEditText);
            allElements.add(circle);
            allElements.add(attempts_circle);
            allElements.add(lastNumLabel);
            allElements.add(tagLabel);
            allElements.add(timeLabel);
            allElements.add(attemptsText);
        }

        if (startGame) {
            Log.i("SetGame", "Игра началась");
            for (View element : allElements) {
                element.setEnabled(true);
                element.setAlpha(1.0F);
            }
            circle.setAlpha(0.05F);
            buttonStart.setEnabled(false);
            buttonStart.setAlpha(0.0F);
            isGameGoing = true;
        } else {
            Log.i("SetGame", "Игра окончилась");
            for (View element : allElements) {
                element.setEnabled(false);
                element.setAlpha(0.0F);
            }
            buttonStart.setEnabled(true);
            buttonStart.setAlpha(1.0F);
            isGameGoing = false;
        }


    }
    
    @Override
    public void onActivitySwitch() {
        if (isGameGoing) {
            SaveGameState();
        }
    }

    @Override
    public void OnTimerTick() {
        runOnUiThread(() -> {
            Log.i("Timer", "Интерфейс реализуется");
            if (isGameGoing && gamelogic != null && timeText != null) {
                timeText.setText(gamelogic.getFormattedElapsedTime());
            }
        });
    }

    public void SaveGameState() {
        if (isGameGoing && gamelogic != null) {
            try {
                SharedPreferences.Editor editor = sharedPreferences.edit();
                gamelogic.StopGame();
                editor.putBoolean("isGameGoing", isGameGoing);
                editor.putInt("targetNumber", gamelogic.getTargetNumber());
                editor.putInt("attempts", gamelogic.getAttempts());
                editor.putBoolean("isGameWon", gamelogic.isGameWon());
                editor.putBoolean("isBeenVeryHot", gamelogic.isBeenVeryHot());
                editor.putLong("startTime", gamelogic.getStartTime());
                editor.putLong("totalElapsedTime", gamelogic.getTotalElapsedTime());
                editor.putString("guessedNumber", String.valueOf(guessedNumber));
                editor.putString("oldGuessedNumber", String.valueOf(oldGuessedNumber));
                editor.putInt("currentTag", currentTag);
                editor.apply();
                Log.i("SaveGameState", "Success");
            } catch (Exception e) {
                Log.e("SaveGameState", "Failed");
                throw new RuntimeException(e);
            }
        }
        else {Log.i("SaveGameState", "Saving skipped");}
    }

    public void GetGameState(ConstraintLayout rootLayout, TextView tagText, TextInputLayout textInputLayout, ImageView circle, ImageView attemptsCircle, TextView currentNumberText, TextView attemptsText) {
        Map<String, ?> allEntries = sharedPreferences.getAll();
        boolean isEmpty = allEntries.isEmpty();
        if (!isEmpty) {

            boolean isGameGoingCheck = sharedPreferences.getBoolean("isGameGoing", false);
            int targetNumber = sharedPreferences.getInt("targetNumber", -1);
            int attempts = sharedPreferences.getInt("attempts", -1);

            if (isGameGoingCheck && targetNumber != -1 && attempts != -1) {
                Log.i("GetGameState", "Проверка пройдена.");
                boolean isGameWon = sharedPreferences.getBoolean("isGameWon", false);
                boolean isBeenVeryHot = sharedPreferences.getBoolean("isBeenVeryHot", false);
                long startTime =  sharedPreferences.getLong("startTime", 0);
                long totalElapsedTime =  sharedPreferences.getLong("totalElapsedTime", 0);
                String guessedNumberTest = sharedPreferences.getString("guessedNumber", "");
                String oldGuessedNumberTest = sharedPreferences.getString("oldGuessedNumber", "");
                int currentTagTest = sharedPreferences.getInt("currentTag", 60);

                try {
                    oldGuessedNumber = Integer.parseInt(oldGuessedNumberTest);
                }
                catch (NumberFormatException e) {
                    oldGuessedNumber = null;
                }

                try {
                    guessedNumber = Integer.parseInt(guessedNumberTest);
                }
                catch (NumberFormatException e) {
                    guessedNumber = null;
                }

                isGameGoing = isGameGoingCheck;
                currentTag = currentTagTest;
                gamelogic = new GameLogic();
                gamelogic.setPreferences(targetNumber, attempts, isGameWon, isBeenVeryHot, startTime, totalElapsedTime);
                gamelogic.setOnTimerTickListener(MainActivity.this);
                gamelogic.ResumeGame();

                SetGame(isGameGoing);
                UpdateView(rootLayout, tagText, textInputLayout, circle, attemptsCircle, currentNumberText, attemptsText);
            } else {
                Log.i("GetGameState", "Проверка не пройдена.");
                SetGame(isGameGoing);
            }
        }
        else {
            SetGame(isGameGoing);
        }
    }

    private void AddGameDataToDatabase(GameLogic gamelogic) {
        if (dbHelper != null && gamelogic != null) {
            int targetNumber = gamelogic.getTargetNumber();
            int attempts = gamelogic.getAttempts();
            boolean success = gamelogic.isGameWon();
            String startTime = gamelogic.getFormattedStartTime();
            long elapsedTime = gamelogic.getTotalElapsedTime();

            dbHelper.addGameStats(targetNumber, attempts, success, startTime, elapsedTime);
        }
    }

    @Override
    protected void onStop() {
        super.onStop();
        onActivitySwitch();
    }

    @Override
    protected void onPause() {
        super.onPause();
        onActivitySwitch(); // Сохранить состояние игры и времени
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (isGameGoing && gamelogic != null) {
            gamelogic.ResumeGame(); // Возобновить таймер
        }
    }
}