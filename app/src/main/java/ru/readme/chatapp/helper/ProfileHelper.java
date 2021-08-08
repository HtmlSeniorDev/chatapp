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
import ru.readme.chatapp.object.requests.AddMoneyRequest;
import ru.readme.chatapp.object.requests.BaseRequest;
import ru.readme.chatapp.object.requests.ChangePasswordRequest;
import ru.readme.chatapp.object.requests.IdRequest;
import ru.readme.chatapp.object.requests.PhotoRequest;
import ru.readme.chatapp.object.requests.UserGiftRequest;
import ru.readme.chatapp.object.requests.UserRequest;
import ru.readme.chatapp.object.responses.BaseResponse;
import ru.readme.chatapp.object.responses.PMChatResponse;
import ru.readme.chatapp.object.responses.PhotoResponse;
import ru.readme.chatapp.object.responses.PhotosResponse;
import ru.readme.chatapp.object.responses.Status;
import ru.readme.chatapp.object.responses.UserResponse;
import ru.readme.chatapp.util.MD5;
import ru.readme.chatapp.util.Network;
import ru.readme.chatapp.util.SettingsHelper;
import ru.readme.chatapp.util.TokenHelper;

/**
 * Created by dima on 20.11.16.
 */

public class ProfileHelper {

    private Context context;
    private OnProfileActionListener listener;
    private SettingsHelper settingsHelper;
    private TokenHelper tokenHelper;

