package ru.readme.chatapp.helper;

import android.content.Context;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import ru.readme.chatapp.object.CoderUser;
import ru.readme.chatapp.object.requests.BaseRequest;
import ru.readme.chatapp.object.responses.RulesResponse;
import ru.readme.chatapp.object.responses.Status;
import ru.readme.chatapp.util.Network;
import ru.readme.chatapp.util.TokenHelper;

import static ru.readme.chatapp.object.requests.BaseRequest.code;

/**
 * Created by dima on 16.12.16.
 */

public class RulesHelper {

    private Context context;
    private OnRulesHelper listener;
    private TokenHelper tokenHelper;


    public RulesHelper(Context context, OnRulesHelper listener) {
        this.context = context;
        this.listener = listener;
        tokenHelper = new TokenHelper(context);
    }

    public interface OnRulesHelper {
        void onGetRules(String s);
    }


    public void getRules() {
        final BaseRequest request = new BaseRequest();
        tokenHelper.getToken(new TokenHelper.OnGetTokenListener() {
            @Override
            public void onGetToken(String token) {
                Call<String> call = Network.getChatNetworkInterface().getRules(token, code(request, CoderUser.get(context)));
                call.enqueue(new Callback<String>() {
                    @Override
                    public void onResponse(Call<String> call, Response<String> response) {
                       // UsersResponse resp = UsersResponse.decode(CoderUser.get(context), response.body(), UsersResponse.class);
                        RulesResponse resp = RulesResponse.decode(CoderUser.get(context), response.body(), RulesResponse.class);
                        if (listener != null) {
                            if (resp != null && resp.getStatus().equals(Status.STATUS_DONE)) {
                                listener.onGetRules(resp.getRules());
                            }
                        }
                    }

                    @Override
                    public void onFailure(Call<String> call, Throwable t) {
                        if (listener != null) {
                            listener.onGetRules("");
                        }
                    }
                });
            }
        });
    }
}
