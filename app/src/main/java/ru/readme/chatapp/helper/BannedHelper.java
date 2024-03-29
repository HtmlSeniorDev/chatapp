package ru.readme.chatapp.helper;

import android.content.Context;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import ru.readme.chatapp.object.CoderUser;
import ru.readme.chatapp.object.requests.BanRequest;
import ru.readme.chatapp.object.requests.BaseRequest;
import ru.readme.chatapp.object.responses.BaseResponse;
import ru.readme.chatapp.object.responses.Status;
import ru.readme.chatapp.object.responses.StatusResponse;
import ru.readme.chatapp.object.responses.UserResponse;
import ru.readme.chatapp.object.responses.UsersResponse;
import ru.readme.chatapp.util.Network;
import ru.readme.chatapp.util.TokenHelper;

import static ru.readme.chatapp.object.requests.BaseRequest.code;

/**
 * Created by dima on 16.12.16.
 */

public class BannedHelper {

    private Context context;
    private OnBannedHelper listener;
    private TokenHelper tokenHelper;


    public BannedHelper(Context context, OnBannedHelper listener) {
        this.context = context;
        this.listener = listener;
        tokenHelper = new TokenHelper(context);
    }

    public interface OnBannedHelper {
        void onUbBanned(boolean done);
        void onGetUsers(List<UserResponse> users);
        void onSuperBan(boolean done);
    }

    public void unBanUser(String user) {
        final BanRequest request = new BanRequest();
        request.setBanned(user);
        tokenHelper.getToken(new TokenHelper.OnGetTokenListener() {
            @Override
            public void onGetToken(String token) {
                Call<String> call = Network.getChatNetworkInterface().unbanUser(token, BaseRequest.code(request, CoderUser.get(context)));
                call.enqueue(new Callback<String>() {
                    @Override
                    public void onResponse(Call<String> call, Response<String> response) {
                        StatusResponse resp = BaseResponse.decode(CoderUser.get(context), response.body(), StatusResponse.class);
                        if (listener != null) {
                            if (resp != null) {
                                listener.onUbBanned(resp.getStatus().equals(Status.STATUS_DONE));
                            } else {
                                listener.onUbBanned(false);
                            }
                        }
                    }

                    @Override
                    public void onFailure(Call<String> call, Throwable t) {
                        if (listener != null) {
                            listener.onUbBanned(false);
                        }
                    }
                });
            }
        });
    }

    public void superban(String user) {
        final BanRequest request = new BanRequest();
        request.setBanned(user);
        tokenHelper.getToken(new TokenHelper.OnGetTokenListener() {
            @Override
            public void onGetToken(String token) {
                Call<String> call = Network.getChatNetworkInterface().superbanUser(token, BaseRequest.code(request, CoderUser.get(context)));
                call.enqueue(new Callback<String>() {
                    @Override
                    public void onResponse(Call<String> call, Response<String> response) {
                        StatusResponse resp = BaseResponse.decode(CoderUser.get(context), response.body(), StatusResponse.class);
                        if (listener != null) {
                            if (resp != null) {
                                listener.onSuperBan(resp.getStatus().equals(Status.STATUS_DONE));
                            } else {
                                listener.onSuperBan(false);
                            }
                        }
                    }

                    @Override
                    public void onFailure(Call<String> call, Throwable t) {
                        if (listener != null) {
                            listener.onSuperBan(false);
                        }
                    }
                });
            }
        });
    }

    public void getBannedUsers() {
        final BaseRequest request = new BaseRequest();
        tokenHelper.getToken(new TokenHelper.OnGetTokenListener() {
            @Override
            public void onGetToken(String token) {
                Call<String> call = Network.getChatNetworkInterface().getBannedUsers(token, code(request, CoderUser.get(context)));
                call.enqueue(new Callback<String>() {
                    @Override
                    public void onResponse(Call<String> call, Response<String> response) {
                        UsersResponse resp = UsersResponse.decode(CoderUser.get(context), response.body(), UsersResponse.class);
                        if (listener != null) {
                            if (resp != null && resp.getStatus().equals(Status.STATUS_DONE)) {
                                listener.onGetUsers(resp.getUsers());
                            } else {
                                listener.onGetUsers(new ArrayList<UserResponse>());
                            }
                        }
                    }

                    @Override
                    public void onFailure(Call<String> call, Throwable t) {
                        if (listener != null) {
                            listener.onGetUsers(new ArrayList<UserResponse>());
                        }
                    }
                });
            }
        });
    }
}
