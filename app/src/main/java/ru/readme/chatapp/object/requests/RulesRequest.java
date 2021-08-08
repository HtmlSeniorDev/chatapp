package ru.readme.chatapp.object.requests;

import java.io.Serializable;

/**
 * Created by dima on 05.01.17.
 */

public class RulesRequest extends BaseRequest implements Serializable {

    private String rules;

    public RulesRequest() {
    }

    public String getRules() {
        return rules;
    }

    public void setPrice(String rules) {
        this.rules = rules;
    }


}