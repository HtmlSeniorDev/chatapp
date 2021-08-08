package ru.readme.chatapp.object.responses;

import java.io.Serializable;

/**
 * Created by dima on 04.01.17.
 */

public class GiftResponse extends BaseResponse implements Serializable {

    private String id;
    private String name;
    private String description;
    private int price;
    private UserResponse fromuser;

    public GiftResponse() {
    }

    public GiftResponse(String status) {
        super(status);
    }

    public static class Builder {

        public Builder() {
            this.item = new GiftResponse();
        }
        private GiftResponse item;

        public Builder setId(final String id) {
            this.item.id = id;
            return this;
        }

        public Builder setName(final String name) {
            this.item.name = name;
            return this;
        }

        public Builder setDescription(final String description) {
            this.item.description = description;
            return this;
        }

        public Builder setPrice(final int price) {
            this.item.price = price;
            return this;
        }

        public GiftResponse build() {
            return this.item;
        }
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public UserResponse getFromuser() {
        return fromuser;
    }

    public void setFromuser(UserResponse fromuser) {
        this.fromuser = fromuser;
    }



    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public int getPrice() {
        return price;
    }

    public void setPrice(int price) {
        this.price = price;
    }



}