package com.example.myapp61;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.DatePickerDialog;
import android.app.Dialog;
import android.app.TimePickerDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;

import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.TimeZone;

public class MainActivity extends AppCompatActivity {

    private ArrayList<Reminder> remindersList;
    private RemindersViewAdapter remindersViewAdapter;
    private Button setReminderButton;
    private Button updateButton;
    private DatabaseHelper databaseHelper;
    private NotificationUtils notificationUtils;
    private Context activityContext;

    private String titleReminder;
    private String textReminder;
    private ZonedDateTime dateTimeReminder;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        activityContext = this;

        //Выпрашивание разрешения
        SharedPreferences sharedPreferences = getSharedPreferences("MyPrefs", Context.MODE_PRIVATE);
        boolean isFirstRun = sharedPreferences.getBoolean("isFirstRun", true);

        if (isFirstRun) {
            Intent intent = new Intent();
            intent.setAction("android.settings.APP_NOTIFICATION_SETTINGS");
            intent.putExtra("android.provider.extra.APP_PACKAGE", getPackageName());
            startActivity(intent);

            // Сохраняем флаг, указывающий, что это не первый запуск приложения
            SharedPreferences.Editor editor = sharedPreferences.edit();
            editor.putBoolean("isFirstRun", false);
            editor.apply();
        }

        setReminderButton = findViewById(R.id.button);
        updateButton = findViewById(R.id.button3);
        RecyclerView remindersRecyclerView = findViewById(R.id.remindersRecyclerView);
        remindersList = new ArrayList<>();
        databaseHelper = new DatabaseHelper(this);
        notificationUtils = new NotificationUtils(this);

        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        remindersRecyclerView.setLayoutManager(layoutManager);

        String title = "Заголовок";
        String text = "ТекстТекстТекстТекстТекстТекстТекстТекстТекст";
        ZonedDateTime dateTime = ZonedDateTime.of(2023, 12, 12, 13, 42, 0, 0, ZoneId.of("Europe/Moscow"));
        if (databaseHelper.getLastInsertedId() == 0) { databaseHelper.addReminder(title, text, dateTime);}

        remindersList = databaseHelper.getAllReminders();

        remindersViewAdapter = new RemindersViewAdapter(this, remindersList);
        remindersRecyclerView.setAdapter(remindersViewAdapter);

