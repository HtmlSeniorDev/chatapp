package ru.readme.chatapp.object.responses;

/**
 * Created by dima on 28.12.16.
 */

import java.io.Serializable;
import java.util.Date;

/**
 *
 * @author dima
 */
public class NoticeResponse extends BaseResponse implements Serializable{
    private UserResponse noticed;
    private String message;
    private Date date;
    private String id;


    public NoticeResponse() {
    }

    public NoticeResponse(String status) {
        super(status);
    }

    public UserResponse getNoticed() {
        return noticed;
    }

    public void setNoticed(UserResponse noticed) {
        this.noticed = noticed;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public Date getDate() {
        return date;
    }

    public void setDate(Date date) {
        this.date = date;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }



    public static class Builder {

        public Builder() {
            this.item = new NoticeResponse();
        }
        private NoticeResponse item;

        public Builder setNoticed(final UserResponse noticed) {
            this.item.noticed = noticed;
            return this;
        }

        public Builder setMessage(final String message) {
            this.item.message = message;
            return this;
        }

        public Builder setDate(final Date date) {
            this.item.date = date;
            return this;
        }

        public Builder setId(final String id) {
            this.item.id = id;
            return this;
        }

        public NoticeResponse build() {
            return this.item;
        }
    }




}
