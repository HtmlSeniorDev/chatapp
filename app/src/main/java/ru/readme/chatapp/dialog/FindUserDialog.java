package ru.readme.chatapp.dialog;

import android.os.Bundle;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.appcompat.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ProgressBar;
import android.widget.TextView;

import java.util.List;
import java.util.regex.Pattern;

import ru.readme.chatapp.R;
import ru.readme.chatapp.adapter.UsersAdapter;
import ru.readme.chatapp.object.responses.UserResponse;
import ru.readme.chatapp.object.responses.UsersResponse;
import ru.readme.chatapp.helper.FindUserHelper;

public class FindUserDialog extends DialogFragment implements FindUserHelper.OnFindUserHelperListener, View.OnClickListener, UsersAdapter.OnUserClickListener {

    private RecyclerView rvUsers;
    private ImageButton ibFind;
    private EditText edNic;
    private ProgressBar pbLoading;
    private TextView tvNoFound;
    private Toolbar toolbar;
    private OnFindUserListener listener;
    private UsersAdapter adapter;
    private FindUserHelper helper;
    private boolean load = false;

    public void setListener(OnFindUserListener listener) {
        this.listener = listener;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.find_user_dialog, null);
        getDialog().requestWindowFeature(Window.FEATURE_NO_TITLE);
        helper = new FindUserHelper(getActivity(),this);
        rvUsers = (RecyclerView) v.findViewById(R.id.rv_find_user_list);
        RecyclerView.LayoutManager manager = new LinearLayoutManager(getActivity());
        rvUsers.setLayoutManager(manager);
        ibFind = (ImageButton) v.findViewById(R.id.ib_find_user_dialog_find);
        edNic = (EditText) v.findViewById(R.id.ed_fin_ser_dialog_user);
        pbLoading = (ProgressBar) v.findViewById(R.id.pb_find_user_progress);
        tvNoFound = (TextView) v.findViewById(R.id.tv_find_user_dialog_no_found);

        toolbar = (Toolbar) v.findViewById(R.id.find_user_dialog_toolbar);
        toolbar.setTitle(R.string.find_user);
        toolbar.inflateMenu(R.menu.find_user_dialog);
        adapter = new UsersAdapter(getActivity(), this, false);
        rvUsers.setAdapter(adapter);
        toolbar.getMenu().findItem(R.id.menu_find_user_dialog_close).setOnMenuItemClickListener(new MenuItem.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem menuItem) {
                dismiss();
                return false;
            }
        });
        if (savedInstanceState != null && savedInstanceState.containsKey("data")) {
            UsersResponse r = (UsersResponse) savedInstanceState.getSerializable("data");
            for (UserResponse u : r.getUsers()) {
                adapter.add(u);
            }
        }
        ibFind.setOnClickListener(this);
        tvNoFound.setVisibility(View.INVISIBLE);
        pbLoading.setVisibility(View.INVISIBLE);
        return v;
    }


    @Override
    public void onClick(View view) {
        if (view.getId() == R.id.ib_find_user_dialog_find) {
            if(!load && edNic.getText().toString().length()>0){
                String fnd = edNic.getText().toString();
                String wsp = fnd.replaceAll(Pattern.quote(" "),"");
                wsp = wsp.replaceAll(Pattern.quote("\n"),"");
                if(wsp.length()>0) {
                    pbLoading.setVisibility(View.VISIBLE);
                    adapter.clear();
                    tvNoFound.setVisibility(View.INVISIBLE);
                    load = true;
                    helper.findUsers(edNic.getText().toString());
                }
            }
        }
    }

    @Override
    public void onFindUsers(boolean done, List<UserResponse> users) {
        pbLoading.setVisibility(View.INVISIBLE);
        load= false;
        if(users.size()>0){
            for(UserResponse user:users){
                adapter.add(user);
            }
        }else{
            tvNoFound.setVisibility(View.VISIBLE);
        }
    }

    @Override
    public void onUserClick(UserResponse user) {
        if (listener != null) {
            listener.onUserFind(user);
        }
        dismiss();
    }

    @Override
    public void onUserLongClick(UserResponse user) {

    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        UsersResponse r = new UsersResponse();
        r.setUsers(adapter.getElements());
        outState.putSerializable("date", r);
        super.onSaveInstanceState(outState);
    }

    public interface OnFindUserListener {
        void onUserFind(UserResponse user);
    }
}
