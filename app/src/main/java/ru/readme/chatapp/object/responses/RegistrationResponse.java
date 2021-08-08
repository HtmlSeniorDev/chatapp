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
public class RegistrationResponse extends BaseResponse implements Serializable {


    private String token;
    private long tokenCreated;
    private long tokenEnd;

    public RegistrationResponse() {
    }

    public static class Builder {

        public Builder() {
            this.item = new RegistrationResponse();
        }
        private RegistrationResponse item;

        public Builder setStatus(final String status) {
            this.item.status = status;
            return this;
        }

        public Builder setToken(final String token) {
            this.item.token = token;
            return this;
        }

        public Builder setTokenCreated(final long tokenCreated) {
            this.item.tokenCreated = tokenCreated;
            return this;
        }

        public Builder setTokenEnd(final long tokenEnd) {
            this.item.tokenEnd = tokenEnd;
            return this;
        }

        public RegistrationResponse build() {
            return this.item;
        }
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

    public void setTokenCreated(long tokenCreated) {
        this.tokenCreated = tokenCreated;
    }

    public long getTokenEnd() {
        return tokenEnd;
    }

    public void setTokenEnd(long tokenEnd) {
        this.tokenEnd = tokenEnd;
    }

}
