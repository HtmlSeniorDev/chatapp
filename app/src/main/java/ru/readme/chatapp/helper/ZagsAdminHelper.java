package ru.readme.chatapp.helper;

import android.content.Context;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import ru.readme.chatapp.object.CoderUser;
import ru.readme.chatapp.object.requests.BaseRequest;
import ru.readme.chatapp.object.requests.UserGiftRequest;
import ru.readme.chatapp.object.requests.ZagsPriceRequest;
import ru.readme.chatapp.object.responses.BaseResponse;
import ru.readme.chatapp.object.responses.Status;
import ru.readme.chatapp.object.responses.ZagsPriceResponse;
import ru.readme.chatapp.util.Network;
import ru.readme.chatapp.util.TokenHelper;

/**
 * Created by dima on 05.01.17.
 */

public class ZagsAdminHelper {

    private TokenHelper tokenHelper;
    private Context context;
    private OnZagsAdminHelperListener listener;

    public ZagsAdminHelper(Context context, OnZagsAdminHelperListener listener) {
        this.context = context;
        this.listener = listener;
        tokenHelper = new TokenHelper(context);
    }

    public void setPrice(final int price) {
        final ZagsPriceRequest request = new ZagsPriceRequest();
        request.setPrice(price);
        new TokenHelper(context).getToken(new TokenHelper.OnGetTokenListener() {
            @Override
            public void onGetToken(String token) {
                Call<String> call = Network.getChatNetworkInterface().setZagsPrice(token, UserGiftRequest.code(request, CoderUser.get(context)));
                call.enqueue(new Callback<String>() {
                    @Override
                    public void onResponse(Call<String> call, Response<String> response) {
                        if (listener != null) {
                            BaseResponse resp = BaseResponse.decode(CoderUser.get(context), response.body(), BaseResponse.class);
                            boolean done = resp != null && resp.getStatus().equals(Status.STATUS_DONE);
                            listener.onSetPrice(done, price);
                        }
                    }

                    @Override
                    public void onFailure(Call<String> call, Throwable t) {
                        listener.onSetPrice(false, price);
                    }
                });
            }
        });
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

    public interface OnZagsAdminHelperListener {

        void onGetPrice(boolean done, int price);

        void onSetPrice(boolean done, int price);
    }
}
