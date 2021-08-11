package ru.readme.chatapp.dialog;

import android.content.Context;
import android.content.DialogInterface;
import androidx.appcompat.app.AlertDialog;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.CheckBox;
import android.widget.EditText;

import java.io.Serializable;

import ru.readme.chatapp.R;

public class CreateRoomDialog {

    private Context context;
    private String parentId;
    private WaitDialog waitDialog;
    private AlertDialog dialog;

    private EditText edName;
    private CheckBox chbUser, chbModer, chbAdmin, chbInvisible, chbBanned;

    private CreateRoomDialog(Context context, String parentId, final OnCreateRoomClickListener listener) {
        this.context = context;
        this.parentId = parentId;
        View cont = LayoutInflater.from(context).inflate(R.layout.create_chat_dialog, null);
        edName = (EditText) cont.findViewById(R.id.ed_create_chat_dialog_name);
        chbAdmin = (CheckBox) cont.findViewById(R.id.chb_create_chat_dialog_admin);
        chbModer = (CheckBox) cont.findViewById(R.id.chb_create_chat_dialog_moder);
        chbBanned = (CheckBox) cont.findViewById(R.id.chb_create_chat_dialog_banned);
        chbInvisible = (CheckBox) cont.findViewById(R.id.chb_create_chat_dialog_invisible);
        chbUser = (CheckBox) cont.findViewById(R.id.chb_create_chat_dialog_user);
        dialog = new AlertDialog.Builder(context)
                .setTitle(R.string.create_room)
                .setView(cont)
                .setNegativeButton(R.string.cancel, null)
                .setPositiveButton(R.string.create, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        if (listener != null) {
                            CreateChat cc = new CreateChat();
                            cc.admin = chbAdmin.isChecked();
                            cc.banned = chbBanned.isChecked();
                            cc.invisible = chbInvisible.isChecked();
                            cc.moder = chbModer.isChecked();
                            cc.user = chbUser.isChecked();
                            cc.name = edName.getText().toString();
                            listener.onCreateRoomClick(cc);

                        }
                    }
                })
                .create();

    }

    public static void show(Context context, String parentId, OnCreateRoomClickListener listener) {
        CreateRoomDialog ccd = new CreateRoomDialog(context, parentId, listener);
        ccd.dialog.show();
    }

    public interface OnCreateRoomClickListener {
        void onCreateRoomClick(CreateChat chat);
    }

    public static class CreateChat implements Serializable {
        boolean admin = true;
        boolean user = false;
        boolean banned = false;
        boolean invisible = false;
        boolean moder = false;
        String name;

        public boolean isAdmin() {
            return admin;
        }

        public void setAdmin(boolean admin) {
            this.admin = admin;
        }

        public boolean isUser() {
            return user;
        }

        public void setUser(boolean user) {
            this.user = user;
        }

        public boolean isBanned() {
            return banned;
        }

        public void setBanned(boolean banned) {
            this.banned = banned;
        }

        public boolean isInvisible() {
            return invisible;
        }

        public void setInvisible(boolean invisible) {
            this.invisible = invisible;
        }

        public boolean isModer() {
            return moder;
        }

        public void setModer(boolean moder) {
            this.moder = moder;
        }

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }
    }
}
