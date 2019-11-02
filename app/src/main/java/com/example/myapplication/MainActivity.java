package com.example.myapplication;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.ScaleAnimation;

import com.example.myapplication.splahView.XsSplashHelper;
import com.example.myapplication.splahView.XsSplashView;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

//        CircleSmartImageView test=(CircleSmartImageView) findViewById(R.id.test);
        //test.setImageUrl("http://b.hiphotos.baidu.com/baike/w%3D268%3Bg%3D0/sign=92e00c9b8f5494ee8722081f15ce87c3/29381f30e924b899c83ff41c6d061d950a7bf697.jpg");
//        test.setImageUrl("http://a.hiphotos.baidu.com/image/pic/item/838ba61ea8d3fd1fc9c7b6853a4e251f94ca5f46.jpg");
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        XsSplashHelper.destroy();
    }

    public void showSplash(View view) {
        updateLocalSplash(view);
        ScaleAnimation myAnimation_Scale;
        myAnimation_Scale =new ScaleAnimation(1f, 0f, 1f, 0f,
                Animation.RELATIVE_TO_SELF, 0.5f, Animation.RELATIVE_TO_SELF, 0.5f);
        myAnimation_Scale.setDuration(1000);

        XsSplashHelper.getBuilder(this)
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
                        Log.e("info","jumpOver:-> ");
                    }

                    @Override
                    public void clickSplash(String link) {
                        Log.e("info","clickSplash:-> ");
                        WebActivity.start(MainActivity.this,link);
                    }
                })
                .build().show();
    }

    public void updateLocalSplash(View view) {
        XsSplashHelper.downLoadSplash(MainActivity.this,"http://39.105.37.124:80/iYou_start.json"
                );



    }
}
