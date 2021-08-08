package ru.readme.chatapp.object.requests;

import java.io.Serializable;

/**
 * Created by dima on 05.01.17.
 */

public class ZagsPriceRequest extends BaseRequest implements Serializable {

    private int price;

    public ZagsPriceRequest() {
    }

    public int getPrice() {
        return price;
    }

    public void setPrice(int price) {
        this.price = price;
    }


}