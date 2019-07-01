package com.example.a29230.myapplication;

import android.Manifest;
import android.annotation.TargetApi;
import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.ContentResolver;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Build;
import android.provider.CalendarContract;
import android.provider.DocumentsContract;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.annotation.RequiresApi;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v4.content.FileProvider;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.text.Html;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.style.ImageSpan;
import android.text.style.UpdateLayout;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import com.getbase.floatingactionbutton.FloatingActionsMenu;
import com.getbase.floatingactionbutton.FloatingActionButton;


import org.litepal.LitePal;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.TimeZone;


public class UpdateActivity extends AppCompatActivity {

    // 声明全局变量，局部变量无法被调用
    private static final String TAG = "EditActivity";
    private EditText editTitle;
    private TextView datetext;
    private EditText editText;
    private String id;
    private SimpleDateFormat simpleDateFormat;
    private Date date;
    private String origin_title;
    private String origin_text;
    private FloatingActionButton floatingActionButton;
    private FloatingActionsMenu floatingActionsMenu;
    private FloatingActionButton floatingActionButtonaddpic;
    private FloatingActionButton floatingActionButtontakephoto;
    private FloatingActionButton floatingActionButtonaddremind;

    private Cursor c;

    public static final int CHOOSE_PHOTO = 2;

    public static final int TAKE_PHONT = 1;
    private Uri imageUri;

    private LinearLayout remind_linearLayout;
    private TextView remind_date;
    private TextView remind_time;
    private String origin_date;
    private String origin_time;
    private String is_remind;
    private long calID;
    private String eventID;
    private Calendar calendar;
    private int cyear;
    private int cmonth;
    private int cday;
    private int chour;
    private int cminute;

    private static String CALENDER_URL = "content://com.android.calendar/calendars";
    private static String CALENDER_EVENT_URL = "content://com.android.calendar/events";
    private static String CALENDER_REMINDER_URL = "content://com.android.calendar/reminders";

    private static String CALENDARS_NAME = "个人话务";
    private static String CALENDARS_ACCOUNT_NAME = "个人话务";
    private static String CALENDARS_ACCOUNT_TYPE = "个人话务";
    private static String CALENDARS_DISPLAY_NAME = "个人话务";

    @RequiresApi(api = Build.VERSION_CODES.N)
    private static int checkAndAddCalendarAccount(Context context) {
        int oldId = checkCalendarAccount(context);
        if (oldId >= 0) {
            return oldId;
        } else {
            long addId = addCalendarAccount(context);
            if (addId >= 0) {
                return checkCalendarAccount(context);
            } else {
                return -1;
            }
        }
    }

    /**
     * 检查是否存在现有账户，存在则返回账户id，否则返回-1
     */
    private static int checkCalendarAccount(Context context) {
        Cursor userCursor = context.getContentResolver().query(Uri.parse(CALENDER_URL), null, null, null, null);
        try {
            if (userCursor == null) { //查询返回空值
                return -1;
            }
            int count = userCursor.getCount();
            if (count > 0) { //存在现有账户，取第一个账户的id返回
                userCursor.moveToFirst();
                return userCursor.getInt(userCursor.getColumnIndex(CalendarContract.Calendars._ID));
            } else {
                return -1;
            }
        } finally {
            if (userCursor != null) {
                userCursor.close();
            }
        }
    }

