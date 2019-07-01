package com.example.a29230.myapplication;


import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.util.List;

public class RecordAdapter extends ArrayAdapter<Record> {

    public int resourceId;

    public RecordAdapter(Context context, int textViewResourceId, List<Record> objects){
        super(context, textViewResourceId, objects);
        resourceId = textViewResourceId;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent){
        Record record = getItem(position); // 获取当前Record的实例

        View view;
        RecyclerView.ViewHolder viewHolder;
        if(convertView == null){
            view = LayoutInflater.from(getContext()).inflate(resourceId, parent,false);
        }else {
            view = convertView;
        }

        //View view = LayoutInflater.from(getContext()).inflate(resourceId, parent, false);
        TextView title = (TextView)view.findViewById(R.id.title);
        TextView date = (TextView)view.findViewById(R.id.date);
        title.setText(record.getTitle());
        date.setText(record.getDate());
        return view;
    }

}
