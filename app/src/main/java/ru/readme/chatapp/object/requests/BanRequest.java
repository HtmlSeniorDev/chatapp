package ru.readme.chatapp.object.requests;

import java.io.Serializable;

/**
 * Created by dima on 15.12.16.
 */

public class BanRequest extends BaseRequest implements Serializable {

    private String banned;
    private String description;
    long time;

    public BanRequest() {
    }

    public String getBanned() {
        return banned;
    }

    public void setBanned(String banned) {
        this.banned = banned;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public long getTime() {
        return time;
    }

    public void setTime(long time) {
        this.time = time;
    }

}