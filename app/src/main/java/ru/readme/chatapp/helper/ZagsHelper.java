package ru.readme.chatapp.helper;

import android.content.Context;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import ru.readme.chatapp.object.CoderUser;
import ru.readme.chatapp.object.requests.BaseRequest;
import ru.readme.chatapp.object.requests.UserGiftRequest;
import ru.readme.chatapp.object.requests.UserRequest;
import ru.readme.chatapp.object.responses.BaseResponse;
import ru.readme.chatapp.object.responses.Status;
import ru.readme.chatapp.object.responses.UserResponse;
import ru.readme.chatapp.object.responses.ZagsPriceResponse;
import ru.readme.chatapp.util.Network;
import ru.readme.chatapp.util.SettingsHelper;
import ru.readme.chatapp.util.TokenHelper;

/**
 * Created by dima on 06.01.17.
 */

public class ZagsHelper {

    private TokenHelper tokenHelper;
    private Context context;
    private ZagsActionListener listener;

    public ZagsHelper(Context context, ZagsActionListener listener) {
        this.context = context;
        this.listener = listener;
        tokenHelper = new TokenHelper(context);
    }

    public void getPrice() {
        final BaseRequest request = new BaseRequest();
        new TokenHelper(context).getToken(new TokenHelper.OnGetTokenListener() {
            @Override
            public void onGetToken(String token) {
                Call<String> call = Network.getChatNetworkInterface().getZagsPrice(token, UserGiftRequest.code(request, CoderUser.get(context)));
                call.enqueue(new Callback<String>() {
                    @Override
                    public void onResponse(Call<String> call, Response<String> response) {
                        if (listener != null) {
                            ZagsPriceResponse resp = ZagsPriceResponse.decode(CoderUser.get(context), response.body(), ZagsPriceResponse.class);
                            boolean done = resp != null && resp.getStatus().equals(Status.STATUS_DONE);
                            listener.onGetPrice(done, done ? resp.getPrice() : 0);
                        }
                    }

                    @Override
                    public void onFailure(Call<String> call, Throwable t) {
                        listener.onGetPrice(false, 0);
                    }
                });
            }
        });
    }

    public void cancelRequest(String user) {
        final UserRequest request = new UserRequest();
        request.setUserId(user);
        new TokenHelper(context).getToken(new TokenHelper.OnGetTokenListener() {
            @Override
            public void onGetToken(String token) {
                Call<String> call = Network.getChatNetworkInterface().cancelZagsRequest(token, UserGiftRequest.code(request, CoderUser.get(context)));
                call.enqueue(new Callback<String>() {
                    @Override
                    public void onResponse(Call<String> call, Response<String> response) {
                        if (listener != null) {
                            BaseResponse resp = BaseResponse.decode(CoderUser.get(context), response.body(), BaseResponse.class);
                            boolean done = resp != null && resp.getStatus().equals(Status.STATUS_DONE);
                            listener.onCancelRequest(done);
                        }
                    }

                    @Override
                    public void onFailure(Call<String> call, Throwable t) {
                        listener.onCancelRequest(false);
                    }
                });
            }
        });
    }

    public void endBrak() {
        final UserRequest request = new UserRequest();
        request.setUserId(null);
        new TokenHelper(context).getToken(new TokenHelper.OnGetTokenListener() {
            @Override
            public void onGetToken(String token) {
                Call<String> call = Network.getChatNetworkInterface().zagsRequest(token, UserGiftRequest.code(request, CoderUser.get(context)));
                call.enqueue(new Callback<String>() {
                    @Override
                    public void onResponse(Call<String> call, Response<String> response) {
                        if (listener != null) {
                            BaseResponse resp = BaseResponse.decode(CoderUser.get(context), response.body(), BaseResponse.class);
                            boolean done = resp != null && resp.getStatus().equals(Status.STATUS_DONE);
                            listener.onUnBrak(done);
                        }
                    }

                    @Override
                    public void onFailure(Call<String> call, Throwable t) {
                        listener.onCancelRequest(false);
                    }
                });
            }
        });
    }

    public void loadUser() {
        final UserRequest request = new UserRequest();
        request.setUserId(new SettingsHelper(context).getUserId());
        new TokenHelper(context).getToken(new TokenHelper.OnGetTokenListener() {
            @Override
            public void onGetToken(String token) {
                Call<String> call = Network.getChatNetworkInterface().getUser(token, BaseRequest.code(request, CoderUser.get(context)));
                call.enqueue(new Callback<String>() {
                    @Override
                    public void onResponse(Call<String> call, Response<String> response) {
                        if (listener != null) {
                            UserResponse resp = BaseResponse.decode(CoderUser.get(context), response.body(), UserResponse.class);
                            listener.onUserLoad(resp);
                        }
                    }

                    @Override
                    public void onFailure(Call<String> call, Throwable t) {
                        if (listener != null) {
                            listener.onUserLoad(null);
                        }
                    }
                });
            }
        });
    }


    public void sendRequest(String user) {
        final UserRequest request = new UserRequest();
        request.setUserId(user);
        new TokenHelper(context).getToken(new TokenHelper.OnGetTokenListener() {
            @Override
            public void onGetToken(String token) {
                Call<String> call = Network.getChatNetworkInterface().zagsRequest(token, UserGiftRequest.code(request, CoderUser.get(context)));
                call.enqueue(new Callback<String>() {
                    @Override
                    public void onResponse(Call<String> call, Response<String> response) {
                        if (listener != null) {
                            BaseResponse resp = BaseResponse.decode(CoderUser.get(context), response.body(), BaseResponse.class);
                            boolean done = resp != null && resp.getStatus().equals(Status.STATUS_DONE);
                            listener.onSendRequest(done);
                        }
                    }

                    @Override
                    public void onFailure(Call<String> call, Throwable t) {
                        listener.onSendRequest(false);
                    }
                });
            }
        });
    }


    public interface ZagsActionListener {
        void onGetPrice(boolean done, int price);

        void onCancelRequest(boolean done);

        void onSendRequest(boolean done);

        void onUnBrak(boolean done);

        void onUserLoad(UserResponse user);
    }
}
