package ru.readme.chatapp.helper;

import android.content.Context;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import ru.readme.chatapp.object.CoderUser;
import ru.readme.chatapp.object.requests.BaseRequest;
import ru.readme.chatapp.object.requests.UserRequest;
import ru.readme.chatapp.object.responses.BaseResponse;
import ru.readme.chatapp.object.responses.UserResponse;
import ru.readme.chatapp.util.Network;
import ru.readme.chatapp.util.SettingsHelper;
import ru.readme.chatapp.util.TokenHelper;

/**
 * Created by dima on 10.01.17.
 */

public class BalanceHeleper {

    private Context context;
    private BalanceHelperListener listener;
    private TokenHelper tokenHelper;

    public BalanceHeleper(Context context, BalanceHelperListener listener) {
        this.context = context;
        this.listener = listener;
        tokenHelper = new TokenHelper(context);
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

    public interface BalanceHelperListener{
        void onGetProfile(UserResponse user);
    }
}
