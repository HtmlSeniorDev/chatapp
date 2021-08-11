package ru.readme.chatapp.dialog;

import android.content.Context;
import android.content.DialogInterface;
import androidx.appcompat.app.AlertDialog;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.CheckBox;
import android.widget.EditText;

import ru.readme.chatapp.R;
import ru.readme.chatapp.object.UserType;
import ru.readme.chatapp.object.responses.RoomResponse;

/**
 * Created by dima on 19.11.16.
 */

public class EditRoomDialog {

    private Context context;
    private AlertDialog dialog;
    private EditText edName;
    private CheckBox chbUser, chbModer, chbAdmin, chbInvisible, chbBanned;

    private EditRoomDialog(Context context, final RoomResponse room, final OnEditRoomClickListener listener) {
        this.context = context;
        View cont = LayoutInflater.from(context).inflate(R.layout.create_chat_dialog, null);
        edName = (EditText) cont.findViewById(R.id.ed_create_chat_dialog_name);
        chbAdmin = (CheckBox) cont.findViewById(R.id.chb_create_chat_dialog_admin);
        chbModer = (CheckBox) cont.findViewById(R.id.chb_create_chat_dialog_moder);
        chbBanned = (CheckBox) cont.findViewById(R.id.chb_create_chat_dialog_banned);
        chbInvisible = (CheckBox) cont.findViewById(R.id.chb_create_chat_dialog_invisible);
        chbUser = (CheckBox) cont.findViewById(R.id.chb_create_chat_dialog_user);
        edName.setText(room.getName());
        if ((room.getMask() & UserType.TYPE_USER) > 0) {
            chbUser.setChecked(true);
        }
        if ((room.getMask() & UserType.TYPE_MODER) > 0) {
            chbModer.setChecked(true);
        }
        if ((room.getMask() & UserType.TYPE_BANNED) > 0) {
            chbBanned.setChecked(true);
        }
        if ((room.getMask() & UserType.TYPE_INVISIBLE) > 0) {
            chbInvisible.setChecked(true);
        }
        dialog = new AlertDialog.Builder(context)
                .setTitle(R.string.edited_room)
                .setView(cont)
                .setNegativeButton(R.string.cancel, null)
                .setPositiveButton(R.string.edit, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        if (listener != null) {
                            int mask = UserType.TYPE_ADMIN;
                            if (chbBanned.isChecked()) {
                                mask = mask | UserType.TYPE_BANNED;
                            }
                            if (chbInvisible.isChecked()) {
                                mask = mask | UserType.TYPE_INVISIBLE;
                            }
                            if (chbModer.isChecked()) {
                                mask = mask | UserType.TYPE_MODER;
                            }
                            if (chbUser.isChecked()) {
                                mask = mask | UserType.TYPE_USER;
                            }
                            room.setMask(mask);
                            room.setName(edName.getText().toString());
                            listener.onEditCategoryClick(room);
                        }
                    }
                })
                .create();

    }

    public static void show(Context context, RoomResponse room, OnEditRoomClickListener listener) {
        EditRoomDialog ccd = new EditRoomDialog(context, room, listener);
        ccd.dialog.show();
    }

    public interface OnEditRoomClickListener {
        void onEditCategoryClick(RoomResponse roomResponse);
    }

}
