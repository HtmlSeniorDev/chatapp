package ru.readme.chatapp.object.requests;

import java.io.Serializable;

/**
 * Created by dima on 21.12.16.
 */

public class AvatarRequest extends BaseRequest implements Serializable{

    private String id;
    private String name;
    private int price;

    public AvatarRequest() {
    }

    public int getPrice() {
        return price;
    }

    public void setPrice(int price) {
        this.price = price;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }
}
