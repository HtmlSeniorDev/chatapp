package ru.readme.chatapp.fragment;


import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;

import ru.readme.chatapp.activity.MainActivity;
import ru.readme.chatapp.R;
import ru.readme.chatapp.activity.RegistrationActivity;
import ru.readme.chatapp.dialog.InfoDialog;
import ru.readme.chatapp.dialog.WaitDialog;
import ru.readme.chatapp.object.responses.Status;
import ru.readme.chatapp.util.CheckNick;
import ru.readme.chatapp.util.SettingsHelper;
import ru.readme.chatapp.helper.LoginHelper;

/**
 * Created by dima on 16.11.16.
 */

public class LoginFragment extends Fragment implements  View.OnClickListener, LoginHelper.OnLoginHelperActionListener {
    private static final int CODE_REGISTR = 54;
    private Button btLogin, btRegistration;
    private EditText edLogin, edPassword;
   // private ImageButton ibInfo;
    private WaitDialog waitDialog;
    private LoginHelper helper;
    private SettingsHelper settingsHelper;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        View v = inflater.inflate(R.layout.login, null);
        waitDialog = new WaitDialog(getActivity());
        settingsHelper = new SettingsHelper(getActivity());
        helper = new LoginHelper(getActivity(), this);
        String login = "";
        String password = "";
        if (savedInstanceState != null) {
            login = savedInstanceState.getString("login", "");
            password = savedInstanceState.getString("password", "");
        }
        edLogin = (EditText) v.findViewById(R.id.ed_login_login);
        edPassword = (EditText) v.findViewById(R.id.ed_login_password);
        btLogin = (Button) v.findViewById(R.id.bt_login_login);
        btRegistration = (Button) v.findViewById(R.id.bt_login_do_registr);


      //  ibInfo = (ImageButton) v.findViewById(R.id.ib_login_login_info);
        edLogin.setText(login);
        edPassword.setText(password);
        btRegistration.setOnClickListener(this);
        btLogin.setOnClickListener(this);
      //  ibInfo.setOnClickListener(this);
        boolean ae = settingsHelper.getAE();

       // InfoDialog.show("ANDROID SUPER CODE", App.code,getActivity());

        if (ae==false){
            String ps = new SettingsHelper(getActivity()).getPassword();
            String lg = new SettingsHelper(getActivity()).getLogin();
            if (ps != null) {
                edPassword.setText(ps);
            }
            if (lg != null) {
                edLogin.setText(lg);
            }
        }
        if (helper.isLogin()  && ae == true && !getArguments().getBoolean("network", false) &&  !getArguments().getBoolean("login", false)) {
            Intent i = new Intent(getActivity(), MainActivity.class);
            i.putExtra("category", "-1");
            startActivity(i);
            getActivity().finish();
        } else if (getArguments().getBoolean("network", false) || getArguments().getBoolean("login", false)) {
            String ps = new SettingsHelper(getActivity()).getPassword();
            String lg = new SettingsHelper(getActivity()).getLogin();
            if (ps != null) {
                edPassword.setText(ps);
            }
            if (lg != null) {
                edLogin.setText(lg);
            }
        }

        return v;
    }


    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putString("login", edLogin.getText().toString());
        outState.putString("password", edPassword.getText().toString());
    }


    @Override
    public void onClick(View view) {
        if (view.getId() == R.id.bt_login_do_registr) {
            if(CheckNick.access("access")==0){
                InfoDialog.show("Информация", "На вашем телефоне имеется root-доступ. Регистрация недоступна.", getActivity());
           return;
            }else {
                startActivityForResult(new Intent(getActivity(), RegistrationActivity.class), CODE_REGISTR);
            }
        } else if (view.getId() == R.id.bt_login_login) {
            waitDialog.show();
            helper.doLogin(edLogin.getText().toString().trim(), edPassword.getText().toString().trim());
        } //else if (view.getId() == R.id.ib_login_login_info) {
          //  InfoDialog.show(R.string.app_name, R.string.app_info, getActivity());
       // }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == CODE_REGISTR) {
            if (resultCode == Activity.RESULT_OK) {
                String login = data.getStringExtra("login");
                String pas = data.getStringExtra("pas");
                edLogin.setText(login);
                edPassword.setText(pas);
            }
        }
        if (resultCode == 2) {
            getActivity().setResult(2);
            getActivity().finish();
        }
    }

    @Override
    public void onLogin(boolean login,boolean version, String st) {

            if(st.equals(Status.STATUS_LOGIN_BAN)){
            InfoDialog.show(getString(R.string.registration), "Ваше устройство заблокировано, авторизация невозможна", getActivity());
        return;
        }

        if (login) {
            helper.getUserType();
        } else {
            waitDialog.hide();
            if(version == true) {
                InfoDialog.show(R.string.version, R.string.version_error, getActivity());
            }else{
                InfoDialog.show(R.string.authorization, R.string.login_error, getActivity());
            }
        }
    }

    @Override
    public void onGetUserType(boolean getType) {
        waitDialog.hide();
        if (getType) {
            Intent i = new Intent(getActivity(), MainActivity.class);
            i.putExtra("category", "-1");
            startActivity(i);
            getActivity().finish();
        } else {
            InfoDialog.show(R.string.authorization, R.string.login_error, getActivity());
        }
    }
}
