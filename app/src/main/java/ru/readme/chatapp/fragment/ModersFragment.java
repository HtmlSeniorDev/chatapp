package ru.readme.chatapp.fragment;

import android.content.DialogInterface;
import android.os.Bundle;
import androidx.annotation.Nullable;
import androidx.fragment.app.FragmentTransaction;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ProgressBar;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import ru.readme.chatapp.R;
import ru.readme.chatapp.adapter.UsersAdapter;
import ru.readme.chatapp.object.responses.UserResponse;
import ru.readme.chatapp.object.responses.UsersResponse;
import ru.readme.chatapp.helper.ModersHelper;

import static ru.readme.chatapp.activity.MainActivity.TAG_PROFILE;

/**
 * Created by dima on 16.12.16.
 */

public class ModersFragment extends MyBaseFragment implements UsersAdapter.OnUserClickListener, ModersHelper.OnModersHelper {
    private ProgressBar pbLoading;
    private SwipeRefreshLayout srlUpdate;
    private RecyclerView rvUsers;
    private List<UserResponse> users = new ArrayList<>();
    private ModersHelper helper;
    private TextView tvNo;

    private UsersAdapter adapter;

    private boolean load = false;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.moders, null);
        helper = new ModersHelper(getActivity(), this);
        pbLoading = (ProgressBar) v.findViewById(R.id.pb_moders_progress);
        srlUpdate = (SwipeRefreshLayout) v.findViewById(R.id.srl_moders_refresh);
        rvUsers = (RecyclerView) v.findViewById(R.id.rv_moders_list);
        tvNo = (TextView)v.findViewById(R.id.tv_moders_no_moders);
        RecyclerView.LayoutManager manager = new LinearLayoutManager(getActivity());
        DividerItemDecoration dividerItemDecoration = new DividerItemDecoration(rvUsers.getContext(),
                DividerItemDecoration.VERTICAL);

        rvUsers.addItemDecoration(dividerItemDecoration);
        rvUsers.setLayoutManager(manager);

        adapter = new UsersAdapter(getActivity(), this);
        rvUsers.setAdapter(adapter);
        if (savedInstanceState != null) {
            if (savedInstanceState.containsKey("data")) {
                UsersResponse mr = (UsersResponse) savedInstanceState.getSerializable("data");
                onGetUsers(mr.getUsers());
            }
        }
        if (users.size() == 0) {
            load();
        }

        srlUpdate.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                load();
            }
        });
        ((AppCompatActivity) getActivity()).getSupportActionBar().setTitle(R.string.moders);

        return v;
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        UsersResponse sav = new UsersResponse();
        sav.setUsers(users);
        outState.putSerializable("data", sav);
    }

    private void load() {
        tvNo.setVisibility(View.INVISIBLE);
        if (!load) {
            helper.getModers();
            load = true;
        }
    }

    private void showUserProfile(UserResponse user) {
        Bundle args = new Bundle();
        args.putString("user", user.getId());
        ProfileFragment fragment = new ProfileFragment();
        fragment.setArguments(args);
        FragmentTransaction fragmentTransaction = getFragmentManager().beginTransaction();
        fragmentTransaction.setCustomAnimations(android.R.anim.fade_in,
                android.R.anim.fade_out);
        fragmentTransaction
                .replace(R.id.ll_base_layout_content, fragment, TAG_PROFILE)
                .addToBackStack(null)
                .commitAllowingStateLoss();
    }

    @Override
    public void onUserClick(final UserResponse user) {
        final List<Integer> actionsIds = new ArrayList<>();
        List<String> actions = new ArrayList<>();
        actions.add(getString(R.string.show_profile));
        actionsIds.add(1);
        new AlertDialog.Builder(getActivity())
                .setTitle(user.getNic())
                .setAdapter(new ArrayAdapter<>(getActivity(), android.R.layout.simple_list_item_1, actions),
                        new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                if (actionsIds.get(i) == 1) {
                                    showUserProfile(user);
                                }
                            }
                        })
                .show();
    }

    @Override
    public void onUserLongClick(UserResponse user) {

    }

    @Override
    public void onGetUsers(List<UserResponse> users) {
        load = false;
        this.users = users;
        pbLoading.setVisibility(View.INVISIBLE);
        srlUpdate.setRefreshing(false);
        adapter.setElements(users);
        if(users.size()==0){
            tvNo.setVisibility(View.VISIBLE);
        }
    }

}
