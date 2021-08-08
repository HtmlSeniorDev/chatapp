package ru.readme.chatapp.object.requests;

import java.io.Serializable;

import ru.readme.chatapp.activity.App;
import ru.readme.chatapp.object.CoderUser;
import ru.readme.chatapp.util.Coder;
import ru.readme.chatapp.util.Network;
import ru.readme.chatapp.util.Truster;

/**
 * Created by dima on 09.12.16.
 */

public class BaseRequest implements Serializable {

    private String signed;
    private final String deviceId = App.generateSystemCode;



    public String getSigned() {
        return signed;
    }

    public String getDeviceId() {
        return deviceId;
    }

    public void setSigned(String signed) {
        this.signed = signed;
    }

    public BaseRequest() {
    }

    public  static String code(BaseRequest o, CoderUser coder){
        if(o!=null){
            o.setSigned(Truster.generate());
        }
        String json = Network.gson().toJson(o);
        return Coder.codeMessage(json,coder);
    }
}
