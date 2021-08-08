package ru.readme.chatapp.helper;

import android.content.Context;
import android.util.Log;

import java.io.File;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import ru.readme.chatapp.object.CoderUser;
import ru.readme.chatapp.object.MessageType;
import ru.readme.chatapp.object.requests.BanRequest;
import ru.readme.chatapp.object.requests.BaseRequest;
import ru.readme.chatapp.object.requests.CreateAttachmentRequest;
import ru.readme.chatapp.object.requests.CreateRoomRequest;
import ru.readme.chatapp.object.requests.GetRoomRequest;
import ru.readme.chatapp.object.requests.KickRequest;
import ru.readme.chatapp.object.requests.MessageRequest;
import ru.readme.chatapp.object.requests.MessagesRequest;
import ru.readme.chatapp.object.requests.NoticeRequest;
import ru.readme.chatapp.object.requests.RemoveMessageRequest;
import ru.readme.chatapp.object.requests.RoomRequest;
import ru.readme.chatapp.object.requests.UserRequest;
import ru.readme.chatapp.object.responses.AttachmentResponse;
import ru.readme.chatapp.object.responses.BaseResponse;
import ru.readme.chatapp.object.responses.MessageResponse;
import ru.readme.chatapp.object.responses.MessagesResponse;
import ru.readme.chatapp.object.responses.PMChatResponse;
import ru.readme.chatapp.object.responses.Status;
import ru.readme.chatapp.object.responses.StatusResponse;
import ru.readme.chatapp.object.responses.UserResponse;
import ru.readme.chatapp.object.responses.UsersResponse;
import ru.readme.chatapp.util.Coder;
import ru.readme.chatapp.util.Network;
import ru.readme.chatapp.util.TokenHelper;

/**
 * Created by dima on 23.11.16.
 */

public class ChatHelper {
    private Context context;
    private OnChatActionListener listener;
    private TokenHelper tokenHelper;
    private int type = -1;
    private String place = "";


    public ChatHelper(Context context, OnChatActionListener listener) {
        this.context = context;
        this.listener = listener;
        tokenHelper = new TokenHelper(context);
    }



    public void setType(int type) {
        this.type = type;
    }

    public void setPlace(String place) {
        this.place = place;
    }

    public void sendMessage(String message, List<String> attachments) {
        final MessageRequest request = new MessageRequest();
        request.setType(type);
        request.setAttachments(attachments);
        request.setMessage(message);
        request.setPlace(place);
        tokenHelper.getToken(new TokenHelper.OnGetTokenListener() {
            @Override
            public void onGetToken(String token) {
                Call<String> call = Network.getChatNetworkInterface().sendMessage(token, BaseRequest.code(request, CoderUser.get(context)));
                call.enqueue(new Callback<String>() {
                    @Override
                    public void onResponse(Call<String> call, Response<String> response) {
                        if (listener != null) {
                            StatusResponse resp = BaseResponse.decode(CoderUser.get(context), response.body(), StatusResponse.class);
                            if (resp != null) {
                                listener.onSendMessage(resp.getStatus().equals(Status.STATUS_DONE), request, resp);
                            } else {
                                listener.onSendMessage(false, request, null);
                            }
                        }
                    }

                    @Override
                    public void onFailure(Call<String> call, Throwable t) {
                        if (listener != null) {
                            request.setMessage(Coder.decodeMessage(request.getMessage(), CoderUser.get(context)));
                            listener.onSendMessage(false, request, null);
                        }
                    }
                });
            }
        });
    }

    public void deleteChat(String chat) {
        final CreateRoomRequest request = new CreateRoomRequest();
        request.setId(chat);
        tokenHelper.getToken(new TokenHelper.OnGetTokenListener() {
            @Override
            public void onGetToken(String token) {
                Call<String> call = Network.getChatNetworkInterface().deleteRoom(token, BaseRequest.code(request, CoderUser.get(context)));
                call.enqueue(new Callback<String>() {
                    @Override
                    public void onResponse(Call<String> call, Response<String> response) {
                        if (listener != null) {
                            StatusResponse resp = BaseResponse.decode(CoderUser.get(context), response.body(), StatusResponse.class);
                            listener.onChatDelete(resp != null && resp.getStatus().equals(Status.STATUS_DONE));
                        }
                    }

                    @Override
                    public void onFailure(Call<String> call, Throwable t) {
                        if (listener != null) {
                            listener.onChatDelete(false);
                        }
                    }
                });
            }
        });
    }

