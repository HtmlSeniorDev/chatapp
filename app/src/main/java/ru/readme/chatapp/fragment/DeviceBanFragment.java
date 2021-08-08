package ru.readme.chatapp.fragment;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import ru.readme.chatapp.activity.MainActivity;
import ru.readme.chatapp.R;
import ru.readme.chatapp.adapter.DeviceBanAdapter;
import ru.readme.chatapp.dialog.AskDialog;
import ru.readme.chatapp.dialog.InfoDialog;
import ru.readme.chatapp.dialog.WaitDialog;
import ru.readme.chatapp.object.responses.DeviceBansResponse;
import ru.readme.chatapp.helper.DeviceBanHelper;

/**
 * Created by dima on 23.01.17.
 */

public class DeviceBanFragment extends MyBaseFragment implements DeviceBanHelper.OnDeviceBanHelperListener, DeviceBanAdapter.OnDeviceClickListener {

    private DeviceBanHelper helper;
    private RecyclerView rvList;
    private ProgressBar pbLoading;
    private TextView tvNo;
    private DeviceBanAdapter adapter;
    private WaitDialog waitDialog;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.bannedevice_d, null);
        helper = new DeviceBanHelper(getActivity(),this);
        rvList = (RecyclerView) v.findViewById(R.id.rv_banned_list);
        tvNo = (TextView) v.findViewById(R.id.tv_banned_no_banned);
        pbLoading = (ProgressBar) v.findViewById(R.id.pb_banned_progress);
        tvNo.setVisibility(View.INVISIBLE);
        waitDialog = new WaitDialog(getActivity());
        adapter = new DeviceBanAdapter(getActivity(), this);
        RecyclerView.LayoutManager manager = new LinearLayoutManager(getActivity());
        rvList.setLayoutManager(manager);
        rvList.setAdapter(adapter);
        if (savedInstanceState != null && savedInstanceState.containsKey("data")) {
            DeviceBansResponse dbr = (DeviceBansResponse) savedInstanceState.getSerializable("data");
            if (dbr.getIds().size() > 0) {
                onGetList(dbr.getIds());
            } else {
                helper.getList();
            }
        } else {
            helper.getList();
        }
        if(getActivity()instanceof MainActivity){
            getActivity().setTitle(R.string.banned_devices);
        }
        return v;
    }

    @Override
    public void onGetList(List<String> devices) {
        pbLoading.setVisibility(View.INVISIBLE);
        if (devices != null && devices.size() > 0) {
            tvNo.setVisibility(View.INVISIBLE);
            for (String id : devices) {
                adapter.add(id);
            }
        } else {
            tvNo.setVisibility(View.VISIBLE);
        }
    }

    @Override
    public void onUnban(boolean done, String id) {
        waitDialog.hide();
        if (done) {
            adapter.delete(id);
            InfoDialog.show(R.string.unban_device, R.string.unban_device_done, getActivity());
        } else {
            InfoDialog.show(R.string.unban_device, R.string.unban_device_error, getActivity());
        }
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        List<String> elements = new ArrayList<>();
        if (adapter != null) {
            elements = adapter.getElements();
        }
        DeviceBansResponse dbr = new DeviceBansResponse();
        dbr.setIds(elements);
        outState.putSerializable("data", dbr);
        super.onSaveInstanceState(outState);
    }

    @Override
    public void onDeviceClick(final String devId) {
        AskDialog.show(getActivity(), R.string.unban_device, R.string.unban__device_ask, new AskDialog.OnAskDialogClickListener() {
            @Override
            public void onAskClick(boolean acept) {
                if (acept) {
                    waitDialog.show();
                    helper.unban(devId);
                }
            }
        });
    }
}
