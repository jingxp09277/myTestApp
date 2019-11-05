package com.example.myapplication.splahView;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.os.AsyncTask;

import android.util.ArrayMap;
import android.util.Log;
import android.view.animation.Animation;

import androidx.annotation.NonNull;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedOutputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;

import static android.content.ContentValues.TAG;

/**
 * Created by Simon on 2018/3/30.
 * Description: ...
 */

public class XsSplashHelper {

    private static XsSplashView xsSplashView;

    private static int newversion;
    private static int StartPicture_switch;
    private static int selectPicture;
    static ArrayMap<String,String> StartPicture = new ArrayMap<>();

    private XsSplashHelper(){}

    public static Builder getBuilder(Activity context){
        return new Builder(context);
    }

    public static void destroy() {
        if (xsSplashView == null)
            return;
        xsSplashView.forceDismiss();
        clear();
    }

    public static void clear(){
        xsSplashView = null;
    }

    public static class Builder {

        private String link;
        private Integer defaultSplashRes;
        private int countDown = 3;
        private int textColorRes = Color.parseColor("#f5f5f5");
        private int textBackgroudColorRes = Color.parseColor("#b6bdcc");
        private int textSizeDp = 10;
        private int textBackgroundSizeDp = 35;
        private int textMarginDp = 12;
        private Animation dismissAnimation;
        private XsSplashView.OnClickSplashListener onClickSplashListener;
        private Activity context;

        public Builder(@NonNull Activity context){
            this.context = context;
        }


        public Builder link(String link){
            this.link = link;
            return this;
        }

        public Builder defaultRes(Integer defaultSplashRes){
            this.defaultSplashRes = defaultSplashRes;
            return this;
        }

        public Builder countDown(int countDown){
            this.countDown = countDown;
            return this;
        }

        public Builder textColorRes(int textColorRes){
            this.textColorRes = textColorRes;
            return this;
        }

        public Builder textBackgroudColorRes(int textBackgroudColorRes){
            this.textBackgroudColorRes = textBackgroudColorRes;
            return this;
        }

        public Builder textSizeDp(int textSizeDp){
            this.textSizeDp = textSizeDp;
            return this;
        }

        public Builder textBackgroundSizeDp(int textBackgroundSizeDp){
            this.textBackgroundSizeDp = textBackgroundSizeDp;
            return this;
        }

        public Builder textMarginDp(int textMarginDp){
            this.textMarginDp = textMarginDp;
            return this;
        }

        public Builder listenr(XsSplashView.OnClickSplashListener onClickSplashListener){
            this.onClickSplashListener = onClickSplashListener;
            return this;
        }

        public Builder dismissAnimation(Animation dismissAnimation){
            this.dismissAnimation = dismissAnimation;
            return this;
        }

        public XsSplashView build(){
            if (xsSplashView == null)
                xsSplashView = new XsSplashView(context);
            xsSplashView.setDefaultSplashRes(defaultSplashRes);
            xsSplashView.setLink(link);
            xsSplashView.setCountdown(countDown);
            xsSplashView.setTextColorRes(textColorRes);
            xsSplashView.setTextBackgroundColorRes(textBackgroudColorRes);
            xsSplashView.setTextSizeDp(textSizeDp);
            xsSplashView.setTextBackgroundSizeDp(textBackgroundSizeDp);
            xsSplashView.setTextMarginDp(textMarginDp);
            if (dismissAnimation != null)
                xsSplashView.setDismissAnimation(dismissAnimation);
            if (onClickSplashListener != null)
                xsSplashView.setOnClickSplashListener(onClickSplashListener);
            return xsSplashView;
        }
    }

    public static void downLoadSplash(Activity context, String adJson) {
        DownLoadTask task = new DownLoadTask(context);
        task.execute(adJson);
    }

    private static class DownLoadTask extends AsyncTask<String, Void, Integer> {

        private Activity activity;

        public DownLoadTask(Activity activity){
            this.activity = activity;
        }