        // Обработка удаления уведомления при клике на него в списке
        remindersRecyclerView.addOnItemTouchListener(new RecyclerView.OnItemTouchListener() {
            @Override
            public boolean onInterceptTouchEvent(@NonNull RecyclerView rv, @NonNull MotionEvent e) {
                View child = rv.findChildViewUnder(e.getX(), e.getY());
                final int position = rv.getChildAdapterPosition(child);

                if (child != null && position != RecyclerView.NO_POSITION) {
                    Reminder selectedReminder = remindersList.get(position);
                    AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this, R.style.MyAlertDialogStyle);
                    builder.setMessage("Вы уверены, что хотите удалить уведомление «" + selectedReminder.getTitle() + "»?")
                            .setCancelable(true)
                            .setPositiveButton("Да", (dialog, which) -> {
                                try {
                                    notificationUtils.cancelNotification(selectedReminder.getId());
                                    databaseHelper.deleteReminder(selectedReminder.getId());
                                    remindersList = databaseHelper.getAllReminders();
                                    remindersViewAdapter.setDataSource(remindersList);
                                    Log.i("Cancel Notification", "Deleted");
                                } catch (Exception ex) {
                                    throw new RuntimeException(ex);
                                }
                            })
                            .setNegativeButton("Отмена", (dialog, which) -> dialog.cancel());

                    AlertDialog alertDialog = builder.create();
                    alertDialog.show();

                    return true; // Событие клика обработано
                }
                return false; // Событие клика не обработано, RecyclerView будет обрабатывать его сам
            }

            @Override
            public void onTouchEvent(@NonNull RecyclerView rv, @NonNull MotionEvent e) {}

            @Override
            public void onRequestDisallowInterceptTouchEvent(boolean disallowIntercept) {}
        });


        updateButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                remindersList = databaseHelper.getAllReminders();
                remindersViewAdapter.setDataSource(remindersList);
            }
        });

        setReminderButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Создание диалогового окна
                final Dialog dialog = new Dialog(MainActivity.this, R.style.MyDialogStyle);


                String titleText = "Заголовок уведомления";
                String bodyText = "Текст уведомления";
                final ZonedDateTime[] reminderDateTime = new ZonedDateTime[1];

                // Установка макета для диалогового окна
                dialog.setContentView(R.layout.custom_dialog_layout);

                Button acceptButton = dialog.findViewById(R.id.button6);
                Button cancelButton = dialog.findViewById(R.id.button7);
                Button dateTimeButton = dialog.findViewById(R.id.button4);
                TextInputLayout titleTextInput = dialog.findViewById(R.id.textInputLayout);
                TextInputLayout bodyTextTextInput = dialog.findViewById(R.id.textInputLayout2);
                TextInputEditText titleEditText = titleTextInput.findViewById(R.id.TextInput1);
                TextInputEditText bodyTextEditText = bodyTextTextInput.findViewById(R.id.TextInput2);


                TextView dateTimeTextView = dialog.findViewById(R.id.textView4);

                acceptButton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        String titleEditTextString = null;
                        try {
                            titleEditTextString = titleEditText.getText().toString();
                        } catch (Exception e) {
                            throw new RuntimeException(e);
                        }
                        String bodyTextEditTextString = null;
                        try {
                            bodyTextEditTextString = bodyTextEditText.getText().toString();
                        } catch (Exception e) {
                            throw new RuntimeException(e);
                        }
                        if (!titleEditTextString.isEmpty() && !bodyTextEditTextString.isEmpty()) {
                            int titleMaxLength = titleTextInput.getCounterMaxLength();
                            int textMaxLength = bodyTextTextInput.getCounterMaxLength();
                            if (titleEditText.getText().length() <= titleMaxLength && bodyTextEditText.getText().length() <= textMaxLength) {
                                try {
                                    titleReminder = titleEditText.getText().toString();
                                    textReminder = bodyTextEditText.getText().toString();
                                    if (reminderDateTime[0] != null) {
                                        dateTimeReminder = reminderDateTime[0];
                                    }
                                    Log.i("SetDataFromDialog", "Успешная инициализация");


                                    try {
                                        int notificationId = databaseHelper.getLastInsertedId() + 1;
                                        Log.i("idToUse", String.valueOf(notificationId));
                                        notificationUtils.showNotification(notificationId, titleReminder, textReminder, dateTimeReminder);
                                        databaseHelper.addReminder(titleReminder, textReminder, dateTimeReminder);
                                        dialog.cancel();
                                        remindersList = databaseHelper.getAllReminders();
                                        remindersViewAdapter.setDataSource(remindersList);
                                    } catch (Exception e) {
                                        Log.e("SetNotificationInMain", "Error");
                                        throw new RuntimeException(e);
                                    }
                                } catch (Exception e) {
                                    Log.e("SetDataFromDialog", "Ошибка установки уведомления");
                                    titleReminder = "";
                                    textReminder = "";
                                    dateTimeReminder = null;
                                    throw new RuntimeException(e);

                                }
                            }
                            else {
                                Toast.makeText(activityContext, "Превышена максимальная длина пользовательского текста", Toast.LENGTH_SHORT).show();
                            }
                        }
                        else {
                            Toast.makeText(activityContext, "Введите заголовок и текст для напоминания", Toast.LENGTH_SHORT).show();
                        }
                    }
                });

                cancelButton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        dialog.cancel();
                    }
                });

                dateTimeButton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        final Calendar currentDateTime = Calendar.getInstance(TimeZone.getTimeZone("Europe/Moscow"));
                        int year = currentDateTime.get(Calendar.YEAR);
                        int month = currentDateTime.get(Calendar.MONTH);
                        int day = currentDateTime.get(Calendar.DAY_OF_MONTH);

                        DatePickerDialog datePickerDialog = new DatePickerDialog(MainActivity.this,
                                (view1, selectedYear, selectedMonth, selectedDay) -> {
                                    final Calendar selectedDateTime = Calendar.getInstance(TimeZone.getTimeZone("Europe/Moscow"));
                                    int hour = selectedDateTime.get(Calendar.HOUR_OF_DAY);
                                    int minute = selectedDateTime.get(Calendar.MINUTE);

                                    selectedDateTime.set(selectedYear, selectedMonth, selectedDay);

                                    new TimePickerDialog(MainActivity.this,
                                            (view2, selectedHour, selectedMinute) -> {
                                                selectedDateTime.set(Calendar.HOUR_OF_DAY, selectedHour);
                                                selectedDateTime.set(Calendar.MINUTE, selectedMinute);

                                                reminderDateTime[0] = ZonedDateTime.ofInstant(selectedDateTime.toInstant(), ZoneId.of("Europe/Moscow"));

                                                if (reminderDateTime[0] != null) {
                                                    acceptButton.setEnabled(true);
                                                    acceptButton.setAlpha(1F);
                                                    String dateString = ConvertMethods.ZoneDateTimeToString(reminderDateTime[0]);
                                                    dateTimeTextView.setText(String.format("Выбранное время: %s", dateString));
                                                } else {
                                                    acceptButton.setEnabled(false);
                                                    acceptButton.setAlpha(0.5F);
                                                }
                                            },
                                            hour, minute, true)
                                            .show();
                                },
                                year, month, day);
                        datePickerDialog.show();
                    }
                });


                /*dialogButton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {

                    }
                });*/

                // Отображение диалога
                dialog.show();
            }
        });


    }
}