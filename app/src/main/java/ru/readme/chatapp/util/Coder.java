package ru.readme.chatapp.util;

import android.util.Base64;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.TimeZone;
import ru.readme.chatapp.object.CoderUser;

public class Coder {
    static {
        System.loadLibrary("coder");
    }


    public static String encode(String s, String key) {
        return base64Encode(code(s.getBytes(), key.getBytes()));
    }

    public static String decode(String s, String key) {
        return new String(code(base64Decode(s), key.getBytes()));
    }

    private static byte[] base64Decode(String s) {
        try {
            return Base64.decode(s, Base64.DEFAULT);
        } catch (Exception e) {
            return new byte[0];
        }
    }

    private static String base64Encode(byte[] data) {
       return Base64.encodeToString(data, Base64.DEFAULT);
    }

    public static String codeMessage(String message, CoderUser user) {
        if(user!=null && user.getLogin()!=null && user.getPassword()!=null) {
            String p1 = encode(message, MD5.MD5(user.getLogin()));
            p1 = encode(p1, MD5.MD5(user.getPassword()));
            return p1;
        }else{
            SimpleDateFormat sdf = new SimpleDateFormat("dd.MM.yyyy");
            sdf.setTimeZone(TimeZone.getTimeZone("GMT"));
            String key = sdf.format(new Date());
            String p1 = encode(message, MD5.MD5(key));
            p1 = encode(p1, MD5.MD5(key));
            return p1;
        }
      // return message;
    }

    public static String decodeMessage(String message, CoderUser user) {
        if(user!=null&& user.getLogin()!=null && user.getPassword()!=null) {
            String p1 = decode(message, MD5.MD5(user.getPassword()));
            p1 = decode(p1, MD5.MD5(user.getLogin()));
            return p1;
        }else{
            SimpleDateFormat sdf = new SimpleDateFormat("dd.MM.yyyy");
            sdf.setTimeZone(TimeZone.getTimeZone("GMT"));
            String key = sdf.format(new Date());
            String p1 = decode(message, MD5.MD5(key));
            p1 = decode(p1, MD5.MD5(key));
            return p1;
        }
   //  return message;
    }

    public static native byte[] code(byte[] source, byte[] password);
}
