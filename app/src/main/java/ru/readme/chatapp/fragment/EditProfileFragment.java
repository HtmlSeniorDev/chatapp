package ru.readme.chatapp.fragment;

import android.app.DatePickerDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.ColorFilter;
import android.graphics.ColorMatrixColorFilter;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.annotation.ColorInt;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.RelativeLayout;
import android.widget.Spinner;

import com.squareup.picasso.Picasso;
import com.thebluealliance.spectrum.SpectrumDialog;

import java.io.File;
import java.io.FileOutputStream;
import java.io.OutputStream;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.regex.Pattern;

import ru.readme.chatapp.activity.MainActivity;
import ru.readme.chatapp.R;
import ru.readme.chatapp.dialog.AddPhotoDialog;
import ru.readme.chatapp.dialog.InfoDialog;
import ru.readme.chatapp.dialog.SelectFileDialog;
import ru.readme.chatapp.dialog.WaitDialog;
import ru.readme.chatapp.object.UserType;
import ru.readme.chatapp.object.requests.PhotoRequest;
import ru.readme.chatapp.object.responses.PhotoResponse;
import ru.readme.chatapp.object.responses.UserResponse;
import ru.readme.chatapp.util.CheckNick;
import ru.readme.chatapp.util.ImagePicker;
import ru.readme.chatapp.util.ImageSetter;
import ru.readme.chatapp.util.Network;
import ru.readme.chatapp.util.SettingsHelper;
import ru.readme.chatapp.helper.EditProfileHelper;
import ru.readme.chatapp.helper.RegistrationHelper;

import static android.app.Activity.RESULT_OK;

/**
 * Created by dima on 21.11.16.
 */

public class EditProfileFragment extends MyBaseFragment implements AddPhotoDialog.OnAddPhotoListener,View.OnClickListener, EditProfileHelper.OnEditProfileListener {

