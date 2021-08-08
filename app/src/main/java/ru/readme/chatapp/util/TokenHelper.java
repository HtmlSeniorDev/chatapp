package ru.readme.chatapp.util;

import android.content.Context;
import android.util.Log;
import com.google.gson.Gson;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import ru.readme.chatapp.object.requests.BaseRequest;
import ru.readme.chatapp.object.requests.LoginRequest;
import ru.readme.chatapp.object.responses.BaseResponse;
import ru.readme.chatapp.object.responses.LoginResponse;
import ru.readme.chatapp.object.responses.Status;
import ru.readme.chatapp.helper.LoginHelper;

public class TokenHelper {

    private Context context;
    private SettingsHelper settingsHelper;

    public TokenHelper(Context context) {
        this.context = context;
        settingsHelper = new SettingsHelper(this.context);
    }

    public void setToken(String token, long end, long created) {
        long btw = System.currentTimeMillis() - created;
        end = end + btw;
        settingsHelper.setToken(token);
        settingsHelper.setTokenEnd(end);
        settingsHelper.setTokenCreated(created);
    }

    public void getToken(final OnGetTokenListener listener) {
        long end = settingsHelper.getTokenEnd();
        if (end - System.currentTimeMillis() > 10000) {
            if (listener != null) {
                listener.onGetToken(settingsHelper.getToken());
            }
        } else {
            LoginRequest request = new LoginRequest();
            request.setLogin(settingsHelper.getLogin());
            request.setPassword(settingsHelper.getPassword());
            Call<String> call = Network.getChatNetworkInterface().login(BaseRequest.code(request,null), LoginHelper.version);
            call.enqueue(new Callback<String>() {
                @Override
                public void onResponse(Call<String> call, Response<String> response) {
                    LoginResponse resp = BaseResponse.decode(null, response.body(),LoginResponse.class);
                    Log.i("Chat",response.body());
                    Log.i("Chat", new Gson().toJson(resp));
                    if (resp != null && resp.getStatus().equals(Status.STATUS_DONE)) {

                        setToken(resp.getToken(), resp.getTokenEnd(), resp.getTokenCreated());
                        if (listener != null) {
                            listener.onGetToken(settingsHelper.getToken());
                           // Log.e("App", "token="+settingsHelper.getToken());
                        }
                    } else {
                        if (listener != null) {
                            listener.onGetToken(null);
                        }
                    }
                }

                @Override
                public void onFailure(Call<String> call, Throwable t) {
                    if (listener != null) {
                        listener.onGetToken(null);
                    }
                }
            });
        }
    }

    public interface OnGetTokenListener {
        void onGetToken(String token);
    }

}
