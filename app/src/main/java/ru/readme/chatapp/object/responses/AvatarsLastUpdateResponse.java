package ru.readme.chatapp.object.responses;

import java.io.Serializable;
import java.util.Date;

/**
 * Created by dima on 21.12.16.
 */

public class AvatarsLastUpdateResponse extends BaseResponse implements Serializable {

    private Date update;

    public AvatarsLastUpdateResponse() {
    }

    public AvatarsLastUpdateResponse(Date update) {
        this.update = update;
    }

    public Date getUpdate() {
        return update;
    }

    public void setUpdate(Date update) {
        this.update = update;
    }

    public AvatarsLastUpdateResponse(String status) {
        super(status);
    }

}