    private EditText edNic, edFirstName, edLastName, edCity, edAbout;
    private RadioButton rbAdmin, rbModer, rbUser, rbInvisible, rbBanned;
    private Button btBday;
    private ImageView ivPhoto;
    private Button btColor;
    private UserResponse user;
    private SettingsHelper settingsHelper;
    private WaitDialog waitDialog;
    private int color = 0;
    private EditProfileHelper helper;
    private View v1, v2, v3, v4;
    private RelativeLayout content;
    private String phId = null;
    private AddPhotoDialog photoDialog;
    private Spinner sex;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.edit_profile, null);
        setHasOptionsMenu(true);
        helper = new EditProfileHelper(this, getActivity());
        waitDialog = new WaitDialog(getActivity());
        settingsHelper = new SettingsHelper(getActivity());
        ivPhoto = (ImageView) v.findViewById(R.id.iv_edit_profile_photo);
        btColor = (Button) v.findViewById(R.id.bt_edit_profile_color);
        edAbout = (EditText) v.findViewById(R.id.ed_edit_profile_user_about);
        edCity = (EditText) v.findViewById(R.id.ed_edit_profile_user_city);
        edFirstName = (EditText) v.findViewById(R.id.ed_edit_profile_user_first_name);
        edLastName = (EditText) v.findViewById(R.id.ed_edit_profile_user_last_name);
        edNic = (EditText) v.findViewById(R.id.ed_edit_profile_user_nic);
        rbAdmin = (RadioButton) v.findViewById(R.id.rb_edit_profile_admin);
        rbModer = (RadioButton) v.findViewById(R.id.rb_edit_profile_moder);
        rbUser = (RadioButton) v.findViewById(R.id.rb_edit_profile_user);
        rbInvisible = (RadioButton) v.findViewById(R.id.rb_edit_profile_invisible);
        rbBanned = (RadioButton) v.findViewById(R.id.rb_edit_profile_banned);
        btBday = (Button) v.findViewById(R.id.bt_edit_profile_bday);
        sex = (Spinner)  v.findViewById(R.id.spinner_sex);
        v1 = v.findViewById(R.id.l4);
        v4 = v.findViewById(R.id.v4);
        content = (RelativeLayout) v.findViewById(R.id.content);
        v2 = v.findViewById(R.id.s1);
        v3 = v.findViewById(R.id.s2);

        ivPhoto.setOnClickListener(this);
        btBday.setOnClickListener(this);
        if (savedInstanceState != null) {
            user = (UserResponse) savedInstanceState.getSerializable("user");
        } else if (getArguments() != null && getArguments().containsKey("user")) {
            user = (UserResponse) getArguments().getSerializable("user");
        }
        if (user != null) {
            setUser();
        }
        btColor.setOnClickListener(this);

        if (settingsHelper.getUserType() != UserType.TYPE_ADMIN) {
            content.removeView(v4);
            content.removeView(v3);
            content.removeView(v2);
            content.removeView(v1);
        }
        return v;
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
                    if (photoDialog != null) {
                        photoDialog.setImage(img);
                    }
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
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        inflater.inflate(R.menu.edit_profile, menu);
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putSerializable("user", user);

    }

    public String ModNick(String nick) {// русс, англ
        nick = nick.trim();
        nick = nick.replace("А" , "A");
        nick = nick.replace("В" , "B");
        nick = nick.replace("С" , "C");
        nick = nick.replace("Е" , "E");
        nick = nick.replace("Н" , "H");
        nick = nick.replace("К" , "K");
        nick = nick.replace("М" , "M");
        nick = nick.replace("О" , "O");
        nick = nick.replace("Р" , "P");
        nick = nick.replace("Т" , "T");
        nick = nick.replace("Х" , "X");

        nick = nick.replace("а" , "a");
        nick = nick.replace("с" , "c");
        nick = nick.replace("е" , "e");
        nick = nick.replace("о" , "o");
        nick = nick.replace("р" , "p");
        nick = nick.replace("х" , "x");
        //nick = nick.replace("к" , "k");
        return nick;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == R.id.menu_edit_profile_save) {
            //Toast.makeText( getActivity(), "Выбранный номер пола:"+sex.getSelectedItemPosition(), Toast.LENGTH_LONG).show();
            if (edNic.getText().length() < 3) {
                InfoDialog.show(R.string.edit_profile, R.string.short_nic, getActivity());
            } else if (edNic.getText().toString().replaceAll(Pattern.quote(" "), "").length() == 0) {
                InfoDialog.show(getString(R.string.edit_profile), getString(R.string.nic_spaces), getActivity());
            } else if(!CheckNick.check(edNic.getText().toString())) {
                InfoDialog.show(getString(R.string.edit_profile), getString(R.string.nic_symbols_error), getActivity());
            }else{
                String nic = edNic.getText().toString();
                nic = nic.replaceAll(Pattern.quote("\n"),"");
                nic = ModNick(nic);
                if(nic.length()>0){
                    while(nic.length()>0 && nic.charAt(0)==' '){
                        nic = nic.substring(1);
                    }
                    while(nic.length()>0 &&nic.charAt(nic.length()-1)==' '){
                        nic = nic.substring(1);
                    }
                }
                waitDialog.show();
                helper.checkNic(nic);
            }
        }
        return super.onOptionsItemSelected(item);
    }

    private void save() {
        UserResponse ur = new UserResponse();
        ur.setId(user.getId());
        String nic = RegistrationHelper.ModNick(edNic.getText().toString());
        nic = nic.replaceAll(Pattern.quote("\n"),"");
        if(nic.length()>0){
            while(nic.length()>0 && nic.charAt(0)==' '){
                nic = nic.substring(1);
            }
            while(nic.length()>0 &&nic.charAt(nic.length()-1)==' '){
                nic = nic.substring(1);
            }
        }
        ur.setNic(nic);
        if (edLastName.getText().length() > 0) {
            ur.setLastName(edLastName.getText().toString());
        }
        if (edFirstName.getText().toString().length() > 0) {
            ur.setFirstName(edFirstName.getText().toString());
        }
        if (edCity.getText().toString().length() > 0) {
            ur.setCity(edCity.getText().toString());
        }
        if (sex.getSelectedItemPosition() >= 0 && sex.getSelectedItemPosition() < 3) {
            ur.setSex(sex.getSelectedItemPosition());
        }
        if (edAbout.getText().toString().length() > 0) {
            ur.setAbout(edAbout.getText().toString());
        }
        ur.setColor(color);
        int tp = UserType.TYPE_USER;
        if (rbAdmin.isChecked()) {
            tp = UserType.TYPE_ADMIN;
        }
        if (rbModer.isChecked()) {
            tp = UserType.TYPE_MODER;
        }
        if (rbUser.isChecked()) {
            tp = UserType.TYPE_USER;
        }
        if (rbInvisible.isChecked()) {
            tp = UserType.TYPE_INVISIBLE;
        }
        if (rbBanned.isChecked()) {
            tp = UserType.TYPE_BANNED;
        }
        ur.setBday(user.getBday());
        ur.setAvatarLink(user.getAvatarLink());
        ur.setType(tp);
        waitDialog.show();
        helper.editProfile(ur);
    }

    private void showTimeDialog() {

        final Calendar calendar = Calendar.getInstance();
        if (user.getBday() != null) {
            calendar.setTime(user.getBday());
        }
        int year = calendar.get(Calendar.YEAR);
        int month = calendar.get(Calendar.MONTH);
        int day = calendar.get(Calendar.DAY_OF_MONTH);
        DatePickerDialog dp = new DatePickerDialog(getActivity(), new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker view, int y, int m, int d) {
                calendar.set(y, m, d);
                long l = System.currentTimeMillis();
                l -= 1000 * 60 * 60 * 24 * 10;
                long dt = calendar.getTimeInMillis();
                if (dt < l) {
                    user.setBday(new Date(dt));
                    SimpleDateFormat sdf = new SimpleDateFormat("dd.MM.yyyy");
                    btBday.setText(sdf.format(user.getBday()));
                }
            }
        }, year, month, day);
        dp.show();
        openYearView(dp.getDatePicker());

    }

    private void openYearView(DatePicker datePicker) {
        try {
            Field mDelegateField = datePicker.getClass().getDeclaredField("mDelegate");
            mDelegateField.setAccessible(true);
            Object delegate = mDelegateField.get(datePicker);
            Method setCurrentViewMethod = delegate.getClass().getDeclaredMethod("setCurrentView", int.class);
            setCurrentViewMethod.setAccessible(true);
            setCurrentViewMethod.invoke(delegate, 1);
        } catch (Exception e) {
        }
    }

    public void setUser() {
        if (user != null) {
            try {
                ImageSetter.setAvatar(user.getAvatarLink(), getActivity(), ivPhoto);

            } catch (Exception e) {
            }
            color = user.getColor();
            setColorBg(color);
            edNic.setText(user.getNic());
         //   if(user.getType()==UserType.TYPE_ADMIN){
                edNic.setEnabled(true);
         //   }
            if (user.getFirstName() != null) {
                edFirstName.setText(user.getFirstName());
            }
            if (user.getLastName() != null) {
                edLastName.setText(user.getLastName());
            }
            if (user.getCity() != null) {
                edCity.setText(user.getCity());
            }
            if (user.getSex() != -1) {
                sex.setSelection(user.getSex());
            }
            if (user.getAbout() != null) {
                edAbout.setText(user.getAbout());
            }
            if (user.getBday() != null) {
                SimpleDateFormat sdf = new SimpleDateFormat("dd.MM.yyyy");
                btBday.setText(sdf.format(user.getBday()));
            }
            int type = settingsHelper.getUserType();
            rbAdmin.setEnabled(type == UserType.TYPE_ADMIN);
            rbModer.setEnabled(type == UserType.TYPE_ADMIN);
            rbInvisible.setEnabled(type == UserType.TYPE_ADMIN || type == UserType.TYPE_MODER);
            rbUser.setEnabled(type == UserType.TYPE_ADMIN || type == UserType.TYPE_MODER);
            rbBanned.setEnabled(type == UserType.TYPE_ADMIN || type == UserType.TYPE_MODER);

            if (user.getId().equals(settingsHelper.getUserId())) {
                rbModer.setEnabled(type == UserType.TYPE_ADMIN || type == UserType.TYPE_MODER);
            }
            rbBanned.setChecked(false);
            rbModer.setChecked(false);
            rbInvisible.setChecked(false);
            rbUser.setChecked(false);
            rbAdmin.setChecked(false);

            switch (user.getType()) {
                case UserType.TYPE_ADMIN:
                    rbAdmin.setChecked(true);
                    break;
                case UserType.TYPE_BANNED:
                    rbBanned.setChecked(true);
                    break;
                case UserType.TYPE_USER:
                    rbUser.setChecked(true);
                    break;
                case UserType.TYPE_MODER:
                    rbModer.setChecked(true);
                    break;
                case UserType.TYPE_INVISIBLE:
                    rbInvisible.setChecked(true);
                    break;
                default:
                    break;
            }
            Picasso.with(getActivity())
                    .load(Network.photoLink(user.getPhoto()))
                    .error(R.drawable.ic_block_grey_700_48dp)
                    .into(ivPhoto);
        }
    }

    @Override
    public void onClick(View view) {
        if (view.getId() == R.id.bt_edit_profile_color) {

            new SpectrumDialog.Builder(getActivity())
                    .setColors(R.array.colors)
                    .setTitle(R.string.select_color)
                    .setPositiveButtonText(R.string.ok)
                    .setNegativeButtonText(R.string.cancel)
                    .setOnColorSelectedListener(new SpectrumDialog.OnColorSelectedListener() {
                        @Override
                        public void onColorSelected(boolean positiveResult, @ColorInt int color) {
                            if (positiveResult) {
                                EditProfileFragment.this.color = color;
                                setColorBg(color);
                            }
                        }
                    }).build().show(getFragmentManager(), "selectColor");


        } else if (view.getId() == R.id.iv_edit_profile_photo) {
            photoDialog = new AddPhotoDialog(this, null, this);
        } else if (view.getId() == R.id.bt_edit_profile_bday) {
            showTimeDialog();
        }
    }



    private void setColorBg(int color) {
        edNic.setTextColor(color);
        Drawable dr = getActivity().getResources().getDrawable(R.drawable.bg_circle_bt);

        int red = (color & 0xFF0000) / 0xFFFF;
        int green = (color & 0xFF00) / 0xFF;
        int blue = color & 0xFF;

        float[] matrix = {0, 0, 0, 0, red,
                0, 0, 0, 0, green,
                0, 0, 0, 0, blue,
                0, 0, 0, 1, 0};

        ColorFilter colorFilter = new ColorMatrixColorFilter(matrix);
        dr.setColorFilter(colorFilter);
        btColor.setBackground(dr);
    }

    @Override
    public void onEditProfile(boolean status) {
        waitDialog.hide();
        if (status) {
            if (getActivity() instanceof MainActivity) {
                (getActivity()).onBackPressed();
            } else {
                getActivity().finish();
            }
        } else {
            InfoDialog.show(R.string.edit_profile, R.string.edit_profile_error, getActivity());
        }
    }

    @Override
    public void onCheckNic(boolean done) {
        waitDialog.hide();
        if (done) {
            save();
        } else {
            InfoDialog.show(R.string.edit_profile, R.string.nic_is_exist_or_error, getActivity());
        }
    }

    @Override
    public void onAddPhoto(PhotoResponse photoResponse) {
        waitDialog.hide();
        if (photoResponse != null) {
            phId = photoResponse.getId();
            helper.setForProfile(photoResponse.getId());
            waitDialog.show();

        } else {
            InfoDialog.show(R.string.photo_add, R.string.photo_add_error, getActivity());
        }
    }


    @Override
    public void onSetForProfile(boolean done) {
        waitDialog.hide();
        if (done) {
            user.setPhoto(phId);
            Picasso.with(getActivity())
                    .load(Network.photoLink(phId))
                    .error(R.drawable.ic_block_grey_700_48dp)
                    .into(ivPhoto);
            InfoDialog.show(R.string.set_image_for_profile, R.string.set_image_for_profile_done, getActivity());
        } else {
            InfoDialog.show(R.string.set_image_for_profile, R.string.set_image_for_profile_error, getActivity());
        }
    }

    @Override
    public void onPrepearAddPhoto(File file, PhotoRequest request) {
        if (file != null) {
            waitDialog.show();
            helper.addPhoto(file, request);
        } else {
            InfoDialog.show(R.string.add_photo, R.string.add_photo_error, getActivity());
        }
    }
}
