package com.example.a29230.myapplication;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.net.Uri;
import android.provider.ContactsContract;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.text.Html;
import android.util.Log;
import android.view.ContextMenu;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import org.litepal.LitePal;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class ContactActivity extends AppCompatActivity {

    ArrayAdapter<String> adapter;

    List<Contact> contactsList = new ArrayList<>();
    private Contact contact;
    private ContactAdapter contactAdapter;
    private Cursor cursor;
    private static String REGEX = "-| ";
    private static String REPLACE = "";
    private String number;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_contact);

        Toolbar toolbar = (Toolbar)findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        // 设置是否有返回箭头
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        showContact();
        contactAdapter = new ContactAdapter(ContactActivity.this, R.layout.contact_item, contactsList);
        ListView contactsView = (ListView)findViewById(R.id.contact_list);
        contactsView.setAdapter(contactAdapter);
        contactsView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
               // Log.d("ContactActivity",contactsList.get(position));
                //Intent intent = new Intent(MainActivity.this, UpdateActivity.class);
                //intent.putExtra("id", String.valueOf(record.getId()));
                //startActivity(intent);
            }
        });
        contactsView.setOnCreateContextMenuListener(new View.OnCreateContextMenuListener() {
            @Override
            public void onCreateContextMenu(ContextMenu menu, View v, ContextMenu.ContextMenuInfo menuInfo) {
                menu.setHeaderTitle("Mennu");
                menu.add(0,0,0,"Call");
                menu.add(0,1,0,"Update");
                menu.add(0,2,0,"Delete");
                menu.add(0,3,0,"Cancel");
            }
        });
        if(ContextCompat.checkSelfPermission(this, Manifest.permission.READ_CONTACTS) != PackageManager.PERMISSION_GRANTED){
            ActivityCompat.requestPermissions(this, new String[]{
                    Manifest.permission.READ_CONTACTS
            },1);
        }else {
            //readContacts();
        }
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
        contact = contactsList.get(position);
        switch (item.getItemId()){
            case 0:
                if(ContextCompat.checkSelfPermission(ContactActivity.this, Manifest.permission.CALL_PHONE) != PackageManager.PERMISSION_GRANTED){
                    ActivityCompat.requestPermissions(ContactActivity.this, new String[]{ Manifest.permission.CALL_PHONE},1);
                }else {
                    Log.d("ContactActivity",contact.getNumber());
                    number = contact.getNumber();
                    Pattern p = Pattern.compile(REGEX);
                    // get a matcher object
                    Matcher m = p.matcher(number);
                    number= m.replaceAll(REPLACE);
                    Log.d("ContactActivity",number);
                    call(number);
                }
                break;
            case 1:
                number = contact.getNumber();
                Pattern p = Pattern.compile(REGEX);
                Matcher m = p.matcher(number);
                String num = m.replaceAll(REPLACE);
                String name = contact.getDisplay_name();
                showUpdateDialog(Long.parseLong(contact.getId()),name,num);
                break;
            case 2:
                if(ContextCompat.checkSelfPermission(ContactActivity.this, Manifest.permission.WRITE_CONTACTS)!= PackageManager.PERMISSION_GRANTED){
                    ActivityCompat.requestPermissions(ContactActivity.this, new  String[]{
                            Manifest.permission.WRITE_CONTACTS
                    },1);
                }else {
                    deleteContact(Long.parseLong(contact.getId()));
                }
                break;
            case 3:
                break;
        }
        return super.onContextItemSelected(item);
    }

    private void call(String number){
        try {
            Intent intent = new Intent(Intent.ACTION_CALL);
            intent.setData(Uri.parse("tel:"+number));
            startActivity(intent);
        }catch (SecurityException e){
            e.printStackTrace();
        }
    }

    public void deleteContact(long rawContactId) {
        getContentResolver().delete(ContentUris.withAppendedId(ContactsContract.RawContacts.CONTENT_URI, rawContactId), null, null);
        showContact();
    }

    public void showContact(){
        cursor = null;
        contactsList.clear();
        try{
            // 查询联系人数据
            cursor = getContentResolver().query(ContactsContract.CommonDataKinds.Phone.CONTENT_URI,
                    null,null,null,null);
            if(cursor != null){
                while (cursor.moveToNext()){
                    String _id = cursor.getString(cursor.getColumnIndex(ContactsContract.CommonDataKinds.Phone.CONTACT_ID));
                    // 获取联系人姓名
                    String displayName = cursor.getString(cursor.getColumnIndex(ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME));
                    // 获取联系人手机号
                    String number = cursor.getString(cursor.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER));
                    //contactsList.add(displayName + "\n" + number);
                    Contact contact = new Contact(_id, displayName, number);
                    contactsList.add(contact);
                }
            }
        }catch (Exception e){
            e.printStackTrace();
        }finally {
            if(cursor != null){
                cursor.close();
            }
        }
        contactAdapter = new ContactAdapter(ContactActivity.this, R.layout.contact_item, contactsList);
        ListView contactsView = (ListView)findViewById(R.id.contact_list);
        contactsView.setAdapter(contactAdapter);
    }

    private void showAddDialog() {
        AlertDialog.Builder customizeDialog = new AlertDialog.Builder(ContactActivity.this);
        final View dialogView = LayoutInflater.from(ContactActivity.this).inflate(R.layout.contact_dialog,null);
        customizeDialog.setTitle("添加联系人");
        customizeDialog.setView(dialogView);
        customizeDialog.setPositiveButton("确定",
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        // 获取EditView中的输入内容
                        EditText name = (EditText) dialogView.findViewById(R.id.name);
                        EditText num = (EditText) dialogView.findViewById(R.id.num);
                        if(ContextCompat.checkSelfPermission(ContactActivity.this, Manifest.permission.WRITE_CONTACTS)!= PackageManager.PERMISSION_GRANTED){
                            ActivityCompat.requestPermissions(ContactActivity.this, new  String[]{
                                    Manifest.permission.WRITE_CONTACTS
                            },1);
                        }else {
                            addContact(name.getText().toString(), num.getText().toString());
                        }
                    }
                });
        customizeDialog.show();
    }

    private void showUpdateDialog(final long rawContactId, final String orgin_name, final String orgin_phoneNum) {
        AlertDialog.Builder customizeDialog = new AlertDialog.Builder(ContactActivity.this);
        final View dialogView = LayoutInflater.from(ContactActivity.this).inflate(R.layout.contact_dialog,null);
        customizeDialog.setTitle("添加联系人");
        customizeDialog.setView(dialogView);
        // 获取EditView中的输入内容
        final EditText name = (EditText) dialogView.findViewById(R.id.name);
        final EditText num = (EditText) dialogView.findViewById(R.id.num);
        name.setText(orgin_name);
        num.setText(orgin_phoneNum);
        customizeDialog.setPositiveButton("确定", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {

                if (!name.getText().toString().trim().isEmpty()&&!num.getText().toString().trim().isEmpty()){
                    if(ContextCompat.checkSelfPermission(ContactActivity.this, Manifest.permission.WRITE_CONTACTS)!= PackageManager.PERMISSION_GRANTED){
                        ActivityCompat.requestPermissions(ContactActivity.this, new  String[]{
                                Manifest.permission.WRITE_CONTACTS
                        },1);
                    }else {
                        updataContact(rawContactId, name.getText().toString(), num.getText().toString());
                    }
                }else {
                    Toast.makeText(ContactActivity.this,"添加失败",Toast.LENGTH_SHORT).show();
                }
            }
        });
        customizeDialog.show();
    }

    public void addContact(String name, String phoneNum) {
        ContentValues values = new ContentValues();
        Uri rawContactUri = getContentResolver().insert(ContactsContract.RawContacts.CONTENT_URI, values);
        long rawContactId = ContentUris.parseId(rawContactUri);
        // 向data表插入数据
        if (!name.trim().isEmpty()&&!phoneNum.trim().isEmpty()) {
            values.clear();
            values.put(ContactsContract.Data.RAW_CONTACT_ID, rawContactId);
            values.put(ContactsContract.Data.MIMETYPE, ContactsContract.CommonDataKinds.StructuredName.CONTENT_ITEM_TYPE);
            values.put(ContactsContract.CommonDataKinds.StructuredName.GIVEN_NAME, name);
            getContentResolver().insert(ContactsContract.Data.CONTENT_URI, values);
            values.clear();
            values.put(ContactsContract.Data.RAW_CONTACT_ID, rawContactId);
            values.put(ContactsContract.Data.MIMETYPE, ContactsContract.CommonDataKinds.Phone.CONTENT_ITEM_TYPE);
            values.put(ContactsContract.CommonDataKinds.Phone.NUMBER, phoneNum);
            values.put(ContactsContract.CommonDataKinds.Phone.TYPE, ContactsContract.CommonDataKinds.Phone.TYPE_MOBILE);
            getContentResolver().insert(ContactsContract.Data.CONTENT_URI, values);
        }else{
            Toast.makeText(ContactActivity.this,"添加失败",Toast.LENGTH_SHORT).show();
        }
        showContact();
    }

    public void updataContact(long rawContactId, String name, String phoneNum) {
        ContentValues values = new ContentValues();
        values.put(ContactsContract.CommonDataKinds.Phone.NUMBER, phoneNum);
        values.put(ContactsContract.CommonDataKinds.StructuredName.GIVEN_NAME, name);
        //values.put(ContactsContract.CommonDataKinds.Phone.TYPE, ContactsContract.CommonDataKinds.Phone.TYPE_MOBILE);
        String where = ContactsContract.Data.RAW_CONTACT_ID + "=?";
        String[] selectionArgs = new String[] { String.valueOf(rawContactId)};
        getContentResolver().update(ContactsContract.Data.CONTENT_URI, values, where, selectionArgs);
        showContact();
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        switch (requestCode){
            case 1:
                if(grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED){
                    call(number);
                    //readContacts();
                }else {
                    Toast.makeText(this,"You denied the permission", Toast.LENGTH_SHORT).show();
                }
                break;
            default:
        }
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
}