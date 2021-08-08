package ru.readme.chatapp.helper;

import android.content.Context;
import android.util.Log;

import com.google.gson.Gson;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import ru.readme.chatapp.object.CoderUser;
import ru.readme.chatapp.object.requests.BaseRequest;
import ru.readme.chatapp.object.requests.GiftRequest;
import ru.readme.chatapp.object.requests.UserRequest;
import ru.readme.chatapp.object.responses.BaseResponse;
import ru.readme.chatapp.object.responses.GiftResponse;
import ru.readme.chatapp.object.responses.GiftsResponse;
import ru.readme.chatapp.object.responses.Status;
import ru.readme.chatapp.object.responses.UserResponse;
import ru.readme.chatapp.util.Network;
import ru.readme.chatapp.util.TokenHelper;

/**
 * Created by dima on 04.01.17.
 */

public class GiftsHelper {

    private TokenHelper tokenHelper;
    private Context context;
    private OnGiftsHelperListener listener;

    public GiftsHelper(Context context, OnGiftsHelperListener listener) {
        this.context = context;
        this.listener = listener;
        tokenHelper = new TokenHelper(context);
    }

    public void getGifts() {
        final BaseRequest request = new BaseRequest();
        tokenHelper.getToken(new TokenHelper.OnGetTokenListener() {
            @Override
            public void onGetToken(String token) {
                Call<String> call = Network.getChatNetworkInterface().getGifts(token, BaseRequest.code(request, CoderUser.get(context)));
                if (call != null) {
                    call.enqueue(new Callback<String>() {
                        @Override
                        public void onResponse(Call<String> call, Response<String> response) {
                            if (listener != null) {
                                if (response != null && response.body() != null) {
                                    GiftsResponse resp = GiftsResponse.decode(CoderUser.get(context), response.body(), GiftsResponse.class);
                                    if (resp != null) {
                                        if (resp.getGifts() == null) {
                                            resp.setGifts(new ArrayList<GiftResponse>());
                                        }
                                        listener.onGetGifts(resp.getStatus().equals(Status.STATUS_DONE), resp.getGifts());
                                    } else {
                                        listener.onGetGifts(false, null);
                                    }
                                } else {
                                    listener.onGetGifts(false, null);
                                }
                            }
                        }

                        @Override
                        public void onFailure(Call<String> call, Throwable t) {
                            if (listener != null) {
                                listener.onGetGifts(false, null);
                            }
                        }
                    });
                } else {

                }
            }
        });
    }

    public void addGift(GiftRequest request, File file) {
        final RequestBody rbId =
                RequestBody.create(
                        MediaType.parse("multipart/form-data"), GiftRequest.code(request, CoderUser.get(context)));
        RequestBody requestFile = RequestBody.create(MediaType.parse("image/png"), file);
        final MultipartBody.Part body =
                MultipartBody.Part.createFormData("file", file.getName(), requestFile);
        new TokenHelper(context).getToken(new TokenHelper.OnGetTokenListener() {
            @Override
            public void onGetToken(String token) {
                RequestBody rbTk =
                        RequestBody.create(
                                MediaType.parse("multipart/form-data"), token);
                Call<String> call = Network.getChatNetworkInterface().addGift(rbTk, body, rbId);
                call.enqueue(new Callback<String>() {
                    @Override
                    public void onResponse(Call<String> call, Response<String> response) {
                        if (listener != null) {
                            GiftResponse resp = GiftResponse.decode(CoderUser.get(context), response.body(), GiftResponse.class);
                            Log.e("App", new Gson().toJson(resp));
                            Log.e("App", response.code() + " ");
                            if (resp != null) {
                                listener.onAddGift(resp.getStatus().equals(Status.STATUS_DONE), resp);
                            } else {
                                listener.onAddGift(false, null);
                            }
                        }
                    }

                    @Override
                    public void onFailure(Call<String> call, Throwable t) {
                        if (listener != null) {
                            listener.onAddGift(false, null);
                        }
                    }
                });
            }
        });

    }

    public void updateGift(GiftRequest request, File file) {
        final RequestBody rbId =
                RequestBody.create(
                        MediaType.parse("multipart/form-data"), GiftRequest.code(request, CoderUser.get(context)));
        RequestBody requestFile = null;
        if (file != null) {
            requestFile = RequestBody.create(MediaType.parse("image/png"), file);
        }else{
            requestFile =  RequestBody.create(MediaType.parse("multipart/form-data"), "1221");
        }

        final MultipartBody.Part body =
                MultipartBody.Part.createFormData(file!=null?"file":"dsfs", file != null ? file.getName() : "121", requestFile);
        new TokenHelper(context).getToken(new TokenHelper.OnGetTokenListener() {
            @Override
            public void onGetToken(String token) {
                RequestBody rbTk =
                        RequestBody.create(
                                MediaType.parse("multipart/form-data"), token);
                Call<String> call = Network.getChatNetworkInterface().updateGift(rbTk, body, rbId);
                call.enqueue(new Callback<String>() {
                    @Override
                    public void onResponse(Call<String> call, Response<String> response) {
                        if (listener != null) {
                            GiftResponse resp = GiftResponse.decode(CoderUser.get(context), response.body(), GiftResponse.class);
                            Log.e("App", new Gson().toJson(resp));
                            Log.e("App", response.code() + " ");
                            if (resp != null) {
                                listener.onUpdateGift(resp.getStatus().equals(Status.STATUS_DONE), resp);
                            } else {
                                listener.onUpdateGift(false, null);
                            }
                        }
                    }

                    @Override
                    public void onFailure(Call<String> call, Throwable t) {
                        if (listener != null) {
                            listener.onUpdateGift(false, null);
                        }
                    }
                });
            }
        });
    }


    public void loadUser(String id) {
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
    public void deleteGift(final String gift) {
        final GiftRequest request = new GiftRequest();
        request.setId(gift);
        tokenHelper.getToken(new TokenHelper.OnGetTokenListener() {
            @Override
            public void onGetToken(String token) {
                Call<String> call = Network.getChatNetworkInterface().deleteGift(token, BaseRequest.code(request, CoderUser.get(context)));
                if (call != null) {
                    call.enqueue(new Callback<String>() {
                        @Override
                        public void onResponse(Call<String> call, Response<String> response) {
                            if (listener != null) {
                                if (response != null && response.body() != null) {
                                    GiftsResponse resp = GiftsResponse.decode(CoderUser.get(context), response.body(), GiftsResponse.class);
                                    if (resp != null) {
                                        if (resp.getGifts() == null) {
                                            resp.setGifts(new ArrayList<GiftResponse>());
                                        }
                                        listener.onDeleteGift(resp.getStatus().equals(Status.STATUS_DONE), gift);
                                    } else {
                                        listener.onDeleteGift(false, gift);
                                    }
                                } else {
                                    listener.onDeleteGift(false, gift);
                                }
                            }
                        }

                        @Override
                        public void onFailure(Call<String> call, Throwable t) {
                            if (listener != null) {
                                listener.onDeleteGift(false, gift);
                            }
                        }
                    });
                } else {

                }
            }
        });
    }

    public interface OnGiftsHelperListener {
        void onGetGifts(boolean done, List<GiftResponse> gifts);

        void onAddGift(boolean done, GiftResponse giftResponse);

        void onUpdateGift(boolean done, GiftResponse giftResponse);

        void onDeleteGift(boolean done, String id);

        void onUserLoad(UserResponse user);
    }
}
