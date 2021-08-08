package ru.readme.chatapp.object.responses;

import java.io.Serializable;

/**
 * Created by dima on 05.01.17.
 */

public class ZagsPriceResponse extends BaseResponse implements Serializable{

    private int price;

    public ZagsPriceResponse() {
    }

    public ZagsPriceResponse(String status) {
        super(status);
    }

    public int getPrice() {
        return price;
    }

    public void setPrice(int price) {
        this.price = price;
    }



}