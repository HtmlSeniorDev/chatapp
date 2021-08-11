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
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;

import ru.readme.chatapp.R;
import ru.readme.chatapp.adapter.ChatsAdapter;
import ru.readme.chatapp.dialog.AskDialog;
import ru.readme.chatapp.dialog.CreateCategoryDialog;
import ru.readme.chatapp.dialog.CreateRoomDialog;
import ru.readme.chatapp.dialog.EditCategoryDialog;
import ru.readme.chatapp.dialog.InfoDialog;
import ru.readme.chatapp.dialog.WaitDialog;
import ru.readme.chatapp.object.UserType;
import ru.readme.chatapp.object.responses.CategoryResponse;
import ru.readme.chatapp.object.responses.PMChatResponse;
import ru.readme.chatapp.object.responses.RoomResponse;
import ru.readme.chatapp.object.responses.RoomsResponse;
import ru.readme.chatapp.util.SettingsHelper;
import ru.readme.chatapp.helper.ChatsHelper;

import static ru.readme.chatapp.activity.MainActivity.TAG_CHAT;
import static ru.readme.chatapp.activity.MainActivity.TAG_CHATS;

/**
 * Created by dima on 16.11.16.
 */

public class ChatsFragment extends MyBaseFragment implements EditCategoryDialog.OnEditCategoryClickListener,
        ChatsAdapter.OnChatsClickListener, CreateCategoryDialog.OnCreateCategoryClickListener,
        ChatsHelper.OnChatsHelperListener, CreateRoomDialog.OnCreateRoomClickListener {

    private SettingsHelper settingsHelper;
    private WaitDialog waitDialog;
    private ChatsHelper helper;

    private String category = "-1";

    private SwipeRefreshLayout srlRefresh;
    private RecyclerView rvList;
    private ChatsAdapter adapter;
    private boolean loading = false;
    private RoomsResponse data = null;
    private CategoryResponse cat = null;
    private CategoryResponse chacheCat = null;
    private ProgressBar pbLoading;


    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        settingsHelper = new SettingsHelper(getActivity());
        helper = new ChatsHelper(getActivity(), this);
        setHasOptionsMenu(true);
        waitDialog = new WaitDialog(getActivity());
        adapter = new ChatsAdapter(getActivity(), this);
        if (savedInstanceState != null) {
            category = savedInstanceState.getString("category", "-1");
            data = (RoomsResponse) savedInstanceState.getSerializable("data");
            cat = (CategoryResponse) savedInstanceState.getSerializable("cat");
        } else if (getArguments() != null) {
            cat = (CategoryResponse) getArguments().getSerializable("cat");
            category = getArguments().getString("category", "-1");
        }
        View v = inflater.inflate(R.layout.chats, null);
        srlRefresh = (SwipeRefreshLayout) v.findViewById(R.id.srl_chats_refresh);
        rvList = (RecyclerView) v.findViewById(R.id.rv_chats_list);
        RecyclerView.LayoutManager manager = new LinearLayoutManager(getActivity());
        DividerItemDecoration dividerItemDecoration = new DividerItemDecoration(rvList.getContext(),
                DividerItemDecoration.VERTICAL);

        pbLoading = (ProgressBar) v.findViewById(R.id.pb_chats_progress);
        pbLoading.setVisibility(View.INVISIBLE);
        rvList.addItemDecoration(dividerItemDecoration);
        rvList.setLayoutManager(manager);
        rvList.setAdapter(adapter);
        srlRefresh.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                if (!loading) {
                    loading = true;
                    helper.getRooms(category);
                }
            }
        });
        if (cat != null) {
            ((AppCompatActivity) getActivity()).getSupportActionBar().setTitle(cat.getName());
        } else {
            ((AppCompatActivity) getActivity()).getSupportActionBar().setTitle(getString(R.string.chats));
        }
        if (savedInstanceState == null || data == null) {
            firstLoad();
        } else {
            setContent(data);
        }
        return v;
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        Log.e("App", settingsHelper.getUserType() + " = userType");
        if (settingsHelper.getUserType() == UserType.TYPE_ADMIN) {
            inflater.inflate(R.menu.chats_admin, menu);
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == R.id.menu_chats_add_category) {
            CreateCategoryDialog.show(getActivity(), category, this);
        } else if (item.getItemId() == R.id.menu_chats_add_chat) {
            CreateRoomDialog.show(getActivity(), category, this);
        } else if (item.getItemId() == R.id.menu_chats_edit_category) {
            if (cat != null) {
                EditCategoryDialog.show(getActivity(), cat, this);
            } else {
                InfoDialog.show(R.string.edited_category, R.string.cant_edit_category, getActivity());
            }
        } else if (item.getItemId() == R.id.menu_chats_edit_category) {
            if (cat != null) {
                EditCategoryDialog.show(getActivity(), cat, this);
            } else {
                InfoDialog.show(R.string.edited_category, R.string.cant_edit_category, getActivity());
            }
        } else if (item.getItemId() == R.id.menu_chats_delete_category) {
            if (cat != null) {
                AskDialog.show(getActivity(), getString(R.string.deleted_category), getString(R.string.ask_delete_category) + " " + cat.getName() + "?"
                        , new AskDialog.OnAskDialogClickListener() {
                            @Override
                            public void onAskClick(boolean acept) {
                                if (acept) {
                                    waitDialog.show();
                                    helper.deleteCategory(cat.getId());
                                }
                            }
                        });
            } else {
                InfoDialog.show(R.string.deleted_category, R.string.cant_delete_category, getActivity());
            }
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onCreateCategoryClick(CreateCategoryDialog.CreateCategory category) {
        waitDialog.show();
        helper.createCategory(category, this.category);
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putString("category", category);
        outState.putSerializable("data", data);
        outState.putSerializable("cat", cat);
    }

    @Override
    public void onCreateCategory(boolean create, RoomsResponse response) {
        waitDialog.hide();
        if (create) {
            setContent(response);
        }
    }

    private void firstLoad() {
        pbLoading.setVisibility(View.VISIBLE);
        loading = true;
        helper.getRooms(category);
    }


    @Override
    public void onGetRooms(boolean get, RoomsResponse response) {
        pbLoading.setVisibility(View.INVISIBLE);
        loading = false;
        if (get) {
            setContent(response);
        }
        srlRefresh.setRefreshing(false);

    }

    private void setContent(RoomsResponse response) {
        adapter.clear();
        data = response;
        for (CategoryResponse cr : response.getCategories()) {
            adapter.add(cr);
        }
        for (RoomResponse rr : response.getRooms()) {
            adapter.add(rr);
        }
        if (settingsHelper.getUserType() == UserType.TYPE_BANNED) {
            if (adapter.getItemCount() > 0) {
                Object o = adapter.getFirstElement();
                if (o != null) {
                    if (o instanceof RoomResponse) {
                        onRoomClick((RoomResponse) o);
                    } else if (o instanceof CategoryResponse) {
                        onCategoryClick((CategoryResponse) o);
                    }
                }
            }
        }
    }


    @Override
    public void onCreateRoom(boolean create, RoomsResponse response) {
        waitDialog.hide();
        if (create) {
            setContent(response);
        }
    }

    @Override
    public void onEditCategory(boolean status) {
        waitDialog.hide();
        if (status) {
            cat = chacheCat;
            chacheCat = null;
            ((AppCompatActivity) getActivity()).getSupportActionBar().setTitle(cat.getName());
            firstLoad();
        } else {
            InfoDialog.show(R.string.edited_category, R.string.error_edit_category, getActivity());
        }

    }

    @Override
    public void onDeleteCategory(boolean status) {
        waitDialog.hide();
        if (status) {
            cat = chacheCat;
            chacheCat = null;
            getActivity().onBackPressed();
        } else {
            InfoDialog.show(R.string.deleted_category, R.string.error_delete_category, getActivity());
        }
    }

    @Override
    public void onCreateRoomClick(CreateRoomDialog.CreateChat chat) {
        waitDialog.show();
        helper.createRoom(chat, category);
    }

    @Override
    public void onRoomClick(RoomResponse room) {
        Bundle args = new Bundle();
        args.putSerializable("room", room);
        ChatFragment fragment = new ChatFragment();
        fragment.setArguments(args);
        if(getFragmentManager()!=null) {
            FragmentTransaction fragmentTransaction = getFragmentManager().beginTransaction();
            fragmentTransaction.setCustomAnimations(android.R.anim.fade_in,
                    android.R.anim.fade_out);
            fragmentTransaction
                    .replace(R.id.ll_base_main_layout_content, fragment, TAG_CHAT)
                    .addToBackStack(TAG_CHAT)
                    .commitAllowingStateLoss();
        }
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
        fragmentTransaction
                .replace(R.id.ll_base_main_layout_content, fragment, TAG_CHAT)
                .addToBackStack(TAG_CHAT)
                .commitAllowingStateLoss();
    }

    @Override
    public void onPersonalChatLongClickl(PMChatResponse chat) {

    }

    @Override
    public void onCategoryClick(CategoryResponse category) {
        Bundle args = new Bundle();
        args.putString("category", category.getId());
        args.putString("name", category.getName());
        args.putSerializable("cat", category);
        ChatsFragment fragment = new ChatsFragment();
        fragment.setArguments(args);
        FragmentTransaction fragmentTransaction = getFragmentManager().beginTransaction();
        fragmentTransaction.setCustomAnimations(android.R.anim.fade_in,
                android.R.anim.fade_out);
        fragmentTransaction
                .replace(R.id.ll_base_main_layout_content, fragment, TAG_CHATS)
                .addToBackStack(TAG_CHATS)
                .commitAllowingStateLoss();

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
    public void onEditCategoryClick(CategoryResponse categoryResponse) {
        waitDialog.show();
        chacheCat = categoryResponse;
        helper.editCategory(categoryResponse);
    }
}
