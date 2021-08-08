package ru.readme.chatapp.object;

import android.content.Context;
import ru.readme.chatapp.util.SettingsHelper;

public class CoderUser {

    private String login;
    private String password;

    public static CoderUser get(Context context){
        SettingsHelper sh = new SettingsHelper(context);
        return new CoderUser(sh.getLogin(), sh.getPassword());
    }

    public static CoderUser get(SettingsHelper sh){
        return new CoderUser(sh.getLogin(), sh.getPassword());
    }

    public CoderUser(String login, String password) {
        this.login = login;
        this.password = password;
    }

    public CoderUser() {
    }

    public String getLogin() {
        return login;
    }

    public void setLogin(String login) {
        this.login = login;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }
}
