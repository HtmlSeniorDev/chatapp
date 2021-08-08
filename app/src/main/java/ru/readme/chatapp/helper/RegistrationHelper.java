package ru.readme.chatapp.helper;

import android.content.Context;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import ru.readme.chatapp.object.requests.BaseRequest;
import ru.readme.chatapp.object.requests.CheckLoginRequest;
import ru.readme.chatapp.object.requests.RegistrationRequest;
import ru.readme.chatapp.object.requests.UserRequest;
import ru.readme.chatapp.object.responses.BaseResponse;
import ru.readme.chatapp.object.responses.RegistrationResponse;
import ru.readme.chatapp.object.responses.Status;
import ru.readme.chatapp.object.responses.StatusResponse;
import ru.readme.chatapp.util.Network;
import ru.readme.chatapp.util.SettingsHelper;
import ru.readme.chatapp.util.TokenHelper;

/**
 * Created by dima on 16.11.16.
 */

public class RegistrationHelper {

    private Context context;
    private OnRegistrationActionsListener listener;
    String version = "2";


    public RegistrationHelper(Context context, OnRegistrationActionsListener listener) {
        this.context = context;
        this.listener = listener;
    }

    public void checkLogin(String login) {
        CheckLoginRequest request = new CheckLoginRequest(login);
        Call<String> call = Network.getChatNetworkInterface().checkLogin(BaseRequest.code(request, null), version);
        call.enqueue(new Callback<String>() {
            @Override
            public void onResponse(Call<String> call, Response<String> response) {
                boolean lg = false;
                StatusResponse resp = BaseResponse.decode(null, response.body(), StatusResponse.class);

                if (resp != null && resp.getStatus().equals(Status.STATUS_VERSION)) {
                    if (listener != null) {
                        listener.onCheckLogin(false,true);
                    }
                }
                else
                if (resp != null && resp.getStatus().equals(Status.STATUS_DONE)) {
                    lg = true;
                }
                if (listener != null) {
                    listener.onCheckLogin(lg, false);
                }
            }

            @Override
            public void onFailure(Call<String> call, Throwable t) {
                if (listener != null) {
                    listener.onCheckLogin(false,false);
                }
            }
        });
    }

    public void checkNic(String nic) {
        UserRequest request = new UserRequest();
        request.setNic(nic);
        Call<String> call = Network.getChatNetworkInterface().checkNic(null,BaseRequest.code(request, null));
        call.enqueue(new Callback<String>() {
            @Override
            public void onResponse(Call<String> call, Response<String> response) {
                boolean lg = false;
                BaseResponse resp = BaseResponse.decode(null, response.body(), BaseResponse.class);
                if (resp != null && resp.getStatus().equals(Status.STATUS_DONE)) {
                    lg = true;
                }
                if (listener != null) {
                    listener.onCheckNic(lg);
                }
            }

            @Override
            public void onFailure(Call<String> call, Throwable t) {
                if (listener != null) {
                    listener.onCheckNic(false);
                }
            }
        });
    }


    public void registration(final String email, final String login, final String password, String nic, int color, String name, String fname, String devid) {
        String nick = ModNick(nic.trim());
        RegistrationRequest request = new RegistrationRequest();
        request.setColor(color);
        request.setEmail(email);
        request.setLogin(login);
        request.setNic(nick);
        request.setName(name);
        request.setFname(fname);
        request.setPassword(password);
        request.setDevId(devid);
        Call<String> call = Network.getChatNetworkInterface().registration(BaseRequest.code(request, null));
        call.enqueue(new Callback<String>() {
            @Override
            public void onResponse(Call<String> call, Response<String> response) {
                RegistrationResponse resp = BaseResponse.decode(null, response.body(), RegistrationResponse.class);
                continueRegistration(resp, login, password);
            }

            @Override
            public void onFailure(Call<String> call, Throwable t) {
                if (listener != null) {
                    listener.onCheckLogin(false,false);
                }
            }
        });

    }

    public static String ModNick(String nick) {// русс, англ
        nick = nick.trim();
        nick = nick.replace("А" , "A");
        nick = nick.replace("В" , "B");
        nick = nick.replace("С" , "C");
        nick = nick.replace("Е" , "E");
        nick = nick.replace("Н" , "H");
        nick = nick.replace("К" , "K");
        nick = nick.replace("М" , "M");
        nick = nick.replace("О" , "O");
        nick = nick.replace("Р" , "P");
        nick = nick.replace("Т" , "T");
        nick = nick.replace("Х" , "X");

        nick = nick.replace("а" , "a");
        nick = nick.replace("с" , "c");
        nick = nick.replace("е" , "e");
        nick = nick.replace("о" , "o");
        nick = nick.replace("р" , "p");
        nick = nick.replace("х" , "x");
      //  nick = nick.replace("к" , "k");
        return nick;
    }

    private void continueRegistration(RegistrationResponse response, String login, String password) {
        boolean reg = false;
        if (response != null) {
            if (response.getStatus().equals(Status.STATUS_REG_BAN)) {
                listener.onRegistration(false,Status.STATUS_REG_BAN);
            }
            if (response.getStatus().equals(Status.STATUS_DONE)) {
                new TokenHelper(context).setToken(response.getToken(), response.getTokenEnd(), response.getTokenCreated());
                SettingsHelper sh = new SettingsHelper(context);
                sh.setLogin(login.trim());
                sh.setPassword(password.trim());
                reg = true;
            }
        }
        if (listener != null) {
            listener.onRegistration(reg,response!=null?response.getStatus(): Status.STATUS_FAIL);
        }
    }

    public interface OnRegistrationActionsListener {
        void onCheckLogin(boolean check, boolean version);

        void onRegistration(boolean registration, String status);

        void onCheckNic(boolean done);
    }
}
