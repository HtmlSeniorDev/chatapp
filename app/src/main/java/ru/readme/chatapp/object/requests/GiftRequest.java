package ru.readme.chatapp.object.requests;

import java.io.Serializable;

/**
 * Created by dima on 04.01.17.
 */

public class GiftRequest extends BaseRequest implements Serializable {

    private String id;
    private String name;
    private String description;
    private int price;

    public GiftRequest() {
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
