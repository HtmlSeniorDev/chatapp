package ru.readme.chatapp.fragment;

import android.content.DialogInterface;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;

import ru.readme.chatapp.activity.MainActivity;
import ru.readme.chatapp.R;
import ru.readme.chatapp.activity.ZagsActivity;
import ru.readme.chatapp.adapter.UsersAdapter;
import ru.readme.chatapp.dialog.AskDialog;
import ru.readme.chatapp.dialog.FindUserDialog;
import ru.readme.chatapp.dialog.InfoDialog;
import ru.readme.chatapp.dialog.WaitDialog;
import ru.readme.chatapp.object.responses.UserResponse;
import ru.readme.chatapp.helper.ZagsHelper;

/**
 * Created by dima on 05.01.17.
 */

public class ZagsFragment extends MyBaseFragment implements View.OnClickListener, FindUserDialog.OnFindUserListener, ZagsHelper.ZagsActionListener, UsersAdapter.OnUserClickListener {

    private UserResponse me;

    private RelativeLayout rlParent;
    private LinearLayout l1, l2, l3;
    private View v1, v2, v3, s1, s2, s3, ss1, ss2, ss3;
    private TextView tvCur, tvPrice;
    private Button btEnd, btSend;
    private ImageButton ibFind;
    private EditText edUser;
    private RecyclerView rvRequests;

    private UserResponse findUser = null;
    private ZagsHelper helper;
    private UsersAdapter adapter;

    private WaitDialog waitDialog;

