package ru.readme.chatapp.fragment;

import android.content.Intent;
import android.os.Bundle;
import androidx.annotation.Nullable;
import androidx.fragment.app.FragmentTransaction;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;

import ru.readme.chatapp.activity.MainActivity;
import ru.readme.chatapp.R;
import ru.readme.chatapp.adapter.ChatsAdapter;
import ru.readme.chatapp.dialog.AskDialog;
import ru.readme.chatapp.dialog.InfoDialog;
import ru.readme.chatapp.dialog.WaitDialog;
import ru.readme.chatapp.object.responses.CategoryResponse;
import ru.readme.chatapp.object.responses.PMChatResponse;
import ru.readme.chatapp.object.responses.PMChatsResponse;
import ru.readme.chatapp.object.responses.RoomResponse;
import ru.readme.chatapp.util.SettingsHelper;
import ru.readme.chatapp.helper.ChatsPMHelper;

import static ru.readme.chatapp.activity.MainActivity.TAG_CHAT;

/**
 * Created by dima on 16.11.16.
 */

public class ChatsPMFragment extends MyBaseFragment implements ChatsAdapter.OnChatsClickListener, ChatsPMHelper.OnChatsPMHelperListener {

    private SettingsHelper settingsHelper;
    private WaitDialog waitDialog;
    private ChatsPMHelper helper;


    private SwipeRefreshLayout srlRefresh;
    private RecyclerView rvList;
    private ChatsAdapter adapter;
    private boolean loading = false;
    private PMChatsResponse data = null;
    private ProgressBar pbLoad;


    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        settingsHelper = new SettingsHelper(getActivity());
        helper = new ChatsPMHelper(getActivity(), this);
        waitDialog = new WaitDialog(getActivity());
        adapter = new ChatsAdapter(getActivity(), this);
        if (savedInstanceState != null) {
            data = (PMChatsResponse) savedInstanceState.getSerializable("data");
        }
        View v = inflater.inflate(R.layout.chats, null);
        srlRefresh = (SwipeRefreshLayout) v.findViewById(R.id.srl_chats_refresh);
        rvList = (RecyclerView) v.findViewById(R.id.rv_chats_list);
        pbLoad = (ProgressBar)v.findViewById(R.id.pb_chats_progress);
        RecyclerView.LayoutManager manager = new LinearLayoutManager(getActivity());
        DividerItemDecoration dividerItemDecoration = new DividerItemDecoration(rvList.getContext(),
                DividerItemDecoration.VERTICAL);

        rvList.addItemDecoration(dividerItemDecoration);
        rvList.setLayoutManager(manager);
        rvList.setAdapter(adapter);
        srlRefresh.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                if (!loading) {
                    loading = true;
                    helper.getChats();
                }
            }
        });
        if (savedInstanceState == null) {
            firstLoad();
        } else {
            setContent(data);
        }
        if(getActivity() instanceof MainActivity) {
            ((AppCompatActivity) getActivity()).getSupportActionBar().setTitle(R.string.pm_messages);
        }
        return v;
    }




    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putSerializable("data", data);
    }


    private void firstLoad() {
        loading = true;
        helper.getChats();
    }


    private void setContent(PMChatsResponse response) {
        if(response!=null && response.getChats()!=null) {
            adapter.clear();
            data = response;
            for (PMChatResponse cr : response.getChats()) {
                adapter.add(cr);
            }
        }
    }


    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 1) {
            if (resultCode == 2) {
                getActivity().setResult(2);
                getActivity().finish();
            } else {
                firstLoad();
            }
        }
    }


    @Override
    public void onGetMessages(boolean create, PMChatsResponse response) {
        waitDialog.hide();
        pbLoad.setVisibility(View.INVISIBLE);
        srlRefresh.setRefreshing(false);
        loading = false;
        if (create) {
            setContent(response);
        }
    }

    @Override
    public void onLeave(boolean done, String id) {
        waitDialog.hide();
        if(done){
            adapter.deleteElement(id);
        }else{
            InfoDialog.show(R.string.leave_pm,R.string.leave_pm_error, getActivity());
        }
    }

    @Override
    public void onRoomClick(RoomResponse room) {

    }

    @Override
    public void onPersonalChatClickl(PMChatResponse chat) {
        Bundle args = new Bundle();
        args.putSerializable("chat", chat);
        ChatFragment fragment = new ChatFragment();
        fragment.setArguments(args);
        FragmentTransaction fragmentTransaction = getFragmentManager().beginTransaction();
        fragmentTransaction.setCustomAnimations(android.R.anim.fade_in,
                android.R.anim.fade_out);
        if(getActivity() instanceof MainActivity) {
            fragmentTransaction
                    .replace(R.id.ll_base_main_layout_content, fragment, TAG_CHAT)
                    .addToBackStack(TAG_CHAT)
                    .commitAllowingStateLoss();
        }else{
            fragmentTransaction
                    .replace(R.id.ll_base_layout_content, fragment, TAG_CHAT)
                    .addToBackStack(TAG_CHAT)
                    .commitAllowingStateLoss();
        }
    }

    @Override
    public void onPersonalChatLongClickl(final PMChatResponse chat) {
        AskDialog.show(getActivity(), R.string.leave_pm, R.string.leave_pm_ask, new AskDialog.OnAskDialogClickListener() {
            @Override
            public void onAskClick(boolean acept) {
                if(acept){
                    waitDialog.show();
                    helper.leave(chat.getId());
                }
            }
        });
    }

    @Override
    public void onCategoryClick(CategoryResponse category) {

    }
}
