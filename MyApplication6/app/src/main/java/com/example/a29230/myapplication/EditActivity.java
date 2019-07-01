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

import java.io.FileNotFoundException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.TimeZone;

import android.provider.CalendarContract.Events;

public class EditActivity extends AppCompatActivity{

    // 声明全局变量，局部变量无法被调用
    private static final String TAG = "EditActivity";
    private EditText editTitle;
    private TextView datetext;
    private EditText editText;
    private String title = "";
    private String pubdate = "";
    private String text = "";
    private FloatingActionsMenu floatingActionsMenu;
    private FloatingActionButton floatingActionButtonaddpic;
    private FloatingActionButton floatingActionButtontakephoto;
    private FloatingActionButton floatingActionButtonaddremind;

    public static final int CHOOSE_PHOTO = 2;

    public static final int TAKE_PHONT = 1;
    private Uri imageUri;

    private LinearLayout remind_linearLayout;
    private boolean is_remind = false;
    private Calendar c;
    private TextView remind_date;
    private TextView remind_time;
    public int cyear;
    public int cmonth;
    public int cday;
    public int chour;
    public  int cminute;

    private long startMillis = 0;
    private long endMillis = 0;

    private static String CALENDER_URL = "content://com.android.calendar/calendars";
    private static String CALENDER_EVENT_URL = "content://com.android.calendar/events";
    private static String CALENDER_REMINDER_URL = "content://com.android.calendar/reminders";

