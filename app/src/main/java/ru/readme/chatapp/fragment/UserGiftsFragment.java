package ru.readme.chatapp.fragment;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.List;

import ru.readme.chatapp.activity.MainActivity;
import ru.readme.chatapp.R;
import ru.readme.chatapp.adapter.GiftsAdapter;
import ru.readme.chatapp.dialog.AddPhotoDialog;
import ru.readme.chatapp.dialog.GiftDialog;
import ru.readme.chatapp.dialog.WaitDialog;
import ru.readme.chatapp.object.responses.GiftResponse;
import ru.readme.chatapp.object.responses.UserResponse;
import ru.readme.chatapp.util.SettingsHelper;
import ru.readme.chatapp.helper.GiftsHelper;

/**
 * Created by dima on 03.01.17.
 */

public class UserGiftsFragment extends MyBaseFragment implements GiftsAdapter.OnGiftElementClickListener, GiftsHelper.OnGiftsHelperListener {

    private GiftsAdapter adapter;
    private RecyclerView rvGifts;

    private GiftResponse folder;
  //  private String user;
    private UserResponse user;

    private GiftsHelper helper;
    private AddPhotoDialog photoDialog;
    private WaitDialog waitDialog;
    private String userId;
    private TextView nogifts;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.user_gifts, null);
        waitDialog = new WaitDialog(getActivity());
        helper = new GiftsHelper(getActivity(), this);
        rvGifts = (RecyclerView) v.findViewById(R.id.rv_user_gifts);
        nogifts = (TextView) v.findViewById(R.id.my_gifts_no);
        if (savedInstanceState != null) {
            user = (UserResponse) savedInstanceState.getSerializable("user");
        }
        waitDialog.show();
        helper.loadUser(user.getId());
        RecyclerView.LayoutManager manager = new GridLayoutManager(getActivity(), 1);
        rvGifts.setLayoutManager(manager);
        if (user == null || user.equals(new SettingsHelper(getActivity()).getUserId())) {
            adapter= new GiftsAdapter(getActivity(), this, true);
        }
        rvGifts.setAdapter(adapter);

        if (getActivity() instanceof MainActivity) {
            ((AppCompatActivity) getActivity()).getSupportActionBar().setTitle(R.string.my_gifts);
        }
        return v;
    }

    @Override
    public void onUserLoad(UserResponse user) {
        waitDialog.hide();
        for (GiftResponse giftResponse : user.getGifts()) {
            adapter.add(giftResponse);
        }
        if (adapter.getItemCount() == 0) {
            //  tvNoGifts.setVisibility(View.VISIBLE);
            //  l5.setVisibility(View.GONE);
            nogifts.setVisibility(View.VISIBLE);
        }
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
    }

    @Override
    public void onGiftElementClick(GiftResponse avatarResponse) {
        new GiftDialog(this, avatarResponse);
    }

    @Override
    public void onGetGifts(boolean done, List<GiftResponse> gifts) {

    }

    @Override
    public void onAddGift(boolean done, GiftResponse giftResponse) {

    }

    @Override
    public void onUpdateGift(boolean done, GiftResponse giftResponse) {

    }

    @Override
    public void onDeleteGift(boolean done, String id) {

    }
}