        @Override
        protected void onPostExecute(Integer integer) {
            super.onPostExecute(integer);
        }

        @Override
        protected Integer doInBackground(String... urls) {
            Bitmap bitmap;
            InputStream is = null;
            BufferedOutputStream bos = null;
            try {
                is = getInputStream(urls[0]);

                String result = InputStream2String(is);
                Log.d(TAG, "doInBackground: result "+ result);

                parseJson(result);

                Log.d(TAG, "doInBackground: imgUrl:" + StartPicture.get("imgUrl"));
                is = getInputStream(StartPicture.get("imgUrl"));

                bitmap = BitmapFactory.decodeStream(is);

                File localFile = new File(activity.getFilesDir().getAbsolutePath().toString() + "/splash.jpg");
                bos = new BufferedOutputStream(new FileOutputStream(localFile));
                bitmap.compress(Bitmap.CompressFormat.JPEG, 80, bos);

                SharedPreferences.Editor editor = activity.getSharedPreferences("splashSP", Context.MODE_PRIVATE).edit();
                editor.putString("splashLink", StartPicture.get("url"));
                editor.apply();
                
            } catch (MalformedURLException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            } finally {
                if (is != null) {
                    try {
                        is.close();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
                if (bos != null){
                    try {
                        bos.flush();
                        bos.close();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }
            return 0;
        }

        private InputStream getInputStream(String s) {
            URL url = null;
            InputStream is = null;
            try {
                url = new URL(s);
                HttpURLConnection urlConn = (HttpURLConnection) url.openConnection();
                // 设置连接主机超时时间
                urlConn.setConnectTimeout(5 * 1000);
                //设置从主机读取数据超时
                urlConn.setReadTimeout(5 * 1000);
                urlConn.setDoInput(true);
                urlConn.setRequestMethod("GET");
                urlConn.connect();
                if (urlConn.getResponseCode() == 200) {
                    // 获取返回的数据
                    is = urlConn.getInputStream();
                    Log.e(TAG, "Get方式请求成功，result--->" + is);
                } else {
                    Log.e(TAG, "Get方式请求失败:"+ urlConn.getResponseCode());
                }
            } catch (MalformedURLException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }
            return is;
        }


        private void parseJson(String result) {
            try {
                JSONArray json = new JSONArray(result);
                for(int i=0;i<json.length();i++)
                {
                    JSONObject jb=json.getJSONObject(i);
                    if(jb.has("newVersion")){
                    Log.d("newVersion", jb.getString("newVersion"));
                    newversion = jb.getInt("newVersion");}
                    if(jb.has("StartPicture_switch")){
                        StartPicture_switch = jb.getInt("StartPicture_switch");
                    Log.d("StartPicture_switch", jb.getString("StartPicture_switch"));}
                    if(jb.has("selectPicture")){
                        selectPicture = jb.getInt("selectPicture");
                    }
                    if(jb.has("StartPicture")) {
                        JSONArray innerjba = jb.getJSONArray("StartPicture");
                        if(selectPicture < innerjba.length()) {
                            JSONObject innerjb = innerjba.getJSONObject(selectPicture);

                            Log.d("name", innerjb.getString("name"));
                            Log.d("imgUrl", innerjb.getString("imgUrl"));
                            Log.d("url", innerjb.getString("url"));

                            StartPicture.put("name", innerjb.getString("name"));
                            StartPicture.put("imgUrl", innerjb.getString("imgUrl"));
                            StartPicture.put("url", innerjb.getString("url"));
                        }
                    }
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }

        private String InputStream2String(InputStream in) throws IOException {


            ByteArrayOutputStream bos = new ByteArrayOutputStream();

            //读取缓存
            byte[] buffer = new byte[2048];
            int length = 0;
            while((length = in.read(buffer)) != -1) {
                bos.write(buffer, 0, length);//写入输出流
            }


            // 根据输出流创建字符串对象
            return new String(bos.toByteArray(), "UTF-8");
            //or
            //bos.toString("UTF-8");
        }
    }

}