    public void editChat(String chat, String name, int mask) {
        final CreateRoomRequest request = new CreateRoomRequest();
        request.setId(chat);
        request.setMask(mask);
        request.setName(name);
        tokenHelper.getToken(new TokenHelper.OnGetTokenListener() {
            @Override
            public void onGetToken(String token) {
                Call<String> call = Network.getChatNetworkInterface().updateRoom(token, BaseRequest.code(request, CoderUser.get(context)));
                call.enqueue(new Callback<String>() {
                    @Override
                    public void onResponse(Call<String> call, Response<String> response) {
                        StatusResponse resp = BaseResponse.decode(CoderUser.get(context), response.body(), StatusResponse.class);
                        if (listener != null) {
                            if (resp != null) {
                                listener.onChatEdit(resp.getStatus().equals(Status.STATUS_DONE));
                            } else {
                                listener.onChatEdit(false);
                            }
                        }
                    }

                    @Override
                    public void onFailure(Call<String> call, Throwable t) {
                        if (listener != null) {
                            listener.onChatEdit(false);
                        }
                    }
                });
            }
        });
    }

    public void deleteMessage(String message) {
        final RemoveMessageRequest request = new RemoveMessageRequest();
        request.setMessage(message);
        tokenHelper.getToken(new TokenHelper.OnGetTokenListener() {
            @Override
            public void onGetToken(String token) {
                Call<String> call = Network.getChatNetworkInterface().deleteMessage(token, BaseRequest.code(request, CoderUser.get(context)));
                call.enqueue(new Callback<String>() {
                    @Override
                    public void onResponse(Call<String> call, Response<String> response) {
                        StatusResponse resp = BaseResponse.decode(CoderUser.get(context), response.body(), StatusResponse.class);
                        if (listener != null) {
                            if (resp != null) {
                                listener.onDeleteMessage(resp.getStatus().equals(Status.STATUS_DONE), request);
                            } else {
                                listener.onDeleteMessage(false, request);
                            }
                        }
                    }

                    @Override
                    public void onFailure(Call<String> call, Throwable t) {
                        if (listener != null) {
                            listener.onDeleteMessage(false, request);
                        }
                    }
                });
            }
        });
    }


    public void kickUser(String user, String room) {
        final KickRequest request = new KickRequest();
        request.setRoom(room);
        request.setKicked(user);
        tokenHelper.getToken(new TokenHelper.OnGetTokenListener() {
            @Override
            public void onGetToken(String token) {
                Call<String> call = Network.getChatNetworkInterface().kickUser(token, BaseRequest.code(request, CoderUser.get(context)));
                call.enqueue(new Callback<String>() {
                    @Override
                    public void onResponse(Call<String> call, Response<String> response) {
                        StatusResponse resp = BaseResponse.decode(CoderUser.get(context), response.body(), StatusResponse.class);
                        if (listener != null) {
                            if (resp != null) {
                                listener.onKick(resp.getStatus().equals(Status.STATUS_DONE));
                            } else {
                                listener.onKick(false);
                            }
                        }
                    }

                    @Override
                    public void onFailure(Call<String> call, Throwable t) {
                        if (listener != null) {
                            listener.onKick(false);
                        }
                    }
                });
            }
        });
    }