    private static String CALENDARS_NAME = "个人话务";
    private static String CALENDARS_ACCOUNT_NAME = "个人话务";
    private static String CALENDARS_ACCOUNT_TYPE = "个人话务";
    private static String CALENDARS_DISPLAY_NAME = "个人话务";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit);

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

        // 插入图片
        floatingActionButtonaddpic.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Toast.makeText(EditActivity.this,"add_pic",Toast.LENGTH_LONG).show();
                if(ContextCompat.checkSelfPermission(EditActivity.this, Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED){
                    ActivityCompat.requestPermissions(EditActivity.this, new String[]{
                            Manifest.permission.WRITE_EXTERNAL_STORAGE},1);
                }else {
                    openAlbum();
                }
            }
        });
        // 插入拍照图片
        /*
        floatingActionButtontakephoto.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
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
                    imageUri = FileProvider.getUriForFile(EditActivity.this,"com.example.a29230.myapplication.fileprovider", outputImage);
                    Log.d("EditActivity", imageUri.toString());
                }else {
                    imageUri = Uri.fromFile(outputImage);
                    Log.d("EditActivity", imageUri.toString());
                }
                // 启动相机程序
                Intent intent = new Intent("android.media.action.IMAGE_CAPTURE");
                intent.putExtra(MediaStore.EXTRA_OUTPUT, imageUri);
                startActivityForResult(intent, TAKE_PHONT);
            }
        });*/

        remind_linearLayout = (LinearLayout)findViewById(R.id.remind_linear);
        floatingActionButtonaddremind.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(EditActivity.this, "add_remind", Toast.LENGTH_LONG).show();
                if(is_remind == false){
                    is_remind = true;
                    remind_linearLayout.setVisibility(View.VISIBLE); //显示设定日期
                    floatingActionButtonaddremind.setIcon(R.drawable.pause);
                }else {
                    is_remind = false;
                    remind_linearLayout.setVisibility(View.GONE);
                    floatingActionButtonaddremind.setIcon(R.drawable.bell);
                }
            }
        });


        remind_date = (TextView)findViewById(R.id.remind_date);
        remind_time = (TextView)findViewById(R.id.remind_time);

        //
        c = Calendar.getInstance();
        c.setTimeInMillis(System.currentTimeMillis());
        cyear = c.get(Calendar.YEAR);
        cmonth = c.get(Calendar.MONTH);
        cday = c.get(Calendar.DAY_OF_MONTH);
        chour = c.get(Calendar.HOUR_OF_DAY);
        cminute = c.get(Calendar.MINUTE);


        remind_date.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new DatePickerDialog(EditActivity.this, new DatePickerDialog.OnDateSetListener() {
                    @Override
                    public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
                        c.setTimeInMillis(System.currentTimeMillis());
                        c.set(Calendar.YEAR, year);
                        c.set(Calendar.MONTH, month);
                        c.set(Calendar.DAY_OF_MONTH,dayOfMonth);
                        Toast.makeText(EditActivity.this, c.get(Calendar.YEAR) + "-" +
                                        c.get(Calendar.MONTH)+ "-" + c.get(Calendar.DAY_OF_MONTH), Toast.LENGTH_SHORT).show();
                        remind_date.setText(" "+ c.get(Calendar.YEAR)+"-"+ (c.get(Calendar.MONTH) + 1) +"-"+c.get(Calendar.DAY_OF_MONTH));
                        cyear = c.get(Calendar.YEAR);
                        cmonth = c.get(Calendar.MONTH);
                        cday = c.get(Calendar.DAY_OF_MONTH);
                    }
                },cyear,cmonth,cday).show();
            }
        });
        remind_time.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new TimePickerDialog(EditActivity.this, new TimePickerDialog.OnTimeSetListener() {
                    @Override
                    public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
                        c.setTimeInMillis(System.currentTimeMillis());
                        c.set(Calendar.HOUR_OF_DAY, hourOfDay);
                        c.set(Calendar.MINUTE, minute);
                        c.set(Calendar.SECOND, 0);
                        c.set(Calendar.MILLISECOND, 0);
                        Toast.makeText(EditActivity.this, c.get(Calendar.HOUR_OF_DAY) + ":"
                                + c.get(Calendar.MINUTE), Toast.LENGTH_SHORT).show();
                        remind_time.setText(" "+c.get(Calendar.HOUR_OF_DAY)+":"+c.get(Calendar.MINUTE)+" ");
                        chour = c.get(Calendar.HOUR_OF_DAY);
                        cminute = c.get(Calendar.MINUTE);
                    }
                }, chour, cminute, true).show();
            }
        });

        // 自动生成日期
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy年MM月dd日 HH:mm:ss");// HH:mm:ss
        Date date = new Date(System.currentTimeMillis());
        datetext = (TextView)findViewById(R.id.date);
        datetext.setText(simpleDateFormat.format(date));
        // 绑定相关 EditText
        editTitle = (EditText)findViewById(R.id.edittitle);
        editText = (EditText)findViewById(R.id.edittext);

        // EditText 编辑焦点
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

    // 显示图片
    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        // super.onActivityResult(requestCode, resultCode, data);
        switch (requestCode){
            case TAKE_PHONT:
                if(resultCode == RESULT_OK){
                    try{
                        //将拍摄的照片显示出来
                        Bitmap bitmap = BitmapFactory.decodeStream(getContentResolver().openInputStream(imageUri));
                        Log.d("EditActivity",imageUri.getPath());
                        insertImg(imageUri.getPath());
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
        // ----------------------------------------------------------------------------------------
        /*
        String tagPath = "<img src=\"" + imagePath + "\"/>";//为图片路径加上<img>标签
        Bitmap bitmap = BitmapFactory.decodeFile(imagePath);
        if (bitmap != null) {
            SpannableString ss = getBitmapMime(imagePath,tagPath);
            insertPhotoToEditText(ss);
        }
        */
        // ----------------------------------------------------------------------------------------
    }

    private void handleImageBeforeKitKat(Intent data){
        Uri uri = data.getData();
        String imagePath = getImagePath(uri, null);
        displayImage(imagePath);
        insertImg(imagePath);
        // ----------------------------------------------------------------------------------------
        /*
        String tagPath = "<img src=\"" + imagePath + "\"/>";//为图片路径加上<img>标签
        Bitmap bitmap = BitmapFactory.decodeFile(imagePath);
        if (bitmap != null) {
            SpannableString ss = getBitmapMime(imagePath,tagPath);
            insertPhotoToEditText(ss);
        }
        */
        // ----------------------------------------------------------------------------------------
    }

    private void insertImg(String path){
        String tagPath = "<img src=\""+path+"\"/>";//为图片路径加上<img>标签
        Bitmap bitmap = BitmapFactory.decodeFile(path);
        if(bitmap != null){
            SpannableString ss = getBitmapMime(path,tagPath);
            insertPhotoToEditText(ss);
            editText.append("\n");
            Log.d("EditActivity", editText.getText().toString());
        }
    }

    /**
     * 将图片插入到EditText中
     * @param ss
     */
    private void insertPhotoToEditText(SpannableString ss) {

        Html.ImageGetter imageGetter = new Html.ImageGetter(){
            @Override
            public Drawable getDrawable(String s) {
                int width = ScreenUtils.getScreenWidth(EditActivity.this);
                int height = ScreenUtils.getScreenHeight(EditActivity.this);
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
        editText.setSelection(editText.length()); //
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

        int width = ScreenUtils.getScreenWidth(EditActivity.this);
        int height = ScreenUtils.getScreenHeight(EditActivity.this);


        Bitmap bitmap = ImageUtils.getSmallBitmap(path,width,height);
        ImageSpan imageSpan = new ImageSpan(this, bitmap);
        ss.setSpan(imageSpan, 0, tagPath.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        return ss;
    }

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

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        title = editTitle.getText().toString();
        pubdate  = datetext.getText().toString();
        text  = editText.getText().toString();
        switch (item.getItemId()){
            case android.R.id.home:
                finish();
                break;
            case R.id.tick:
                if(!editTitle.getText().toString().trim().isEmpty()&&!editText.getText().toString().trim().isEmpty()){
                    Record record = new Record();
                    record.setTitle(title);
                    record.setDate(pubdate);
                    record.setText(Html.toHtml(editText.getText()));
                    if(!remind_date.getText().toString().isEmpty()&&!remind_time.getText().toString().isEmpty()){
                        Calendar begin_calendar = Calendar.getInstance();
                        record.setRemind_date(remind_date.getText().toString());
                        record.setRemind_time(remind_time.getText().toString());

                        long calID = checkAndAddCalendarAccount(EditActivity.this);

                        Calendar beginTime = Calendar.getInstance();
                        // 这是开始提醒时间
                        beginTime.set(cyear, cmonth, cday, chour, cminute);
                        startMillis = beginTime.getTimeInMillis();  //  转为机器时间

                        Calendar endTime = Calendar.getInstance();
                        endTime.set(cyear, cmonth, cday, chour, cminute);  // 开始时间
                        endMillis = endTime.getTimeInMillis();  //  转为机器时间

                        Log.d("EditActivity",startMillis+"");
                        Log.d("EditActivity",endMillis+"");

                        ContentResolver cr = getContentResolver();
                        ContentValues values = new ContentValues();

                        values.put(Events.DTSTART, startMillis);
                        values.put(Events.DTEND, endMillis);

                        values.put(Events.TITLE, editTitle.getText().toString());
                        values.put(Events.DESCRIPTION, editText.getText().toString());
                        values.put(Events.CALENDAR_ID, calID);
                        values.put(Events.HAS_ALARM, 1);//设置有闹钟提醒
                        values.put(Events.EVENT_TIMEZONE, "Asia/Shanghai");
                        Uri uri = cr.insert(Events.CONTENT_URI, values); //插入时间

                        // 读取事件 ID，也就是 Uri 的最后一部分
                        long eventID = Long.parseLong(uri.getLastPathSegment());

                        record.setEventID(eventID+"");
                        Log.d("EditActivity", eventID+"is True");
                        ContentResolver recr = getContentResolver();
                        ContentValues remindvalues = new ContentValues();
                        remindvalues.put(CalendarContract.Reminders.MINUTES, 1);
                        remindvalues.put(CalendarContract.Reminders.EVENT_ID, eventID);
                        remindvalues.put(CalendarContract.Reminders.METHOD, CalendarContract.Reminders.METHOD_ALERT);
                        uri = recr.insert(CalendarContract.Reminders.CONTENT_URI, remindvalues);
                        record.setIs_remind("1");
                    }else {
                        record.setEventID("0");
                        record.setIs_remind("0");
                    }
                    record.save();
                    Log.d("EditActivity","Save is OK!");
                }
                finish();
        }
        return true;
    }
}
