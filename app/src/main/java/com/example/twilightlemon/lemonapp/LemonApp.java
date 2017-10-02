package com.example.twilightlemon.lemonapp;
import android.app.Activity;
import android.app.DownloadManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.IntentSender;
import android.content.SharedPreferences;
import android.content.res.Resources;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.PixelFormat;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.graphics.Rect;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.StrictMode;
import android.renderscript.Allocation;
import android.renderscript.Element;
import android.renderscript.RenderScript;
import android.renderscript.ScriptIntrinsicBlur;
import android.support.annotation.NonNull;
import android.support.annotation.RequiresApi;
import android.support.design.widget.BottomNavigationView;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.text.format.DateUtils;
import android.util.AttributeSet;
import android.util.Base64;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.webkit.CookieManager;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.SeekBar;
import android.widget.SimpleAdapter;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ImageView;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.util.EntityUtils;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLConnection;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.TimerTask;
import java.util.concurrent.Exchanger;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static android.R.id.list;
import static android.graphics.Color.parseColor;

public class LemonApp extends AppCompatActivity {
    MediaPlayer mediaPlayer = new MediaPlayer();
    private LrcView lrcBig;
    private Handler handler = new Handler( );
    private Runnable runnable = new Runnable( ) {
        @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN)
        public void run ( ) {
            if(mediaPlayer.isPlaying()==false) {
                Toast.makeText(getApplicationContext(), "UUUUHHHMM",
                        Toast.LENGTH_SHORT).show();
                handler.removeCallbacks(runnable);
                MListSeleindex++;
                list.setItemChecked(MListSeleindex, true);
                //获得选中项的HashMap对象
                MseekBar.setProgress(0);
                lrcBig.updateTime(0);
                HashMap<String, String> map = (HashMap<String, String>) list.getItemAtPosition(MListSeleindex);
                String title = map.get("ItemTitle").replace("[SQ]","").replace("[HQ]","");
                String content = map.get("ItemText");
                MusicText.setText(title + "-" + content);
                MButton.setText("暂停");
                MseekBar.setMax(mediaPlayer.getDuration());
                try {
                    GetMusicLyric(MusicIDData.get(MListSeleindex));
                    URL url = new URL("http://y.gtimg.cn/music/photo_new/T002R300x300M000" + MImageIDData.get(MListSeleindex) + ".jpg");
                    URLConnection conn = url.openConnection();
                    conn.connect();
                    InputStream in = conn.getInputStream();
                    Bitmap bmp = BitmapFactory.decodeStream(in);
                    MImage.setBackground(new BitmapDrawable(bmp));
                    handler.postDelayed(runnable, 500);
                    mediaPlayer.stop();
                    mediaPlayer = new MediaPlayer();
                    File f=new File(SDPATH+"LemonApp/MusicDownload/"+MusicText.getText()+".m4a");
                    if(f.exists())
                        mediaPlayer.setDataSource(f.toString());
                    else mediaPlayer.setDataSource("http://cc.stream.qqmusic.qq.com/C100" + MusicIDData.get(MListSeleindex) + ".m4a?fromtag=52");
                    mediaPlayer.prepare();//缓冲
                    mediaPlayer.start();//开始或恢复播放
                } catch (Exception e) {
                }
            }else{if(isChanging==false) {
                MseekBar.setProgress(mediaPlayer.getCurrentPosition()); MseekBar.setMax(mediaPlayer.getDuration());
                lrcBig.updateTime(mediaPlayer.getCurrentPosition());
            }}
            handler.postDelayed(this,500);     //postDelayed(this,1000)方法安排一个Runnable对象到主线程队列中
        }
    };
    boolean isChanging=false;
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            moveTaskToBack(false);
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }
    private void GetMusicLyric(String ID)  {
        String lyricdata="";
        lrcBig.reset();
        lrcBig.initEntryList();
        lrcBig.initNextTime();
        try{
        HttpGet httpRequest = new HttpGet("https://c.y.qq.com/lyric/fcgi-bin/fcg_query_lyric_new.fcg?callback=MusicJsonCallback_lrc&pcachetime=1494070301711&songmid="+ID+"&g_tk=5381&jsonpCallback=MusicJsonCallback_lrc&loginUin=0&hostUin=0&format=jsonp&inCharset=utf8&outCharset=utf-8&notice=0&platform=yqq&needNewCode=0");
        httpRequest.setHeader("User-Agent", "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/57.0.2987.110 Safari/537.36");
        httpRequest.setHeader("Accept", "*/*");
        httpRequest.setHeader("Referer", "https://y.qq.com/portal/player.html");
        httpRequest.setHeader("Cookie", "tvfe_boss_uuid=c3db0dcc4d677c60; pac_uid=1_2728578956; qq_slist_autoplay=on; ts_refer=ADTAGh5_playsong; RK=pKOOKi2f1O; pgv_pvi=8927113216; o_cookie=2728578956; pgv_pvid=5107924810; ptui_loginuin=2728578956; ptcz=897c17d7e17ae9009e018ebf3f818355147a3a26c6c67a63ae949e24758baa2d; pt2gguin=o2728578956; pgv_si=s5715204096; qqmusic_fromtag=66; yplayer_open=1; ts_last=y.qq.com/portal/player.html; ts_uid=996779984; yq_index=0");
        httpRequest.setHeader("Host", "c.y.qq.com");
        HttpResponse httpResponse = new DefaultHttpClient().execute(httpRequest);
        if(httpResponse.getStatusLine().getStatusCode() == 200){
            String Ct=EntityUtils.toString(httpResponse.getEntity(),"utf-8");
            String Ctlyric=Ct.substring(Ct.indexOf("\"lyric\":\"")+9,Ct.indexOf("\",\""));
            lyricdata = new String(Base64.decode(Ctlyric.getBytes(), Base64.DEFAULT));
            if(!Ct.contains("\"trans\":\"\"")){
            String Cttrans=Ct.substring(Ct.indexOf("\"trans\":\"")+9,Ct.indexOf("\"})"));
            String transdata =new String(Base64.decode(Cttrans.getBytes(), Base64.DEFAULT));
           // SendMessageBox(lyricdata +transdata);
            ArrayList<String> datatimes=new ArrayList<String>();
            ArrayList<String> datatexs=new ArrayList<String>();
            String[] dt=lyricdata.split("[\n]");
            for (String x:dt) {
              parserLine(x,datatimes,datatexs);
            }
            //sdm("d1");
            ArrayList<String> dataatimes=new ArrayList<String>();
            ArrayList<String> dataatexs=new ArrayList<String>();
            String[] dta=transdata.split("[\n]");
            for (String x:dta) {
               parserLine(x,dataatimes,dataatexs);
            }
              //  sdm("d2");
            ArrayList<String> KEY=new ArrayList<String>();
            ArrayList<String> VALUE=new ArrayList<String>();
            List<LrcEntry> list=new ArrayList<>();
            for (String x:datatimes) {
                KEY.add(x);
            }
                //sdm("d3");
            for (String x:dataatimes) {
                if(!KEY.contains(x))
                  KEY.add(x);
            }
                //sdm("d4");
            for(int i=0;i!=dataatimes.size();i++){
                try{
                String dsta=datatexs.get(i)+"^"+dataatexs.get(i);
                VALUE.add(dsta);}catch(Exception e){}
            }
                //sdm("d5   "+dataatexs.size()+"   "+dataatimes.size()+"   "+datatexs.size()+"   "+datatimes.size()+"   "+KEY.size());
            for(int i=0;i!=KEY.size();i++){
                try{
                    long key=strToTime(KEY.get(i));
                    String value =VALUE.get(i);
                    list.add(new LrcEntry(key,value.replace("^","\n").replace("//","")));
            }catch(Exception e){}
            }
            lrcBig.reset();
            lrcBig.initEntryList();
            lrcBig.initNextTime();
            lrcBig.onLrcLoaded(list);
            }
        }
        }catch(Exception e){
            SendMessageBox(e.getMessage().toString());}
    }
    private long strToTime(String ts)
    {
        long time=0;
        Matcher timeMatcher = Pattern.compile("(\\d\\d):(\\d\\d)\\.(\\d\\d)").matcher(ts);
        while (timeMatcher.find()) {
            long min = Long.parseLong(timeMatcher.group(1));
            long sec = Long.parseLong(timeMatcher.group(2));
            long mil = Long.parseLong(timeMatcher.group(3));
            time = min * DateUtils.MINUTE_IN_MILLIS + sec * DateUtils.SECOND_IN_MILLIS + mil * 10;
        }
        return time;
    }
    public void SendMessageBox(String msg){
        AlertDialog.Builder dlg = new AlertDialog.Builder(LemonApp.this);
        dlg.setTitle("提示");
        dlg.setMessage(msg);
        dlg.setPositiveButton("确定",null);
        dlg.show();
    }
    public void sdm(String m){
        Toast.makeText(getApplicationContext(), m,
                Toast.LENGTH_SHORT).show();
    }
    public String parserLine(String str,ArrayList<String> times,ArrayList<String>texs){
        if (!str.startsWith("[ti:")&&!str.startsWith("[ar:")&&!str.startsWith("[al:")&&!str.startsWith("[by:")&&!str.startsWith("[offset:")){
            String TimeData=Text(str,"[","]",0,0)+"0";
            times.add(TimeData);
            String INFO=Text(str,"[","]",0,1);
            String io="["+INFO+"]";
            String TexsData=str.replace(io,"");
            texs.add(TexsData);
            return TimeData+"     "+TexsData;
        }else return "";
    }
    public static String Text(String all, String r, String l, int t,int f)
    {

        int A = all.indexOf(r, t);
        int B = all.indexOf(l, A + 1)+f;
        if (A < 0 || B < 0)
        {
            return null;
        }
        else
        {
            A = A + r.length();
            B = B - A;
            if (A < 0 || B < 0)
            {
                return null;
            }
            return all.substring(A, B);
        }

    }
    private BottomNavigationView.OnNavigationItemSelectedListener mOnNavigationItemSelectedListener
            = new BottomNavigationView.OnNavigationItemSelectedListener() {

        @Override
        public boolean onNavigationItemSelected(@NonNull MenuItem item) {
            switch (item.getItemId()) {
                case R.id.navigation_home:
                    findViewById(R.id.MinePage).setVisibility(View.GONE);
                    findViewById(R.id.RobotPage).setVisibility(View.GONE);
                    findViewById(R.id.MusicPage).setVisibility(View.VISIBLE);
                    return true;
                case R.id.navigation_dashboard:
                    findViewById(R.id.MinePage).setVisibility(View.GONE);
                    findViewById(R.id.RobotPage).setVisibility(View.VISIBLE);
                    findViewById(R.id.MusicPage).setVisibility(View.GONE);
                    return true;
                case R.id.navigation_notifications:
                    findViewById(R.id.MinePage).setVisibility(View.VISIBLE);
                    findViewById(R.id.RobotPage).setVisibility(View.GONE);
                    findViewById(R.id.MusicPage).setVisibility(View.GONE);
                    return true;
            }
            return false;
        }

    };
    ArrayList<String> MusicIDData=new ArrayList<String>();
    String SDPATH="";
    ArrayList<String> MImageIDData=new ArrayList<String>();
    int MListSeleindex=0;
    ListView list;
    TextView MusicText;
    Button MButton;
    SeekBar MseekBar;
    RelativeLayout MImage;
    private void SearchMusic(String text){
        try{
            String url="http://59.37.96.220/soso/fcgi-bin/client_search_cp?format=json&t=0&inCharset=GB2312&outCharset=utf-8&qqmusic_ver=1302&catZhida=0&p=1&n=20&w="+ URLEncoder.encode(text, "utf-8")+"&flag_qc=0&remoteplace=sizer.newclient.song&new_json=1&lossless=0&aggr=1&cr=1&sem=0&force_zonghe=0";
            HttpGet httpRequest = new HttpGet(url);
            HttpResponse httpResponse = new DefaultHttpClient().execute(httpRequest);
            if(httpResponse.getStatusLine().getStatusCode() == 200){
                String json = EntityUtils.toString(httpResponse.getEntity()).replace("<em>","").replace("</em>","");
                JSONObject jo = new JSONObject(json);
                int i=0;
                MusicIDData.clear();
                MImageIDData.clear();
                ArrayList<HashMap<String, String>> mylist = new ArrayList<HashMap<String, String>>();
                while (i < jo.getJSONObject("data").getJSONObject("song").getJSONArray("list") .length())
                {
                    JSONArray ja=jo.getJSONObject("data").getJSONObject("song").getJSONArray("list");
                    JSONObject jos=ja.getJSONObject(i);
                    String name=jos.getString("name")+"";
                    String isx="";
                    for(int ix=0;ix!=jos.getJSONArray("singer").length();ix++){
                        isx+=jos.getJSONArray("singer").getJSONObject(ix).getString("name")+"/";
                    }
                    String Singer=isx.substring(0, isx.lastIndexOf("/"));
                    String MusicID=jos.getString("mid")+"";
                    String ImageID=jos.getJSONObject("album").getString("mid")+"";
                    String Fotmat=jos.getJSONObject("file").getString("size_flac")+"";
                    String HQfotmat=jos.getJSONObject("file").getString("size_ogg")+"";
                    String Q="";
                    if (Fotmat != "0")
                        Q = "[SQ]";
                    if (HQfotmat != "0")
                        if (Fotmat == "0")
                            Q = "[HQ]";
                    MusicIDData.add(MusicID);
                    MImageIDData.add(ImageID);
                    HashMap<String, String> map = new HashMap<String, String>();
                    map.put("ItemTitle", name+" "+Q);
                    map.put("ItemText", Singer);
                    mylist.add(map);
                    i++;
                }
                SimpleAdapter mSchedule = new SimpleAdapter(LemonApp.this, mylist,
                        R.layout.my_listitem,
                        new String[] {"ItemTitle", "ItemText","MusicIndex"},
                        new int[] {R.id.ItemTitle,R.id.ItemText,R.id.MusicIndex});
                list.setAdapter(mSchedule);
            }else{Toast.makeText(getBaseContext(),"Ds", Toast.LENGTH_SHORT).show();}
        }catch(Exception e){SendMessageBox(e.getMessage());}
    }
    public void MusicDownloadOnClick(View view){
        try{
        RelativeLayout PATENT= (RelativeLayout)view.getParent();
        int index= Integer.parseInt(((TextView)PATENT.findViewById(R.id.MusicIndex)).getText().toString());
        String downloadUrl="http://cc.stream.qqmusic.qq.com/C100"+MusicIDData.get(index)+".m4a?fromtag=52";
        HashMap<String,String> map=(HashMap<String,String>)list.getItemAtPosition(index);
        String name=(map.get("ItemTitle").replace("[SQ]","").replace("[HQ]","")+"-"+map.get("ItemText")+".m4a").replace("\\","").replace("/","");
            DownloadManager downloadManager = (DownloadManager)getSystemService(DOWNLOAD_SERVICE);
            DownloadManager.Request request = new
            DownloadManager.Request(Uri.parse(downloadUrl));
            request.setDestinationInExternalPublicDir("LemonApp/MusicDownload", name);
            request.setTitle(name);
            request.setDescription("小萌音乐正在下载中");
            request.setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED);
            downloadManager.enqueue(request);
        sdm("正在下载:"+name);}catch(Exception e){SendMessageBox(e.getMessage());}
    }
    EditText RobotText;
    Button RobotSendButton;
    ListView RobotContent;
    ArrayList<String> RTEXT=new ArrayList<String>();
    ArrayList<String> UTEXT=new ArrayList<String>();
    public static Bitmap getHttpBitmap(String url){
        URL myFileURL;
        Bitmap bitmap=null;
        try{
            myFileURL = new URL(url);
            //获得连接
            HttpURLConnection conn=(HttpURLConnection)myFileURL.openConnection();
            //设置超时时间为6000毫秒，conn.setConnectionTiem(0);表示没有时间限制
            conn.setConnectTimeout(0);
            //连接设置获得数据流
            conn.setDoInput(true);
            //不使用缓存
            conn.setUseCaches(false);
            //这句可有可无，没有影响
            //conn.connect();
            //得到数据流
            InputStream is = conn.getInputStream();
            //解析得到图片
            bitmap = BitmapFactory.decodeStream(is);
            //关闭数据流
            is.close();
        }catch(Exception e){
            e.printStackTrace();
        }
        return bitmap;
    }
    @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setSoftInputMode
                (WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN|
                        WindowManager.LayoutParams.SOFT_INPUT_ADJUST_PAN);
        setContentView(R.layout.activity_lemon_app);
        SDPATH = Environment.getExternalStorageDirectory().getPath() + "//";
        if (mediaPlayer.isPlaying()) {
            mediaPlayer.reset();
        }
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            getWindow().addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
        }
        findViewById(R.id.USERNAME).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                final EditText qq = new EditText(LemonApp.this);
                qq.setHint("QQ账号");
                AlertDialog.Builder builder = new AlertDialog.Builder(LemonApp.this);
                builder.setTitle("登录到小萌").setView(qq)
                        .setNegativeButton("关闭", null);
                builder.setPositiveButton("完成", new DialogInterface.OnClickListener() {

                    @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN)
                    public void onClick(DialogInterface dialog, int which) {
                        try{//((CircleImageView)findViewById(R.id.USERTX)).setImageBitmap(getHttpBitmap("http://q2.qlogo.cn/headimg_dl?bs=qq&dst_uin="+qq.getText().toString()+"&spec=100"));
                            final File f=new File(SDPATH+"LemonApp/UserCache/"+qq.getText().toString()+".jpg");
                            if(f.exists()){
                                ((CircleImageView)findViewById(R.id.USERTX)).setImageBitmap(BitmapFactory.decodeFile(f.toString()));
                            }else{
                                final DownloadManager downloadManager = (DownloadManager)getSystemService(DOWNLOAD_SERVICE);
                                DownloadManager.Request request = new DownloadManager.Request(Uri.parse("http://q2.qlogo.cn/headimg_dl?bs=qq&dst_uin="+qq.getText().toString()+"&spec=100"));
                                request.setDestinationInExternalPublicDir("LemonApp/UserCache",qq.getText().toString()+".jpg");
                                final long id= downloadManager.enqueue(request);
                                LemonApp.this.registerReceiver(new BroadcastReceiver() {
                                                                   @Override
                                                                   public void onReceive(Context context, Intent intent) {
                                                                       DownloadManager.Query query = new DownloadManager.Query();
                                                                       query.setFilterById(id);
                                                                       Cursor c = downloadManager.query(query);
                                                                       if (c.moveToFirst()) {
                                                                           if(c.getInt(c.getColumnIndex(DownloadManager.COLUMN_STATUS))==DownloadManager.STATUS_SUCCESSFUL)
                                                                           {
                                                                               ((CircleImageView)findViewById(R.id.USERTX)).setImageBitmap(BitmapFactory.decodeFile(f.toString()));
                                                                           }
                                                                       }
                                                                   }
                                                               },
                                        new IntentFilter(DownloadManager.ACTION_DOWNLOAD_COMPLETE));
                            }
                            String name="";
                            String json = HtmlService.getHtml("http://r.pengyou.com/fcg-bin/cgi_get_portrait.fcg?uins="+qq.getText().toString(),false).replace("portraitCallBack(","").replace(")","");
                            JSONObject jo=new JSONObject(json);
                            name=jo.getJSONArray(qq.getText().toString()).getString(6);
                        ((TextView)findViewById(R.id.USERNAME)).setText(name);
                        SharedPreferences preferences = LemonApp.this.getSharedPreferences("Cookie", Context.MODE_PRIVATE);
                        SharedPreferences.Editor editor = preferences.edit();
                        editor.putString("qq",qq.getText().toString());
                        editor.putString("name",name);
                        editor.commit();}catch(Exception e){}
                    }
                });
                builder.show();
            }
        });
        RobotText= (EditText) findViewById(R.id.RobotText);
        RobotSendButton = (Button) findViewById(R.id.RobotSendButton);
       RobotContent= (ListView) findViewById(R.id.RobotContent);
        RobotSendButton.setOnClickListener(new View.OnClickListener() {
           @Override
            public void onClick(View view) {
              try{
                    SharedPreferences sp = LemonApp.this.getSharedPreferences("Cookie", Context.MODE_PRIVATE);
                    RobotAdapter ra;
                    if(sp.contains("name")) {
                        JSONObject jo=new JSONObject(HtmlService.getHtml("http://www.tuling123.com/openapi/api?key=0651b32a3a6c8f54c7869b9e62872796&info=" + URLEncoder.encode(RobotText.getText().toString(),"utf-8")+"&userid="+sp.getString("qq", ""),true));
                        RTEXT.add(jo.getString("text"));
                        UTEXT.add(RobotText.getText().toString());
                        ra = new RobotAdapter(LemonApp.this, RTEXT, UTEXT,BitmapFactory.decodeFile(SDPATH+"LemonApp/UserCache/"+sp.getString("qq", "")+".jpg"), sp.getString("name", ""));
                    }else {
                        JSONObject jo=new JSONObject(HtmlService.getHtml("http://www.tuling123.com/openapi/api?key=0651b32a3a6c8f54c7869b9e62872796&info=" + URLEncoder.encode(RobotText.getText().toString(),"utf-8"),true));
                        RTEXT.add(jo.getString("text"));
                        UTEXT.add(RobotText.getText().toString());
                        ra = new RobotAdapter(LemonApp.this, RTEXT, UTEXT, BitmapFactory.decodeResource(getResources(), R.mipmap.icon), "小萌用户");
                    }RobotContent.setAdapter(ra);
                    RobotText.setText("");
                    RobotContent.setSelection(RobotContent.getBottom());
                    ((InputMethodManager)getSystemService(Context.INPUT_METHOD_SERVICE)).hideSoftInputFromWindow(LemonApp.this.getCurrentFocus().getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);
                }catch(Exception e){}
            }
        });
        ImageButton MGDButton= (ImageButton) findViewById(R.id.MGDButton);
        MGDButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                    EditText dt=(EditText) findViewById(R.id.music_search);
                    String ID=dt.getText().toString();
                    try{
                        HttpGet httpRequest = new HttpGet("https://c.y.qq.com/qzone/fcg-bin/fcg_ucc_getcdinfo_byids_cp.fcg?type=1&json=1&utf8=1&onlysong=0&disstid="+ID+"&format=json&g_tk=1157737156&loginUin=2728578956&hostUin=0&format=json&inCharset=utf8&outCharset=utf-8&notice=0&platform=yqq&needNewCode=0");
                        httpRequest.setHeader("User-Agent", "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/57.0.2987.110 Safari/537.36");
                        httpRequest.setHeader("Accept", "*/*");
                        httpRequest.setHeader("Referer", "https://y.qq.com/portal/player.html");
                        httpRequest.setHeader("Cookie", "pgv_pvi=1693112320; RK=DKOGai2+wu; pgv_pvid=1804673584; ptcz=3a23e0a915ddf05c5addbede97812033b60be2a192f7c3ecb41aa0d60912ff26; pgv_si=s4366031872; _qpsvr_localtk=0.3782697029073365; ptisp=ctc; luin=o2728578956; lskey=00010000863c7a430b79e2cf0263ff24a1e97b0694ad14fcee720a1dc16ccba0717d728d32fcadda6c1109ff; pt2gguin=o2728578956; uin=o2728578956; skey=@PjlklcXgw; p_uin=o2728578956; p_skey=ROnI4JEkWgKYtgppi3CnVTETY3aHAIes-2eDPfGQcVg_; pt4_token=wC-2b7WFwI*8aKZBjbBb7f4Am4rskj11MmN7bvuacJQ_; p_luin=o2728578956; p_lskey=00040000e56d131f47948fb5a2bec49de6174d7938c2eb45cb224af316b053543412fd9393f83ee26a451e15; ts_refer=ui.ptlogin2.qq.com/cgi-bin/login; ts_last=y.qq.com/n/yqq/playlist/2591355982.html; ts_uid=1420532256; yqq_stat=0");
                        httpRequest.setHeader("Host", "c.y.qq.com");
						HttpResponse httpResponse = new DefaultHttpClient().execute(httpRequest);
                        if(httpResponse.getStatusLine().getStatusCode() == 200){
                            String json = EntityUtils.toString(httpResponse.getEntity());
                            JSONObject jo = new JSONObject(json);
                            int i=0;
                            MusicIDData.clear();
                            MImageIDData.clear();
                            ArrayList<HashMap<String, String>> mylist = new ArrayList<HashMap<String, String>>();
                            while (i < jo.getJSONArray("cdlist") .getJSONObject(0).getJSONArray("songlist").length())
                            {
                                JSONArray ja=jo.getJSONArray("cdlist") .getJSONObject(0).getJSONArray("songlist");
                                JSONObject jos=ja.getJSONObject(i);
                                String name=jos.getString("songname");
                                String isx="";
                                for(int ix=0;ix!=jos.getJSONArray("singer").length();ix++){
                                    isx+=jos.getJSONArray("singer").getJSONObject(ix).getString("name")+"/";
                                }
                                String Singer=isx.substring(0, isx.lastIndexOf("/"));
                                String MusicID=jos.getString("songmid");
                                String ImageID=jos.getString("albummid") ;
                                String Fotmat=jos.getString("sizeflac");
                                String HQfotmat=jos.getString("size320");
                                String Q="";
                                if (Fotmat != "0")
                                    Q = "[SQ]";
                                if (HQfotmat != "0")
                                    if (Fotmat == "0")
                                        Q = "[HQ]";
                                MusicIDData.add(MusicID);
                                MImageIDData.add(ImageID);
                                HashMap<String, String> map = new HashMap<String, String>();
                                map.put("ItemTitle", name+" "+Q);
                                map.put("ItemText", Singer);
                                map.put("MusicIndex",i+"");
                                mylist.add(map);
                                i++;
                            }
                            SimpleAdapter mSchedule = new SimpleAdapter(LemonApp.this, mylist,
                                    R.layout.my_listitem,
                                    new String[] {"ItemTitle", "ItemText","MusicIndex"},
                                    new int[] {R.id.ItemTitle,R.id.ItemText,R.id.MusicIndex});
                            list.setAdapter(mSchedule);
                        }
                    }
                    catch(Exception e){SendMessageBox(e.getMessage());}
            }
        });
        lrcBig = (LrcView) findViewById(R.id.lrc);
        MusicText= (TextView) findViewById(R.id.MusicText);
        MButton = (Button) findViewById(R.id.MButton);
        MseekBar= (SeekBar) findViewById(R.id.MusicSeek);
        MImage = (RelativeLayout) findViewById(R.id.MImage);
        getSupportActionBar().hide();
        findViewById(R.id.MDownloadALL).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(list.getCount()>0)
                {
                    sdm("正在下载全部");
                    for(int i=0;i!=list.getCount();i++){
                        HashMap<String,String> map=(HashMap<String,String>)list.getItemAtPosition(i);
                        new AsyncTask<String[], Integer, Void>(){
                            @Override
                            protected Void doInBackground(String[]... params) {
                                DownloadManager downloadManager = (DownloadManager)getSystemService(DOWNLOAD_SERVICE);
                                DownloadManager.Request request = new DownloadManager.Request(Uri.parse(params[0][0]));
                                request.setDestinationInExternalPublicDir("LemonApp/MusicDownload", params[0][1]);
                                request.setTitle(params[0][1]);
                                request.setDescription("小萌音乐正在下载中");
                                request.setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED);
                                downloadManager.enqueue(request);
                                return null;
                            }
                        }.execute(new String[]{"http://cc.stream.qqmusic.qq.com/C100"+MusicIDData.get(i)+".m4a?fromtag=52",(map.get("ItemTitle").replace("[SQ]","").replace("[HQ]","")+"-"+map.get("ItemText")+".m4a").replace("\\","").replace("/","")});
                    }
                }
            }
        });
        StrictMode.setThreadPolicy(new StrictMode.ThreadPolicy.Builder().detectDiskReads().detectDiskWrites().detectNetwork().penaltyLog().build());
        BottomNavigationView navigation = (BottomNavigationView) findViewById(R.id.navigation);
        navigation.setOnNavigationItemSelectedListener(mOnNavigationItemSelectedListener);
        MseekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int i, boolean b) {

            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
                isChanging=true;
            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                mediaPlayer.seekTo(MseekBar.getProgress());
                isChanging=false;
            }
        });
        MButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(MButton.getText()=="暂停")
                {handler.removeCallbacks(runnable);MButton.setText("播放");mediaPlayer.pause();}
                else{handler.postDelayed(runnable,1000);MButton.setText("暂停");mediaPlayer.start();}
            }
        });
      //  list.setDividerHeight(1);
        final EditText et= (EditText) findViewById(R.id.music_search);
        et.setText("2591355982");
        et.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                SearchMusic(et.getText().toString());
                return false;
            }
        });
        list = (ListView) findViewById(R.id.music_List);
        list.setOnItemClickListener(new AdapterView.OnItemClickListener(){
            @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN)
            @Override
            public void onItemClick(AdapterView<?> arg0, View arg1, int arg2,
                                    long arg3) {
                //获得选中项的HashMap对象
                handler.removeCallbacks(runnable);
                MseekBar.setProgress(0);
                lrcBig.updateTime(0);
                HashMap<String,String> map=(HashMap<String,String>)list.getItemAtPosition(arg2);
                String title = map.get("ItemTitle").replace("[SQ]","").replace("[HQ]","");
                String content=map.get("ItemText");
                MusicText.setText(title+"-"+content);
                MButton.setText("暂停");
                MseekBar.setMax(mediaPlayer.getDuration());
                try{
                GetMusicLyric(MusicIDData.get(arg2));
                URL url = new URL("http://y.gtimg.cn/music/photo_new/T002R500x500M000"+MImageIDData.get(arg2)+".jpg");
                URLConnection conn = url.openConnection();
                conn.connect();
                InputStream in = conn.getInputStream();
                Bitmap bmp = BitmapFactory.decodeStream(in);
                MImage.setBackground(new BitmapDrawable(bmp));
                handler.postDelayed(runnable,500);
                MListSeleindex=arg2;
                mediaPlayer.stop();
                mediaPlayer=new MediaPlayer();
                File f=new File(SDPATH+"LemonApp/MusicDownload/"+MusicText.getText()+".m4a");
                if(f.exists())
                    mediaPlayer.setDataSource(f.toString());
                else mediaPlayer.setDataSource("http://cc.stream.qqmusic.qq.com/C100"+MusicIDData.get(arg2)+".m4a?fromtag=52");
                mediaPlayer.prepare();//缓冲
                mediaPlayer.start();//开始或恢复播放
                }catch(Exception e){SendMessageBox(e.getMessage());}
            }

        });
        SharedPreferences sp = LemonApp.this.getSharedPreferences("Cookie", Context.MODE_PRIVATE);
        if(sp.contains("name")) {
            ((TextView) findViewById(R.id.USERNAME)).setText(sp.getString("name", ""));
            final File f=new File(SDPATH+"LemonApp/UserCache/"+sp.getString("qq", "")+".jpg");
            if(f.exists()) {
                ((CircleImageView) findViewById(R.id.USERTX)).setImageBitmap(BitmapFactory.decodeFile(f.toString()));
            }
        }try{
            String data = HtmlService.getHtml("http://git.oschina.net/TwilightLemon/Updata/raw/master/AndroidUpdata.au",true);
            final Double v=Double.parseDouble(Text(data,"-","-",0,1));
            String c="       "+Text(data,"+","+",0,1).replace(".","\n");
            if(1.2<v){
                final TextView tv=new TextView(this);
                tv.setText("       新版本:"+v+"\n"+c);
                final AlertDialog.Builder builder = new AlertDialog.Builder(LemonApp.this);
                builder.setTitle("小萌有新版本啦").setView(tv)
                        .setNegativeButton("关闭", null);
                builder.setPositiveButton("更新", new DialogInterface.OnClickListener() {
                    @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN)
                    public void onClick(DialogInterface dialog, int which) {
                        final String name="LemonUpdata.apk";
                        File f=new File(SDPATH+"LemonApp/Cache/"+name);
                        if(f.exists())
                            f.delete();
                        final DownloadManager downloadManager = (DownloadManager)getSystemService(DOWNLOAD_SERVICE);
                        DownloadManager.Request request = new DownloadManager.Request(Uri.parse("http://git.oschina.net/TwilightLemon/Updata/raw/master/LemonUpdata"+v+".apk"));
                        request.setDestinationInExternalPublicDir("LemonApp/Cache",name);
                        request.setTitle(name);
                        request.setDescription("小萌更新包正在下载中");
                        request.setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED);
                        final long id= downloadManager.enqueue(request);
                        LemonApp.this.registerReceiver(new BroadcastReceiver() {
                                                           @Override
                                                           public void onReceive(Context context, Intent intent) {
                                                               DownloadManager.Query query = new DownloadManager.Query();
                                                               query.setFilterById(id);
                                                               Cursor c = downloadManager.query(query);
                                                               if (c.moveToFirst()) {
                                                                   if(c.getInt(c.getColumnIndex(DownloadManager.COLUMN_STATUS))==DownloadManager.STATUS_SUCCESSFUL)
                                                                   {
                                                                       Intent in = new Intent(Intent.ACTION_VIEW);
                                                                       Uri uri = Uri.parse("file://LemonApp/Cache/"+name);
                                                                       in.setDataAndType(uri, "application/vnd.android.package-archive");
                                                                       in.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                                                                       LemonApp.this.startActivity(in);
                                                                   }
                                                               }
                                                           }
                                                       },
                                new IntentFilter(DownloadManager.ACTION_DOWNLOAD_COMPLETE));
                    }
                });
                builder.show();
            }
        }catch(Exception e){}
        try {
            final RelativeLayout UBG= (RelativeLayout) findViewById(R.id.UBG);
            TextView PTX= (TextView) findViewById(R.id.PTX);
            JSONObject obj = new JSONObject(HtmlService.getHtml("http://www.bing.com/HPImageArchive.aspx?format=js&idx=0&n=1&mkt=zh-CN",true));
            String url="http://cn.bing.com" +obj.getJSONArray("images").getJSONObject(0).getString("url");
            String ivt =obj.getJSONArray("images").getJSONObject(0).getString("copyright");
            PTX.setText(ivt);
            final String name=Text(url,"/az/hprichbg/rb/",".jpg",0,1)+".jpg";
            SharedPreferences preferences = LemonApp.this.getSharedPreferences("Cookie", Context.MODE_PRIVATE);
            SharedPreferences.Editor editor = preferences.edit();
            editor.putString("ivt",ivt);
            editor.putString("ivn",name);
            editor.commit();
            File f=new File(SDPATH+"LemonApp/Cache/"+name);
            if(f.exists()){
                UBG.setBackground(BitmapDrawable.createFromPath(SDPATH+"LemonApp/Cache/"+name));
            }else{
                final DownloadManager downloadManager = (DownloadManager)getSystemService(DOWNLOAD_SERVICE);
                DownloadManager.Request request = new DownloadManager.Request(Uri.parse(url));
                request.setDestinationInExternalPublicDir("LemonApp/Cache",name);
                final long id= downloadManager.enqueue(request);
                LemonApp.this.registerReceiver(new BroadcastReceiver() {
                                                   @Override
                                                   public void onReceive(Context context, Intent intent) {
                                                       DownloadManager.Query query = new DownloadManager.Query();
                                                       query.setFilterById(id);
                                                       Cursor c = downloadManager.query(query);
                                                       if (c.moveToFirst()) {
                                                           if(c.getInt(c.getColumnIndex(DownloadManager.COLUMN_STATUS))==DownloadManager.STATUS_SUCCESSFUL)
                                                           {
                                                               UBG.setBackground(BitmapDrawable.createFromPath(SDPATH+"LemonApp/Cache/"+name));
                                                           }
                                                       }
                                                   }
                                               },
                        new IntentFilter(DownloadManager.ACTION_DOWNLOAD_COMPLETE));
            }
        }catch(Exception e){
            SharedPreferences sps = LemonApp.this.getSharedPreferences("Cookie", Context.MODE_PRIVATE);
            File f=new File(SDPATH+"LemonApp/Cache/"+sps.getString("ivn",""));
            if(f.exists()){
                if(sps.contains("ivt")){
                RelativeLayout UBG= (RelativeLayout) findViewById(R.id.UBG);
                TextView PTX= (TextView) findViewById(R.id.PTX);
                UBG.setBackground(BitmapDrawable.createFromPath(f.toString()));
                PTX.setText(sps.getString("ivt",""));
                }
            }
        }
        //SendMessageBox(sp.getString("name","")+"\n"+sp.getString("qq",""));
    }
}
