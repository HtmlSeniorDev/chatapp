package ru.readme.chatapp.helper;

import android.content.Context;
import android.util.Log;

import com.google.gson.Gson;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import ru.readme.chatapp.object.CoderUser;
import ru.readme.chatapp.object.requests.BaseRequest;
import ru.readme.chatapp.object.requests.IdRequest;
import ru.readme.chatapp.object.responses.BaseResponse;
import ru.readme.chatapp.object.responses.DeviceBansResponse;
import ru.readme.chatapp.object.responses.Status;
import ru.readme.chatapp.util.Network;
import ru.readme.chatapp.util.TokenHelper;

/**
 * Created by dima on 23.01.17.
 */

public class DeviceBanHelper {

    private TokenHelper tokenHelper;
    private Context context;
    private OnDeviceBanHelperListener listener;

    public DeviceBanHelper(Context context, OnDeviceBanHelperListener listener) {
        this.context = context;
        this.listener = listener;
        tokenHelper = new TokenHelper(context);
    }

    public void getList() {
        final BaseRequest request = new BaseRequest();
        tokenHelper.getToken(new TokenHelper.OnGetTokenListener() {
            @Override
            public void onGetToken(String token) {
                Call<String> call = Network.getChatNetworkInterface().getBannedDevices(token, BaseRequest.code(request, CoderUser.get(context)));
                if (call != null) {
                    call.enqueue(new Callback<String>() {
                        @Override
                        public void onResponse(Call<String> call, Response<String> response) {
                            if (listener != null) {
                                if (response != null && response.body() != null) {
                                    DeviceBansResponse resp = DeviceBansResponse.decode(CoderUser.get(context), response.body(), DeviceBansResponse.class);
                                    if (resp != null) {
                                        if (resp.getStatus() == null) {
                                            resp.setIds(new ArrayList());
                                        }
                                        Log.e("App", new Gson().toJson(resp));
                                        listener.onGetList(resp.getIds());
                                    } else {
                                        listener.onGetList(new ArrayList<String>());
                                    }
                                } else {
                                    listener.onGetList(new ArrayList<String>());
                                }
                            }
                        }

                        @Override
                        public void onFailure(Call<String> call, Throwable t) {
                            if (listener != null) {
                                listener.onGetList(new ArrayList<String>());
                            }
                        }
                    });
                }
            }
        });
    }

    public void unban(final String id) {
        final IdRequest request = new IdRequest(id);
        tokenHelper.getToken(new TokenHelper.OnGetTokenListener() {
            @Override
            public void onGetToken(String token) {
                Call<String> call = Network.getChatNetworkInterface().unbanDevice(token, BaseRequest.code(request, CoderUser.get(context)));
                if (call != null) {
                    call.enqueue(new Callback<String>() {
                        @Override
                        public void onResponse(Call<String> call, Response<String> response) {
                            if (listener != null) {
                                if (response != null && response.body() != null) {
                                    BaseResponse resp = BaseResponse.decode(CoderUser.get(context), response.body(), DeviceBansResponse.class);
                                    listener.onUnban(resp != null ? resp.getStatus().equals(Status.STATUS_DONE) : false, id);
                                } else {
                                    listener.onUnban(false, id);
                                }
                            }
                        }

                        @Override
                        public void onFailure(Call<String> call, Throwable t) {
                            if (listener != null) {
                                listener.onUnban(false, id);
                            }
                        }
                    });
                }
            }
        });
    }

    public interface OnDeviceBanHelperListener {
        void onGetList(List<String> devices);

        void onUnban(boolean done, String id);
    }
}
