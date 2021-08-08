package ru.readme.chatapp.object.responses;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by dima on 21.12.16.
 */

public class AvatarsResponse extends BaseResponse implements Serializable{

    private List<AvatarResponse> avatars = new ArrayList<AvatarResponse>();

    public AvatarsResponse() {
    }

    public AvatarsResponse(String status) {
        super(status);
    }



    public List<AvatarResponse> getAvatars() {
        return avatars;
    }

    public void setAvatars(List<AvatarResponse> avatars) {
        this.avatars = avatars;
    }
}
