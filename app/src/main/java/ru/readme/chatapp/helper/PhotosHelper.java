package ru.readme.chatapp.helper;

import android.content.Context;
import android.util.Log;

import com.google.gson.Gson;

import java.io.File;
import java.util.ArrayList;

import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import ru.readme.chatapp.object.CoderUser;
import ru.readme.chatapp.object.requests.BaseRequest;
import ru.readme.chatapp.object.requests.FolderRequest;
import ru.readme.chatapp.object.requests.IdRequest;
import ru.readme.chatapp.object.requests.PhotoRequest;
import ru.readme.chatapp.object.responses.BaseResponse;
import ru.readme.chatapp.object.responses.FolderResponse;
import ru.readme.chatapp.object.responses.PhotoResponse;
import ru.readme.chatapp.object.responses.PhotosResponse;
import ru.readme.chatapp.object.responses.Status;
import ru.readme.chatapp.util.Network;
import ru.readme.chatapp.util.TokenHelper;

/**
 * Created by dima on 03.01.17.
 */

public class PhotosHelper {
    private Context context;
    private TokenHelper tokenHelper;
    private OnPhotoHelperListener listener;

    public PhotosHelper(Context context, OnPhotoHelperListener listener) {
        this.context = context;
        this.listener = listener;
        tokenHelper = new TokenHelper(context);
    }


    public void getFolder(String userId, String folderId) {
        final FolderRequest request = new FolderRequest();
        request.setId(userId);
        request.setParent(folderId);
        tokenHelper.getToken(new TokenHelper.OnGetTokenListener() {
            @Override
            public void onGetToken(String token) {
                Call<String> call = Network.getChatNetworkInterface().getPhotoFolder(token, BaseRequest.code(request, CoderUser.get(context)));
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
                                        listener.onGetPhotos(resp.getStatus().equals(Status.STATUS_DONE), resp);
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

    public void addFolder(final FolderRequest request) {
        tokenHelper.getToken(new TokenHelper.OnGetTokenListener() {
            @Override
            public void onGetToken(String token) {
                Call<String> call = Network.getChatNetworkInterface().addPhotoFolder(token, BaseRequest.code(request, CoderUser.get(context)));
                if (call != null) {
                    call.enqueue(new Callback<String>() {
                        @Override
                        public void onResponse(Call<String> call, Response<String> response) {
                            if (listener != null) {
                                if (response != null && response.body() != null) {
                                    FolderResponse resp = FolderResponse.decode(CoderUser.get(context), response.body(), FolderResponse.class);
                                    if (resp != null) {
                                        listener.onAddPhotoFolder(resp.getStatus().equals(Status.STATUS_DONE), resp);
                                    } else {
                                        listener.onAddPhotoFolder(false, null);
                                    }
                                } else {
                                    listener.onAddPhotoFolder(false, null);
                                }
                            }
                        }

                        @Override
                        public void onFailure(Call<String> call, Throwable t) {
                            if (listener != null) {
                                listener.onAddPhotoFolder(false, null);
                            }
                        }
                    });
                } else {

                }
            }
        });
    }

    public void updateFolder(final FolderRequest request) {
        tokenHelper.getToken(new TokenHelper.OnGetTokenListener() {
            @Override
            public void onGetToken(String token) {
                Call<String> call = Network.getChatNetworkInterface().updatePhotoFolder(token, BaseRequest.code(request, CoderUser.get(context)));
                if (call != null) {
                    call.enqueue(new Callback<String>() {
                        @Override
                        public void onResponse(Call<String> call, Response<String> response) {
                            if (listener != null) {
                                if (response != null && response.body() != null) {
                                    FolderResponse resp = FolderResponse.decode(CoderUser.get(context), response.body(), FolderResponse.class);
                                    Log.e("App", new Gson().toJson(resp));
                                    if (resp != null) {
                                        listener.onUpdatePhotoFolder(resp.getStatus().equals(Status.STATUS_DONE), resp);
                                    } else {
                                        listener.onUpdatePhotoFolder(false, null);
                                    }
                                } else {
                                    listener.onUpdatePhotoFolder(false, null);
                                }
                            }
                        }

                        @Override
                        public void onFailure(Call<String> call, Throwable t) {
                            if (listener != null) {
                                listener.onUpdatePhotoFolder(false, null);
                            }
                        }
                    });
                } else {

                }
            }
        });
    }


    public void deleteFolder(String folder) {
        final FolderRequest request = new FolderRequest();
        request.setId(folder);
        tokenHelper.getToken(new TokenHelper.OnGetTokenListener() {
            @Override
            public void onGetToken(String token) {
                Call<String> call = Network.getChatNetworkInterface().deletePhotoFolder(token, BaseRequest.code(request, CoderUser.get(context)));
                if (call != null) {
                    call.enqueue(new Callback<String>() {
                        @Override
                        public void onResponse(Call<String> call, Response<String> response) {
                            if (listener != null) {
                                if (response != null && response.body() != null) {
                                    BaseResponse resp = BaseResponse.decode(CoderUser.get(context), response.body(), BaseResponse.class);
                                    Log.e("App", new Gson().toJson(resp));
                                    if (resp != null) {
                                        listener.onFolderDelete(resp.getStatus().equals(Status.STATUS_DONE));
                                    } else {
                                        listener.onFolderDelete(false);
                                    }
                                } else {
                                    listener.onFolderDelete(false);
                                }
                            }
                        }

                        @Override
                        public void onFailure(Call<String> call, Throwable t) {
                            if (listener != null) {
                                listener.onFolderDelete(false);
                            }
                        }
                    });
                } else {

                }
            }
        });
    }

    public void deletePhoto(final String photo) {
        final IdRequest request = new IdRequest();
        request.setId(photo);
        tokenHelper.getToken(new TokenHelper.OnGetTokenListener() {
            @Override
            public void onGetToken(String token) {
                Call<String> call = Network.getChatNetworkInterface().deletePhoto(token, BaseRequest.code(request, CoderUser.get(context)));
                if (call != null) {
                    call.enqueue(new Callback<String>() {
                        @Override
                        public void onResponse(Call<String> call, Response<String> response) {
                            if (listener != null) {
                                if (response != null && response.body() != null) {
                                    BaseResponse resp = BaseResponse.decode(CoderUser.get(context), response.body(), BaseResponse.class);
                                    Log.e("App", new Gson().toJson(resp));
                                    if (resp != null) {
                                        listener.onDeletePhoto(resp.getStatus().equals(Status.STATUS_DONE), photo);
                                    } else {
                                        listener.onDeletePhoto(false, photo);
                                    }
                                } else {
                                    listener.onDeletePhoto(false, photo);
                                }
                            }
                        }

                        @Override
                        public void onFailure(Call<String> call, Throwable t) {
                            if (listener != null) {
                                listener.onDeletePhoto(false, photo);
                            }
                        }
                    });
                } else {

                }
            }
        });
    }

    public void setForProfile(final String photo) {
        final IdRequest request = new IdRequest();
        request.setId(photo);
        tokenHelper.getToken(new TokenHelper.OnGetTokenListener() {
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

    public interface OnPhotoHelperListener {
        void onGetPhotos(boolean done, PhotosResponse response);

        void onAddPhotoFolder(boolean done, FolderResponse response);

        void onUpdatePhotoFolder(boolean done, FolderResponse response);

        void onAddPhoto(PhotoResponse photoResponse);

        void onFolderDelete(boolean done);

        void onDeletePhoto(boolean done, String id);

        void onSetForProfile(boolean done);
    }
}
