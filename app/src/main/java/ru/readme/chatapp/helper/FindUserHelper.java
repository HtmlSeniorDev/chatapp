package ru.readme.chatapp.helper;

import android.content.Context;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import ru.readme.chatapp.object.CoderUser;
import ru.readme.chatapp.object.requests.UserGiftRequest;
import ru.readme.chatapp.object.requests.UserRequest;
import ru.readme.chatapp.object.responses.Status;
import ru.readme.chatapp.object.responses.UserResponse;
import ru.readme.chatapp.object.responses.UsersResponse;
import ru.readme.chatapp.util.Network;
import ru.readme.chatapp.util.TokenHelper;

/**
 * Created by dima on 06.01.17.
 */

public class FindUserHelper {

    private TokenHelper tokenHelper;
    private Context context;
    private OnFindUserHelperListener listener;

    public FindUserHelper(Context context, OnFindUserHelperListener listener) {
        this.context = context;
        this.listener = listener;
        tokenHelper = new TokenHelper(context);
    }

    public void findUsers(String nic) {
        final UserRequest request = new UserRequest();
        request.setNic(nic);
        new TokenHelper(context).getToken(new TokenHelper.OnGetTokenListener() {
            @Override
            public void onGetToken(String token) {
                Call<String> call = Network.getChatNetworkInterface().findUsers(token, UserGiftRequest.code(request, CoderUser.get(context)));
                call.enqueue(new Callback<String>() {
                    @Override
                    public void onResponse(Call<String> call, Response<String> response) {
                        if (listener != null) {
                            UsersResponse resp = UsersResponse.decode(CoderUser.get(context), response.body(), UsersResponse.class);
                            boolean done = resp != null && resp.getStatus().equals(Status.STATUS_DONE);
                            listener.onFindUsers(done, done ? resp.getUsers() : new ArrayList<UserResponse>());
                        }
                    }

                    @Override
                    public void onFailure(Call<String> call, Throwable t) {
                        listener.onFindUsers(false, new ArrayList<UserResponse>());
                    }
                });
            }
        });
    }

    public interface OnFindUserHelperListener {
        void onFindUsers(boolean done, List<UserResponse> users);
    }
}
