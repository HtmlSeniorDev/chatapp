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
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

import ru.readme.chatapp.R;
import ru.readme.chatapp.adapter.UsersAdapter;
import ru.readme.chatapp.object.responses.UserResponse;
import ru.readme.chatapp.object.responses.UsersResponse;
import ru.readme.chatapp.helper.BannedHelper;

import static ru.readme.chatapp.activity.MainActivity.TAG_PROFILE;

public class BannedFragment extends MyBaseFragment implements UsersAdapter.OnUserClickListener, BannedHelper.OnBannedHelper {
    private ProgressBar pbLoading;
    private SwipeRefreshLayout srlUpdate;
    private RecyclerView rvUsers;
    private List<UserResponse> users = new ArrayList<>();
    private BannedHelper helper;
    private TextView tvNo;
    private UsersAdapter adapter;
    private boolean load = false;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.banned, null);
        helper = new BannedHelper(getActivity(), this);
        pbLoading = (ProgressBar) v.findViewById(R.id.pb_banned_progress);
        srlUpdate = (SwipeRefreshLayout) v.findViewById(R.id.srl_banned_refresh);
        rvUsers = (RecyclerView) v.findViewById(R.id.rv_banned_list);
        tvNo = (TextView)v.findViewById(R.id.tv_banned_no_banned);
        RecyclerView.LayoutManager manager = new LinearLayoutManager(getActivity());
        DividerItemDecoration dividerItemDecoration = new DividerItemDecoration(rvUsers.getContext(), DividerItemDecoration.VERTICAL);
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
        ((AppCompatActivity) getActivity()).getSupportActionBar().setTitle(R.string.banned);

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
            helper.getBannedUsers();
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
        actions.add(getString(R.string.unban_user));
        actionsIds.add(4);
        actions.add(getString(R.string.superban_user));
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
                                            .setMessage(R.string.unban_ask)
                                            .setPositiveButton(R.string.yes, new DialogInterface.OnClickListener() {
                                                @Override
                                                public void onClick(DialogInterface dialogInterface, int i) {
                                                    helper.unBanUser(user.getId());
                                                }
                                            })
                                            .setNegativeButton(R.string.cancel, null)
                                            .show();
                                }else if (actionsIds.get(i) == 4) {
                                    new AlertDialog.Builder(getActivity())
                                            .setTitle(user.getNic())
                                            .setMessage(R.string.superban_ask)
                                            .setPositiveButton(R.string.yes, new DialogInterface.OnClickListener() {
                                                @Override
                                                public void onClick(DialogInterface dialogInterface, int i) {
                                                    helper.superban(user.getId());
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
    public void onUbBanned(boolean done) {
        if (getActivity() != null && done) {
            Toast.makeText(getActivity(), R.string.user_unbanned_done, Toast.LENGTH_LONG).show();
        } else if (getActivity() != null) {
            Toast.makeText(getActivity(), R.string.user_unbanned_error, Toast.LENGTH_LONG).show();
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
        if(users.size()==0){
            tvNo.setVisibility(View.VISIBLE);
        }
    }

    @Override
    public void onSuperBan(boolean done) {
        if (getActivity() != null && done) {
            Toast.makeText(getActivity(), R.string.user_superban_done, Toast.LENGTH_LONG).show();
        } else if (getActivity() != null) {
            Toast.makeText(getActivity(), R.string.user_superban_error, Toast.LENGTH_LONG).show();
        }
        srlUpdate.setRefreshing(true);
        load();
    }
}
