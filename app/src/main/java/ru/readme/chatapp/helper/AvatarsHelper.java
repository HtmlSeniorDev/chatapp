package ru.readme.chatapp.helper;

import android.content.Context;

import java.io.File;
import java.util.ArrayList;

import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import ru.readme.chatapp.object.CoderUser;
import ru.readme.chatapp.object.requests.AvatarRequest;
import ru.readme.chatapp.object.requests.BaseRequest;
import ru.readme.chatapp.object.requests.SetDefaultAvatarRequest;
import ru.readme.chatapp.object.responses.AvatarResponse;
import ru.readme.chatapp.object.responses.AvatarsResponse;
import ru.readme.chatapp.object.responses.BaseResponse;
import ru.readme.chatapp.object.responses.Status;
import ru.readme.chatapp.object.responses.StatusResponse;
import ru.readme.chatapp.util.Network;
import ru.readme.chatapp.util.TokenHelper;

/**
 * Created by dima on 22.12.16.
 */

public class AvatarsHelper {

    private Context context;
    private TokenHelper tokenHelper;
    private OnAvatarHelperListener listener;

    public AvatarsHelper(Context context, OnAvatarHelperListener listener) {
        this.context = context;
        this.listener = listener;
        tokenHelper = new TokenHelper(context);
    }

    public void add(String name, int price) {
        final AvatarRequest request = new AvatarRequest();
        request.setName(name);
        request.setPrice(price);
        tokenHelper.getToken(new TokenHelper.OnGetTokenListener() {
            @Override
            public void onGetToken(String token) {
                Call<String> call = Network.getChatNetworkInterface().addAvatar(token, BaseRequest.code(request, CoderUser.get(context)));
                if (call != null) {
                    call.enqueue(new Callback<String>() {
                        @Override
                        public void onResponse(Call<String> call, Response<String> response) {
                            if (listener != null) {
                                if (response != null && response.body() != null) {
                                    AvatarResponse resp = AvatarResponse.decode(CoderUser.get(context), response.body(), AvatarResponse.class);
                                    if (resp != null) {
                                        listener.onAddAvatar(resp.getStatus().equals(Status.STATUS_DONE), resp);
                                    } else {
                                        listener.onAddAvatar(false, null);
                                    }
                                } else {
                                    listener.onAddAvatar(false, null);
                                }
                            }
                        }

                        @Override
                        public void onFailure(Call<String> call, Throwable t) {
                            if (listener != null) {
                                listener.onAddAvatar(false, null);
                            }
                        }
                    });
                } else {

                }
            }
        });
    }

    public void update(String id, String name, int price) {
        final AvatarRequest request = new AvatarRequest();
        request.setName(name);
        request.setPrice(price);
        request.setId(id);
        tokenHelper.getToken(new TokenHelper.OnGetTokenListener() {
            @Override
            public void onGetToken(String token) {
                Call<String> call = Network.getChatNetworkInterface().updateAvatar(token, BaseRequest.code(request, CoderUser.get(context)));
                if (call != null) {
                    call.enqueue(new Callback<String>() {
                        @Override
                        public void onResponse(Call<String> call, Response<String> response) {
                            if (listener != null) {
                                if (response != null && response.body() != null) {
                                    AvatarResponse resp = AvatarResponse.decode(CoderUser.get(context), response.body(), AvatarResponse.class);
                                    if (resp != null) {
                                        listener.onUpdateAvatar(resp.getStatus().equals(Status.STATUS_DONE), resp);
                                    } else {
                                        listener.onUpdateAvatar(false, null);
                                    }
                                } else {
                                    listener.onUpdateAvatar(false, null);
                                }
                            }
                        }

                        @Override
                        public void onFailure(Call<String> call, Throwable t) {
                            if (listener != null) {
                                listener.onUpdateAvatar(false, null);
                            }
                        }
                    });
                } else {

                }
            }
        });
    }

    public void remove(String id) {
        final AvatarRequest request = new AvatarRequest();
        request.setId(id);
        tokenHelper.getToken(new TokenHelper.OnGetTokenListener() {
            @Override
            public void onGetToken(String token) {
                Call<String> call = Network.getChatNetworkInterface().removeAvatar(token, BaseRequest.code(request, CoderUser.get(context)));
                if (call != null) {
                    call.enqueue(new Callback<String>() {
                        @Override
                        public void onResponse(Call<String> call, Response<String> response) {
                            if (listener != null) {
                                if (response != null && response.body() != null) {
                                    BaseResponse resp = BaseResponse.decode(CoderUser.get(context), response.body(), BaseResponse.class);
                                    if (resp != null) {
                                        listener.onRemoveAvatar(resp.getStatus().equals(Status.STATUS_DONE), request);
                                    } else {
                                        listener.onRemoveAvatar(false, request);
                                    }
                                } else {
                                    listener.onRemoveAvatar(false, request);
                                }
                            }
                        }

                        @Override
                        public void onFailure(Call<String> call, Throwable t) {
                            if (listener != null) {
                                listener.onRemoveAvatar(false, request);
                            }
                        }
                    });
                } else {

                }
            }
        });
    }