    /**
     * 添加日历账户，账户创建成功则返回账户id，否则返回-1
     */
    private static long addCalendarAccount(Context context) {
        TimeZone timeZone = TimeZone.getDefault();
        ContentValues value = new ContentValues();
        value.put(CalendarContract.Calendars.NAME, CALENDARS_NAME);
        value.put(CalendarContract.Calendars.ACCOUNT_NAME, CALENDARS_ACCOUNT_NAME);
        value.put(CalendarContract.Calendars.ACCOUNT_TYPE, CALENDARS_ACCOUNT_TYPE);
        value.put(CalendarContract.Calendars.CALENDAR_DISPLAY_NAME, CALENDARS_DISPLAY_NAME);
        value.put(CalendarContract.Calendars.VISIBLE, 1);
        value.put(CalendarContract.Calendars.CALENDAR_COLOR, Color.BLUE);
        value.put(CalendarContract.Calendars.CALENDAR_ACCESS_LEVEL, CalendarContract.Calendars.CAL_ACCESS_OWNER);
        value.put(CalendarContract.Calendars.SYNC_EVENTS, 1);
        value.put(CalendarContract.Calendars.CALENDAR_TIME_ZONE, timeZone.getID());
        value.put(CalendarContract.Calendars.OWNER_ACCOUNT, CALENDARS_ACCOUNT_NAME);
        value.put(CalendarContract.Calendars.CAN_ORGANIZER_RESPOND, 0);

        Uri calendarUri = Uri.parse(CALENDER_URL);
        calendarUri = calendarUri.buildUpon()
                .appendQueryParameter(CalendarContract.CALLER_IS_SYNCADAPTER, "true")
                .appendQueryParameter(CalendarContract.Calendars.ACCOUNT_NAME, CALENDARS_ACCOUNT_NAME)
                .appendQueryParameter(CalendarContract.Calendars.ACCOUNT_TYPE, CALENDARS_ACCOUNT_TYPE)
                .build();

        Uri result = context.getContentResolver().insert(calendarUri, value);
        long id = result == null ? -1 : ContentUris.parseId(result);
        return id;
    }

    //private LinearLayout remind_linearLayout;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_update);

        // 获取上一个Activity传递过来的值
        Intent intent = getIntent();
        id = intent.getStringExtra("id");

        // 查询指定id的数据
        c = LitePal.findBySQL("select * from Record where id = ?",id);
        c.moveToFirst();