    public void banUser(String user, String description, long time) {
        final BanRequest request = new BanRequest();
        request.setBanned(user);
        request.setDescription(description);
        request.setTime(time);
        tokenHelper.getToken(new TokenHelper.OnGetTokenListener() {
            @Override
            public void onGetToken(String token) {
                Call<String> call = Network.getChatNetworkInterface().banUser(token, BaseRequest.code(request, CoderUser.get(context)));
                call.enqueue(new Callback<String>() {
                    @Override
                    public void onResponse(Call<String> call, Response<String> response) {
                        StatusResponse resp = BaseResponse.decode(CoderUser.get(context), response.body(), StatusResponse.class);
                        if (listener != null) {
                            if (resp != null) {
                                Log.e("Ban", resp.getStatus());
                                listener.onBanUser(resp.getStatus().equals(Status.STATUS_DONE));
                            } else {
                                listener.onBanUser(false);
                            }
                        }
                    }

                    @Override
                    public void onFailure(Call<String> call, Throwable t) {
                        if (listener != null) {
                            listener.onBanUser(false);
                        }
                    }
                });
            }
        });
    }

    public void setInvisibleUser(String user) {
        final BanRequest request = new BanRequest();
        request.setBanned(user);
        tokenHelper.getToken(new TokenHelper.OnGetTokenListener() {
            @Override
            public void onGetToken(String token) {
                Call<String> call = Network.getChatNetworkInterface().setInvisible(token, BaseRequest.code(request, CoderUser.get(context)));
                call.enqueue(new Callback<String>() {
                    @Override
                    public void onResponse(Call<String> call, Response<String> response) {
                        StatusResponse resp = BaseResponse.decode(CoderUser.get(context), response.body(), StatusResponse.class);
                        if (listener != null) {
                            if (resp != null) {
                                Log.e("Ban", resp.getStatus());
                                listener.onSetInvisible(resp.getStatus().equals(Status.STATUS_DONE));
                            } else {
                                listener.onSetInvisible(false);
                            }
                        }
                    }

                    @Override
                    public void onFailure(Call<String> call, Throwable t) {
                        if (listener != null) {
                            listener.onSetInvisible(false);
                        }
                    }
                });
            }
        });
    }

    public void sendNotice(String user, String message) {
        final NoticeRequest request = new NoticeRequest();
        request.setMessage(message);
        request.setUserId(user);
        tokenHelper.getToken(new TokenHelper.OnGetTokenListener() {
            @Override
            public void onGetToken(String token) {
                Call<String> call = Network.getChatNetworkInterface().sendNotice(token, BaseRequest.code(request, CoderUser.get(context)));
                call.enqueue(new Callback<String>() {
                    @Override
                    public void onResponse(Call<String> call, Response<String> response) {
                        StatusResponse resp = BaseResponse.decode(CoderUser.get(context), response.body(), StatusResponse.class);
                        if (listener != null) {
                            if (resp != null) {
                                Log.e("Ban", resp.getStatus());
                                listener.onSendNotice(resp.getStatus().equals(Status.STATUS_DONE));
                            } else {
                                listener.onSendNotice(false);
                            }
                        }
                    }

                    @Override
                    public void onFailure(Call<String> call, Throwable t) {
                        if (listener != null) {
                            listener.onSendNotice(false);
                        }
                    }
                });
            }
        });
    }



    public void unKickUser(String user, String room) {
        final KickRequest request = new KickRequest();
        request.setRoom(room);
        request.setKicked(user);
        tokenHelper.getToken(new TokenHelper.OnGetTokenListener() {
            @Override
            public void onGetToken(String token) {
                Call<String> call = Network.getChatNetworkInterface().unKickUser(token, BaseRequest.code(request, CoderUser.get(context)));
                call.enqueue(new Callback<String>() {
                    @Override
                    public void onResponse(Call<String> call, Response<String> response) {
                        StatusResponse resp = BaseResponse.decode(CoderUser.get(context), response.body(), StatusResponse.class);
                        if (listener != null) {
                            if (resp != null) {
                                listener.onUnKick(resp.getStatus().equals(Status.STATUS_DONE));
                            } else {
                                listener.onUnKick(false);
                            }
                        }
                    }

                    @Override
                    public void onFailure(Call<String> call, Throwable t) {
                        if (listener != null) {
                            listener.onUnKick(false);
                        }
                    }
                });
            }
        });
    }

