package ru.readme.chatapp.helper;

import android.content.Context;
import android.util.Log;

import com.google.gson.Gson;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import ru.readme.chatapp.dialog.CreateCategoryDialog;
import ru.readme.chatapp.dialog.CreateRoomDialog;
import ru.readme.chatapp.object.CoderUser;
import ru.readme.chatapp.object.UserType;
import ru.readme.chatapp.object.requests.BaseRequest;
import ru.readme.chatapp.object.requests.CreateCategoryRequest;
import ru.readme.chatapp.object.requests.CreateRoomRequest;
import ru.readme.chatapp.object.requests.RoomsRequest;
import ru.readme.chatapp.object.responses.BaseResponse;
import ru.readme.chatapp.object.responses.CategoryResponse;
import ru.readme.chatapp.object.responses.RoomsResponse;
import ru.readme.chatapp.object.responses.Status;
import ru.readme.chatapp.object.responses.StatusResponse;
import ru.readme.chatapp.util.Network;
import ru.readme.chatapp.util.TokenHelper;

/**
 * Created by dima on 19.11.16.
 */

public class ChatsHelper {

    private Context context;
    private OnChatsHelperListener listener;

    public ChatsHelper(Context context, OnChatsHelperListener listener) {
        this.context = context;
        this.listener = listener;
    }

    public void createCategory(CreateCategoryDialog.CreateCategory cc, String parent) {
        if (cc != null) {
            int mask = UserType.TYPE_ADMIN;
            if (cc.isBanned()) {
                mask = mask | UserType.TYPE_BANNED;
            }
            if (cc.isInvisible()) {
                mask = mask | UserType.TYPE_INVISIBLE;
            }
            if (cc.isModer()) {
                mask = mask | UserType.TYPE_MODER;
            }
            if (cc.isUser()) {
                mask = mask | UserType.TYPE_USER;
            }
            Log.e("App", "mask=" + mask);
            final CreateCategoryRequest request = new CreateCategoryRequest();
            request.setMask(mask);
            request.setName(cc.getName());
            request.setParent(parent);
            new TokenHelper(context).getToken(new TokenHelper.OnGetTokenListener() {
                @Override
                public void onGetToken(String token) {
                    Call<String> call = Network.getChatNetworkInterface().createCategory(token, BaseRequest.code(request, CoderUser.get(context)));
                    call.enqueue(new Callback<String>() {
                        @Override
                        public void onResponse(Call<String> call, Response<String> response) {
                            RoomsResponse resp = BaseResponse.decode(CoderUser.get(context), response.body(), RoomsResponse.class);
                            if (listener != null) {
                                if (resp != null && resp.getStatus().equals(Status.STATUS_DONE)) {
                                    listener.onCreateCategory(resp.getStatus().equals(Status.STATUS_DONE), resp);
                                } else {
                                    listener.onCreateCategory(false, null);
                                }
                            }
                        }

                        @Override
                        public void onFailure(Call<String> call, Throwable t) {
                            if (listener != null) {
                                listener.onCreateCategory(false, null);
                            }
                        }
                    });
                }
            });
        } else {
            if (listener != null) {
                listener.onCreateCategory(false, null);
            }
        }
    }

    public void getRooms(String category) {
        final RoomsRequest request = new RoomsRequest();
        request.setParent(category);
        new TokenHelper(context).getToken(new TokenHelper.OnGetTokenListener() {
            @Override
            public void onGetToken(String token) {
                Call<String> call = Network.getChatNetworkInterface().getRooms(token, BaseRequest.code(request, CoderUser.get(context)));
                call.enqueue(new Callback<String>() {
                    @Override
                    public void onResponse(Call<String> call, Response<String> response) {
                        Log.e("App resp", "get rooms code="+ response.code()+"");
                        if(response.code()!=200){
                            try {
                                Log.e("App resp", response.errorBody().string() + "");
                            } catch (Exception ex){}
                        }
                        if (listener != null) {
                            RoomsResponse resp = BaseResponse.decode(CoderUser.get(context), response.body(), RoomsResponse.class);
                            if (resp != null) {
                                listener.onGetRooms(resp.getStatus().equals(Status.STATUS_DONE), resp);
                            } else {
                                listener.onGetRooms(false, null);
                            }
                        } else {
                            Log.e("App resp", "get rooms not listener");
                        }
                    }

                    @Override
                    public void onFailure(Call<String> call, Throwable t) {
                        if (listener != null) {
                            Log.e("App req", BaseRequest.code(request, CoderUser.get(context)), t);
                            listener.onGetRooms(false, null);
                        }
                    }
                });
            }
        });
    }

