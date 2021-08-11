package ru.readme.chatapp.fragment;

import android.os.Bundle;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;

import java.util.regex.Pattern;

import ru.readme.chatapp.R;
import ru.readme.chatapp.adapter.AvatarsAdapter;
import ru.readme.chatapp.dialog.AskDialog;
import ru.readme.chatapp.dialog.InfoDialog;
import ru.readme.chatapp.dialog.WaitDialog;
import ru.readme.chatapp.object.responses.AvatarResponse;
import ru.readme.chatapp.object.responses.AvatarsResponse;
import ru.readme.chatapp.object.responses.UserResponse;
import ru.readme.chatapp.helper.SelectAvatarsHelper;

/**
 * Created by dima on 23.12.16.
 */

public class SetAvatarFragment extends MyBaseFragment implements AvatarsAdapter.OnAvatarElementClickListener, SelectAvatarsHelper.SelectAvatarHelperListener{

    private AvatarsAdapter adapter;
    private RecyclerView rvList;
    private ProgressBar pbLoading;
    private SelectAvatarsHelper helper;
    private UserResponse user;
    private WaitDialog waitDialog;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.select_avatars,null);
        adapter = new AvatarsAdapter(getActivity(),this);
        waitDialog = new WaitDialog(getActivity());
        helper = new SelectAvatarsHelper(getActivity(),this);
        RecyclerView.LayoutManager manager = new GridLayoutManager(getActivity(), 3);
        rvList = (RecyclerView)v.findViewById(R.id.rv_select_avatars_list);
        rvList.setLayoutManager(manager);
        rvList.setAdapter(adapter);
        pbLoading = (ProgressBar)v.findViewById(R.id.pb_select_avatars_progress);
        if(savedInstanceState!=null && savedInstanceState.containsKey("data")){
            AvatarsResponse data = (AvatarsResponse)savedInstanceState.getSerializable("data");
            for(AvatarResponse a:data.getAvatars()){
                adapter.add(a);
            }
        }
        if(savedInstanceState!=null && savedInstanceState.containsKey("user")){
            user = (UserResponse)savedInstanceState.getSerializable("user");
        }
        if(adapter.getItemCount()>0){
            pbLoading.setVisibility(View.INVISIBLE);
        }else{
            pbLoading.setVisibility(View.VISIBLE);
            helper.getAvatars();
        }
        if(user==null){
            setTitle(0);
        }
        helper.getUser();
        if(user!=null){
            onGetUser(true,user);
        }

        return v;
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        AvatarsResponse data = new AvatarsResponse();
        data.setAvatars(adapter.getElements());
        outState.putSerializable("data",data);
        outState.putSerializable("user",user);
    }

    @Override
    public void onAvatarElementClick(final AvatarResponse avatarResponse) {
        String ask = getString(R.string.purchese_avatar_asl_1).replaceAll(Pattern.quote("%"),convertKopps(avatarResponse.getPrice()));
        AskDialog.show(getActivity(), R.string.purchese_avatar, ask, new AskDialog.OnAskDialogClickListener() {
            @Override
            public void onAskClick(boolean acept) {
                if(user!=null && acept){
                    if(user.getBalance()>=avatarResponse.getPrice()){
                        waitDialog.show();
                        helper.setAvatar(avatarResponse.getId());
                    }else{
                        InfoDialog.show(R.string.purchese_avatar,R.string.balance_limit, getActivity());
                    }
                }
            }
        });

    }

    @Override
    public void onGetAvatars(boolean done, AvatarsResponse response) {
        pbLoading.setVisibility(View.INVISIBLE);
        if(done && response.getAvatars()!=null){
            for (AvatarResponse a: response.getAvatars()){
                if(a!=null && a.getPrice()>=0) {
                    adapter.add(a);
                }
            }
        }else{
            InfoDialog.show(R.string.load_avatars,R.string.not_load_avatars,getActivity());
        }
    }

    @Override
    public void onSetAvatar(boolean done) {
        waitDialog.hide();
        if(done){
            helper.getUser();
            InfoDialog.show(R.string.purchese_avatar,R.string.purchese_avatar_done, getActivity());
        }else{
            InfoDialog.show(R.string.purchese_avatar,R.string.purchese_avatar_error, getActivity());
        }
    }

    private void setTitle(int balance){
        if(!isDetached()) {
            String title = getString(R.string.balance) + ": " + convertKopps(balance);
            ((AppCompatActivity) getActivity()).getSupportActionBar().setTitle(title);
        }
    }

    @Override
    public void onGetUser(boolean done, UserResponse response) {
        if(done) {
            this.user = response;
            setTitle(user.getBalance());
        }
    }

    private String convertKopps(int kops){
        int price = kops / 100;
        float pr = price - (float) kops / 100;
        if (pr < 0) {
            pr = 1f - pr;
        }
        String spr = pr + "000";
        spr = spr.substring(2, 4);
        spr = price + "," + spr + " \u20BD";
        return spr;
    }
}
