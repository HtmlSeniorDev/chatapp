package ru.readme.chatapp.helper;

import android.content.Context;
import android.util.Log;

import com.google.gson.Gson;

import java.io.File;

import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import ru.readme.chatapp.object.CoderUser;
import ru.readme.chatapp.object.requests.BaseRequest;
import ru.readme.chatapp.object.requests.IdRequest;
import ru.readme.chatapp.object.requests.PhotoRequest;
import ru.readme.chatapp.object.requests.UserRequest;
import ru.readme.chatapp.object.responses.BaseResponse;
import ru.readme.chatapp.object.responses.PhotoResponse;
import ru.readme.chatapp.object.responses.Status;
import ru.readme.chatapp.object.responses.UserResponse;
import ru.readme.chatapp.util.Network;
import ru.readme.chatapp.util.TokenHelper;

/**
 * Created by dima on 21.11.16.
 */

public class EditProfileHelper {

    private OnEditProfileListener listener;
    private Context context;


    public EditProfileHelper(OnEditProfileListener listener, Context context) {
        this.listener = listener;
        this.context = context;
    }

    public void checkNic(String nic) {
        final UserRequest request = new UserRequest();
        request.setNic(nic);
        new TokenHelper(context).getToken(new TokenHelper.OnGetTokenListener() {
            @Override
            public void onGetToken(String token) {
                Call<String> call = Network.getChatNetworkInterface().checkNic(token, BaseRequest.code(request, CoderUser.get(context)));
                call.enqueue(new Callback<String>() {
                    @Override
                    public void onResponse(Call<String> call, Response<String> response) {
                        boolean lg = false;
                        BaseResponse resp = BaseResponse.decode(CoderUser.get(context), response.body(), BaseResponse.class);
                        Log.e("App", new Gson().toJson(resp));
                        if (resp != null && resp.getStatus().equals(Status.STATUS_DONE)) {
                            lg = true;
                        }
                        if (listener != null) {
                            listener.onCheckNic(lg);
                        }
                    }

                    @Override
                    public void onFailure(Call<String> call, Throwable t) {
                        if (listener != null) {
                            listener.onCheckNic(false);
                        }
                    }
                });
            }
        });

    }


    public void editProfile(UserResponse user) {
        final UserRequest request = new UserRequest();
        request.setUserId(user.getId());
        request.setNic(user.getNic());
        request.setAbout(user.getAbout());
        if (user.getBday() != null) {
            request.setBday(user.getBday().getTime());
        }
        request.setCity(user.getCity());
        request.setSex(user.getSex());
        request.setColor(user.getColor());
        request.setFirstName(user.getFirstName());
        request.setAvatarLink(user.getAvatarLink());
        request.setLastName(user.getLastName());
        request.setType(user.getType());
        new TokenHelper(context).getToken(new TokenHelper.OnGetTokenListener() {
            @Override
            public void onGetToken(String token) {
                Call<String> call = Network.getChatNetworkInterface().updateUser(token, BaseRequest.code(request, CoderUser.get(context)));
                call.enqueue(new Callback<String>() {
                    @Override
                    public void onResponse(Call<String> call, Response<String> response) {
                        if (listener != null) {
                            UserResponse resp = BaseResponse.decode(CoderUser.get(context), response.body(), UserResponse.class);
                            listener.onEditProfile(resp != null && resp.getStatus().equals(Status.STATUS_DONE));
                        }
                    }

                    @Override
                    public void onFailure(Call<String> call, Throwable t) {
                        if (listener != null) {
                            listener.onEditProfile(false);
                        }
                    }
                });
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

    public void setForProfile(final String photo) {
        final IdRequest request = new IdRequest();
        request.setId(photo);
        new TokenHelper(context).getToken(new TokenHelper.OnGetTokenListener() {
            @Override
            public void onGetToken(String token) {
                Call<String> call = Network.getChatNetworkInterface().setForUser(token, BaseRequest.code(request, CoderUser.get(context)));
                if (call != null) {
                    call.enqueue(new Callback<String>() {
                        @Override
                        public void onResponse(Call<String> call, Response<String> response) {
                            if (listener != null) {
                                if (response != null && response.body() != null) {
                                    BaseResponse resp = BaseResponse.decode(CoderUser.get(context), response.body(), BaseResponse.class);
                                    Log.e("App", new Gson().toJson(resp));
                                    if (resp != null) {
                                        listener.onSetForProfile(resp.getStatus().equals(Status.STATUS_DONE));
                                    } else {
                                        listener.onSetForProfile(false);
                                    }
                                } else {
                                    listener.onSetForProfile(false);
                                }
                            }
                        }

                        @Override
                        public void onFailure(Call<String> call, Throwable t) {
                            if (listener != null) {
                                listener.onSetForProfile(false);
                            }
                        }
                    });
                } else {

                }
            }
        });
    }

    public interface OnEditProfileListener {
        void onEditProfile(boolean status);

        void onCheckNic(boolean done);

        void onSetForProfile(boolean done);

        void onAddPhoto(PhotoResponse photoResponse);
    }
}
