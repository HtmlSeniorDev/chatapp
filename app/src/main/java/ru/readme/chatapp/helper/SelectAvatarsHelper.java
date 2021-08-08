package ru.readme.chatapp.helper;

import android.content.Context;

import java.util.ArrayList;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import ru.readme.chatapp.object.CoderUser;
import ru.readme.chatapp.object.requests.AvatarRequest;
import ru.readme.chatapp.object.requests.BaseRequest;
import ru.readme.chatapp.object.requests.UserRequest;
import ru.readme.chatapp.object.responses.AvatarResponse;
import ru.readme.chatapp.object.responses.AvatarsResponse;
import ru.readme.chatapp.object.responses.BaseResponse;
import ru.readme.chatapp.object.responses.Status;
import ru.readme.chatapp.object.responses.UserResponse;
import ru.readme.chatapp.util.Network;
import ru.readme.chatapp.util.TokenHelper;

/**
 * Created by dima on 23.12.16.
 */

public class SelectAvatarsHelper  {

    private TokenHelper tokenHelper;
    private Context context;
    private SelectAvatarHelperListener listener;

    public SelectAvatarsHelper(Context context, SelectAvatarHelperListener listener) {
        this.context = context;
        this.listener = listener;
        tokenHelper = new TokenHelper(context);
    }

    public void getAvatars(){
        final BaseRequest request = new BaseRequest();
        tokenHelper.getToken(new TokenHelper.OnGetTokenListener() {
            @Override
            public void onGetToken(String token) {
                Call<String> call = Network.getChatNetworkInterface().getAvatarsDescription(token, BaseRequest.code(request, CoderUser.get(context)));
                if (call != null) {
                    call.enqueue(new Callback<String>() {
                        @Override
                        public void onResponse(Call<String> call, Response<String> response) {
                            if (listener != null) {
                                if (response != null && response.body() != null) {
                                    AvatarsResponse resp = AvatarsResponse.decode(CoderUser.get(context), response.body(), AvatarsResponse.class);
                                    if (resp != null) {
                                        if (resp.getAvatars() == null) {
                                            resp.setAvatars(new ArrayList<AvatarResponse>());
                                        }
                                        listener.onGetAvatars(resp.getStatus().equals(Status.STATUS_DONE), resp);
                                    } else {
                                        listener.onGetAvatars(false, null);
                                    }
                                } else {
                                    listener.onGetAvatars(false, null);
                                }
                            }
                        }

                        @Override
                        public void onFailure(Call<String> call, Throwable t) {
                            if (listener != null) {
                                listener.onGetAvatars(false, null);
                            }
                        }
                    });
                } else {

                }
            }
        });
    }
    public void getUser(){
        String id = "-1";
        final UserRequest request = new UserRequest();
        if (id != null && !id.equals("-1")) {
            request.setUserId(id);
        }
        new TokenHelper(context).getToken(new TokenHelper.OnGetTokenListener() {
            @Override
            public void onGetToken(String token) {
                Call<String> call = Network.getChatNetworkInterface().getUser(token, BaseRequest.code(request, CoderUser.get(context)));
                call.enqueue(new Callback<String>() {
                    @Override
                    public void onResponse(Call<String> call, Response<String> response) {
                        if (listener != null) {
                            if (response != null && response.body() != null) {
                                UserResponse resp = UserResponse.decode(CoderUser.get(context), response.body(), UserResponse.class);
                                if (resp != null) {
                                    listener.onGetUser(resp.getStatus().equals(Status.STATUS_DONE), resp);
                                } else {
                                    listener.onGetUser(false, null);
                                }
                            } else {
                                listener.onGetUser(false, null);
                            }
                        }
                    }

                    @Override
                    public void onFailure(Call<String> call, Throwable t) {
                        if (listener != null) {
                            listener.onGetUser(false,null);
                        }
                    }
                });
            }
        });
    }
    public void setAvatar(String id){
        final AvatarRequest request = new AvatarRequest();
        if (id != null && !id.equals("-1")) {
            request.setId(id);
        }
        new TokenHelper(context).getToken(new TokenHelper.OnGetTokenListener() {
            @Override
            public void onGetToken(String token) {
                Call<String> call = Network.getChatNetworkInterface().purcheseAvatar(token, BaseRequest.code(request, CoderUser.get(context)));
                call.enqueue(new Callback<String>() {
                    @Override
                    public void onResponse(Call<String> call, Response<String> response) {
                        if (listener != null) {
                            if (response != null && response.body() != null) {
                                BaseResponse resp = BaseResponse.decode(CoderUser.get(context), response.body(), BaseResponse.class);
                                if (resp != null) {
                                    listener.onSetAvatar(resp.getStatus().equals(Status.STATUS_DONE));
                                } else {
                                    listener.onSetAvatar(false);
                                }
                            } else {
                                listener.onSetAvatar(false);
                            }
                        }
                    }

                    @Override
                    public void onFailure(Call<String> call, Throwable t) {
                        if (listener != null) {
                            listener.onSetAvatar(false);
                        }
                    }
                });
            }
        });
    }

    public interface SelectAvatarHelperListener{
        void onGetAvatars(boolean done, AvatarsResponse response);
        void onSetAvatar(boolean done);
        void onGetUser(boolean done, UserResponse response);
    }
}
