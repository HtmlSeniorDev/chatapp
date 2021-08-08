package ru.readme.chatapp.util;

import android.content.Context;

import com.google.gson.Gson;
import ru.readme.chatapp.object.UserType;
import ru.readme.chatapp.object.responses.RoomResponse;

public class SettingsHelper {

    private final String login = "login";
    private final String password = "password";
    private final String token = "token";
    private final String userType = "usertype";
    private final String userId = "userid";
    private final String userNic = "usernic";
    private final String tokenEnd = "tokenEnd";
    private final String tokenCreated = "tokenCreated";
    private final String settings = "settings";
    private final String lastavatarsupdate = "lastavatarsupdate";
    private final String lastchat = "lastchat";

    private final String avassize = "avassize";
    private final String textsize = "textsize";
    private final String chaterssize = "chaterssize";
    private final String roomssize = "roomssize";

    private final String kbhide = "kbhide";
    private final String push = "push";
    private final String autoenter = "autoenter";
    private final String screenlock = "screenlock";

    private Context context;

    public SettingsHelper(Context context) {
        this.context = context;
    }

    public String getLogin() {
        return context.getSharedPreferences(settings, Context.MODE_PRIVATE)
                .getString(this.login, null);
    }

    public boolean setLogin(String lgn) {
        return context.getSharedPreferences(settings, Context.MODE_PRIVATE)
                .edit()
                .putString(login, lgn)
                .commit();
    }

    public boolean setUserId(String id) {
        return context.getSharedPreferences(settings, Context.MODE_PRIVATE)
                .edit()
                .putString(userId, id)
                .commit();
    }

    public String getUserId() {
        return context.getSharedPreferences(settings, Context.MODE_PRIVATE)
                .getString(userId, null);
    }

    public boolean setUserNic(String nic) {
        return context.getSharedPreferences(settings, Context.MODE_PRIVATE)
                .edit()
                .putString(userNic, nic)
                .commit();
    }

    public String getUserNic() {
        return context.getSharedPreferences(settings, Context.MODE_PRIVATE)
                .getString(userNic, null);
    }

    public String getPassword() {
        return context.getSharedPreferences(settings, Context.MODE_PRIVATE)
                .getString(password, null);
    }

    public boolean setPassword(String pass) {
        return context.getSharedPreferences(settings, Context.MODE_PRIVATE)
                .edit()
                .putString(password, pass)
                .commit();
    }

    public String getToken() {
        return context.getSharedPreferences(settings, Context.MODE_PRIVATE)
                .getString(token, null);
    }

    public RoomResponse getLastChat() {
        String cht = context.getSharedPreferences(settings, Context.MODE_PRIVATE)
                .getString(lastchat, null);
        if (cht == null) {
            return null;
        }
        try {
            return new Gson().fromJson(cht, RoomResponse.class);
        } catch (Exception e) {
            return null;
        }
    }

    public boolean setLastChat(RoomResponse chat) {
        if (chat == null) {
            return context.getSharedPreferences(settings, Context.MODE_PRIVATE)
                    .edit()
                    .putString(lastchat, null)
                    .commit();
        } else {
            try {
                return context.getSharedPreferences(settings, Context.MODE_PRIVATE)
                        .edit()
                        .putString(lastchat, new Gson().toJson(chat))
                        .commit();
            } catch (Exception e) {
                return context.getSharedPreferences(settings, Context.MODE_PRIVATE)
                        .edit()
                        .putString(lastchat, null)
                        .commit();
            }
        }

    }

    public boolean setToken(String tkn) {
        return context.getSharedPreferences(settings, Context.MODE_PRIVATE)
                .edit()
                .putString(token, tkn)
                .commit();
    }

    public boolean clear() {
        return context.getSharedPreferences(settings, Context.MODE_PRIVATE)
                .edit()
                .clear()
                .commit();
    }

    public int getUserType() {
        return context.getSharedPreferences(settings, Context.MODE_PRIVATE)
                .getInt(userType, UserType.TYPE_USER);
    }

