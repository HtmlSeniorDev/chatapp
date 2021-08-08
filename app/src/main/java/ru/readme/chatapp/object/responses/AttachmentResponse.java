/*
 * Херня ебаная
 * Эта ебаная херня скорее всего может наебнуться, но мне глубоко похую на это
 * Если это кто-то читает, то в данный момент вероятно я уже пью пиво и разобраться с багами смогу только завтра
 * Если "завтра" уже наступило, читаем выше еще раз
 */
package ru.readme.chatapp.object.responses;

import java.io.Serializable;

/**
 * @author dima
 */
public class AttachmentResponse extends BaseResponse implements Serializable {

    private String id;
    private String fileName;

    public AttachmentResponse() {
    }

    public String getFileName() {
        return fileName;
    }

    public void setFileName(String fileName) {
        this.fileName = fileName;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }


    public static class Builder {

        private AttachmentResponse item;

        public Builder() {
            this.item = new AttachmentResponse();
        }

        public Builder setStatus(final String status) {
            this.item.status = status;
            return this;
        }

        public Builder setId(final String id) {
            this.item.id = id;
            return this;
        }

        public Builder setFileName(final String fileName) {
            this.item.fileName = fileName;
            return this;
        }

        public AttachmentResponse build() {
            return this.item;
        }
    }

}
