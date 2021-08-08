package ru.readme.chatapp.helper;

import android.content.Context;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import ru.readme.chatapp.object.CoderUser;
import ru.readme.chatapp.object.requests.BaseRequest;
import ru.readme.chatapp.object.requests.IdRequest;
import ru.readme.chatapp.object.requests.UserRequest;
import ru.readme.chatapp.object.responses.BaseResponse;
import ru.readme.chatapp.object.responses.RoomResponse;
import ru.readme.chatapp.object.responses.Status;
import ru.readme.chatapp.object.responses.UserResponse;
import ru.readme.chatapp.util.Network;
import ru.readme.chatapp.util.TokenHelper;

/**
 * Created by dima on 30.11.16.
 */

public class MainActivityHelper {

    private Context context;
    private OnMainHelperActionListener listener;

    public MainActivityHelper(Context context, OnMainHelperActionListener listener) {
        this.context = context;
        this.listener = listener;
    }


    public void loadProfile() {
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
                        UserResponse resp = BaseResponse.decode(CoderUser.get(context), response.body(), UserResponse.class);
                        if (listener != null) {
                            listener.onGetProfile(resp);
                        }
                    }

                    @Override
                    public void onFailure(Call<String> call, Throwable t) {
                        if (listener != null) {
                            listener.onGetProfile(null);
                        }
                    }
                });
            }
        });
    }

    public void getLastRoom() {
        String id = "-1";
        final BaseRequest request = new BaseRequest();
        new TokenHelper(context).getToken(new TokenHelper.OnGetTokenListener() {
            @Override
            public void onGetToken(String token) {
                Call<String> call = Network.getChatNetworkInterface().getLastRoom(token, BaseRequest.code(request, CoderUser.get(context)));
                call.enqueue(new Callback<String>() {
                    @Override
                    public void onResponse(Call<String> call, Response<String> response) {
                        RoomResponse resp = BaseResponse.decode(CoderUser.get(context), response.body(), RoomResponse.class);
                        if (listener != null) {
                            listener.onGetLastRoom(resp);
                        }
                    }

                    @Override
                    public void onFailure(Call<String> call, Throwable t) {
                        if (listener != null) {
                            listener.onGetLastRoom(null);
                        }
                    }
                });
            }
        });
    }

    public void readNotice(final String id) {
        final IdRequest request = new IdRequest();
        request.setId(id);
        new TokenHelper(context).getToken(new TokenHelper.OnGetTokenListener() {
            @Override
            public void onGetToken(String token) {
                Call<String> call = Network.getChatNetworkInterface().readNotice(token, BaseRequest.code(request, CoderUser.get(context)));
                call.enqueue(new Callback<String>() {
                    @Override
                    public void onResponse(Call<String> call, Response<String> response) {
                        BaseResponse resp = BaseResponse.decode(CoderUser.get(context), response.body(), RoomResponse.class);
                        if (listener != null) {
                            listener.onReadNotice(resp!=null && resp.getStatus().equals(Status.STATUS_DONE), id);
                        }
                    }

                    @Override
                    public void onFailure(Call<String> call, Throwable t) {
                        if (listener != null) {
                            listener.onReadNotice(false,id);
                        }
                    }
                });
            }
        });
    }

    public interface OnMainHelperActionListener {
        void onGetProfile(UserResponse response);
        void onGetLastRoom(RoomResponse roomResponse);
        void onReadNotice(boolean done, String id);
    }
}
