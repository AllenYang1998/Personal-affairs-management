package com.example.a29230.myapplication;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.util.List;

public class BillAdapter extends ArrayAdapter<Bill> {

    public int resourceId;

    public BillAdapter(Context context, int textViewResourceId, List<Bill> objects){
        super(context, textViewResourceId, objects);
        resourceId = textViewResourceId;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent){
        Bill bill = getItem(position); // 获取当前Record的实例

        View view;
        RecyclerView.ViewHolder viewHolder;
        if(convertView == null){
            view = LayoutInflater.from(getContext()).inflate(resourceId, parent,false);
        }else {
            view = convertView;
        }

        //View view = LayoutInflater.from(getContext()).inflate(resourceId, parent, false);
        TextView date = (TextView)view.findViewById(R.id.bill_date);
        TextView describe = (TextView)view.findViewById(R.id.describe);
        TextView cost = (TextView)view.findViewById(R.id.cost);
        date.setText(bill.getDate());
        describe.setText(bill.getDescribe());
        cost.setText(bill.getCost());
        return view;
    }
}
