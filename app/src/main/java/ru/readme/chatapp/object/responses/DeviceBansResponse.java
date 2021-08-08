package ru.readme.chatapp.object.responses;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by dima on 23.01.17.
 */

public class DeviceBansResponse extends BaseResponse implements Serializable{

    private List<String> ids = new ArrayList<String>();

    public DeviceBansResponse() {
    }

    public DeviceBansResponse(String status) {
        super(status);
    }

    public List<String> getIds() {
        return ids;
    }

    public void setIds(List<String> ids) {
        this.ids = ids;
    }



}