    public ProfileHelper(Context context, OnProfileActionListener listener) {
        this.context = context;
        this.listener = listener;
        settingsHelper = new SettingsHelper(context);
        tokenHelper = new TokenHelper(context);
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


    public void getPhotos(String id) {
        final UserRequest request = new UserRequest();
        request.setUserId(id);
        tokenHelper.getToken(new TokenHelper.OnGetTokenListener() {
            @Override
            public void onGetToken(String token) {
                Call<String> call = Network.getChatNetworkInterface().getPhotos(token, BaseRequest.code(request, CoderUser.get(context)));
                if (call != null) {
                    call.enqueue(new Callback<String>() {
                        @Override
                        public void onResponse(Call<String> call, Response<String> response) {
                            if (listener != null) {
                                if (response != null && response.body() != null) {
                                    PhotosResponse resp = PhotosResponse.decode(CoderUser.get(context), response.body(), PhotosResponse.class);
                                    if (resp != null) {
                                        if (resp.getPhotos() == null) {
                                            resp.setPhotos(new ArrayList());
                                        }
                                        Log.e("App", new Gson().toJson(resp));
                                        listener.onGetPhotos(resp.getStatus().equals(Status.STATUS_DONE), resp.getPhotos());
                                    } else {
                                        listener.onGetPhotos(false, null);
                                    }
                                } else {
                                    listener.onGetPhotos(false, null);
                                }
                            }
                        }

                        @Override
                        public void onFailure(Call<String> call, Throwable t) {
                            if (listener != null) {
                                listener.onGetPhotos(false, null);
                            }
                        }
                    });
                } else {

                }
            }
        });
    }

    public void addPhoto(File file, PhotoRequest photoRequest) {
        final RequestBody rbId =
                RequestBody.create(
                        MediaType.parse("multipart/form-data"), PhotoRequest.code(photoRequest, CoderUser.get(context)));
        RequestBody requestFile = RequestBody.create(MediaType.parse("image/png"), file);
        final MultipartBody.Part body =
                MultipartBody.Part.createFormData("file", file.getName(), requestFile);
        new TokenHelper(context).getToken(new TokenHelper.OnGetTokenListener() {
            @Override
            public void onGetToken(String token) {
                RequestBody rbTk =
                        RequestBody.create(
                                MediaType.parse("multipart/form-data"), token);
                Call<String> call = Network.getChatNetworkInterface().addPhoto(rbTk, body, rbId);
                call.enqueue(new Callback<String>() {
                    @Override
                    public void onResponse(Call<String> call, Response<String> response) {
                        if (listener != null) {
                            PhotoResponse resp = PhotoResponse.decode(CoderUser.get(context), response.body(), PhotoResponse.class);
                            Log.e("App", new Gson().toJson(resp));
                            Log.e("App", response.code() + " ");
                            if (resp != null) {
                                listener.onAddPhoto(resp);
                            } else {
                                listener.onAddPhoto(null);
                            }
                        }
                    }

                    @Override
                    public void onFailure(Call<String> call, Throwable t) {
                        if (listener != null) {
                            listener.onAddPhoto(null);
                        }
                    }
                });
            }
        });

    }

    public void startDialog(String user) {
        final UserRequest request = new UserRequest();
        request.setUserId(user);
        new TokenHelper(context).getToken(new TokenHelper.OnGetTokenListener() {
            @Override
            public void onGetToken(String token) {
                Call<String> call = Network.getChatNetworkInterface().getPersonalChat(token, BaseRequest.code(request, CoderUser.get(context)));
                call.enqueue(new Callback<String>() {
                    @Override
                    public void onResponse(Call<String> call, Response<String> response) {
                        if (listener != null) {
                            PMChatResponse resp = BaseResponse.decode(CoderUser.get(context), response.body(), PMChatResponse.class);
                            boolean done = resp != null && resp.getStatus().equals(Status.STATUS_DONE);
                            listener.onStartDialog(done, done ? resp : null);
                        }
                    }

                    @Override
                    public void onFailure(Call<String> call, Throwable t) {
                        listener.onStartDialog(false, null);
                    }
                });
            }
        });
    }

    public void sendGift(String gift, String to, boolean anon) {
        final UserGiftRequest request = new UserGiftRequest();
        request.setId(gift);
        request.setAnon(anon);
        request.setToUser(to);
        new TokenHelper(context).getToken(new TokenHelper.OnGetTokenListener() {
            @Override
            public void onGetToken(String token) {
                Call<String> call = Network.getChatNetworkInterface().sendGift(token, UserGiftRequest.code(request, CoderUser.get(context)));
                call.enqueue(new Callback<String>() {
                    @Override
                    public void onResponse(Call<String> call, Response<String> response) {
                        if (listener != null) {
                            BaseResponse resp = BaseResponse.decode(CoderUser.get(context), response.body(), BaseResponse.class);
                            boolean done = resp != null && resp.getStatus().equals(Status.STATUS_DONE);
                            listener.onSendGift(done);
                        }
                    }

                    @Override
                    public void onFailure(Call<String> call, Throwable t) {
                        listener.onSendGift(false);
                    }
                });
            }
        });
    }

    public void changePassword(String oldPassword, final String newPassword) {
        String md5 = MD5.MD5(newPassword + oldPassword);
        final ChangePasswordRequest request = new ChangePasswordRequest();
        request.setOldCoded(md5);
        request.setNewPassword(newPassword);
        new TokenHelper(context).getToken(new TokenHelper.OnGetTokenListener() {
            @Override
            public void onGetToken(String token) {
                Call<String> call = Network.getChatNetworkInterface().changePassword(token, BaseRequest.code(request, CoderUser.get(context)));
                call.enqueue(new Callback<String>() {
                    @Override
                    public void onResponse(Call<String> call, Response<String> response) {
                        if (listener != null) {
                            BaseResponse resp = BaseResponse.decode(CoderUser.get(context), response.body(), BaseResponse.class);
                            if (resp == null) {
                                CoderUser cu = CoderUser.get(context);
                                cu.setPassword(newPassword);
                                resp = BaseResponse.decode(cu, response.body(), BaseResponse.class);
                            }
                            Log.e("App", new Gson().toJson(resp));
                            boolean done = resp != null && resp.getStatus().equals(Status.STATUS_DONE);
                            listener.onChangePassword(done, done ? newPassword : null);
                        }
                    }

                    @Override
                    public void onFailure(Call<String> call, Throwable t) {
                        listener.onChangePassword(false, null);
                    }
                });
            }
        });
    }

    public void AddMoney(String u, String money) {
        final AddMoneyRequest request = new AddMoneyRequest();
        request.setSum(money);
        request.setUser(u);
        new TokenHelper(context).getToken(new TokenHelper.OnGetTokenListener() {
            @Override
            public void onGetToken(String token) {
                Call<String> call = Network.getChatNetworkInterface().AddMoney(token, BaseRequest.code(request, CoderUser.get(context)));
                call.enqueue(new Callback<String>() {
                    @Override
                    public void onResponse(Call<String> call, Response<String> response) {
                        if (listener != null) {
                            BaseResponse resp = BaseResponse.decode(CoderUser.get(context), response.body(), BaseResponse.class);
                            boolean done = resp != null && resp.getStatus().equals(Status.STATUS_DONE);
                            listener.onAddMoney(done);
                        }
                    }

                    @Override
                    public void onFailure(Call<String> call, Throwable t) {
                        listener.onAddMoney(false);
                    }
                });
            }
        });
    }

    public void banDevice(String deviceId) {
       final IdRequest request = new IdRequest(deviceId);
        new TokenHelper(context).getToken(new TokenHelper.OnGetTokenListener() {
            @Override
            public void onGetToken(String token) {
                Call<String> call = Network.getChatNetworkInterface().banDevice(token, BaseRequest.code(request, CoderUser.get(context)));
                call.enqueue(new Callback<String>() {
                    @Override
                    public void onResponse(Call<String> call, Response<String> response) {
                        if (listener != null) {
                            BaseResponse resp = BaseResponse.decode(CoderUser.get(context), response.body(), BaseResponse.class);

                            Log.e("App", new Gson().toJson(resp));
                            boolean done = resp != null && resp.getStatus().equals(Status.STATUS_DONE);
                            listener.onDeviceBan(done);
                        }
                    }

                    @Override
                    public void onFailure(Call<String> call, Throwable t) {
                        listener.onDeviceBan(false);
                    }
                });
            }
        });
    }


    public interface OnProfileActionListener {
        void onUserLoad(UserResponse user);

        void onGetPhotos(boolean done, List<PhotoResponse> photos);

        void onAddPhoto(PhotoResponse photoResponse);

        void onChangePassword(boolean done, String newPassword);

        void onAddMoney(boolean done);

        void onStartDialog(boolean status, PMChatResponse response);

        void onSendGift(boolean status);

        void onDeviceBan(boolean done);

    }
}
