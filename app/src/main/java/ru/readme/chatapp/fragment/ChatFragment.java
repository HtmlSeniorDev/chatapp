package ru.readme.chatapp.fragment;

import android.Manifest;
import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.os.Vibrator;
import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.FragmentTransaction;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.SimpleItemAnimator;
import android.text.Editable;
import android.text.Selection;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.ArrayAdapter;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.Toast;

import java.io.File;
import java.io.FileOutputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.regex.Pattern;

import io.github.rockerhieu.emojicon.EmojiconEditText;
import io.github.rockerhieu.emojicon.EmojiconGridFragment;
import io.github.rockerhieu.emojicon.EmojiconsFragment;
import io.github.rockerhieu.emojicon.emoji.Emojicon;
import ru.readme.chatapp.activity.App;
import ru.readme.chatapp.activity.MainActivity;
import ru.readme.chatapp.activity.PMChatsActivity;
import ru.readme.chatapp.activity.ProfileActivity;
import ru.readme.chatapp.R;
import ru.readme.chatapp.adapter.AddAttachmentsAdapter;
import ru.readme.chatapp.adapter.ChatAdapter;
import ru.readme.chatapp.adapter.UsersAdapter;
import ru.readme.chatapp.dialog.AskDialog;
import ru.readme.chatapp.dialog.BanDialog;
import ru.readme.chatapp.dialog.EditRoomDialog;
import ru.readme.chatapp.dialog.InfoDialog;
import ru.readme.chatapp.dialog.NoticeDialog;
import ru.readme.chatapp.dialog.SelectFileDialog;
import ru.readme.chatapp.dialog.WaitDialog;
import ru.readme.chatapp.object.MessageType;
import ru.readme.chatapp.object.UserType;
import ru.readme.chatapp.object.requests.MessageRequest;
import ru.readme.chatapp.object.requests.MessagesRequest;
import ru.readme.chatapp.object.requests.RemoveMessageRequest;
import ru.readme.chatapp.object.responses.AttachmentResponse;
import ru.readme.chatapp.object.responses.MessageResponse;
import ru.readme.chatapp.object.responses.MessagesResponse;
import ru.readme.chatapp.object.responses.PMChatResponse;
import ru.readme.chatapp.object.responses.RoomResponse;
import ru.readme.chatapp.object.responses.Status;
import ru.readme.chatapp.object.responses.StatusResponse;
import ru.readme.chatapp.object.responses.UserResponse;
import ru.readme.chatapp.object.responses.UsersResponse;
import ru.readme.chatapp.util.ImagePicker;
import ru.readme.chatapp.util.SettingsHelper;
import ru.readme.chatapp.helper.ChatHelper;

import static android.app.Activity.RESULT_OK;
import static ru.readme.chatapp.activity.MainActivity.TAG_CHAT;