    public void getKickedUsers(String room) {
        final RoomRequest request = new RoomRequest();
        request.setRoom(room);
        tokenHelper.getToken(new TokenHelper.OnGetTokenListener() {
            @Override
            public void onGetToken(String token) {
                Call<String> call = Network.getChatNetworkInterface().getKickedUsers(token, BaseRequest.code(request, CoderUser.get(context)));
                call.enqueue(new Callback<String>() {
                    @Override
                    public void onResponse(Call<String> call, Response<String> response) {
                        UsersResponse resp = UsersResponse.decode(CoderUser.get(context), response.body(), UsersResponse.class);
                        if (listener != null) {
                            if (resp != null && resp.getStatus().equals(Status.STATUS_DONE)) {
                                listener.onGetKicked(resp.getUsers());
                            } else {
                                listener.onGetKicked(new ArrayList<UserResponse>());
                            }
                        }
                    }

                    @Override
                    public void onFailure(Call<String> call, Throwable t) {
                        if (listener != null) {
                            listener.onGetKicked(new ArrayList<UserResponse>());
                        }
                    }
                });
            }
        });
    }

    public void leaveRoom(String room) {
        final RoomRequest request = new RoomRequest();
        request.setRoom(room);
        tokenHelper.getToken(new TokenHelper.OnGetTokenListener() {
            @Override
            public void onGetToken(String token) {
                Call<String> call = Network.getChatNetworkInterface().leaveRoom(token, BaseRequest.code(request, CoderUser.get(context)));
                if (call != null) {
                    call.enqueue(new Callback<String>() {
                        @Override
                        public void onResponse(Call<String> call, Response<String> response) {

                        }

                        @Override
                        public void onFailure(Call<String> call, Throwable t) {

                        }
                    });
                } else {

                }
            }
        });
    }


    public void loadMessages(Date lastDate, int findType) {
        final MessagesRequest request = new MessagesRequest();
        request.setCount(20);
        request.setFindType(findType);
        request.setLastDate(lastDate!=null?lastDate.getTime():-1);
        request.setPlace(place);
        tokenHelper.getToken(new TokenHelper.OnGetTokenListener() {
            @Override
            public void onGetToken(String token) {
                Call<String> call = null;
                if (type == MessageType.TYPE_CHAT) {
                    call = Network.getChatNetworkInterface().getMessages(token, BaseRequest.code(request, CoderUser.get(context)));
                } else if (type == MessageType.TYPE_PM) {
                    call = Network.getChatNetworkInterface().getPMMessages(token, BaseRequest.code(request, CoderUser.get(context)));
                }
                if (call != null) {
                    call.enqueue(new Callback<String>() {
                        @Override
                        public void onResponse(Call<String> call, Response<String> response) {
                            try{
                            MessagesResponse resp = BaseResponse.decode(CoderUser.get(context), response.body(), MessagesResponse.class);

                            if (resp != null) {
                                if (listener != null) {
                                    boolean done = resp.getStatus().equals(Status.STATUS_DONE);
                                    listener.onGetMessages(done, resp.getStatus(), done ? resp.getMessages() : new ArrayList<MessageResponse>());
                                }
                            } else {
                                listener.onGetMessages(false, Status.STATUS_FAIL, new ArrayList<MessageResponse>());
                            }
                            } catch (Exception e) {
                                Log.e("GLOBAL ERROR!!!", "MSG:"+e.getMessage(), e);
                                e.printStackTrace();

                            }
                        }

                        @Override
                        public void onFailure(Call<String> call, Throwable t) {
                            if (listener != null) {
                                listener.onGetMessages(false, Status.STATUS_FAIL, new ArrayList<MessageResponse>());
                            }
                        }
                    });
                } else {
                    listener.onGetMessages(false, Status.STATUS_FAIL, new ArrayList<MessageResponse>());
                }
            }
        });
    }