    public void editCategory(CategoryResponse category) {
        if (category != null) {
            final CreateCategoryRequest request = new CreateCategoryRequest();
            request.setName(category.getName());
            request.setMask(category.getMask());
            request.setId(category.getId());
            new TokenHelper(context).getToken(new TokenHelper.OnGetTokenListener() {
                @Override
                public void onGetToken(String token) {
                    Call<String> call = Network.getChatNetworkInterface().updateCategory(token, BaseRequest.code(request, CoderUser.get(context)));
                    call.enqueue(new Callback<String>() {
                        @Override
                        public void onResponse(Call<String> call, Response<String> response) {
                            if (listener != null) {
                                StatusResponse resp = BaseResponse.decode(CoderUser.get(context), response.body(), StatusResponse.class);
                                listener.onEditCategory(resp != null && resp.getStatus().equals(Status.STATUS_DONE));
                            }
                        }

                        @Override
                        public void onFailure(Call<String> call, Throwable t) {
                            if (listener != null) {
                                listener.onEditCategory(false);
                            }
                        }
                    });
                }
            });
        } else {
            if (listener != null) {
                listener.onEditCategory(false);
            }
        }
    }

    public void deleteCategory(String category) {
        final CreateCategoryRequest request = new CreateCategoryRequest();
        request.setId(category);
        new TokenHelper(context)
                .getToken(new TokenHelper.OnGetTokenListener() {
                    @Override
                    public void onGetToken(String token) {
                        Call<String> call = Network.getChatNetworkInterface().deleteCategory(token, BaseRequest.code(request, CoderUser.get(context)));
                        call.enqueue(new Callback<String>() {
                            @Override
                            public void onResponse(Call<String> call, Response<String> response) {
                                if (listener != null) {
                                    StatusResponse resp = BaseResponse.decode(CoderUser.get(context), response.body(), StatusResponse.class);
                                    listener.onDeleteCategory(resp != null && resp.getStatus().equals(Status.STATUS_DONE));
                                }
                            }

                            @Override
                            public void onFailure(Call<String> call, Throwable t) {
                                if (listener != null) {
                                    listener.onDeleteCategory(false);
                                }
                            }
                        });
                    }
                });
    }

    public void createRoom(CreateRoomDialog.CreateChat cc, String category) {
        if (cc != null) {
            int mask = UserType.TYPE_ADMIN;
            if (cc.isBanned()) {
                mask = mask | UserType.TYPE_BANNED;
            }
            if (cc.isInvisible()) {
                mask = mask | UserType.TYPE_INVISIBLE;
            }
            if (cc.isModer()) {
                mask = mask | UserType.TYPE_MODER;
            }
            if (cc.isUser()) {
                mask = mask | UserType.TYPE_USER;
            }
            final CreateRoomRequest request = new CreateRoomRequest();
            request.setMask(mask);
            request.setName(cc.getName());
            request.setCategory(category);
            new TokenHelper(context).getToken(new TokenHelper.OnGetTokenListener() {
                @Override
                public void onGetToken(String token) {
                    Call<String> call = Network.getChatNetworkInterface().createRoom(token, BaseRequest.code(request, CoderUser.get(context)));
                    call.enqueue(new Callback<String>() {
                        @Override
                        public void onResponse(Call<String> call, Response<String> response) {
                            if (listener != null) {
                                RoomsResponse resp = BaseResponse.decode(CoderUser.get(context), response.body(), RoomsResponse.class);
                                Log.e("App",new Gson().toJson(resp));
                                listener.onCreateRoom(resp.getStatus().equals(Status.STATUS_DONE), resp);
                            }
                        }

                        @Override
                        public void onFailure(Call<String> call, Throwable t) {
                            Log.e("App", new Gson().toJson(request), t);
                            if (listener != null) {
                                listener.onCreateCategory(false, null);
                            }
                        }
                    });
                }
            });
        } else {
            if (listener != null) {
                listener.onCreateCategory(false, null);
            }
        }
    }

    public interface OnChatsHelperListener {
        void onCreateCategory(boolean create, RoomsResponse response);

        void onGetRooms(boolean get, RoomsResponse response);

        void onCreateRoom(boolean create, RoomsResponse response);

        void onEditCategory(boolean status);

        void onDeleteCategory(boolean status);
    }
}
