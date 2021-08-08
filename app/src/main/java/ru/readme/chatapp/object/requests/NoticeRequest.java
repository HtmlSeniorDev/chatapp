package ru.readme.chatapp.object.requests;

/**
 * Created by dima on 28.12.16.
 */


public class NoticeRequest extends BaseRequest{

    private String userId;
    private String message;

    public NoticeRequest() {
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }




}
