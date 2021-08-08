package ru.readme.chatapp.fragment;

import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.DownloadListener;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import ru.readme.chatapp.activity.MainActivity;
import ru.readme.chatapp.R;
import ru.readme.chatapp.dialog.AddBalanceDialog;
import ru.readme.chatapp.dialog.InfoDialog;
import ru.readme.chatapp.dialog.WaitDialog;
import ru.readme.chatapp.object.responses.UserResponse;
import ru.readme.chatapp.util.MD5;
import ru.readme.chatapp.util.SettingsHelper;
import ru.readme.chatapp.helper.BalanceHeleper;

import static ru.readme.chatapp.R.string.balance;

public class BalanceFragment extends MyBaseFragment implements BalanceHeleper.BalanceHelperListener, AddBalanceDialog.OnAddBalanceSelectListener {

    private WebView web;
    private BalanceHeleper heleper;
    private WaitDialog waitDialog;
    private boolean load = false;
    private String id;

    private Handler handler = new Handler(new Handler.Callback() {
        @Override
        public boolean handleMessage(Message msg) {
            if (msg.what == 1) {
                if (!load) {
                    load = true;
                    heleper.loadUser();
                }
                handler.sendEmptyMessageDelayed(1, 5000);
            }
            return false;
        }
    });

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.balance, null);
        heleper = new BalanceHeleper(getActivity(), this);
        if(getActivity() instanceof MainActivity) {
            ((AppCompatActivity) getActivity()).getSupportActionBar().setTitle(R.string.balance);
        }
        if (savedInstanceState != null && savedInstanceState.containsKey("id")) {
            id = savedInstanceState.getString("id");
        } else if (getArguments() != null && getArguments().containsKey("id")) {
            id = getArguments().getString("id");
        }
        waitDialog = new WaitDialog(getActivity());
        web = (WebView) v.findViewById(R.id.vw_balance_web);
        if(getActivity() instanceof MainActivity) {
            waitDialog.show();
            handler.sendEmptyMessage(1);
        }
        new AddBalanceDialog(getActivity(), this, id!=null);

        web.setWebViewClient(new MyWebViewClient());
        WebSettings webSettings = web.getSettings();
        webSettings.setJavaScriptEnabled(true);
        webSettings.setDomStorageEnabled(true);
        web.setDownloadListener(new DownloadListener() {
            @Override
            public void onDownloadStart(String url, String userAgent, String contentDisposition, String mimetype, long contentLength) {
                Log.e("App", url);
            }
        });
        return v;
    }

    @Override
    public void onGetProfile(UserResponse user) {
        waitDialog.hide();
        if (user != null) {
            if(!isDetached()) {
                String title = getString(balance) + ": " + convertKopps(user.getBalance());
                ((AppCompatActivity) getActivity()).getSupportActionBar().setTitle(title);
            }
        } else {
            waitDialog.show();
            handler.sendEmptyMessage(1);
        }
    }

    private String convertKopps(int kops) {
        int price = kops / 100;
        float pr = price - (float) kops / 100;
        if (pr < 0) {
            pr = 1f - pr;
        }
        String spr = pr + "000";
        spr = spr.substring(2, 4);
        spr = price + "," + spr + " " + getString(R.string.monet);
        return spr;
    }

    @Override
    public void onCancelAdd() {
        if(getActivity() instanceof MainActivity) {
            ((MainActivity) getActivity()).setChatsFragment();
        }else{
            getActivity().finish();
        }
    }

    @Override
    public void onAddBalance(int add) {
        String iid = id;
        if(iid==null){
            iid = new SettingsHelper(getActivity()).getUserId();
        }
        String uid = iid + MD5.MD5("" + System.currentTimeMillis()).substring(0, 4);
        web.loadUrl("https://www.air-chat.tk/pay/pay.php?rub=" + add + "&uid=" + uid);
    }

    private class MyWebViewClient extends WebViewClient {

        public MyWebViewClient() {
        }

        @Override
        public boolean shouldOverrideUrlLoading(WebView view, String url) {
            view.loadUrl(url);
            if (url.indexOf("success.php") > -1) {
                try {
                    InfoDialog.show(R.string.add_balance, R.string.add_balance_done, getActivity());
                } catch (Exception e) {
                }
            } else if (url.indexOf("fail.php") > -1) {
                try {
                    InfoDialog.show(R.string.add_balance, R.string.add_balance_error, getActivity());
                } catch (Exception e) {

                }
            }
            return true;
        }
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        if(id!=null){
            outState.putString("id",id);
        }
        super.onSaveInstanceState(outState);
    }
}
