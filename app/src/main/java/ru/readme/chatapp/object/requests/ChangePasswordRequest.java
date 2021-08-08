package ru.readme.chatapp.object.requests;

import java.io.Serializable;

/**
 * Created by dima on 04.01.17.
 */

public class ChangePasswordRequest extends BaseRequest implements Serializable {
    private String newPassword;
    private String oldCoded;

    public ChangePasswordRequest() {
    }

    public String getNewPassword() {
        return newPassword;
    }

    public void setNewPassword(String newPassword) {
        this.newPassword = newPassword;
    }

    public String getOldCoded() {
        return oldCoded;
    }

    public void setOldCoded(String oldCoded) {
        this.oldCoded = oldCoded;
    }



}
