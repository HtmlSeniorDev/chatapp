package ru.readme.chatapp.fragment;

import android.content.DialogInterface;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

import ru.readme.chatapp.R;
import ru.readme.chatapp.adapter.UsersAdapter;
import ru.readme.chatapp.object.responses.UserResponse;
import ru.readme.chatapp.object.responses.UsersResponse;
import ru.readme.chatapp.helper.InvisiblesHelper;

import static ru.readme.chatapp.activity.MainActivity.TAG_PROFILE;

/**
 * Created by dima on 16.12.16.
 */

public class InvisiblesFragment extends MyBaseFragment implements UsersAdapter.OnUserClickListener, InvisiblesHelper.OnInvisibleHelper {
    private ProgressBar pbLoading;
    private SwipeRefreshLayout srlUpdate;
    private RecyclerView rvUsers;
    private List<UserResponse> users = new ArrayList<>();
    private InvisiblesHelper helper;
    private TextView tvNo;

    private UsersAdapter adapter;

    private boolean load = false;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.banned, null);
        helper = new InvisiblesHelper(getActivity(), this);
        pbLoading = (ProgressBar) v.findViewById(R.id.pb_banned_progress);
        srlUpdate = (SwipeRefreshLayout) v.findViewById(R.id.srl_banned_refresh);
        rvUsers = (RecyclerView) v.findViewById(R.id.rv_banned_list);
        tvNo = (TextView) v.findViewById(R.id.tv_banned_no_banned);
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
        ((AppCompatActivity) getActivity()).getSupportActionBar().setTitle(R.string.invisbles);

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
            helper.getUsers();
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
        actionsIds.add(3);
        actions.add(getString(R.string.restore));
        new AlertDialog.Builder(getActivity())
                .setTitle(user.getNic())
                .setAdapter(new ArrayAdapter<>(getActivity(), android.R.layout.simple_list_item_1, actions),
                        new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                if (actionsIds.get(i) == 1) {
                                    showUserProfile(user);
                                } else if (actionsIds.get(i) == 3) {
                                    new AlertDialog.Builder(getActivity())
                                            .setTitle(user.getNic())
                                            .setMessage(R.string.restore_ask)
                                            .setPositiveButton(R.string.yes, new DialogInterface.OnClickListener() {
                                                @Override
                                                public void onClick(DialogInterface dialogInterface, int i) {
                                                    helper.onUnSet(user.getId());
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
    public void onUserLongClick(UserResponse user) {

    }


    @Override
    public void onUnSet(boolean done) {
        if (getActivity() != null && done) {
            Toast.makeText(getActivity(), R.string.user_unset_invis_done, Toast.LENGTH_LONG).show();
        } else if (getActivity() != null) {
            Toast.makeText(getActivity(), R.string.user_unset_invis_error, Toast.LENGTH_LONG).show();
        }
        srlUpdate.setRefreshing(true);
        load();
    }

    @Override
    public void onGetUsers(List<UserResponse> users) {
        load = false;
        this.users = users;
        pbLoading.setVisibility(View.INVISIBLE);
        srlUpdate.setRefreshing(false);
        adapter.setElements(users);
        if (users.size() == 0) {
            tvNo.setVisibility(View.VISIBLE);
        }
    }


}
