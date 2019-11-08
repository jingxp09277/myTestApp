package com.ask.commonLib;

import android.content.Context;
import android.content.SharedPreferences;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import android.text.TextUtils;

import java.util.Set;

public class SPUtil {
    private static final String TAG = SPUtil.class.getSimpleName();
    private static SharedPreferences mSp;

    private SPUtil() {
    }

    public static void init(Context context) {
        if (mSp == null) {
            String packageName = context.getApplicationInfo().packageName;
            String[] strings = packageName.split("\\.");
            String name = strings[strings.length - 1];
            mSp = context.getApplicationContext().getSharedPreferences(TextUtils.isEmpty(name) ? "null" : name, Context.MODE_PRIVATE);
        }
    }

    public static void put(@NonNull String key, @Nullable Object object) {
        if (mSp == null) {
            throw new RuntimeException("please call SPUtil.init(Context) before call this method");
        }
        SharedPreferences.Editor editor = mSp.edit();
        try {
            if (object instanceof String) {
                editor.putString(key, (String) object);
            } else if (object instanceof Integer) {
                editor.putInt(key, (Integer) object);
            } else if (object instanceof Boolean) {
                editor.putBoolean(key, (Boolean) object);
            } else if (object instanceof Float) {
                editor.putFloat(key, (Float) object);
            } else if (object instanceof Long) {
                editor.putLong(key, (Long) object);
            } else {
                editor.putString(key, String.valueOf(object));
            }
            editor.apply();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static <V> V get(@NonNull String key, V defaultValue) {
        if (mSp == null) {
            throw new RuntimeException("please call SPUtil.init(Context) before call this method");
        }
        try {
            if (defaultValue instanceof String) {
                return (V)mSp.getString(key, (String) defaultValue);
            } else if (defaultValue instanceof Integer) {
                return (V)(Integer)mSp.getInt(key, (Integer) defaultValue);
            } else if (defaultValue instanceof Boolean) {
                return (V)(Boolean)mSp.getBoolean(key, (Boolean) defaultValue);
            } else if (defaultValue instanceof Float) {
                return (V)(Float)mSp.getFloat(key, (Float) defaultValue);
            } else if (defaultValue instanceof Long) {
                return (V)(Long)mSp.getLong(key, (Long) defaultValue);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return defaultValue;
    }

    public static void putStringSet(@NonNull String key, @Nullable Set<String> set) {
        if (mSp == null) {
            throw new RuntimeException("please call SPUtil.init(Context) before call this method");
        }
        mSp.edit().putStringSet(key, set).apply();
    }

    public static Set<String> getStringSet(@NonNull String key, Set<String> defaultValue) {
        if (mSp == null) {
            throw new RuntimeException("please call SPUtil.init(Context) before call this method");
        }
        return mSp.getStringSet(key, defaultValue);
    }
}
