package ru.readme.chatapp.object.responses;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by dima on 28.12.16.
 */

public class NoticesResponse  extends BaseResponse implements Serializable {

    private List<NoticeResponse> notices = new ArrayList();

    public NoticesResponse() {
    }

    public NoticesResponse(String status) {
        super(status);
    }

    public static class Builder {

        public Builder() {
            this.item = new NoticesResponse();
        }
        private NoticesResponse item;

        public Builder setNotices(final List<NoticeResponse> notices) {
            this.item.notices = notices;
            return this;
        }

        public NoticesResponse build() {
            return this.item;
        }
    }

    public List<NoticeResponse> getNotices() {
        return notices;
    }

    public void setNotices(List<NoticeResponse> notices) {
        this.notices = notices;
    }



}
