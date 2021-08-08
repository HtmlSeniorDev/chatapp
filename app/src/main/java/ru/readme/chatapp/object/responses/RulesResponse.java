package ru.readme.chatapp.object.responses;

import java.io.Serializable;

/**
 * Created by dima on 05.01.17.
 */

public class RulesResponse extends BaseResponse implements Serializable{

    private String rules;

    public RulesResponse() {
    }

    public RulesResponse(String status) {
        super(status);
    }

    public String getRules() {
        return rules;
    }

    public void setPrice(String rules) {
        this.rules = rules;
    }



}