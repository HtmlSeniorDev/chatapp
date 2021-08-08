package ru.readme.chatapp.fragment;

import android.Manifest;
import android.content.DialogInterface;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.squareup.picasso.Picasso;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;

import ru.readme.chatapp.R;
import ru.readme.chatapp.adapter.GiftsAdapter;
import ru.readme.chatapp.dialog.AddGiftDialog;
import ru.readme.chatapp.dialog.InfoDialog;
import ru.readme.chatapp.dialog.WaitDialog;
import ru.readme.chatapp.object.requests.GiftRequest;
import ru.readme.chatapp.object.responses.GiftResponse;
import ru.readme.chatapp.object.responses.GiftsResponse;
import ru.readme.chatapp.object.responses.UserResponse;
import ru.readme.chatapp.util.Network;
import ru.readme.chatapp.helper.GiftsHelper;

/**
 * Created by dima on 04.01.17.
 */

public class GiftsFragment extends MyBaseFragment implements GiftsHelper.OnGiftsHelperListener, GiftsAdapter.OnGiftElementClickListener {

    private GiftsAdapter adapter;
    private RecyclerView rvList;
    private ProgressBar pbLoading;
    private WaitDialog waitDialog;
    private GiftsHelper helper;

    private File tempFile = null;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.gifts, null);
        rvList = (RecyclerView) v.findViewById(R.id.rv_gifts_list);
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
        setHasOptionsMenu(true);
        ((AppCompatActivity) getActivity()).getSupportActionBar().setTitle(R.string.gifts);
        requestStoragePermission();
        return v;
    }

    @Override
    public void onGiftElementClick(final GiftResponse giftResponse) {
        final List<Integer> actionsIds = new ArrayList<>();
        List<String> actions = new ArrayList<>();
        actions.add(getString(R.string.edit));
        actionsIds.add(1);
        actionsIds.add(3);
        actions.add(getString(R.string.delete));

        new AlertDialog.Builder(getActivity())
                .setTitle(giftResponse.getName())
                .setAdapter(new ArrayAdapter<>(getActivity(), android.R.layout.simple_list_item_1, actions),
                        new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                if (actionsIds.get(i) == 1) {
                                    tempFile = null;
                                    new AddGiftDialog(GiftsFragment.this, new AddGiftDialog.OnGiftDialogUpdateListener() {
                                        @Override
                                        public void onPrepearGift(String id, String name, String description, int price, File avatar) {
                                            if (name.length() > 0) {
                                                waitDialog.show();
                                                tempFile = avatar;
                                                GiftRequest rq = new GiftRequest();
                                                rq.setDescription(description);
                                                rq.setPrice(price);
                                                rq.setName(name);
                                                rq.setId(id);
                                                helper.updateGift(rq, tempFile);
                                            } else {
                                                InfoDialog.show(R.string.update_gift_title, R.string.add_gift_no_data, getActivity());
                                            }

                                        }
                                    }, giftResponse);
                                } else if (actionsIds.get(i) == 3) {
                                    new AlertDialog.Builder(getActivity())
                                            .setTitle(giftResponse.getName())
                                            .setMessage(R.string.delete_gift_ask)
                                            .setPositiveButton(R.string.yes, new DialogInterface.OnClickListener() {
                                                @Override
                                                public void onClick(DialogInterface dialogInterface, int i) {
                                                    waitDialog.show();
                                                    helper.deleteGift(giftResponse.getId());
                                                }
                                            })
                                            .setNegativeButton(R.string.cancel, null)
                                            .show();
                                }
                            }
                        })
                .show();
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
        waitDialog.hide();
        tempFile = null;
        if (done) {
            adapter.add(response);
            String av = getString(R.string.add_gift_done);
            av = av.replaceAll(Pattern.quote("%"), "\"" + response.getName() + "\"");
            InfoDialog.show(R.string.add_gift_title, av, getActivity());
        } else {
            InfoDialog.show(R.string.add_gift_title, R.string.add_gift_error, getActivity());
        }
    }

    @Override
    public void onUpdateGift(boolean done, GiftResponse response) {
        waitDialog.hide();
        if (done) {
            adapter.add(response);
            Picasso.with(getActivity())
                    .invalidate(Network.giftLink(response.getId()));
            String av = getString(R.string.update_gift_done);
            av = av.replaceAll(Pattern.quote("%"), "\"" + response.getName() + "\"");
            InfoDialog.show(R.string.update_gift_title, av, getActivity());
        } else {
            InfoDialog.show(R.string.update_gift_title, R.string.update_gift_error, getActivity());
        }
    }

    @Override
    public void onDeleteGift(boolean done, String id) {
        waitDialog.hide();
        if (done) {
            InfoDialog.show(R.string.delete_gift, R.string.delete_gift_done, getActivity());
            GiftResponse r = new GiftResponse();
            r.setId(id);
            adapter.remove(r);
        } else {
            InfoDialog.show(R.string.delete_gift, R.string.delete_gift_error, getActivity());
        }
    }

    @Override
    public void onUserLoad(UserResponse user) {

    }

    private void requestStoragePermission() {
        ActivityCompat.requestPermissions(getActivity(), new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, 1);
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        GiftsResponse data = new GiftsResponse();
        data.setGifts(adapter.getElements());
        outState.putSerializable("data", data);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String permissions[], int[] grantResults) {
        switch (requestCode) {
            case 1: {
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    requestStoragePermission();

                } else {
                    InfoDialog.show(R.string.select_file, R.string.no_permission, getActivity());
                }
                return;
            }
        }
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        inflater.inflate(R.menu.gifts, menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == R.id.menu_gifts_add) {
            tempFile = null;
            new AddGiftDialog(this, new AddGiftDialog.OnGiftDialogAddListener() {
                @Override
                public void onPrepearGift(String name, String description, int price, File avatar) {
                    if (name.length() > 0 && avatar != null) {
                        waitDialog.show();
                        tempFile = avatar;
                        GiftRequest rq = new GiftRequest();
                        rq.setName(name);
                        rq.setDescription(description);
                        rq.setPrice(price);
                        helper.addGift(rq, tempFile);
                    } else {
                        InfoDialog.show(R.string.add_gift_title, R.string.add_gift_no_data, getActivity());
                    }
                }
            });
        }
        return super.onOptionsItemSelected(item);
    }
}
