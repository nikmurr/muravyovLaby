package com.example.myapp61;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Locale;

public class RemindersViewAdapter extends RecyclerView.Adapter<RemindersViewAdapter.ReminderViewHolder> {

    private Context mContext;
    private ArrayList<Reminder> mRemindersList;
    private DateTimeFormatter mDateTimeFormatter;

    public RemindersViewAdapter(Context context, ArrayList<Reminder> remindersList) {
        mContext = context;
        mRemindersList = remindersList;
        mDateTimeFormatter = DateTimeFormatter.ofPattern("dd.MM.yyyy HH:mm", Locale.getDefault());
    }

    @NonNull
    @Override
    public ReminderViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(mContext).inflate(R.layout.reminder_item, parent, false);
        return new ReminderViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ReminderViewHolder holder, int position) {
        Reminder reminder = mRemindersList.get(position);

        holder.titleTextView.setText(reminder.getTitle());
        holder.textTextView.setText(reminder.getText());

        String formattedDateTime = reminder.getReminderDate().format(mDateTimeFormatter);
        // Установка текста `dateTextView`
        holder.dateTextView.setText(formattedDateTime);
    }

    @Override
    public int getItemCount() {
        return mRemindersList.size();
    }

    public static class ReminderViewHolder extends RecyclerView.ViewHolder {
        TextView titleTextView;
        TextView textTextView;
        TextView dateTextView;

        public ReminderViewHolder(@NonNull View itemView) {
            super(itemView);
            titleTextView = itemView.findViewById(R.id.titleTextView);
            textTextView = itemView.findViewById(R.id.textTextView);
            dateTextView = itemView.findViewById(R.id.timeTextView);
        }
    }

    public void setDataSource(ArrayList<Reminder> remindersList) {
        mRemindersList = remindersList;
        notifyDataSetChanged();
    }
}