public class ChatFragment extends MyBaseFragment implements SelectFileDialog.OnFileSelectListener,
        ChatHelper.OnChatActionListener, View.OnClickListener, ChatAdapter.OnChatMessageClickListener,
        EmojiconGridFragment.OnEmojiconClickedListener, EmojiconsFragment.OnEmojiconBackspaceClickedListener,
        AddAttachmentsAdapter.OnAttachDeleteListener, UsersAdapter.OnUserClickListener {

    public static String curRoom = null;
    private EmojiconEditText edMessage;
    private ImageButton ibSmile, ibSend;
    private RecyclerView rvUsers;
    private RecyclerView rvAttachments;
    private LinearLayout llAttachments;
    private AddAttachmentsAdapter addAttachmentsAdapter;
    private boolean emoji = false;
    private FrameLayout frEmoji;
    private RoomResponse room;
    private SettingsHelper settingsHelper;
    private InputMethodManager im;
    private WaitDialog waitDialog;
    private UsersAdapter usersAdapter;
    private RecyclerView rvList;
    private boolean usersVisible = false;
    private View vUsersShandow;
    private Vibrator vibrator;
    private ChatHelper helper;
    private RoomResponse chacheRoom;
    private PMChatResponse chat;
    private boolean loadbefore = false;
    private boolean hasbefore = true;
    private boolean vibrate = false;
    private EmojiconsFragment emojes;
    private boolean needExit = true;
    private boolean noLoad = false;
    private int testSend = 0;
    private ChatAdapter adapter;
    private boolean load = false;
    private boolean usersLoad = false;
    private LinearLayoutManager lmManager;
    private Date firsInitDate = new Date();
    private int type = -1;

    private Handler handler = new Handler(new Handler.Callback() {
        @Override
        public boolean handleMessage(Message message) {
            if (message.what == 1) {//загрузка сообщений в комнате каждые 3 секунды
                if (!load && !noLoad) {
                    load = true;
                    if (adapter.getItemCount() == 0) {
                        helper.loadMessages(null, MessagesRequest.FIND_TYPE_BEFORE);
                        helper.loadMessages(null, MessagesRequest.FIND_TYPE_AFTER);
                    } else if (loadbefore) {
                        if (adapter.getItemCount() > 0) {
                            helper.loadMessages(adapter.getFirstElement().getCreatedAt(), MessagesRequest.FIND_TYPE_BEFORE);
                        }
                    } else {
                        if (adapter.getLastElement().getCreatedAt().after(firsInitDate)) {
                            helper.loadMessages(adapter.getLastElement().getCreatedAt(), MessagesRequest.FIND_TYPE_AFTER);
                        } else {
                            helper.loadMessages(firsInitDate, MessagesRequest.FIND_TYPE_AFTER);
                        }

                    }
                }
                if (getActivity() != null) {
                    handler.sendEmptyMessageDelayed(1, 3000);
                }
            } else if (message.what == 2) {// загрузка пользователей в комнате каждые 5 секунды
                if (!usersLoad && room != null && !noLoad) {
                    usersLoad = true;
                    if (room != null) {
                        helper.loadUsers(room.getId());
                    }
                }
                if (getActivity() != null) {
                    handler.sendEmptyMessageDelayed(2, 5000);
                }
            }
            return true;
        }
    });


    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        final View v = inflater.inflate(R.layout.chat, null);
        waitDialog = new WaitDialog(getActivity());
        settingsHelper = new SettingsHelper(getActivity());
        setHasOptionsMenu(true);
        helper = new ChatHelper(getActivity(), this);
        im = (InputMethodManager) getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
        edMessage = (EmojiconEditText) v.findViewById(R.id.ed_chat_message);
        ibSend = (ImageButton) v.findViewById(R.id.ib_chat_send);
        ibSmile = (ImageButton) v.findViewById(R.id.ib_chat_smiles);
        frEmoji = (FrameLayout) v.findViewById(R.id.emojicons);
        rvList = (RecyclerView) v.findViewById(R.id.rv_chat_list);
        rvUsers = (RecyclerView) v.findViewById(R.id.rv_chat_users);
        vUsersShandow = v.findViewById(R.id.v_chat_users_shandow);
        vibrator = (Vibrator) getActivity().getSystemService(Context.VIBRATOR_SERVICE);
        llAttachments = (LinearLayout) v.findViewById(R.id.ll_chat_attachments);
        rvAttachments = (RecyclerView) v.findViewById(R.id.rv_chat_attachments);
        addAttachmentsAdapter = new AddAttachmentsAdapter(getActivity(), this);
        LinearLayoutManager layoutManager = new LinearLayoutManager(getActivity(), LinearLayoutManager.HORIZONTAL, false);
        rvAttachments.setLayoutManager(layoutManager);
        rvAttachments.setAdapter(addAttachmentsAdapter);
        float textUserSize = (float) settingsHelper.getChatersSize() * getResources().getDimension(R.dimen.one_sp);
        usersAdapter = new UsersAdapter(getActivity(), this, true, textUserSize);
        LinearLayoutManager lll = new LinearLayoutManager(getActivity(), LinearLayoutManager.VERTICAL, false);
        rvUsers.setLayoutManager(lll);
        rvUsers.setAdapter(usersAdapter);
        ibSmile.setOnClickListener(this);
        ibSend.setOnClickListener(this);
        edMessage.setOnClickListener(this);
        adapter = new ChatAdapter(this, this, type);
        lmManager = new LinearLayoutManager(getActivity());
        rvList.setLayoutManager(lmManager);
        rvList.setAdapter(adapter);
        //edMessage.addTextChangedListener(enterRemover);
        RecyclerView.ItemAnimator animator = rvList.getItemAnimator();
        if (animator instanceof SimpleItemAnimator) {
            ((SimpleItemAnimator) animator).setSupportsChangeAnimations(false);
        }
        setMainList();
        initState(savedInstanceState);
        if (room != null) {
            curRoom = room.getId();
            helper.setPlace(room.getId());
            ((AppCompatActivity) getActivity()).getSupportActionBar().setTitle(room.getName());

        } else if (chat != null) {
            helper.setPlace(chat.getId());
            String name = "";
            String meId = settingsHelper.getUserId();
            for (UserResponse userResponse : chat.getUsers()) {
                if (!meId.equals(userResponse.getId())) {
                    name += userResponse.getNic() + ", ";
                }
            }
            if (name.length() > 2) {
                name = name.substring(0, name.length() - 2);
            }
            ((AppCompatActivity) getActivity()).getSupportActionBar().setTitle(name);
        } else {
            InfoDialog.show(R.string.chat, R.string.chat_room_error, getActivity());
        }


        if (room != null || chat != null) {
            helper.setType(type);
            loadFirst();
            handler.sendEmptyMessageDelayed(1, 1000);
            if(type == MessageType.TYPE_PM) {
            rvList.addOnScrollListener(new RecyclerView.OnScrollListener() {
                @Override
                public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                    if (dy < 0) {
                        int pastVisiblesItems = lmManager.findFirstVisibleItemPosition();
                        if (pastVisiblesItems < 1 && hasbefore) {
                            loadbefore = true;
                            if (!load) {
                                Log.e("App", "load before");
                                if (adapter.getItemCount() > 0) {
                                    load = true;
                                    Log.e("App", "load before start");
                                    helper.loadMessages(adapter.getFirstElement().getCreatedAt(), MessagesRequest.FIND_TYPE_BEFORE);
                                }
                            }
                        }
                    }

                }
            });
            }
        } else {
            Log.e("App", "chat is null");
        }

        if (room != null) {
            settingsHelper.setLastChat(room);
            handler.sendEmptyMessageDelayed(2, 1000);
        }

        return v;
    }

    private boolean checkMessage(String message) {
        if (message.indexOf("http://") > -1) {
            return false;
        }
        if (message.indexOf("https://") > -1) {
            return false;
        }

        int smiles = 0;
        char[] chars = message.toCharArray();
        for (char ch : chars) {
            Log.e("Code", ch + " " + (int) ch);
            if (ch > 3000) {
                smiles++;
            }
            if (smiles > 10) {
                return false;
            }
        }
        return true;
    }

    @Override
    public void onPause() {
       // load = true;
       // noLoad = true;
        load = false;
        noLoad = false;
        super.onPause();
    }


    @Override
    public void onResume() {
        load = false;
        noLoad = false;
        super.onResume();
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        needExit = true;
        emojes = EmojiconsFragment.newInstance(false);
        getChildFragmentManager()
                .beginTransaction()
                .replace(R.id.emojicons, emojes, "emoj")
                .commit();
        edMessage.requestFocus();
        if (type == MessageType.TYPE_CHAT && settingsHelper.getUserType() == UserType.STRING_INVISIBLE) {
            ibSend.setEnabled(false);
            ibSend.setClickable(false);
        } else {
            ibSend.setEnabled(true);
            ibSend.setClickable(true);
        }

    }

    private void setMainList() {
        rvList.addOnLayoutChangeListener(new View.OnLayoutChangeListener() {
            @Override
            public void onLayoutChange(View v,
                                       int left, int top, int right, final int bottom,
                                       int oldLeft, int oldTop, int oldRight, final int oldBottom) {

                if (bottom < oldBottom) {
                    rvList.postDelayed(new Runnable() {
                        @Override
                        public void run() {

                            rvList.scrollBy(0, oldBottom - bottom);
                        }
                    }, 10);
                }
            }
        });
    }

    private void initState(Bundle savedInstanceState) {
        if (savedInstanceState != null) {
            edMessage.setText(savedInstanceState.getString("message", ""));
            room = (RoomResponse) savedInstanceState.getSerializable("room");
            chat = (PMChatResponse) savedInstanceState.getSerializable("chat");
            if (room != null) {
                type = MessageType.TYPE_CHAT;
                adapter.setType(type);
                UsersResponse users = (UsersResponse) savedInstanceState.getSerializable("users");
                usersVisible = savedInstanceState.getBoolean("showUsers", false);
                usersAdapter.setElements(users.getUsers());
                showUsersMenu(usersVisible);
            } else if (chat != null) {
                type = MessageType.TYPE_PM;
                adapter.setType(type);
            }
            MessagesResponse msr = (MessagesResponse) savedInstanceState.getSerializable("messages");
            for (MessageResponse mr : msr.getMessages()) {
                adapter.addElement(mr);
            }
        } else if (getArguments() != null) {
            if (getArguments().containsKey("room")) {
                room = (RoomResponse) getArguments().getSerializable("room");
                type = MessageType.TYPE_CHAT;
                adapter.setType(type);
            } else if (getArguments().containsKey("chat")) {
                chat = (PMChatResponse) getArguments().getSerializable("chat");
                type = MessageType.TYPE_PM;
                adapter.setType(type);
            }
        }
    }

    private void loadFirst() {
        if (!load) {
            load = true;
            helper.loadMessages(null, MessagesRequest.FIND_TYPE_BEFORE);
            handler.sendEmptyMessageDelayed(1, 3000);
        }
        if (!usersLoad) {
            if (room != null) {
                usersLoad = true;
                helper.loadUsers(room.getId());
            }
        }
    }

    private void hideKeyboard() {
        im.hideSoftInputFromWindow(getActivity().getCurrentFocus().getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);
        im.hideSoftInputFromWindow(edMessage.getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);
        im.hideSoftInputFromWindow(getActivity().getCurrentFocus().getWindowToken(), InputMethodManager.HIDE_IMPLICIT_ONLY);
        im.hideSoftInputFromWindow(edMessage.getWindowToken(), InputMethodManager.HIDE_IMPLICIT_ONLY);
    }

    private void showKeyboard() {
        hideEmoji();
        im.showSoftInput(edMessage, InputMethodManager.SHOW_IMPLICIT);
    }

    private void setEmojiconFragment() {
        emoji = true;
        hideKeyboard();
        getActivity().getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);
        ViewGroup.LayoutParams lp = frEmoji.getLayoutParams();
        lp.height = getResources().getDimensionPixelSize(R.dimen.emojy_height);
        frEmoji.setLayoutParams(lp);

    }

    private void hideEmoji() {
        emoji = false;
        im.hideSoftInputFromWindow(edMessage.getWindowToken(), 0);
        getActivity().getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_UNSPECIFIED);
        ViewGroup.LayoutParams lp = frEmoji.getLayoutParams();
        lp.height = 0;
        frEmoji.setLayoutParams(lp);
    }

    @Override
    public void onEmojiconClicked(Emojicon emojicon) {
        EmojiconsFragment.input(edMessage, emojicon);
    }

      @Override
    public void onEmojiconBackspaceClicked(View v) {
        EmojiconsFragment.backspace(edMessage);
    }


    @Override
    public void onDestroy() {
        super.onDestroy();
        if (type == MessageType.TYPE_CHAT && needExit) {
            curRoom = null;
            App.leave(room.getId());
        }
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);

        outState.putString("message", edMessage.getText().toString());
        outState.putSerializable("room", room);
        MessagesResponse msr = new MessagesResponse();
        msr.setMessages(adapter.getElements());
        outState.putSerializable("messages", msr);
        if (room != null) {
            UsersResponse users = new UsersResponse();
            users.setUsers(usersAdapter.getElements());
            outState.putSerializable("users", users);
            outState.putBoolean("showUsers", usersVisible);
        }
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        int userType = settingsHelper.getUserType();
        if (userType == UserType.TYPE_ADMIN && type == MessageType.TYPE_CHAT) {
            inflater.inflate(R.menu.chat_admin, menu);
        } else if (userType == UserType.TYPE_MODER && type == MessageType.TYPE_CHAT) {
            inflater.inflate(R.menu.chat_moder, menu);
        } else {
            if (type == MessageType.TYPE_CHAT) {
                inflater.inflate(R.menu.chat_menu, menu);
            } else if (type == MessageType.TYPE_PM) {
                inflater.inflate(R.menu.chat, menu);
            }
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        if (item.getItemId() == R.id.menu_chat_delete_chat) {
            AskDialog.show(getActivity(), getString(R.string.deleted_room), getString(R.string.ask_delete_room)
                    , new AskDialog.OnAskDialogClickListener() {
                        @Override
                        public void onAskClick(boolean acept) {
                            if (acept) {
                                waitDialog.show();
                                helper.deleteChat(room.getId());
                            }
                        }
                    });
        } else if (item.getItemId() == R.id.menu_chat_edit_chat) {
            EditRoomDialog.show(getActivity(), room, new EditRoomDialog.OnEditRoomClickListener() {
                @Override
                public void onEditCategoryClick(RoomResponse roomResponse) {
                    waitDialog.show();
                    chacheRoom = roomResponse;
                    helper.editChat(roomResponse.getId(), roomResponse.getName(), roomResponse.getMask());
                }
            });
        } else if (item.getItemId() == R.id.menu_chat_people) {
            usersVisible = !usersVisible;
            showUsersMenu(usersVisible);

        } else if (item.getItemId() == R.id.menu_chat_kicked) {
            showKickedUsers();
        } else if (item.getItemId() == R.id.menu_chat_pm) {
            showPM();
        } else if (item.getItemId() == R.id.menu_chat_add_attachment) {
            showFileChoiser();

        }
        return super.onOptionsItemSelected(item);
    }

    private void showUsersMenu(boolean show) {
        if (show) {
            vUsersShandow.setVisibility(View.VISIBLE);
            rvUsers.setAlpha(0f);
            rvUsers.getLayoutParams().width = getResources().getDimensionPixelOffset(R.dimen.users_list_width);
            adapter.notifyDataSetChanged();
            rvUsers.animate()
                    .alpha(1.0f)
                    .setDuration(150)
                    .setListener(new AnimatorListenerAdapter() {
                        @Override
                        public void onAnimationEnd(Animator animation) {
                            super.onAnimationEnd(animation);
                            rvUsers.setAlpha(1f);
                        }
                    });
            vUsersShandow.setAlpha(0f);
            vUsersShandow.animate()
                    .alpha(1.0f)
                    .setDuration(250)
                    .setListener(new AnimatorListenerAdapter() {
                        @Override
                        public void onAnimationEnd(Animator animation) {
                            super.onAnimationEnd(animation);
                            vUsersShandow.setAlpha(1f);
                        }
                    });

        } else {
            rvUsers.animate()
                    .alpha(0.0f)
                    .setDuration(250)
                    .setListener(new AnimatorListenerAdapter() {
                        @Override
                        public void onAnimationEnd(Animator animation) {
                            super.onAnimationEnd(animation);
                            rvUsers.getLayoutParams().width = 0;
                            rvUsers.setAlpha(0f);
                            adapter.notifyDataSetChanged();
                        }
                    });
            vUsersShandow.animate()
                    .alpha(1.0f)
                    .setDuration(150)
                    .setListener(new AnimatorListenerAdapter() {
                        @Override
                        public void onAnimationEnd(Animator animation) {
                            super.onAnimationEnd(animation);
                            vUsersShandow.setAlpha(0f);
                            vUsersShandow.setVisibility(View.VISIBLE);
                        }
                    });
        }
    }

    @Override
    public void onClick(View view) {
        if (view.getId() == R.id.ib_chat_smiles) {
            if (!emoji) {
                ibSmile.setImageResource(R.drawable.ic_keyboard_light_blue_500_24dp);
                hideKeyboard();
                setEmojiconFragment();
            } else {
                ibSmile.setImageResource(R.drawable.ic_insert_emoticon_light_blue_500_24dp);
                hideEmoji();
                showKeyboard();

            }
        } else if (view.getId() == R.id.ed_chat_message) {
            if (emoji) {
                hideKeyboard();
            }
        } else if (view.getId() == R.id.ib_chat_send) {
            send();
        }
    }


    public void showFileChoiser() {
        if (ActivityCompat.checkSelfPermission(getActivity(), Manifest.permission.WRITE_EXTERNAL_STORAGE)
                != PackageManager.PERMISSION_GRANTED) {
            requestStoragePermission();
        } else {
            SelectFileDialog.show(this, this);
        }
    }

    private void requestStoragePermission() {
        ActivityCompat.requestPermissions(getActivity(), new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, 1);
    }


    public void send() {
        ViewGroup.LayoutParams lp = llAttachments.getLayoutParams();
        lp.height = 0;
        llAttachments.setLayoutParams(lp);
        String message = edMessage.getText().toString();
        message = message.replaceAll(Pattern.quote("\n"), " ");
        if (message.length() > 0 || addAttachmentsAdapter.getItemCount() > 0) {
            if (checkMessage(message)) {
                if (message.length() == 0) {
                    message = getString(R.string.attachments);
                }
                List<String> atts = new ArrayList<>();
                for (AttachmentResponse ar : addAttachmentsAdapter.getElements()) {
                    atts.add(ar.getId());
                }
                helper.sendMessage(message, atts);
                edMessage.setText("");
                addAttachmentsAdapter.clear();
                boolean kbh = settingsHelper.getKBHide();
                if(kbh) {
                    hideKeyboard();
                    hideEmoji();
                }
                try {
                    File subdir = new File(getActivity().getFilesDir(), "tmp");
                    subdir.delete();
                } catch (Exception e) {
                }
            } else {
                InfoDialog.show(R.string.send_message, R.string.send_message_error_smiles_or_link, getActivity());
            }
        }
    }

    @Override
    public void onSendMessage(boolean result, MessageRequest request, StatusResponse response) {
        if (!result) {
            if (response != null && response.getStatus().equals(Status.STATUS_LIMIT)) {
                InfoDialog.show(getString(R.string.send_message), getString(R.string.send_message_error_limit) + " \"" + request.getMessage() + "\"", getActivity());
                if (request != null) {
                    edMessage.setText(request.getMessage());
                }
            } else if (response != null && response.getStatus().equals(Status.STATUS_KICKED)) {
                showMeKicked();
            } else {
                try {
                    InfoDialog.show(getString(R.string.send_message), getString(R.string.send_message_error) + " \"" + request.getMessage() + "\"", getActivity());

                    if (request != null) {
                        edMessage.setText(request.getMessage());
                    }
                } catch (Exception e) {
                }
            }
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String permissions[], int[] grantResults) {
        switch (requestCode) {
            case 1: {
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    showFileChoiser();

                } else {
                    InfoDialog.show(R.string.select_file, R.string.no_permission, getActivity());
                }
                return;
            }
        }
    }


    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == SelectFileDialog.CODE_GALLERY) {
            if (resultCode == RESULT_OK) {
                Bitmap bitmap = ImagePicker.getImageFromResult(getActivity(), resultCode, data);
                File subdir = new File(getActivity().getFilesDir(), "tmp");
                if (!subdir.exists()) {
                    subdir.mkdirs();
                }
                String name = System.currentTimeMillis() + ".jpeg";
                try {
                    File img = new File(getActivity().getFilesDir() + "/tmp/", name);
                    OutputStream os = new FileOutputStream(img);
                    bitmap.compress(Bitmap.CompressFormat.JPEG, 85, os);
                    os.flush();
                    os.close();
                    prepearAttachment(img.toString());
                } catch (Exception e) {
                    Log.e("App", "create photo", e);
                }
            }
        } else if (resultCode == 2) {
            getActivity().setResult(2);
            getActivity().finish();
        }
    }

    @Override
    public void onChatEdit(boolean result) {
        waitDialog.hide();
        if (result) {
            room = chacheRoom;
            ((AppCompatActivity) getActivity()).getSupportActionBar().setTitle(room.getName());
        } else {
            InfoDialog.show(R.string.edit_room, R.string.error_edit_room, getActivity());
            chacheRoom = null;
        }
    }

    @Override
    public void onChatDelete(boolean status) {
        waitDialog.hide();
        if (status) {
            getActivity().onBackPressed();
        } else {
            InfoDialog.show(R.string.deleted_room, R.string.error_delete_room, getActivity());
        }
    }

    @Override
    public void onDeleteMessage(boolean result, RemoveMessageRequest request) {

    }

    @Override
    public void onGetMessages(boolean result, String status, List<MessageResponse> messages) {
        if (status.equals(Status.STATUS_DONE)) {

            if (adapter.getItemCount() == 0 && chat != null) {
                if (getActivity() instanceof MainActivity) {
                    ((MainActivity) getActivity()).forceReloadProfile();
                }
            }

            if (testSend > 0) {
                testSend--;
                //   helper.sendTest(type, room.getId(), new ArrayList<String>());
            }
            int scrollto = -1;
            if (loadbefore) {
                loadbefore = false;
                if (result && messages.size() == 0) {
                    hasbefore = false;

                }
                scrollto = messages.size();
            }
            load = false;
            boolean toEnd = false;
            if (adapter.getItemCount() == 0) {
                toEnd = true;
            }
            int pastVisiblesItems = lmManager.findLastVisibleItemPosition();
            if (pastVisiblesItems >= adapter.getItemCount() - 1 && messages.size() > 0) {
                toEnd = true;
            }

            for (MessageResponse mr : messages) {
                adapter.addElement(mr);
            }
            if (toEnd) {
                rvList.scrollToPosition(adapter.getItemCount() - 1);
            }
            if (scrollto > 0) {
                rvList.scrollToPosition(scrollto + 2);
            }
            vibrate = true;
        } else if (status.equals(Status.STATUS_KICKED)) {
            showMeKicked();
        }
    }

    private void showMeKicked() {
        new AlertDialog.Builder(getActivity())
                .setTitle(R.string.app_name)
                .setMessage(R.string.you_kicked)
                .setCancelable(false)
                .setPositiveButton(R.string.ok, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        doBack();
                    }
                }).show();
    }

    @Override
    public void onUsersLoad(boolean result, List<UserResponse> users) {
        usersLoad = false;
        if (result) {
            usersAdapter.setElements(users);
        }
    }

    @Override
    public void onKick(boolean done) {
        if (getActivity() != null && done) {
            Toast.makeText(getActivity(), R.string.user_kicked, Toast.LENGTH_LONG).show();
        } else if (getActivity() != null) {
            Toast.makeText(getActivity(), R.string.user_kicked_error, Toast.LENGTH_LONG).show();
        }
    }

    @Override
    public void onUnKick(boolean done) {
        if (getActivity() != null && done) {
            Toast.makeText(getActivity(), R.string.user_un_kicked, Toast.LENGTH_LONG).show();
        } else if (getActivity() != null) {
            Toast.makeText(getActivity(), R.string.user_un_kicked_error, Toast.LENGTH_LONG).show();
        }
    }

    @Override
    public void onGetKicked(final List<UserResponse> users) {
        waitDialog.hide();
        final List<String> names = new ArrayList<>();
        for (UserResponse user : users) {
            names.add(user.getNic());
        }
        new AlertDialog.Builder(getActivity())
                .setTitle(R.string.kicked)
                .setAdapter(new ArrayAdapter<>(getActivity(), android.R.layout.simple_list_item_1, names),
                        new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, final int j) {
                                new AlertDialog.Builder(getActivity())
                                        .setTitle(names.get(j))
                                        .setMessage(R.string.unkick_ask)
                                        .setPositiveButton(R.string.yes, new DialogInterface.OnClickListener() {
                                            @Override
                                            public void onClick(DialogInterface dialogInterface, int i) {
                                                helper.unKickUser(users.get(j).getId(), room.getId());
                                            }
                                        })
                                        .setNegativeButton(R.string.cancel, null)
                                        .show();

                            }
                        })
                .show();
    }

    @Override
    public void onBanUser(boolean done) {
        if (getActivity() != null && done) {
            Toast.makeText(getActivity(), R.string.user_banned_done, Toast.LENGTH_LONG).show();
        } else if (getActivity() != null) {
            Toast.makeText(getActivity(), R.string.user_banned_error, Toast.LENGTH_LONG).show();
        }
    }

    @Override
    public void onSetInvisible(boolean done) {
        if (getActivity() != null && done) {
            Toast.makeText(getActivity(), R.string.user_set_invisible_done, Toast.LENGTH_LONG).show();
        } else if (getActivity() != null) {
            Toast.makeText(getActivity(), R.string.user_set_invisible_error, Toast.LENGTH_LONG).show();
        }
    }

    @Override
    public void onSendNotice(boolean done) {
        if (getActivity() != null && done) {
            Toast.makeText(getActivity(), R.string.user_send_notice_done, Toast.LENGTH_LONG).show();
        } else if (getActivity() != null) {
            Toast.makeText(getActivity(), R.string.user_send_notice_error, Toast.LENGTH_LONG).show();
        }
    }


    @Override
    public void onMessageClick(MessageResponse messageResponse) {

    }

    @Override
    public void onMessageUserLongClick(UserResponse user) {

    }

    @Override
    public void onMessageUserClick(final UserResponse user) {
       // if (settingsHelper.getUserType() == UserType.TYPE_ADMIN || settingsHelper.getUserType() == UserType.TYPE_MODER) {
            final List<Integer> actionsIds = new ArrayList<>();
            List<String> actions = new ArrayList<>();
            if(type == MessageType.TYPE_CHAT) {
                actions.add(getString(R.string.paste));
                actionsIds.add(1);
            }
            actions.add(getString(R.string.show_profile));
            actionsIds.add(2);

            if (settingsHelper.getUserType() == UserType.TYPE_ADMIN || settingsHelper.getUserType() == UserType.TYPE_MODER) {
                actionsIds.add(3);
                actions.add(getString(R.string.ban_user));
                actionsIds.add(4);
                actions.add(getString(R.string.set_invisible));
                actionsIds.add(5);
                actions.add(getString(R.string.send_notice_touser));
                if (type == MessageType.TYPE_CHAT && (settingsHelper.getUserType() == UserType.TYPE_ADMIN)) {
                    actions.add(getString(R.string.kick));
                    actionsIds.add(6);
                }

            }
            actionsIds.add(7);
             actions.add(getString(R.string.send_pm_msg));
            new AlertDialog.Builder(getActivity())
                    .setTitle(user.getNic())
                    .setAdapter(new ArrayAdapter<>(getActivity(), android.R.layout.simple_list_item_1, actions),
                            new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialogInterface, int i) {
                                    if (actionsIds.get(i) == 1) {
                                        if (user != null) {
                                            String text = edMessage.getText().toString();
                                            if (text.length() > 0) {
                                                text = text + " " + user.getNic() + ", ";
                                            } else {
                                                text = user.getNic() + ", ";
                                            }
                                            edMessage.setText(text);
                                            int position = edMessage.length();
                                            Editable etext = edMessage.getText();
                                            Selection.setSelection(etext, position);
                                        }
                                    } else if (actionsIds.get(i) == 2) {
                                        showUserProfile(user);

                                    } else if (actionsIds.get(i) == 3) {
                                        if (settingsHelper.getUserType() == UserType.TYPE_ADMIN || settingsHelper.getUserType() == UserType.TYPE_MODER) {
                                            BanDialog.show(user, getActivity(), new BanDialog.OnBanClickListener() {
                                                @Override
                                                public void onBanClick(UserResponse user, String description, long time) {
                                                    helper.banUser(user.getId(), description, time);
                                                }
                                            });
                                        }
                                    } else if (actionsIds.get(i) == 4) {
                                        {
                                            new AlertDialog.Builder(getActivity())
                                                    .setTitle("Невидимка")
                                                    .setMessage("Пользователь больше не сможет общаться в комнатах.\n Продолжить?")
                                                    .setPositiveButton(R.string.yes, new DialogInterface.OnClickListener() {
                                                        @Override
                                                        public void onClick(DialogInterface dialogInterface, int i) {
                                                            helper.setInvisibleUser(user.getId());
                                                        }
                                                    })
                                                    .setNegativeButton(R.string.cancel, null)
                                                    .show();
                                        }
                                } else if (actionsIds.get(i) == 6) {
                                    if (settingsHelper.getUserType() == UserType.TYPE_ADMIN || settingsHelper.getUserType() == UserType.TYPE_MODER) {
                                        new AlertDialog.Builder(getActivity())
                                                .setTitle("Кикнуть")
                                                .setMessage("Данная комната станет недоступна для пользователя. \nПродолжить?")
                                                .setPositiveButton(R.string.yes, new DialogInterface.OnClickListener() {
                                                    @Override
                                                    public void onClick(DialogInterface dialogInterface, int i) {
                                                        helper.kickUser(user.getId(), room.getId());
                                                    }
                                                })
                                                .setNegativeButton(R.string.cancel, null)
                                                .show();
                                    }
                                    }
                                    else if (actionsIds.get(i) == 7) {
                                          waitDialog.show();
                                            helper.startDialog(user.getId());
                                    }
                                    else if (actionsIds.get(i) == 5) {
                                        {
                                            NoticeDialog.show(user, ChatFragment.this.getActivity(), new NoticeDialog.OnNoticeClickListener() {
                                                @Override
                                                public void onBanClick(UserResponse u, String description) {
                                                    helper.sendNotice(u.getId(), description);
                                                }
                                            });
                                        }
                                    }
                                }
                            })
                    .show();
    }


    @Override
    public void onStartDialog(boolean status, PMChatResponse response) {
        waitDialog.hide();
        if (status) {
            Bundle args = new Bundle();
            args.putSerializable("chat", response);
            ChatFragment fragment = new ChatFragment();
            fragment.setArguments(args);
            FragmentTransaction fragmentTransaction = getFragmentManager().beginTransaction();
            fragmentTransaction.setCustomAnimations(android.R.anim.fade_in,
                    android.R.anim.fade_out);
            if (getActivity() instanceof MainActivity) {
                fragmentTransaction
                        .replace(R.id.ll_base_main_layout_content, fragment, TAG_CHAT)
                        .addToBackStack(TAG_CHAT)
                        .commitAllowingStateLoss();
            } else {
                fragmentTransaction
                        .replace(R.id.ll_base_layout_content, fragment, TAG_CHAT)
                        .addToBackStack(TAG_CHAT)
                        .commitAllowingStateLoss();
            }

        }
    }

    private void showKickedUsers() {
        waitDialog.show();
        helper.getKickedUsers(room.getId());
    }

    private void showUserProfile(UserResponse user) {
        needExit = false;
        Intent intent = new Intent(getActivity(), ProfileActivity.class);
        intent.putExtra("id", user.getId());
        startActivityForResult(intent, 1);
    }

    private void showPM() {
        needExit = false;
        Intent intent = new Intent(getActivity(), PMChatsActivity.class);
        startActivityForResult(intent, 1);
    }

    @Override
    public void onAddNew(MessageResponse ms) {
        if (ms.getUser() == null || !ms.getUser().getId().equals(settingsHelper.getUserId())) {
            if (vibrate) {
                //  vibrator.vibrate(200);
            }

        }
    }

    @Override
    public void doBack() {
        if (emoji) {
            hideEmoji();
        } else {
            super.doBack();
        }
    }


    @Override
    public void onFileSelect(String path) {
        prepearAttachment(path);
    }

    private void prepearAttachment(final String path) {
        waitDialog.show();
        String name = path;
        if (path.lastIndexOf("/") > -1) {
            name = path.substring(path.lastIndexOf("/"));
        }
        helper.addAttachment(name, new ChatHelper.OnAddAttachListener() {
            @Override
            public void onAddAttach(boolean result, AttachmentResponse response) {
                waitDialog.hide();
                if (result) {
                    response.setFileName(path);
                    uploadAttachment(response);
                } else {
                    InfoDialog.show(R.string.add_attachment, R.string.add_attachment_error, getActivity());
                }
            }
        });
    }

    private void uploadAttachment(final AttachmentResponse attachmentResponse) {
        waitDialog.show();
        File f = new File(attachmentResponse.getFileName());
        Log.e("App", "filename=" + f.getAbsolutePath());
        helper.uploadAttachment(attachmentResponse.getId(), f, new ChatHelper.OnAttachmentUploadListener() {
            @Override
            public void onAttachmentUpload(boolean result) {
                waitDialog.hide();
                if (result) {
                    ViewGroup.LayoutParams lp = llAttachments.getLayoutParams();
                    lp.height = getResources().getDimensionPixelSize(R.dimen.att_height);
                    llAttachments.setLayoutParams(lp);
                    addAttachmentsAdapter.add(attachmentResponse);
                } else {
                    InfoDialog.show(R.string.add_attachment, R.string.add_attachment_upload_error, getActivity());
                }
            }
        });
    }

    @Override
    public void onAttachmentDelete(AttachmentResponse ar, int pos) {
        addAttachmentsAdapter.deleteElement(pos);
        if (addAttachmentsAdapter.getItemCount() == 0) {
            ViewGroup.LayoutParams lp = llAttachments.getLayoutParams();
            lp.height = 0;
            llAttachments.setLayoutParams(lp);
        }
    }

    @Override
    public void onUserClick(UserResponse user) {
        showUsersMenu(false);
        onMessageUserClick(user);
    }

    @Override
    public void onUserLongClick(UserResponse user) {
        onMessageUserLongClick(user);
    }
}
