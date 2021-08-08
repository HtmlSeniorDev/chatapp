/*
 * Херня ебаная
 * Эта ебаная херня скорее всего может наебнуться, но мне глубоко похую на это
 * Если это кто-то читает, то в данный момент вероятно я уже пью пиво и разобраться с багами смогу только завтра
 * Если "завтра" уже наступило, читаем выше еще раз
 */
package ru.readme.chatapp.object.responses;

import java.io.Serializable;

/**
 *
 * @author dima
 */
public class LoginResponse extends BaseResponse implements Serializable {

    private String token;
    private long tokenCreated;
    private long tokenEnd;

    public LoginResponse() {
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public long getTokenCreated() {
        return tokenCreated;
    }

    public void setTokenCreated(long tokenCreate) {
        this.tokenCreated = tokenCreate;
    }

    public long getTokenEnd() {
        return tokenEnd;
    }

    public void setTokenEnd(long tokenEnd) {
        this.tokenEnd = tokenEnd;
    }

}
