package ru.readme.chatapp.fragment;

import android.os.Bundle;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.TextView;

import ru.readme.chatapp.R;
import ru.readme.chatapp.helper.RulesHelper;

public class RulesFragment extends MyBaseFragment implements RulesHelper.OnRulesHelper {
    private ProgressBar pbLoading;
   // private SwipeRefreshLayout srlUpdate;
    private RulesHelper helper;
    private TextView tvNo;

    private boolean load = false;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.rules, null);
        helper = new RulesHelper(getActivity(), this);
        pbLoading = (ProgressBar) v.findViewById(R.id.pb_rules_progress);
      //  srlUpdate = (SwipeRefreshLayout) v.findViewById(R.id.srl_rules_refresh);
        tvNo = (TextView)v.findViewById(R.id.tv_rules_no_rules);

           load();

       /* srlUpdate.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                load();
            }
        });*/

        ((AppCompatActivity) getActivity()).getSupportActionBar().setTitle(R.string.rules);

        return v;
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
      //  UsersResponse sav = new UsersResponse();
      //  sav.setUsers(users);
     //   outState.putSerializable("data", sav);
    }

    private void load() {
        tvNo.setVisibility(View.INVISIBLE);
        if (!load) {
            helper.getRules();
            load = true;
        }
    }

    @Override
    public void onGetRules(String s) {
        load = false;
        pbLoading.setVisibility(View.INVISIBLE);
     //   srlUpdate.setRefreshing(false);
        tvNo.setText(s);
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.N) {
            tvNo.setText(Html.fromHtml(s,Html.FROM_HTML_MODE_LEGACY));
        } else {
            tvNo.setText(Html.fromHtml(s));
        }
        tvNo.setVisibility(View.VISIBLE);
    }

}
