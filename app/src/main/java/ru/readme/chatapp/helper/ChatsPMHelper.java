package ru.readme.chatapp.helper;

import android.content.Context;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import ru.readme.chatapp.object.CoderUser;
import ru.readme.chatapp.object.requests.BaseRequest;
import ru.readme.chatapp.object.requests.IdRequest;
import ru.readme.chatapp.object.requests.TokenRequest;
import ru.readme.chatapp.object.responses.BaseResponse;
import ru.readme.chatapp.object.responses.PMChatsResponse;
import ru.readme.chatapp.object.responses.Status;
import ru.readme.chatapp.util.Network;
import ru.readme.chatapp.util.TokenHelper;

/**
 * Created by dima on 19.11.16.
 */

public class ChatsPMHelper {

    private Context context;
    private OnChatsPMHelperListener listener;

    public ChatsPMHelper(Context context, OnChatsPMHelperListener listener) {
        this.context = context;
        this.listener = listener;
    }


    public void getChats() {
        final TokenRequest request = new TokenRequest();
        new TokenHelper(context).getToken(new TokenHelper.OnGetTokenListener() {
            @Override
            public void onGetToken(String token) {
                Call<String> call = Network.getChatNetworkInterface().getPMs(token, BaseRequest.code(request, CoderUser.get(context)));
                call.enqueue(new Callback<String>() {
                    @Override
                    public void onResponse(Call<String> call, Response<String> response) {
                        PMChatsResponse resp = BaseResponse.decode(CoderUser.get(context),response.body(),PMChatsResponse.class);
                        if (listener != null) {
                            boolean st = resp!=null && resp.getStatus().equals(Status.STATUS_DONE);
                            listener.onGetMessages(st, st ?resp : null);
                        }
                    }

                    @Override
                    public void onFailure(Call<String> call, Throwable t) {
                        if (listener != null) {
                            listener.onGetMessages(false, null);
                        }
                    }
                });
            }
        });
    }

    public void leave(final String id) {
        final IdRequest request = new IdRequest();
        request.setId(id);
        new TokenHelper(context).getToken(new TokenHelper.OnGetTokenListener() {
            @Override
            public void onGetToken(String token) {
                Call<String> call = Network.getChatNetworkInterface().leavePersonalChat(token, BaseRequest.code(request, CoderUser.get(context)));
                call.enqueue(new Callback<String>() {
                    @Override
                    public void onResponse(Call<String> call, Response<String> response) {
                        PMChatsResponse resp = BaseResponse.decode(CoderUser.get(context),response.body(),PMChatsResponse.class);
                        if (listener != null) {
                            boolean st = resp!=null && resp.getStatus().equals(Status.STATUS_DONE);
                            listener.onLeave(st, id);
                        }
                    }

                    @Override
                    public void onFailure(Call<String> call, Throwable t) {
                        if (listener != null) {
                            listener.onLeave(false, id);
                        }
                    }
                });
            }
        });
    }


    public interface OnChatsPMHelperListener {

        void onGetMessages(boolean create, PMChatsResponse response);
        void onLeave(boolean done, String id);

    }
}
