package ru.readme.chatapp.helper;

import android.content.Context;
import android.util.Log;

import com.google.gson.Gson;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import ru.readme.chatapp.object.CoderUser;
import ru.readme.chatapp.object.requests.BaseRequest;
import ru.readme.chatapp.object.requests.LoginRequest;
import ru.readme.chatapp.object.requests.UserRequest;
import ru.readme.chatapp.object.responses.BaseResponse;
import ru.readme.chatapp.object.responses.LoginResponse;
import ru.readme.chatapp.object.responses.Status;
import ru.readme.chatapp.object.responses.UserResponse;
import ru.readme.chatapp.util.Network;
import ru.readme.chatapp.util.SettingsHelper;
import ru.readme.chatapp.util.TokenHelper;

/**
 * Created by dima on 17.11.16.
 */

public class LoginHelper {

    private Context context;
    private OnLoginHelperActionListener listener;
    private SettingsHelper settingsHelper;
    private TokenHelper tokenHelper;
    public static  String version = "2";

    public LoginHelper(Context context, OnLoginHelperActionListener listener) {
        this.context = context;
        this.listener = listener;
        settingsHelper = new SettingsHelper(context);
        tokenHelper = new TokenHelper(context);
    }

    public boolean isLogin() {
        return (settingsHelper.getToken() != null);
    }

    public void doSaveLogin() {
        String login = settingsHelper.getLogin();
        String pas = settingsHelper.getPassword();
        doLogin(login, pas);
    }

    public void doLogin(final String login, final String password) {
        LoginRequest request = new LoginRequest.Builder()
                .setPassword(password)
                .setLogin(login)
                .build();
        Call<String> call = Network.getChatNetworkInterface().login(BaseRequest.code(request, null), version);
        call.enqueue(new Callback<String>() {
            @Override
            public void onResponse(Call<String> call, Response<String> response) {
                LoginResponse resp = BaseResponse.decode(null, response.body(),LoginResponse.class);
                System.out.println("response" + resp);
                if (resp != null && resp.getStatus().equals(Status.STATUS_LOGIN_BAN)) {
                    if (listener != null) {
                        listener.onLogin(false,false,resp.getStatus());
                    }
                } else
                if (resp != null && resp.getStatus().equals(Status.STATUS_VERSION)) {
                    if (listener != null) {
                        listener.onLogin(false,true,resp.getStatus());
                    }
                } else
                if (resp != null && resp.getStatus().equals(Status.STATUS_DONE)) {
                    tokenHelper.setToken(resp.getToken(), resp.getTokenEnd(), resp.getTokenCreated());
                    settingsHelper.setPassword(password);
                    settingsHelper.setLogin(login);
                    if (listener != null) {
                        listener.onLogin(true,false,resp.getStatus());
                    }
                } else {
                    if (listener != null) {
                        listener.onLogin(false,false,resp.getStatus());
                    }
                }
            }

            @Override
            public void onFailure(Call<String> call, Throwable t) {
                if (listener != null) {
                    listener.onLogin(false,false, "");
                }
            }
        });

    }

    public void getUserType() {
        final UserRequest request = new UserRequest();
        tokenHelper.getToken(new TokenHelper.OnGetTokenListener() {
            @Override
            public void onGetToken(String token) {
                Call<String> call = Network.getChatNetworkInterface().getUser(token, BaseRequest.code(request,CoderUser.get(context)));
                Log.i("Chat", new Gson().toJson(request));
                call.enqueue(new Callback<String>() {
                    @Override
                    public void onResponse(Call<String> call, Response<String> response) {
                        Log.i("Chat", new Gson().toJson(response.body()));
                        UserResponse resp = BaseResponse.decode(CoderUser.get(context),response.body(),UserResponse.class);
                        if (resp != null && resp.getStatus().equals(Status.STATUS_DONE)) {
                            settingsHelper.setUserId(resp.getId());
                            settingsHelper.setUserType(resp.getType());
                            if (listener != null) {
                                listener.onGetUserType(true);
                            }
                        } else {
                            if (listener != null) {
                                listener.onGetUserType(false);
                            }
                        }
                    }

                    @Override
                    public void onFailure(Call<String> call, Throwable t) {
                        Log.i("Chat", "getUser", t);
                        if (listener != null) {
                            listener.onGetUserType(false);
                        }
                    }
                });
            }
        });
    }

    public interface OnLoginHelperActionListener {
        void onLogin(boolean login,boolean version, String status);

        void onGetUserType(boolean getType);
    }
}