    public boolean setUserType(int type) {
        return context.getSharedPreferences(settings, Context.MODE_PRIVATE)
                .edit()
                .putInt(userType, type)
                .commit();
    }

    public int getAvasSize() {
        return context.getSharedPreferences(settings, Context.MODE_PRIVATE)
                .getInt(avassize, 36);
    }


    public boolean setAvasSize(int size) {
        return context.getSharedPreferences(settings, Context.MODE_PRIVATE)
                .edit()
                .putInt(avassize, size)
                .commit();
    }

    public int getTextSize() {
        return context.getSharedPreferences(settings, Context.MODE_PRIVATE)
                .getInt(textsize, 8);
    }


    public boolean setTextSize(int size) {
        return context.getSharedPreferences(settings, Context.MODE_PRIVATE)
                .edit()
                .putInt(textsize, size)
                .commit();
    }

    public int getChatersSize() {
        return context.getSharedPreferences(settings, Context.MODE_PRIVATE)
                .getInt( chaterssize, 8);
    }


    public boolean setChatersSize(int size) {
        return context.getSharedPreferences(settings, Context.MODE_PRIVATE)
                .edit()
                .putInt(chaterssize, size)
                .commit();
    }

    public int getRoomsSize() {
        return context.getSharedPreferences(settings, Context.MODE_PRIVATE)
                .getInt(roomssize, 10);
    }


    public boolean setRoomsSize(int size) {
        return context.getSharedPreferences(settings, Context.MODE_PRIVATE)
                .edit()
                .putInt(roomssize, size)
                .commit();
    }

    public long getTokenEnd() {
        return context.getSharedPreferences(settings, Context.MODE_PRIVATE)
                .getLong(tokenEnd, 0);
    }

    public long getAvatarsUpdate() {
        return context.getSharedPreferences(settings, Context.MODE_PRIVATE)
                .getLong(lastavatarsupdate, 0);
    }


    public boolean setTokenEnd(long end) {
        return context.getSharedPreferences(settings, Context.MODE_PRIVATE)
                .edit()
                .putLong(tokenEnd, end)
                .commit();
    }

    public boolean setAvatarsUpdate(long end) {
        return context.getSharedPreferences(settings, Context.MODE_PRIVATE)
                .edit()
                .putLong(lastavatarsupdate, end)
                .commit();
    }

    public long getTokenCreated() {
        return context.getSharedPreferences(settings, Context.MODE_PRIVATE)
                .getLong(tokenCreated, 0);
    }

    public boolean setTokenCreated(long created) {
        return context.getSharedPreferences(settings, Context.MODE_PRIVATE)
                .edit()
                .putLong(tokenCreated, created)
                .commit();
    }


    public boolean getKBHide() {
        return context.getSharedPreferences(settings, Context.MODE_PRIVATE)
                .getBoolean(kbhide, true);
    }

    public boolean setKBHide(boolean b) {
        return context.getSharedPreferences(settings, Context.MODE_PRIVATE)
                .edit()
                .putBoolean(kbhide, b)
                .commit();
    }

    public boolean getPush() {
        return context.getSharedPreferences(settings, Context.MODE_PRIVATE)
                .getBoolean(push, true);
    }

    public boolean setPush(boolean b) {
        return context.getSharedPreferences(settings, Context.MODE_PRIVATE)
                .edit()
                .putBoolean(push, b)
                .commit();
    }

    public boolean getAE() {
        return context.getSharedPreferences(settings, Context.MODE_PRIVATE)
                .getBoolean(autoenter, true);
    }

    public boolean setAE(boolean b) {
        return context.getSharedPreferences(settings, Context.MODE_PRIVATE)
                .edit()
                .putBoolean(autoenter, b)
                .commit();
    }


    public boolean getSL() {
        return context.getSharedPreferences(settings, Context.MODE_PRIVATE)
                .getBoolean(screenlock, true);
    }

    public boolean setSL(boolean b) {
        return context.getSharedPreferences(settings, Context.MODE_PRIVATE)
                .edit()
                .putBoolean(screenlock, b)
                .commit();
    }

}
