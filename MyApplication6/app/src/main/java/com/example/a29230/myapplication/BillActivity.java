package com.example.a29230.myapplication;

import android.content.ContentUris;
import android.content.ContentValues;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.provider.CalendarContract;
import android.provider.ContactsContract;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.ContextMenu;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import org.litepal.LitePal;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class BillActivity extends AppCompatActivity {

    private Cursor cursor;
    List<Bill> billsList = new ArrayList<>();
    private BillAdapter billAdapter;
    private ListView listView;
    private TextView cost_count;
    private float count;
    private Bill bill;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_bill);
        LitePal.getDatabase();

        Toolbar toolbar = (Toolbar)findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        // 设置是否有返回箭头
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        cost_count = (TextView)findViewById(R.id.cost_count);

        //showBill();
        // 浮动按钮
        FloatingActionButton floatingActionButton = (FloatingActionButton)findViewById(R.id.add);
        floatingActionButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showAddDialog();
            }
        });
    }

    @Override
    public boolean onContextItemSelected(MenuItem item) {
        ContextMenu.ContextMenuInfo menuInfo = (ContextMenu.ContextMenuInfo)item.getMenuInfo();
        AdapterView.AdapterContextMenuInfo info = (AdapterView.AdapterContextMenuInfo)item.getMenuInfo();
        View itemView = info.targetView;
        int position = info.position;
        int id = (int)info.id;
        switch (item.getItemId()){
            case 0:
                break;
            case 1:
                bill = billsList.get(position);
                LitePal.deleteAll(Bill.class,"id=?",String.valueOf(bill.getId()));
                showBill();
                break;
        }
        return super.onContextItemSelected(item);
    }

    private void showAddDialog() {
        AlertDialog.Builder customizeDialog = new AlertDialog.Builder(BillActivity.this);
        final View dialogView = LayoutInflater.from(BillActivity.this).inflate(R.layout.bill_dialog,null);
        customizeDialog.setTitle("添加账单");
        customizeDialog.setView(dialogView);
        customizeDialog.setPositiveButton("确定",
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        // 获取EditView中的输入内容
                        EditText describe = (EditText) dialogView.findViewById(R.id.describe);
                        EditText cost = (EditText) dialogView.findViewById(R.id.cost);
                        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy年MM月dd日 HH:mm:ss");// HH:mm:ss
                        Date date = new Date(System.currentTimeMillis());
                        if(!cost.getText().toString().trim().isEmpty()) {
                            Bill bill = new Bill();
                            bill.setDate(simpleDateFormat.format(date));
                            bill.setDescribe(describe.getText().toString());
                            bill.setCost(cost.getText().toString());
                            bill.save();
                            Toast.makeText(BillActivity.this,"添加成功",Toast.LENGTH_SHORT).show();
                        }else{
                            Toast.makeText(BillActivity.this,"添加失败",Toast.LENGTH_SHORT).show();
                        }
                        showBill();
                    }
                });
        customizeDialog.show();
    }

    public void showBill(){
        count = 0;
        billsList.clear();
        billsList = LitePal.order("date desc").find(Bill.class);

        for(Bill bill : billsList) {
            count += Float.parseFloat(bill.getCost());
            Log.d("BillActivity",count+"");
        }
        Log.d("BillActivity",count+"");
        cost_count.setText(count+"");

        billAdapter = new BillAdapter(BillActivity.this, R.layout.bill_item, billsList );
        listView = (ListView)findViewById(R.id.bill_list);
        listView.setAdapter(billAdapter);

        listView.setOnCreateContextMenuListener(new View.OnCreateContextMenuListener() {
            @Override
            public void onCreateContextMenu(ContextMenu menu, View v, ContextMenu.ContextMenuInfo menuInfo) {
                menu.setHeaderTitle("Mennu");
                menu.add(0,0,0,"Cancel");
                menu.add(0,1,0,"Delete");
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.universal_toolbar, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        switch (item.getItemId()){
            case android.R.id.home:
                finish();
                break;
        }
        return true;
    }

    // 在activity运行状态是加载事务
    @Override
    protected void onResume() {
        super.onResume();
        showBill();
    }
}
