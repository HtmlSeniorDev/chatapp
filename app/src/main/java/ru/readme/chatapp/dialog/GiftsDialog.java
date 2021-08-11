package ru.readme.chatapp.dialog;

import android.os.Bundle;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.appcompat.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.ProgressBar;
import android.widget.Toast;

import java.util.List;

import ru.readme.chatapp.R;
import ru.readme.chatapp.adapter.GiftsAdapter;
import ru.readme.chatapp.object.responses.GiftResponse;
import ru.readme.chatapp.object.responses.GiftsResponse;
import ru.readme.chatapp.object.responses.UserResponse;
import ru.readme.chatapp.helper.GiftsHelper;

public class GiftsDialog extends DialogFragment implements GiftsHelper.OnGiftsHelperListener, GiftsAdapter.OnGiftElementClickListener {

    private GiftsAdapter adapter;
    private RecyclerView rvList;
    private ProgressBar pbLoading;
    private WaitDialog waitDialog;
    private GiftsHelper helper;
    private Toolbar toolbar;
    private OnSelectGiftListener listener;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.gifts_dialog, null);
        getDialog().requestWindowFeature(Window.FEATURE_NO_TITLE);
        rvList = (RecyclerView) v.findViewById(R.id.rv_gifts_list);
        toolbar = (Toolbar) v.findViewById(R.id.gifts_dialog_toolbar);
        pbLoading = (ProgressBar) v.findViewById(R.id.pb_gifts_progress);
        helper = new GiftsHelper(getActivity(), this);
        adapter = new GiftsAdapter(getActivity(), this);
        waitDialog = new WaitDialog(getActivity());
        RecyclerView.LayoutManager manager = new GridLayoutManager(getActivity(), 3);
        rvList = (RecyclerView) v.findViewById(R.id.rv_gifts_list);
        rvList.setLayoutManager(manager);
        rvList.setAdapter(adapter);
        if (savedInstanceState != null && savedInstanceState.containsKey("data")) {
            GiftsResponse data = (GiftsResponse) savedInstanceState.getSerializable("data");
            for (GiftResponse a : data.getGifts()) {
                adapter.add(a);
            }
        }
        if (adapter.getItemCount() == 0) {
            helper.getGifts();
        } else {
            pbLoading.setVisibility(View.INVISIBLE);
        }
        toolbar.inflateMenu(R.menu.gifts_dialog);
        toolbar.getMenu().findItem(R.id.menu_gifts_dialog_close).setOnMenuItemClickListener(new MenuItem.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem menuItem) {
                dismiss();
                return false;
            }
        });
        toolbar.setTitle(R.string.gifts);
        return v;
    }

    public void setOnSelectGiftListener(OnSelectGiftListener listener) {
        this.listener = listener;
    }

    @Override
    public void onGiftElementClick(final GiftResponse giftResponse) {
        if (listener != null) {
            listener.onSelectGift(giftResponse);
            this.dismiss();
        }
    }

    @Override
    public void onGetGifts(boolean done, List<GiftResponse> gifts) {
        pbLoading.setVisibility(View.INVISIBLE);
        if (done) {
            for (GiftResponse a : gifts) {
                adapter.add(a);
            }
        } else {
            Toast.makeText(getActivity(), R.string.not_load_gifts, Toast.LENGTH_LONG).show();
        }
    }

    @Override
    public void onAddGift(boolean done, GiftResponse response) {

    }

    @Override
    public void onUpdateGift(boolean done, GiftResponse response) {

    }

    @Override
    public void onDeleteGift(boolean done, String id) {

    }

    @Override
    public void onUserLoad(UserResponse user) {

    }


    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        GiftsResponse data = new GiftsResponse();
        data.setGifts(adapter.getElements());
        outState.putSerializable("data", data);
    }


    public interface OnSelectGiftListener {
        void onSelectGift(GiftResponse gift);
    }

}
