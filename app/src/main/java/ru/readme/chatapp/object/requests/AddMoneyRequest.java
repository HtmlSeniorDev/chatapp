package ru.readme.chatapp.object.requests;

import java.io.Serializable;

/**
 * Created by dima on 04.01.17.
 */

public class AddMoneyRequest extends BaseRequest implements Serializable {
    private String sum;
    private String user;

    public AddMoneyRequest() {
    }

    public String getSum() {
        return sum;
    }

    public void setSum(String sum) {
        this.sum= sum;
    }

    public String getU() {
        return user;
    }

    public void setUser(String user) {
        this.user= user;
    }



}