    public void uploadAttachment(final String id, final File file, final OnAttachmentUploadListener listener) {
        RequestBody requestFile = RequestBody.create(MediaType.parse(""), file);
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
                Call<String> call = Network.getChatNetworkInterface().uploadAttachment(rbTk, rbId, body);
                call.enqueue(new Callback<String>() {
                    @Override
                    public void onResponse(Call<String> call, Response<String> response) {
                        if (listener != null) {
                            StatusResponse resp = BaseResponse.decode(CoderUser.get(context), response.body(), StatusResponse.class);
                            if (resp != null) {
                                listener.onAttachmentUpload(response != null && response.body() != null && resp.getStatus().equals(Status.STATUS_DONE));
                            } else {
                                listener.onAttachmentUpload(false);
                            }
                        }
                    }

                    @Override
                    public void onFailure(Call<String> call, Throwable t) {
                        if (listener != null) {
                            listener.onAttachmentUpload(false);
                        }
                    }
                });
            }
        });
    }


    public void addAttachment(String name, final OnAddAttachListener listener) {
        final CreateAttachmentRequest request = new CreateAttachmentRequest();
        request.setFileName(name);
        tokenHelper.getToken(new TokenHelper.OnGetTokenListener() {
            @Override
            public void onGetToken(String token) {
                Call<String> call = Network.getChatNetworkInterface().addAttachment(token, BaseRequest.code(request, CoderUser.get(context)));
                call.enqueue(new Callback<String>() {
                    @Override
                    public void onResponse(Call<String> call, Response<String> response) {
                        if (listener != null) {

                            if (response != null && response.body() != null) {
                                AttachmentResponse resp = BaseResponse.decode(CoderUser.get(context), response.body(), AttachmentResponse.class);
                                if (resp != null && resp.getStatus().equals(Status.STATUS_DONE)) {
                                    listener.onAddAttach(true, resp);
                                } else {
                                    listener.onAddAttach(false, null);
                                }
                            } else {
                                listener.onAddAttach(false, null);
                            }
                        }
                    }

                    @Override
                    public void onFailure(Call<String> call, Throwable t) {
                        if (listener != null) {
                            listener.onAddAttach(false, null);
                        }
                    }
                });
            }
        });


    }

    public void loadUsers(String room) {
        final GetRoomRequest request = new GetRoomRequest();
        request.setRoom(room);
        new TokenHelper(context).getToken(new TokenHelper.OnGetTokenListener() {
            @Override
            public void onGetToken(String token) {
                Call<String> call = Network.getChatNetworkInterface().getRoomUsers(token, BaseRequest.code(request, CoderUser.get(context)));
                call.enqueue(new Callback<String>() {
                    @Override
                    public void onResponse(Call<String> call, Response<String> response) {
                        if (listener != null) {
                            if (response != null && response.code() == 200) {
                                UsersResponse resp = BaseResponse.decode(CoderUser.get(context), response.body(), UsersResponse.class);
                                if (resp != null) {
                                    listener.onUsersLoad(resp.getStatus().equals(Status.STATUS_DONE), resp.getUsers());
                                } else {
                                    listener.onUsersLoad(false, new ArrayList<UserResponse>());
                                }


                            } else {
                                listener.onUsersLoad(false, new ArrayList<UserResponse>());
                            }
                        }
                    }

                    @Override
                    public void onFailure(Call<String> call, Throwable t) {
                        if (listener != null) {
                            listener.onUsersLoad(false, new ArrayList<UserResponse>());
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


    public interface OnChatActionListener {
        void onSendMessage(boolean result, MessageRequest request, StatusResponse resp);

        void onChatEdit(boolean result);

        void onChatDelete(boolean result);

        void onDeleteMessage(boolean result, RemoveMessageRequest request);

        void onGetMessages(boolean result, String status, List<MessageResponse> messages);

        void onUsersLoad(boolean result, List<UserResponse> users);

        void onKick(boolean done);

        void onUnKick(boolean done);

        void onGetKicked(List<UserResponse> users);

        void onBanUser(boolean done);

        void onSetInvisible(boolean done);

        void onSendNotice(boolean done);

        void onStartDialog(boolean status, PMChatResponse response);
    }

    public interface OnAddAttachListener {
        void onAddAttach(boolean result, AttachmentResponse response);
    }

    public interface OnAttachmentUploadListener {
        void onAttachmentUpload(boolean result);
    }
}
