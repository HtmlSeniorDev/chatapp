package ru.readme.chatapp.util;

import android.content.Context;
import android.provider.Settings;

public class DID {

    public static String DEVICE_ID(Context c) {
        return Settings.Secure.getString(c.getContentResolver(),
                        Settings.Secure.ANDROID_ID);
    }

}