package com.example.course_work;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

public class GameStatAdapter extends RecyclerView.Adapter<GameStatAdapter.ViewHolder> {

    private List<GameStatModel> gameStatsList;
    private Context context;

    public GameStatAdapter(Context context, List<GameStatModel> gameStatsList) {
        this.context = context;
        this.gameStatsList = gameStatsList;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.activity_item_game_stat, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        GameStatModel gameStat = gameStatsList.get(position);

        holder.targetNumberTextView.setText(gameStat.getTargetNumberString());
        holder.successTextView.setText(gameStat.isSuccess() ? "победа" : "поражение");
        holder.startTimeTextView.setText(gameStat.getStartTime());
        holder.elapsedTimeTextView.setText(gameStat.getElapsedTimeString());
        holder.attemptsTextView.setText(String.valueOf(gameStat.getAttempts()));

        if (gameStat.isSuccess()) {
            holder.cardView.setCardBackgroundColor(ContextCompat.getColor(context, R.color.base_olive));
            holder.targetNumberTextView.setTextColor(ContextCompat.getColor(context, R.color.base_background));
            holder.successTextView.setTextColor(ContextCompat.getColor(context, R.color.base_background));
        }
        else {
            holder.cardView.setCardBackgroundColor(ContextCompat.getColor(context, R.color.extra_pink));
            holder.targetNumberTextView.setTextColor(ContextCompat.getColor(context, R.color.base_white));
            holder.successTextView.setTextColor(ContextCompat.getColor(context, R.color.base_white));
        }
    }

    @Override
    public int getItemCount() {
        return gameStatsList.size();
    }

    public void setGameStatsList(List<GameStatModel> newGameStatsList) {
        this.gameStatsList = newGameStatsList;
        notifyDataSetChanged(); // Уведомление об изменении данных
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        CardView cardView;
        TextView targetNumberTextView;
        TextView attemptsTextView;
        TextView successTextView;
        TextView startTimeTextView;
        TextView elapsedTimeTextView;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            targetNumberTextView = itemView.findViewById(R.id.targetNumberTextView);
            attemptsTextView = itemView.findViewById(R.id.attemptsTextView);
            successTextView = itemView.findViewById(R.id.resultTextView);
            startTimeTextView = itemView.findViewById(R.id.startTimeTextView);
            elapsedTimeTextView = itemView.findViewById(R.id.elapsedTimeTextView);
            cardView = itemView.findViewById(R.id.rootCardView);
        }
    }
}
