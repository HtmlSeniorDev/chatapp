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
import ru.readme.chatapp.object.responses.CategoryResponse;

public class EditCategoryDialog {

    private Context context;
    private WaitDialog waitDialog;
    private AlertDialog dialog;
    private EditText edName;
    private CheckBox chbUser, chbModer, chbAdmin, chbInvisible, chbBanned;

    private EditCategoryDialog(Context context, final CategoryResponse category, final OnEditCategoryClickListener listener) {
        this.context = context;
        View cont = LayoutInflater.from(context).inflate(R.layout.create_category_dialog, null);
        edName = (EditText) cont.findViewById(R.id.ed_create_category_dialog_name);
        chbAdmin = (CheckBox) cont.findViewById(R.id.chb_create_category_dialog_admin);
        chbModer = (CheckBox) cont.findViewById(R.id.chb_create_category_dialog_moder);
        chbBanned = (CheckBox) cont.findViewById(R.id.chb_create_category_dialog_banned);
        chbInvisible = (CheckBox) cont.findViewById(R.id.chb_create_category_dialog_invisible);
        chbUser = (CheckBox) cont.findViewById(R.id.chb_create_category_dialog_user);
        edName.setText(category.getName());
        if ((category.getMask() & UserType.TYPE_USER) > 0) {
            chbUser.setChecked(true);
        }
        if ((category.getMask() & UserType.TYPE_MODER) > 0) {
            chbModer.setChecked(true);
        }
        if ((category.getMask() & UserType.TYPE_BANNED) > 0) {
            chbBanned.setChecked(true);
        }
        if ((category.getMask() & UserType.TYPE_INVISIBLE) > 0) {
            chbInvisible.setChecked(true);
        }
        dialog = new AlertDialog.Builder(context)
                .setTitle(R.string.edited_category)
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
                            category.setMask(mask);
                            category.setName(edName.getText().toString());
                            listener.onEditCategoryClick(category);
                        }
                    }
                })
                .create();

    }

    public static void show(Context context, CategoryResponse category, OnEditCategoryClickListener listener) {
        EditCategoryDialog ccd = new EditCategoryDialog(context, category, listener);
        ccd.dialog.show();
    }

    public interface OnEditCategoryClickListener {
        void onEditCategoryClick(CategoryResponse categoryResponse);
    }

}
