package com.example.a29230.myapplication;

import android.content.ContentUris;
import android.content.Intent;
import android.net.Uri;
import android.provider.CalendarContract;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.ContextMenu;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ListView;
import android.support.v7.widget.Toolbar;
import android.widget.Toast;

import org.litepal.LitePal;

import java.util.ArrayList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

public class MainActivity extends AppCompatActivity {

    private List<Record> records = new ArrayList<>();
    private DrawerLayout mDrawerLayout;
    private Record record;
    private ListView listView;
    private RecordAdapter recordAdapter;
    private static boolean mBackKeyPressed = false;//记录是否有首次按键

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Button contact = (Button)findViewById(R.id.contact);
        contact.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, ContactActivity.class);
                startActivity(intent);
            }
        });
        Button bill = (Button)findViewById(R.id.bill);
        bill.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, BillActivity.class);
                startActivity(intent);
            }
        });
        // 创建数据库
        LitePal.getDatabase();

        Toolbar toolbar = (Toolbar)findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        mDrawerLayout = (DrawerLayout)findViewById(R.id.drawer_layout);
        ActionBar actionBar = getSupportActionBar();
        if(actionBar != null){
            actionBar.setDisplayHomeAsUpEnabled(true);
            actionBar.setHomeAsUpIndicator(R.drawable.menu);
        }

        // 浮动按钮
        FloatingActionButton floatingActionButton = (FloatingActionButton)findViewById(R.id.add);
        floatingActionButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, EditActivity.class);
                startActivity(intent);
            }
        });
    }


    //  为 toolbar 添加菜单
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.maintoolbar, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()){
            case android.R.id.home:
                mDrawerLayout.openDrawer(GravityCompat.START);
                break;
        }
        return true;
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
                break;
            case 2:
                record = records.get(position);
                Intent intent = new Intent(MainActivity.this, UpdateActivity.class);
                intent.putExtra("id", String.valueOf(record.getId()));
                startActivity(intent);
                break;
            case 3:
                record = records.get(position);
                if(!record.getEventID().equals("0")){
                    Uri deleteUri = null;
                    deleteUri = ContentUris.withAppendedId(CalendarContract.Events.CONTENT_URI, Long.parseLong(record.getEventID()));
                    getContentResolver().delete(deleteUri, null, null);
                }
                LitePal.deleteAll(Record.class,"id=?",String.valueOf(record.getId()));
                records = LitePal.order("id desc").find(Record.class);
                recordAdapter = new RecordAdapter(MainActivity.this, R.layout.record_item, records);
                listView = (ListView)findViewById(R.id.list_view);
                listView.setAdapter(recordAdapter);
                break;
        }
        return super.onContextItemSelected(item);
    }

    // 在activity运行状态是加载事务
    @Override
    protected void onResume(){
        super.onResume();
        Log.e("MainActivity", "onResume()");
        records = LitePal.order("date desc").find(Record.class);
        recordAdapter = new RecordAdapter(MainActivity.this, R.layout.record_item, records);
        listView = (ListView)findViewById(R.id.list_view);
        listView.setAdapter(recordAdapter);
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                record = records.get(position);
                Intent intent = new Intent(MainActivity.this, UpdateActivity.class);
                intent.putExtra("id", String.valueOf(record.getId()));
                startActivity(intent);
            }
        });

        listView.setOnCreateContextMenuListener(new View.OnCreateContextMenuListener() {
            @Override
            public void onCreateContextMenu(ContextMenu menu, View v, ContextMenu.ContextMenuInfo menuInfo) {
                menu.setHeaderTitle("Mennu");
                menu.add(0,0,0,"OK");
                menu.add(0,1,0,"Cancel");
                menu.add(0,2,0,"Update");
                menu.add(0,3,0,"Delete");
            }
        });
    }

    // 双击退出键退出程序
    @Override
    public void onBackPressed() {
        if(!mBackKeyPressed){
            Toast.makeText(this, "再按一次退出程序", Toast.LENGTH_SHORT).show();
            mBackKeyPressed = true;
            new Timer().schedule(new TimerTask() {//延时两秒，如果超出则擦错第一次按键记录
                @Override
                public void run() {
                    mBackKeyPressed = false;
                }
            }, 2000);
        }else{//退出程序
            this.finish();
            System.exit(0);
        }
    }
}
