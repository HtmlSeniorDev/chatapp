package ru.readme.chatapp.fragment;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import androidx.annotation.ColorInt;
import androidx.fragment.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import com.thebluealliance.spectrum.SpectrumDialog;
import java.util.regex.Pattern;
import ru.readme.chatapp.activity.App;
import ru.readme.chatapp.R;
import ru.readme.chatapp.dialog.InfoDialog;
import ru.readme.chatapp.dialog.WaitDialog;
import ru.readme.chatapp.object.responses.Status;
import ru.readme.chatapp.util.CheckNick;
import ru.readme.chatapp.helper.RegistrationHelper;

public class RegistrationFragment extends Fragment implements/* GoogleApiClient.OnConnectionFailedListener ,*/View.OnClickListener, RegistrationHelper.OnRegistrationActionsListener {

    private int color = 0;
    private EditText edLogin, edPassword,  edNic;
    private Button btRegistration, btColor;
    private LinearLayout color_page;
    private RegistrationHelper helper;
    private WaitDialog waitDialog;
    private String notdata = "";


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.registration, null);
        waitDialog = new WaitDialog(getActivity());
        helper = new RegistrationHelper(getActivity(), this);
        String login = "";
        String password = "";
        String nic = "";
        color = getResources().getColor(R.color.black);

        if (savedInstanceState != null) {
            login = savedInstanceState.getString("login", "");
            password = savedInstanceState.getString("password", "");
            nic = savedInstanceState.getString("nic", "");
            color = savedInstanceState.getInt("color", getResources().getColor(R.color.black));
        }
        edLogin = (EditText) v.findViewById(R.id.ed_registration_login);
        edNic = (EditText) v.findViewById(R.id.ed_registration_nic);
        color_page = (LinearLayout) v.findViewById(R.id.color_page);
        edPassword = (EditText) v.findViewById(R.id.ed_registration_password);
        btColor = (Button) v.findViewById(R.id.bt_registration_color);
        btRegistration = (Button) v.findViewById(R.id.bt_registration_registration);
        edPassword.setText(password);
        edNic.setText(nic);
        edLogin.setText(login);
        btColor.setBackgroundColor(color);
        btColor.setOnClickListener(this);
        btRegistration.setOnClickListener(this);
        return v;
    }

    @Override
    public void onClick(View view) {
        if (view.getId() == R.id.bt_registration_color) {
            new SpectrumDialog.Builder(getActivity())
                    .setColors(R.array.colors)
                    .setTitle(R.string.select_color)
                    .setPositiveButtonText(R.string.ok)
                    .setNegativeButtonText(R.string.cancel)
                    .setOnColorSelectedListener(new SpectrumDialog.OnColorSelectedListener() {
                        @Override
                        public void onColorSelected(boolean positiveResult, @ColorInt int color) {
                            if (positiveResult) {
                                RegistrationFragment.this.color = color;
                                btColor.setBackgroundColor(color);
                            }
                        }
                    }).build().show(getFragmentManager(), "selectColor");

        } else if (view.getId() == R.id.bt_registration_registration) {
            waitDialog.show();
            helper.checkLogin(edLogin.getText().toString());

        }
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putInt("color", color);
        outState.putString("login", edLogin.getText().toString().trim());
        outState.putString("password", edPassword.getText().toString().trim());
        outState.putString("nic", edNic.getText().toString().trim());
    }

    @Override
    public void onCheckLogin(boolean check, boolean version) {

        if (check) {
            if (CheckNick.check(edNic.getText().toString().trim())) {
                helper.checkNic(edNic.getText().toString().trim());
            } else {
                waitDialog.hide();
                InfoDialog.show(getString(R.string.registration), getString(R.string.nic_symbols_error), getActivity());
            }
        } else {
            waitDialog.hide();
            if(version == true) {
                InfoDialog.show(R.string.version, R.string.version_error, getActivity());
            }else {
                InfoDialog.show(getString(R.string.registration), getString(R.string.login_is_exist), getActivity());
            }
        }
    }

    @Override
    public void onRegistration(boolean registration, String status) {
        waitDialog.hide();
        if(status.equals(Status.STATUS_REG_LIMIT_DAY )){
            InfoDialog.show(getString(R.string.registration), "Лимит регистраций на сегодняшний день для вашего устройства исчерпан, повторите завтра", getActivity());
       return;
        }
        if(status.equals(Status.STATUS_REG_LIMIT_ALL) ){
            InfoDialog.show(getString(R.string.registration), "Лимит регистраций для вашего устройства исчерпан", getActivity());
            return;
        }
        if(status.equals(Status.STATUS_REG_BAN )){
            InfoDialog.show(getString(R.string.registration), "Ваше устройство заблокировано, регистрация невозможна", getActivity());
            return;
        }
        if (registration) {
            Intent i = new Intent();
            i.putExtra("login", edLogin.getText().toString().trim());
            i.putExtra("pas", edPassword.getText().toString().trim());
            getActivity().setResult(Activity.RESULT_OK, i);
            getActivity().finish();
        } else {
                InfoDialog.show(getString(R.string.registration), getString(R.string.registration_error), getActivity());
            }
    }

    @Override
    public void onCheckNic(boolean done) {
        if (done) {
            String login = edLogin.getText().toString().trim();
            String password = edPassword.getText().toString().trim();
            String nic = edNic.getText().toString().trim();
            nic = CheckNick.ModNick(nic);
            if (password.length() >= 1) {
                if (nic.length() > 0) {
                    if (nic.replaceAll(Pattern.quote(" "), "").length() > 0) {
                        if(App.generateSystemCode == "null"){
                            InfoDialog.show(getString(R.string.registration), "Для вашего устройства регистрация недоступна", getActivity());
                            return;
                        }else {
                            helper.registration("email", login, password, nic, color, "","", App.generateSystemCode);
                        }
                    } else {
                        waitDialog.hide();
                        InfoDialog.show(getString(R.string.registration), getString(R.string.nic_spaces), getActivity());
                    }
                } else {
                    waitDialog.hide();
                    InfoDialog.show(getString(R.string.registration), getString(R.string.short_nic), getActivity());
                }
            } else {
                waitDialog.hide();
                InfoDialog.show(getString(R.string.registration), getString(R.string.password_not_correct), getActivity());
            }
        } else {
            waitDialog.hide();
            InfoDialog.show(getString(R.string.registration), getString(R.string.nic_is_exist_or_error), getActivity());
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == 2) {
            getActivity().setResult(2);
            getActivity().finish();
        }
    }
}