    public void setDefault(String id, int group) {
        final SetDefaultAvatarRequest request = new SetDefaultAvatarRequest();
        request.setAvatar(id);
        request.setGroup(group);
        tokenHelper.getToken(new TokenHelper.OnGetTokenListener() {
            @Override
            public void onGetToken(String token) {
                Call<String> call = Network.getChatNetworkInterface().setDefaultAvatars(token, BaseRequest.code(request, CoderUser.get(context)));
                if (call != null) {
                    call.enqueue(new Callback<String>() {
                        @Override
                        public void onResponse(Call<String> call, Response<String> response) {
                            if (listener != null) {
                                if (response != null && response.body() != null) {
                                    BaseResponse resp = BaseResponse.decode(CoderUser.get(context), response.body(), BaseResponse.class);
                                    if (resp != null) {
                                        listener.onSetDefaultAvatar(resp.getStatus().equals(Status.STATUS_DONE));
                                    } else {
                                        listener.onSetDefaultAvatar(false);
                                    }
                                } else {
                                    listener.onSetDefaultAvatar(false);
                                }
                            }
                        }

                        @Override
                        public void onFailure(Call<String> call, Throwable t) {
                            if (listener != null) {
                                listener.onSetDefaultAvatar(false);
                            }
                        }
                    });
                } else {

                }
            }
        });
    }

    public void getAvatars() {
        final BaseRequest request = new BaseRequest();
        tokenHelper.getToken(new TokenHelper.OnGetTokenListener() {
            @Override
            public void onGetToken(String token) {
                Call<String> call = Network.getChatNetworkInterface().getAvatarsDescription(token, BaseRequest.code(request, CoderUser.get(context)));
                if (call != null) {
                    call.enqueue(new Callback<String>() {
                        @Override
                        public void onResponse(Call<String> call, Response<String> response) {
                            if (listener != null) {
                                if (response != null && response.body() != null) {
                                    AvatarsResponse resp = AvatarsResponse.decode(CoderUser.get(context), response.body(), AvatarsResponse.class);
                                    if (resp != null) {
                                        if (resp.getAvatars() == null) {
                                            resp.setAvatars(new ArrayList<AvatarResponse>());
                                        }
                                        listener.onGetAvatars(resp.getStatus().equals(Status.STATUS_DONE), resp);
                                    } else {
                                        listener.onGetAvatars(false, null);
                                    }
                                } else {
                                    listener.onGetAvatars(false, null);
                                }
                            }
                        }

                        @Override
                        public void onFailure(Call<String> call, Throwable t) {
                            if (listener != null) {
                                listener.onGetAvatars(false, null);
                            }
                        }
                    });
                } else {

                }
            }
        });
    }

    public void uploadAvatar(final String id, final File file, final OnAvatarUploadListener listener) {
        RequestBody requestFile = RequestBody.create(MediaType.parse("image/png"), file);
        final RequestBody rbId =
                RequestBody.create(
                        MediaType.parse("multipart/form-data"), id);
        final MultipartBody.Part body =
                MultipartBody.Part.createFormData("file", file.getName(), requestFile);
        new TokenHelper(context).getToken(new TokenHelper.OnGetTokenListener() {
            @Override
            public void onGetToken(String token) {
                RequestBody rbTk =
                        RequestBody.create(
                                MediaType.parse("multipart/form-data"), token);
                Call<String> call = Network.getChatNetworkInterface().uploadAvatar(rbTk, rbId, body);
                call.enqueue(new Callback<String>() {
                    @Override
                    public void onResponse(Call<String> call, Response<String> response) {
                        if (listener != null) {
                            StatusResponse resp = BaseResponse.decode(CoderUser.get(context), response.body(), StatusResponse.class);
                            if (resp != null) {
                                listener.onUploadAvatar(response != null && response.body() != null && resp.getStatus().equals(Status.STATUS_DONE), id);
                            } else {
                                listener.onUploadAvatar(false, id);
                            }
                        }
                    }

                    @Override
                    public void onFailure(Call<String> call, Throwable t) {
                        if (listener != null) {
                            listener.onUploadAvatar(false, id);
                        }
                    }
                });
            }
        });
    }


    public interface OnAvatarHelperListener {
        void onAddAvatar(boolean done, AvatarResponse response);

        void onUpdateAvatar(boolean done, AvatarResponse response);

        void onRemoveAvatar(boolean done, AvatarRequest request);

        void onSetDefaultAvatar(boolean done);

        void onGetAvatars(boolean done, AvatarsResponse response);
    }

    public interface OnAvatarUploadListener {
        void onUploadAvatar(boolean done, String id);
    }
}
