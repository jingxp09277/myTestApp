package com.ask.commonLib;

import android.content.Context;
import android.os.Handler;
import android.os.Message;
import android.widget.Toast;


public class OtherUtil {
    public static void toast(final Context context, final CharSequence text) {
        new Handler(context.getMainLooper()).post(new Runnable() {
            @Override
            public void run() {
                Toast.makeText(context.getApplicationContext(), text, Toast.LENGTH_SHORT).show();
            }
        });
    }

    public static void toast(final Context context, final int resId) {
        toast(context, context.getText(resId));
    }

    public static void removeMessage(Handler handler, int what) {
        handler.removeMessages(what);
    }

    public static void sendMessage(Handler handler, int what) {
        sendMessage(handler, what, 0);
    }

    public static void sendMessage(Handler handler, int what, long interval) {
        Message message = Message.obtain();
        message.what = what;
        sendMessage(handler, message, interval);
    }

    public static void sendMessage(Handler handler, Message message) {
        sendMessage(handler, message, 0);
    }

    public static void sendMessage(Handler handler, Message message, long interval) {
        handler.removeMessages(message.what);
        if (interval <= 0) {
            handler.sendMessage(message);
        } else {
            handler.sendMessageDelayed(message, interval);
        }
    }

    public static String getDeviceBrand() {
        return android.os.Build.BRAND;
    }
}
