package com.example.myapp3;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

public class MyAdapter extends RecyclerView.Adapter<MyAdapter.ViewHolder> {

    private List<MyDataModel> dataList; //

    // Конструктор для передачи списка данных
    public MyAdapter(List<MyDataModel> dataList) {
        this.dataList = dataList;
    }

    // Создание новых View и ViewHolder'ов
    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.list_item_layout, parent, false);
        return new ViewHolder(view);
    }

    // Связывание данных с представлениями
    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        MyDataModel data = dataList.get(position);
        holder.textViewID.setText(data.getId());
        holder.textViewSongTitle.setText(data.getTrackTitle());
        holder.textViewArtist.setText(data.getArtist());
        holder.textViewDate.setText(String.format("Дата добавления:%s", data.getEntryTime()));
    }

    // Возвращает размер вашего списка данных
    @Override
    public int getItemCount() {
        return dataList.size();
    }

    // ViewHolder, который содержит представления элемента списка
    public static class ViewHolder extends RecyclerView.ViewHolder {
        public TextView textViewID;
        public TextView textViewSongTitle;
        public TextView textViewArtist;
        public TextView textViewDate;

        public ViewHolder(View itemView) {
            super(itemView);
            textViewID = itemView.findViewById(R.id.textViewID);
            textViewSongTitle = itemView.findViewById(R.id.textViewSongTitle);
            textViewArtist = itemView.findViewById(R.id.textViewArtist);
            textViewDate = itemView.findViewById(R.id.textViewDate);
        }
    }
}
