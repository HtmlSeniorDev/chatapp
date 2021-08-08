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
public class LoginRequest extends BaseRequest implements Serializable{
    
    private String login;
    private String password;



    public LoginRequest() {
    }

    private LoginRequest(Builder builder) {
        setLogin(builder.login);
        setPassword(builder.password);
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


    public static final class Builder {
        private String login;
        private String password;

        public Builder() {
        }

        public Builder setLogin(String val) {
            login = val;
            return this;
        }

        public Builder setPassword(String val) {
            password = val;
            return this;
        }

        public LoginRequest build() {
            return new LoginRequest(this);
        }
    }
}
