/*
 * Херня ебаная
 * Эта ебаная херня скорее всего может наебнуться, но мне глубоко похую на это
 * Если это кто-то читает, то в данный момент вероятно я уже пью пиво и разобраться с багами смогу только завтра
 * Если "завтра" уже наступило, читаем выше еще раз
 */
package ru.readme.chatapp.object.requests;

import java.io.Serializable;

/**
 *
 * @author dima
 */
public class RegistrationRequest  extends BaseRequest implements Serializable {
    private String login;
    private String password;
    private String nic;
    private String email;
    private int color;
    private String name;
    private String fname;
    private String devid;



    public RegistrationRequest() {
    }

    public String getLogin() {
        return login;
    }

    public String getName() {
        return name;
    }
    public String getFname() {
        return fname;
    }

    public void setLogin(String login) {
        this.login = login;
    }

    public void setName(String name) {
        this.name = name;
    }
    public void setFname(String fname) {
        this.fname = fname;
    }
    public void setDevId(String devid) {
        this.devid = devid;
    }
    public String getDevId() {
        return devid;
    }
    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getNic() {
        return nic;
    }

    public String getEmail() {
        return email;
    }

    public void setNic(String nic) {
        this.nic = nic;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public int getColor() {
        return color;
    }

    public void setColor(int color) {
        this.color = color;
    }
    
    
}