    private UserResponse cur = null;


    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.zags_fragment, null);
        helper = new ZagsHelper(getActivity(), this);
        waitDialog = new WaitDialog(getActivity());
        l1 = (LinearLayout) v.findViewById(R.id.l1);
        l2 = (LinearLayout) v.findViewById(R.id.l2);
        l3 = (LinearLayout) v.findViewById(R.id.l3);
        v1 = v.findViewById(R.id.v1);
        v2 = v.findViewById(R.id.v2);
        v3 = v.findViewById(R.id.v3);
        s1 = v.findViewById(R.id.s1);
        ss1 = v.findViewById(R.id.ss1);
        s2 = v.findViewById(R.id.s2);
        ss2 = v.findViewById(R.id.ss2);
        s3 = v.findViewById(R.id.s3);
        ss3 = v.findViewById(R.id.ss3);
        tvCur = (TextView) v.findViewById(R.id.tv_zags_fragment_brak);
        tvPrice = (TextView) v.findViewById(R.id.tv_zags_price);
        btEnd = (Button) v.findViewById(R.id.bt_zags_end);
        btSend = (Button) v.findViewById(R.id.bt_zags_send);
        ibFind = (ImageButton) v.findViewById(R.id.ib_zags_find);
        edUser = (EditText) v.findViewById(R.id.ed_zags_user);
        rvRequests = (RecyclerView) v.findViewById(R.id.rv_zags_incoming);
        rlParent = (RelativeLayout) v.findViewById(R.id.rl_zags_parent);
        btSend.setOnClickListener(this);
        btEnd.setOnClickListener(this);
        ibFind.setOnClickListener(this);

        adapter = new UsersAdapter(getActivity(), this);
        LinearLayoutManager manager = new LinearLayoutManager(getActivity());
        rvRequests.setLayoutManager(manager);
        rvRequests.setAdapter(adapter);

        if (savedInstanceState != null && savedInstanceState.containsKey("me")) {
            me = (UserResponse) savedInstanceState.getSerializable("me");
        } else if (getArguments() != null && getArguments().containsKey("me")) {
            me = (UserResponse) getArguments().getSerializable("me");
        }
        if(savedInstanceState==null && getArguments()!=null && getArguments().containsKey("user")){
            findUser = (UserResponse)getArguments().getSerializable("user");
            edUser.setText(findUser.getNic());
        }
        if (me == null) {
            waitDialog.show();
            helper.loadUser();
        } else {
            initContent();
        }


        ((AppCompatActivity) getActivity()).getSupportActionBar().setTitle(R.string.virt_zags);
        return v;
    }

    private void initContent() {
        if (me.getZags() == null) {
            tvPrice.setText("");
            helper.getPrice();
            rlParent.removeView(l1);
            rlParent.removeView(s1);
            rlParent.removeView(ss1);
            rlParent.removeView(v1);
            if (me.getZagsRequest() == null || me.getZagsRequest().size() == 0) {
                rlParent.removeView(l3);
                rlParent.removeView(s3);
                rlParent.removeView(ss3);
                rlParent.removeView(v3);
            } else {
                for (UserResponse ur : me.getZagsRequest()) {
                    adapter.add(ur);
                }
            }
        } else {
            rlParent.removeView(l2);
            rlParent.removeView(s2);
            rlParent.removeView(ss2);
            rlParent.removeView(v2);
            rlParent.removeView(l3);
            rlParent.removeView(s3);
            rlParent.removeView(ss3);
            rlParent.removeView(v3);
            tvCur.setText(me.getZags().getNic());
            tvCur.setTextColor(me.getZags().getColor());
        }
    }

    @Override
    public void onClick(View view) {
        if (view.getId() == R.id.bt_zags_end) {
            AskDialog.show(getActivity(), R.string.virt_zags, R.string.end_brak_ask, new AskDialog.OnAskDialogClickListener() {
                @Override
                public void onAskClick(boolean acept) {
                    if(acept) {
                        waitDialog.show();
                        helper.endBrak();
                    }
                }

            });
        } else if (view.getId() == R.id.bt_zags_send) {
            if (findUser != null && !findUser.getId().equals(me.getId())) {
                waitDialog.show();
                helper.sendRequest(findUser.getId());
            }
        } else if (view.getId() == R.id.ib_zags_find) {
            FindUserDialog d = new FindUserDialog();
            d.setListener(this);
            d.show(getFragmentManager(), "find_user");
        }
    }

    @Override
    public void onUserFind(UserResponse user) {
        findUser = user;
        edUser.setText(findUser.getNic());
    }

    @Override
    public void onGetPrice(boolean done, int price) {
        String text = getString(R.string.zags_price);
        text = text.replaceAll(Pattern.quote("%"), "" + (price / 100));
        tvPrice.setText(text);
    }

    @Override
    public void onCancelRequest(boolean done) {
        waitDialog.hide();
        if (done) {
            adapter.delete(cur);
        }
        cur = null;
    }

    @Override
    public void onSendRequest(boolean done) {
        waitDialog.hide();
        if (cur != null) {
            cur = null;
            if (done) {
               reload();
            }
        } else if (done) {
            findUser = null;
            edUser.setText("");
            InfoDialog.show(R.string.virt_zags, R.string.virt_zags_send_request_done, getActivity());
        } else {
            InfoDialog.show(R.string.virt_zags, R.string.virt_zags_send_request_error, getActivity());
        }
    }

    @Override
    public void onUnBrak(boolean done) {
        waitDialog.hide();
        if (done) {
           reload();
        }
    }

    private void reload(){
        if(getActivity() instanceof MainActivity){
            ((MainActivity) getActivity()).setupVirtZagsFragment();
        }else if(getActivity() instanceof ZagsActivity){
            ((ZagsActivity) getActivity()).reload();
        }
    }

    @Override
    public void onUserLoad(UserResponse user) {
        waitDialog.hide();
        if (user != null) {
            me = user;
            initContent();
        } else {
            doBack();
        }
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        outState.putSerializable("me", me);
        super.onSaveInstanceState(outState);
    }

    @Override
    public void onUserClick(final UserResponse user) {
        final List<Integer> actionsIds = new ArrayList<>();
        List<String> actions = new ArrayList<>();
        actions.add(getString(R.string.acept));
        actionsIds.add(1);
        actionsIds.add(3);
        actions.add(getString(R.string.reject));
        new AlertDialog.Builder(getActivity())
                .setTitle(user.getNic())
                .setAdapter(new ArrayAdapter<>(getActivity(), android.R.layout.simple_list_item_1, actions),
                        new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                if (actionsIds.get(i) == 1) {
                                    waitDialog.show();
                                    cur = user;
                                    helper.sendRequest(user.getId());
                                } else if (actionsIds.get(i) == 3) {
                                    cur = user;
                                    waitDialog.show();
                                    helper.cancelRequest(user.getId());
                                }
                            }
                        })
                .show();
    }

    @Override
    public void onUserLongClick(UserResponse user) {

    }
}
