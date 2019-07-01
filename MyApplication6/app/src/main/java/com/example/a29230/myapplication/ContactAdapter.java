package com.example.a29230.myapplication;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.util.List;

public class ContactAdapter extends ArrayAdapter<Contact> {

    public int resourceId;

    public ContactAdapter(Context context, int textViewResourceId, List<Contact> objects){
        super(context, textViewResourceId, objects);
        resourceId = textViewResourceId;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent){
        Contact contact = getItem(position); // 获取当前Record的实例

        View view;
        RecyclerView.ViewHolder viewHolder;
        if(convertView == null){
            view = LayoutInflater.from(getContext()).inflate(resourceId, parent,false);
        }else {
            view = convertView;
        }

        //View view = LayoutInflater.from(getContext()).inflate(resourceId, parent, false);
        TextView diaplay_name = (TextView)view.findViewById(R.id.diaplay_name);
        TextView number = (TextView)view.findViewById(R.id.number);
        diaplay_name.setText(contact.getDisplay_name());
        number.setText(contact.getNumber());
        return view;
    }
}
