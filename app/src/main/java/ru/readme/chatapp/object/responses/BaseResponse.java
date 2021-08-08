package ru.readme.chatapp.object.responses;

import android.util.Log;

import java.io.Serializable;

import ru.readme.chatapp.activity.MainActivity;
import ru.readme.chatapp.object.CoderUser;
import ru.readme.chatapp.util.Coder;
import ru.readme.chatapp.util.Network;

/**
 * Created by dima on 09.12.16.
 */

public class BaseResponse implements Serializable {

    String status;

    public BaseResponse() {
    }

    public BaseResponse(String status) {
        this.status = status;
    }

    public static <T extends BaseResponse> T decode(CoderUser coder, String coded, Class<T> cl) {
        try {
           // Log.e("App", coded);
            String json = Coder.decodeMessage(coded, coder);

            T obj = Network.gson().fromJson(json, cl);
            if (obj == null) {
                json = Coder.decodeMessage(coded, null);
                obj = Network.gson().fromJson(json, cl);
            }
            if (!(obj instanceof LoginResponse) &&(obj).getStatus()!=null &&  (obj).getStatus().equals(Status.STATUS_UNAUTORIZE)) {
                if (MainActivity.ma != null) {
                    MainActivity.ma.unlogin();
                }
            }
//            Log.i("Chat", new Gson().toJson(obj));
            return obj;
        } catch (Exception e) {
            Log.e("Chat Error", "decode json", e);
            return null;
        }
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }


}
