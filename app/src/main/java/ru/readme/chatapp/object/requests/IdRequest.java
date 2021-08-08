package ru.readme.chatapp.object.requests;

/**
 * Created by dima on 31.12.16.
 */

public class IdRequest extends BaseRequest{

    private String id;

    public IdRequest(String id) {
        this.id = id;
    }

    public IdRequest() {
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }



}
