package ru.readme.chatapp.activity;

import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.ads.AdView;

import ru.readme.chatapp.R;
import ru.readme.chatapp.dialog.InfoDialog;
import ru.readme.chatapp.fragment.LoginFragment;

public class LoginActivity extends AppCompatActivity {
    private AdView mAdView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.base_layout);
        if (savedInstanceState == null) {
            Bundle args = new Bundle();
            if(getIntent().hasExtra("network")){
                args.putBoolean("network",true);
                if(getIntent().getBooleanExtra("network",false)){
                    InfoDialog.show(R.string.network_error, R.string.reconnect,this);
                }
            }
            if(getIntent().hasExtra("login")){
                args.putBoolean("login",true);
                if(getIntent().getBooleanExtra("login",false)){
                    InfoDialog.show(R.string.other_login, R.string.other_login_message,this);
                }
            }
            LoginFragment l = new LoginFragment();
            l.setArguments(args);
            getSupportFragmentManager().beginTransaction()
                    .replace(R.id.ll_base_layout_content,  l)
                    .commit();
        }

    }


}
