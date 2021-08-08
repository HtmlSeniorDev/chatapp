package ru.readme.chatapp.object.responses;

import java.io.Serializable;

/**
 * Created by dima on 21.12.16.
 */

public class AvatarResponse extends BaseResponse implements Serializable{

    private String id;
    private String name;
    private int price;

    public AvatarResponse() {
    }

    public AvatarResponse(String status) {
        super(status);
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

    public int getPrice() {
        return price;
    }

    public void setPrice(int price) {
        this.price = price;
    }
}
