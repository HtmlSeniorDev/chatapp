package ru.readme.chatapp.object.requests;

import java.io.Serializable;

/**
 * Created by dima on 22.12.16.
 */

public class SetDefaultAvatarRequest extends BaseRequest implements Serializable {
    private String avatar;
    private int group;

    public SetDefaultAvatarRequest() {
    }

    public String getAvatar() {
        return avatar;
    }

    public void setAvatar(String avatar) {
        this.avatar = avatar;
    }

    public int getGroup() {
        return group;
    }

    public void setGroup(int group) {
        this.group = group;
    }
}