        Toolbar toolbar = (Toolbar)findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        // 设置是否有返回箭头
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        // 悬浮按钮
        floatingActionsMenu = (FloatingActionsMenu)findViewById(R.id.fam) ;
        floatingActionsMenu.setVisibility(View.GONE);
        floatingActionButtonaddpic = (FloatingActionButton)findViewById(R.id.add_pic);
        //floatingActionButtontakephoto = (FloatingActionButton)findViewById(R.id.take_photo);
        floatingActionButtonaddremind = (FloatingActionButton)findViewById(R.id.add_remind);
        floatingActionButtonaddpic.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Toast.makeText(EditActivity.this,"add_pic",Toast.LENGTH_LONG).show();
                if(ContextCompat.checkSelfPermission(UpdateActivity.this, Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED){
                    ActivityCompat.requestPermissions(UpdateActivity.this, new String[]{
                            Manifest.permission.WRITE_EXTERNAL_STORAGE},1);
                }else {
                    openAlbum();
                }
            }
        });
        /*
        floatingActionButtontakephoto.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(UpdateActivity.this,"take_photo",Toast.LENGTH_LONG).show();
                File outputImage = new File(getExternalCacheDir(),"output_image.jpg");
                try {
                    if(outputImage.exists()){
                        outputImage.delete();
                    }
                    outputImage.createNewFile();
                }catch (IOException e){
                    e.printStackTrace();
                }
                if(Build.VERSION.SDK_INT >= 24){
                    imageUri = FileProvider.getUriForFile(UpdateActivity.this,"com.example.a29230.myapplication.fileprovider", outputImage);
                }else {
                    imageUri = Uri.fromFile(outputImage);
                }
                // 启动相机程序
                Intent intent = new Intent("android.media.action.IMAGE_CAPTURE");
                intent.putExtra(MediaStore.EXTRA_OUTPUT, imageUri);
                startActivityForResult(intent, TAKE_PHONT);
            }
        });*/

        remind_linearLayout = (LinearLayout)findViewById(R.id.remind_linear);
        is_remind = c.getString(3);
        Log.d("UpdateActivity","is_remind:"+c.getString(7));
        eventID = c.getString(2);
        remind_date = (TextView)findViewById(R.id.remind_date);
        remind_time = (TextView)findViewById(R.id.remind_time);
        // 判断是否有事务提醒
        if(is_remind.equals("1")){

            remind_linearLayout .setVisibility(View.VISIBLE);
            remind_date.setText(c.getString(4));
            remind_time.setText(c.getString(5));
            origin_date = c.getString(4);
            origin_time = c.getString(5);
            floatingActionButtonaddremind.setIcon(R.drawable.pause);
        }else {
            remind_linearLayout.setVisibility(View.GONE);
            //floatingActionButtonaddremind.setIcon(R.drawable.bell);
        }
        Log.d("UpdateActivity","eventId:"+eventID+" "+is_remind+" "+c.getString(3)+" "+c.getString(4));
        floatingActionButtonaddremind.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if(is_remind.equals("0")){
                    Log.d("UpdateActivity","one");
                    is_remind = "1";
                    remind_linearLayout.setVisibility(View.VISIBLE); //显示设定日期
                    floatingActionButtonaddremind.setIcon(R.drawable.pause);
                }else if(is_remind.equals("1")){
                    Log.d("UpdateActivity","two");
                    is_remind = "0";
                    remind_linearLayout.setVisibility(View.GONE);
                    floatingActionButtonaddremind.setIcon(R.drawable.bell);
                    if(!eventID.equals("0")){
                        Log.d("UpdateActivity","three");
                        Uri deleteUri = null;
                        deleteUri = ContentUris.withAppendedId(CalendarContract.Events.CONTENT_URI, Long.parseLong(eventID));
                        getContentResolver().delete(deleteUri, null, null);
                        remind_time.setText("");
                        remind_date.setText("");
                    }
                    eventID = "0";
                }
            }
        });
        calendar = Calendar.getInstance();
        calendar .setTimeInMillis(System.currentTimeMillis());
        cyear = calendar .get(Calendar.YEAR);
        cmonth = calendar .get(Calendar.MONTH);
        cday = calendar .get(Calendar.DAY_OF_MONTH);
        chour = calendar .get(Calendar.HOUR_OF_DAY);
        cminute = calendar .get(Calendar.MINUTE);
        remind_date.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new DatePickerDialog(UpdateActivity.this, new DatePickerDialog.OnDateSetListener() {
                    @Override
                    public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
                        calendar.setTimeInMillis(System.currentTimeMillis());
                        calendar.set(Calendar.YEAR, year);
                        calendar.set(Calendar.MONTH, month);
                        calendar.set(Calendar.DAY_OF_MONTH,dayOfMonth);
                        remind_date.setText(" "+ calendar.get(Calendar.YEAR)+"-"+ (calendar.get(Calendar.MONTH) + 1) +"-"+calendar.get(Calendar.DAY_OF_MONTH));
                        cyear = calendar.get(Calendar.YEAR);
                        cmonth = calendar.get(Calendar.MONTH);
                        cday = calendar.get(Calendar.DAY_OF_MONTH);
                    }
                },cyear,cmonth,cday).show();
            }
        });
        remind_time.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new TimePickerDialog(UpdateActivity.this, new TimePickerDialog.OnTimeSetListener() {
                    @Override
                    public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
                        calendar.setTimeInMillis(System.currentTimeMillis());
                        calendar.set(Calendar.HOUR_OF_DAY, hourOfDay);
                        calendar.set(Calendar.MINUTE, minute);
                        calendar.set(Calendar.SECOND, 0);
                        calendar.set(Calendar.MILLISECOND, 0);
                        remind_time.setText(" "+calendar.get(Calendar.HOUR_OF_DAY)+":"+calendar.get(Calendar.MINUTE)+" ");
                        chour = calendar.get(Calendar.HOUR_OF_DAY);
                        cminute = calendar.get(Calendar.MINUTE);
                    }
                }, chour, cminute, true).show();
            }
        });
        // 自动生成日期
        simpleDateFormat = new SimpleDateFormat("yyyy年MM月dd日 HH:mm:ss");// HH:mm:ss
        date = new Date(System.currentTimeMillis());

        editTitle = (EditText)findViewById(R.id.edittitle);
        datetext = (TextView)findViewById(R.id.date);
        editText = (EditText)findViewById(R.id.edittext);

        // c.getString(1) 为事务编辑日期
        // c.getString(2) eventID为提醒事件ID
        // c.getString(3) is_remid 是否设置了定时
        // c.getString(4) 为事务提醒日期
        // c.getString(5) 为事务提醒时间
        // c.getString(6) 为事务内容
        // c.getString(7) 为事务标题


        origin_title = c.getString(7);
        editTitle.setText(c.getString(7));
        //datetext.setText(simpleDateFormat.format(date));
        datetext.setText(c.getString(1));

        Html.ImageGetter imageGetter = new Html.ImageGetter(){
            @Override
                public Drawable getDrawable(String s) {
              /*  Drawable drawable = null;
                drawable = Drawable.createFromPath(s);

                drawable.setBounds(0,0,480,480);
                return drawable;*/
                int width = ScreenUtils.getScreenWidth(UpdateActivity.this);
                int height = ScreenUtils.getScreenHeight(UpdateActivity.this);
                Bitmap bitmap = ImageUtils.getSmallBitmap(s,width,480);
                Drawable drawable = new BitmapDrawable(bitmap);
                drawable.setBounds(0,0,width,height);
                return drawable;
            }
        };
        origin_text  = c.getString(6);
        editText.setText(Html.fromHtml(c.getString(6), imageGetter,null));

        //editText.setText(origin_text );
        Log.d("UpdateActivity","FromHtml text:"+editText.getText().toString());
        Log.d("UpdateActivity","ToHtml text:"+Html.toHtml(editText.getText()));

        editText.setOnFocusChangeListener(new android.view.View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (hasFocus) {
                    // 此处为获得焦点时的处理内容
                    floatingActionsMenu.setVisibility(View.VISIBLE);
                } else {
                    // 此处为失去焦点时的处理内容
                    floatingActionsMenu.setVisibility(View.GONE);
                }
            }
        });
    }

    private void openAlbum(){
        Intent intent = new Intent("android.intent.action.GET_CONTENT");
        intent.setType("image/*");
        startActivityForResult(intent, CHOOSE_PHOTO);//打开相册
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        // super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        switch (requestCode){
            case 1:
                if(grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED){
                    openAlbum();
                }else {
                    Toast.makeText(this,"You denied the permission",Toast.LENGTH_SHORT).show();
                }
                break;
            default:
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        // super.onActivityResult(requestCode, resultCode, data);
        switch (requestCode){
            case TAKE_PHONT:
                if(resultCode == RESULT_OK){
                    try{
                        //将拍摄的照片显示出来
                        Bitmap bitmap = BitmapFactory.decodeStream(getContentResolver().openInputStream(imageUri));
                    }catch (FileNotFoundException e){
                        e.printStackTrace();
                    }
                }
                break;
            case CHOOSE_PHOTO:
                if(resultCode == RESULT_OK) {
                    // 判断手机系统版号
                    if (Build.VERSION.SDK_INT >= 19) {
                        // 4.4及以上系统使用这个方法处理图片
                        handleImageOnKitKat(data);
                    }else {
                        // 4.4 以下系统使用这个方法处理图片
                        handleImageBeforeKitKat(data);
                    }
                }
                break;
            default:
                break;
        }
    }

    @TargetApi(19)
    private void handleImageOnKitKat(Intent data){
        String imagePath = null;
        Uri uri = data.getData();
        if(DocumentsContract.isDocumentUri(this,uri)){
            // 如果是document类型的Uri，则通过document id 处理
            String docId = DocumentsContract.getDocumentId(uri);
            if("com.android.providers.media.documents".equals(uri.getAuthority())){
                String id = docId.split(":")[1]; //解析出数字格式id
                String selection = MediaStore.Images.Media._ID + "=" + id;
                imagePath = getImagePath(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, selection);
            }else if("com.android.providers.downloads.documents".equals(uri.getAuthority())){
                Uri contentUri = ContentUris.withAppendedId(Uri.parse("content:/downloads/public_downloads"),Long.valueOf(docId));
                imagePath = getImagePath(contentUri, null);
            }
        }else if("content".equalsIgnoreCase(uri.getScheme())){
            // 如果是content类型的Uri，则使用普通的处理方式
            imagePath = getImagePath(uri, null);
        }else if("file".equalsIgnoreCase(uri.getScheme())){
            // 如果是file类型的Uri，直接获取图片的路径即可
            imagePath = uri.getPath();
        }
        displayImage(imagePath);
        insertImg(imagePath);
    }

    private void handleImageBeforeKitKat(Intent data){
        Uri uri = data.getData();
        String imagePath = getImagePath(uri, null);
        displayImage(imagePath);
        insertImg(imagePath);
    }

    private void insertImg(String path){
        String tagPath = "<img src=\""+path+"\"/>";//为图片路径加上<img>标签
        Bitmap bitmap = BitmapFactory.decodeFile(path);
        if(bitmap != null){
            SpannableString ss = getBitmapMime(path,tagPath);
            insertPhotoToEditText(ss);
            editText.append("\n");
            //Log.d("UpdateActivity", editText.getText().toString());
        }
    }

    // ----------------------------------------------------------------------------------------
    /**
     * 将图片插入到EditText中
     * @param ss
     */
    private void insertPhotoToEditText(SpannableString ss) {

        Html.ImageGetter imageGetter = new Html.ImageGetter(){
            @Override
            public Drawable getDrawable(String s) {
                int width = ScreenUtils.getScreenWidth(UpdateActivity.this);
                int height = ScreenUtils.getScreenHeight(UpdateActivity.this);
                Bitmap bitmap = ImageUtils.getSmallBitmap(s,width,480);
                Drawable drawable = new BitmapDrawable(bitmap);
                drawable.setBounds(0,0,width,height);
                return drawable;
            }
        };
        /*editText.setText(Html.fromHtml(editText.getText().toString(), imageGetter,null));*/
        // 先toHtml
        Log.d("UpdateActivity","1:"+editText.getText().toString());
        // Editable et = new SpannableStringBuilder(Html.fromHtml(editText.getText().toString(), imageGetter,null));//editText.getText();
        //Editable et = new SpannableStringBuilder(Html.toHtml(editText.getText()));
        Editable et = editText.getText();
        int start = editText.getSelectionStart();
        et.insert(start,Html.fromHtml(ss.toString(),imageGetter,null));
        editText.setText(et);
        editText.setSelection(editText.length());
        editText.setFocusableInTouchMode(true);
        editText.setFocusable(true);

        // 插入第二张图片后上一张就OBJ乱码了
        //editText.setText(Html.fromHtml(editText.getText().toString(), imageGetter,null));
        Log.d("UpdateActivity","2:"+editText.getText().toString());
        Log.d("UpdateActivity","2from:"+Html.fromHtml(editText.getText().toString(), imageGetter,null));
        Log.d("UpdateActivity","2 to :"+Html.toHtml(editText.getText()));
        //editText.setText(Html.fromHtml(editText.getText().toString(), imageGetter,null));
        //Log.d("UpdateActivity","3:"+editText.getText().toString());
    }

    private SpannableString getBitmapMime(String path,String tagPath) {
        SpannableString ss = new SpannableString(tagPath);//这里使用加了<img>标签的图片路径

        int width = ScreenUtils.getScreenWidth(UpdateActivity.this);
        int height = ScreenUtils.getScreenHeight(UpdateActivity.this);


        Bitmap bitmap = ImageUtils.getSmallBitmap(path,width,height);
        ImageSpan imageSpan = new ImageSpan(this, bitmap);
        ss.setSpan(imageSpan, 0, tagPath.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        return ss;
    }
    // ----------------------------------------------------------------------------------------


    private String getImagePath(Uri uri, String selection){
        String path = null;
        // 通过Uri和selection来获取真实的图片路径
        Cursor cursor = getContentResolver().query(uri, null, selection, null, null);
        if(cursor != null){
            if(cursor.moveToFirst()){
                path = cursor.getString(cursor.getColumnIndex(MediaStore.Images.Media.DATA));
            }
            cursor.close();
        }
        return path;
    }

    private void displayImage(String imagePath){
        if(imagePath != null){
            Bitmap bitmap = BitmapFactory.decodeFile(imagePath);
        }else {
            Toast.makeText(this,"failed to get image", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.toolbar, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        switch (item.getItemId()){
            case android.R.id.home:
                finish();
                break;
            case R.id.tick:
                Log.d("UpdateActivity","origin_text:"+origin_text);
                Log.d("UpdateActivity","text:"+editText.getText().toString());
                if(!editTitle.getText().toString().trim().isEmpty()&&!editText.getText().toString().trim().isEmpty()
                &&!editText.getText().toString().equals(origin_text)) {//不为空则通过 && 原来与现在不同
                    Log.d("UpdateActivity","Pass");
                    Record record = new Record();
                    record.setTitle(editTitle.getText().toString());
                    record.setDate(simpleDateFormat.format(date));
                    record.setText(Html.toHtml(editText.getText()));
                    Log.d("UpdateActivity","save text:"+Html.toHtml(editText.getText()));

                    if(is_remind.equals("0")){
                        remind_date.setText("");
                        remind_time.setText("");
                        record.setRemind_date(remind_date.getText().toString());
                        record.setRemind_time(remind_time.getText().toString());
                        Log.d("UpdateActivity","eventId:"+eventID+" "+is_remind+" "+remind_date.getText().toString()+" "+remind_time.getText().toString());
                        record.setEventID(eventID);
                        record.setIs_remind("0");
                    }
                    else // 设定定时
                    {
                        calID = checkAndAddCalendarAccount(UpdateActivity.this);
                        record.setRemind_date(remind_date.getText().toString());
                        record.setRemind_time(remind_time.getText().toString());
                        long startMillis = 0;
                        long endMillis =0;
                        // 这是开始提醒时间
                        Calendar beginTime = Calendar.getInstance();
                        beginTime.set(cyear, cmonth, cday, chour, cminute);
                        startMillis = beginTime.getTimeInMillis();  //  转为机器时间
                        Calendar endTime = Calendar.getInstance();
                        endTime.set(cyear, cmonth, cday, chour, cminute);  // 开始时间
                        endMillis = endTime.getTimeInMillis();  //  转为机器时间
                        ContentResolver cr = getContentResolver();
                        ContentValues values = new ContentValues();
                        Uri updateUri = null;
                        values.put(CalendarContract.Events.DTSTART, startMillis);
                        values.put(CalendarContract.Events.DTEND, endMillis);
                        values.put(CalendarContract.Events.TITLE, editTitle.getText().toString());
                        values.put(CalendarContract.Events.DESCRIPTION, editText.getText().toString());
                        values.put(CalendarContract.Events.CALENDAR_ID, calID);
                        values.put(CalendarContract.Events.HAS_ALARM, 1);//设置有闹钟提醒
                        values.put(CalendarContract.Events.EVENT_TIMEZONE, "Asia/Shanghai");
                        if(eventID.equals("0")){  // 之前没有设定
                            Uri uri = cr.insert(CalendarContract.Events.CONTENT_URI, values); //插入时间
                            // 读取事件 ID，也就是 Uri 的最后一部分
                            long ID = Long.parseLong(uri.getLastPathSegment());
                            record.setEventID(ID+"");
                            ContentResolver recr = getContentResolver();
                            ContentValues remindvalues = new ContentValues();
                            remindvalues.put(CalendarContract.Reminders.MINUTES, 1);
                            remindvalues.put(CalendarContract.Reminders.EVENT_ID, ID);
                            Log.d("UpdateActivity",eventID+" "+ID);
                            remindvalues.put(CalendarContract.Reminders.METHOD, CalendarContract.Reminders.METHOD_ALERT);
                            uri = recr.insert(CalendarContract.Reminders.CONTENT_URI, remindvalues);
                            Log.d("UpdateActivity","eventId0:"+ID+" "+is_remind+" "+remind_date.getText().toString()+" "+remind_time.getText().toString());
                        }else { //之前有设定
                            record.setEventID(eventID);
                            updateUri = ContentUris.withAppendedId(CalendarContract.Events.CONTENT_URI, Long.parseLong(eventID));
                            getContentResolver().update(updateUri, values, null, null);
                            Log.d("UpdateActivity","eventId1:"+eventID+" "+is_remind+" "+remind_date.getText().toString()+" "+remind_time.getText().toString());
                        }
                        record.setIs_remind("1");
                    }

                    Log.d("UpdateActivity","Update is OK!");
                    record.updateAll("id=?", id);
                }
                finish();
                break;
        }
        return true;
    }
}
