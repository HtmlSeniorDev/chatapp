package ru.readme.chatapp.object.requests;

import java.io.Serializable;

/**
 * Created by dima on 04.01.17.
 */

public class UserGiftRequest extends BaseRequest implements Serializable {
    private String id;
    private String toUser;
    private boolean anon = false;

    public UserGiftRequest() {
    }

    public boolean isAnon() {
        return anon;
    }

    public void setAnon(boolean anon) {
        this.anon = anon;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getToUser() {
        return toUser;
    }

    public void setToUser(String toUser) {
        this.toUser = toUser;
    }


}
