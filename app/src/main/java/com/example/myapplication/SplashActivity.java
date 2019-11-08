package com.example.myapplication;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.ScaleAnimation;

import com.example.myapplication.splahView.XsSplashHelper;
import com.example.myapplication.splahView.XsSplashView;

public class SplashActivity extends Activity {
    private Activity activity;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);

        activity = this;

        /*还没有加载布局是睡眠1秒，确保黑屏或白屏效果明显*/
        try {
            Thread.sleep(1000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        mHandler.postDelayed(new Runnable() {
            @Override
            public void run() {
//                Intent intent = new Intent(activity, AdsActivity.class);
//                startActivity(intent);
                showSplash();
                //finish();
//                overridePendingTransition(R.anim.fade, R.anim.hold);
            }
        }, 1000);

    }

    private Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
        }
    };

    public void showSplash() {
        updateLocalSplash();
        ScaleAnimation myAnimation_Scale;
        myAnimation_Scale = new ScaleAnimation(1f, 0f, 1f, 0f,
                Animation.RELATIVE_TO_SELF, 0.5f, Animation.RELATIVE_TO_SELF, 0.5f);
        myAnimation_Scale.setDuration(1000);
        XsSplashView xsSplashView = XsSplashHelper.getBuilder(this)
//                .link("https://simonrepo.github.io")
//                .defaultRes(R.mipmap.ic_launcher)//若不设置默认图 且本地无广告的情况下 则不会显示广告页
                .countDown(5)//若不设置 则默认倒计时3S
//                .textSizeDp(10)
//                .textBackgroundSizeDp(35)
//                .textMarginDp(12)
//                .textColorRes(Color.parseColor("#ffffff"))
//                .textBackgroudColorRes(Color.parseColor("#888888"))
//                .dismissAnimation(myAnimation_Scale)//若不设置 则默认为淡出动画
                .listenr(new XsSplashView.OnClickSplashListener() {
                    @Override
                    public void jumpOver() {
                        Log.e("info", "jumpOver:-> ");
                        Intent intent = new Intent(activity,MainActivity.class);
                        activity.startActivity(intent);
                        finish();
                        overridePendingTransition(R.anim.fade, R.anim.hold);
                    }

                    @Override
                    public void clickSplash(String link) {
                        Log.e("info", "clickSplash:-> ");
                        //WebActivity.start(activity, link);
//                        Intent intent = new Intent(activity,MainActivity.class);
//                        activity.startActivity(intent);
                        activity.startActivity(generateIntent(Intent.ACTION_VIEW, "http://www.baidu.com", Intent.CATEGORY_DEFAULT));

                        finish();
                        overridePendingTransition(R.anim.fade, R.anim.hold);
                    }
                })
                .build();
        xsSplashView.show();
        if(!xsSplashView.isShow()){
            Intent intent = new Intent(activity,MainActivity.class);
            activity.startActivity(intent);
            finish();
            overridePendingTransition(R.anim.fade, R.anim.hold);
        }


    }

    public void updateLocalSplash() {
        XsSplashHelper.downLoadSplash(activity, "http://39.105.37.124:80/iYou_start.json");

    }

    private Intent generateIntent(String action, String data, String category) {
        Intent intent = new Intent();
        if (!TextUtils.isEmpty(action)) intent.setAction(action);
        if (!TextUtils.isEmpty(data)) intent.setData(Uri.parse(data));
        if (!TextUtils.isEmpty(category)) intent.addCategory(category);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        return intent;
    }

}
