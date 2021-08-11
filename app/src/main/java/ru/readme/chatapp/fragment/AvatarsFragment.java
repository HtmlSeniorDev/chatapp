package ru.readme.chatapp.fragment;

import android.Manifest;
import android.content.DialogInterface;
import android.content.pm.PackageManager;
import android.os.Bundle;
import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ProgressBar;
import android.widget.Toast;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;

import ru.readme.chatapp.R;
import ru.readme.chatapp.adapter.AvatarsAdapter;
import ru.readme.chatapp.dialog.AddAvatarDialog;
import ru.readme.chatapp.dialog.InfoDialog;
import ru.readme.chatapp.dialog.WaitDialog;
import ru.readme.chatapp.object.UserType;
import ru.readme.chatapp.object.requests.AvatarRequest;
import ru.readme.chatapp.object.responses.AvatarResponse;
import ru.readme.chatapp.object.responses.AvatarsResponse;
import ru.readme.chatapp.helper.AvatarsHelper;
import ru.readme.chatapp.helper.AvatarsLoader;

public class AvatarsFragment extends MyBaseFragment implements AvatarsAdapter.OnAvatarElementClickListener, AvatarsHelper.OnAvatarHelperListener {

    private AvatarsAdapter adapter;
    private RecyclerView rvList;
    private ProgressBar pbLoading;
    private WaitDialog waitDialog;
    private AvatarsHelper helper;
    private File tempFile = null;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.avatars, null);
        helper = new AvatarsHelper(getActivity(), this);
        waitDialog = new WaitDialog(getActivity());
        adapter = new AvatarsAdapter(getActivity(), this);
        pbLoading = (ProgressBar) v.findViewById(R.id.pb_avatars_progress);
        RecyclerView.LayoutManager manager = new GridLayoutManager(getActivity(), 3);
        rvList = (RecyclerView) v.findViewById(R.id.rv_avatars_list);
        rvList.setLayoutManager(manager);
        rvList.setAdapter(adapter);
        if (savedInstanceState != null && savedInstanceState.containsKey("data")) {
            AvatarsResponse data = (AvatarsResponse) savedInstanceState.getSerializable("data");
            for (AvatarResponse a : data.getAvatars()) {
                adapter.add(a);
            }
        }
        if (adapter.getItemCount() == 0) {
            helper.getAvatars();
        } else {
            pbLoading.setVisibility(View.INVISIBLE);
        }
        setHasOptionsMenu(true);
        ((AppCompatActivity) getActivity()).getSupportActionBar().setTitle(R.string.avatars);
        requestStoragePermission();
        return v;
    }


    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        inflater.inflate(R.menu.avatars, menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == R.id.menu_avatars_add) {
            tempFile = null;
            new AddAvatarDialog(this, new AddAvatarDialog.OnAvatarDialogAddListener() {
                @Override
                public void onPrepearAvatar(String name, int price, File avatar) {
                    if (name.length() > 0 && avatar != null) {
                        waitDialog.show();
                        tempFile = avatar;
                        helper.add(name, price);
                    } else {
                        InfoDialog.show(R.string.add_avatar, R.string.add_avatar_no_data, getActivity());
                    }
                }
            });
        }
        return super.onOptionsItemSelected(item);
    }

    private void requestStoragePermission() {
        ActivityCompat.requestPermissions(getActivity(), new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, 1);
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
    public void onAvatarElementClick(final AvatarResponse avatarResponse) {
        final List<Integer> actionsIds = new ArrayList<>();
        List<String> actions = new ArrayList<>();
        actions.add(getString(R.string.edit));
        actionsIds.add(1);
        actionsIds.add(4);
        actions.add(getString(R.string.set_default_for_group));
        actionsIds.add(3);
        actions.add(getString(R.string.delete));

        new AlertDialog.Builder(getActivity())
                .setTitle(avatarResponse.getName())
                .setAdapter(new ArrayAdapter<>(getActivity(), android.R.layout.simple_list_item_1, actions),
                        new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                if (actionsIds.get(i) == 1) {
                                    tempFile = null;
                                    new AddAvatarDialog(AvatarsFragment.this, new AddAvatarDialog.OnAvatarDialogUpdateListener() {
                                        @Override
                                        public void onPrepearAvatar(String id,String name, int price, File avatar) {
                                            if (name.length() > 0) {
                                                waitDialog.show();
                                                tempFile = avatar;
                                                helper.update(id,name,price);
                                            } else {
                                                InfoDialog.show(R.string.add_avatar, R.string.add_avatar_no_data, getActivity());
                                            }
                                        }
                                    },avatarResponse);
                                } else if (actionsIds.get(i) == 3) {
                                    new AlertDialog.Builder(getActivity())
                                            .setTitle(avatarResponse.getName())
                                            .setMessage(R.string.delete_avatar_ask)
                                            .setPositiveButton(R.string.yes, new DialogInterface.OnClickListener() {
                                                @Override
                                                public void onClick(DialogInterface dialogInterface, int i) {
                                                    waitDialog.show();
                                                    helper.remove(avatarResponse.getId());
                                                }
                                            })
                                            .setNegativeButton(R.string.cancel, null)
                                            .show();
                                } else if (actionsIds.get(i) == 4) {
                                    //по умолчанию
                                    setForGroupShow(avatarResponse.getId());
                                }
                            }
                        })
                .show();
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        AvatarsResponse data = new AvatarsResponse();
        data.setAvatars(adapter.getElements());
        outState.putSerializable("data", data);
    }

    @Override
    public void onAddAvatar(boolean done, final AvatarResponse response) {
        if (done) {
            helper.uploadAvatar(response.getId(), tempFile, new AvatarsHelper.OnAvatarUploadListener() {
                @Override
                public void onUploadAvatar(boolean done, String id) {
                    waitDialog.hide();
                    if (done) {
                        AvatarsLoader.load(id, new AvatarsLoader.OnAvatarLoadListener() {
                            @Override
                            public void onLoad(String id) {
                                adapter.add(response);
                                adapter.notifyItemChanged(adapter.getItemCount() - 1);
                            }
                        });
                        adapter.add(response);
                        String av = getString(R.string.add_avatar_done);
                        av = av.replaceAll(Pattern.quote("%"), "\"" + response.getName() + "\"");
                        InfoDialog.show(R.string.add_avatar, av, getActivity());
                    } else {
                        adapter.add(response);
                        InfoDialog.show(R.string.add_avatar, R.string.error_upload_avatar, getActivity());
                    }
                }
            });
        } else {
            waitDialog.hide();
            InfoDialog.show(R.string.add_avatar, R.string.error_add_avatar, getActivity());
        }
    }

    @Override
    public void onUpdateAvatar(boolean done, final AvatarResponse response) {
        if (done) {
            if(tempFile!=null) {
                helper.uploadAvatar(response.getId(), tempFile, new AvatarsHelper.OnAvatarUploadListener() {
                    @Override
                    public void onUploadAvatar(boolean done, String id) {
                        waitDialog.hide();
                        if (done) {
                            AvatarsLoader.load(id, new AvatarsLoader.OnAvatarLoadListener() {
                                @Override
                                public void onLoad(String id) {
                                    adapter.add(response);
                                    adapter.notifyItemChanged(adapter.getItemCount() - 1);
                                }
                            });
                            adapter.add(response);
                            String av = getString(R.string.update_avatar_done);
                            av = av.replaceAll(Pattern.quote("%"), "\"" + response.getName() + "\"");
                            InfoDialog.show(R.string.update_avatar, av, getActivity());
                        } else {
                            adapter.add(response);
                            InfoDialog.show(R.string.update_avatar, R.string.error_upload_avatar, getActivity());
                        }
                    }
                });
            }else{
                waitDialog.hide();
                adapter.add(response);
                String av = getString(R.string.update_avatar_done);
                av = av.replaceAll(Pattern.quote("%"), "\"" + response.getName() + "\"");
                InfoDialog.show(R.string.update_avatar, av, getActivity());
            }
        } else {
            waitDialog.hide();
            InfoDialog.show(R.string.update_avatar, R.string.error_update_avatar, getActivity());
        }
    }

    private void setForGroupShow(final String avatarId){
        final List<Integer> actionsIds = new ArrayList<>();
        List<String> actions = new ArrayList<>();
        actions.add(getString(R.string.user_admin));
        actionsIds.add(UserType.TYPE_ADMIN);
        actions.add(getString(R.string.user_moder));
        actionsIds.add(UserType.TYPE_MODER);
        actions.add(getString(R.string.user_user));
        actionsIds.add(UserType.TYPE_USER);
        actions.add(getString(R.string.user_invisible));
        actionsIds.add(UserType.TYPE_INVISIBLE);
        actions.add(getString(R.string.user_banned));
        actionsIds.add(UserType.TYPE_BANNED);

        new AlertDialog.Builder(getActivity())
                .setTitle(R.string.set_default_for_group_users)
                .setAdapter(new ArrayAdapter<>(getActivity(), android.R.layout.simple_list_item_1, actions),
                        new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                waitDialog.show();
                                helper.setDefault(avatarId,actionsIds.get(i));
                            }
                        })
                .show();
    }

    @Override
    public void onRemoveAvatar(boolean done, AvatarRequest request) {
        waitDialog.hide();
        if (done) {
            InfoDialog.show(R.string.delete_avatar, R.string.done_delete_avatar, getActivity());
            AvatarResponse r = new AvatarResponse();
            r.setId(request.getId());
            adapter.remove(r);
        } else {
            InfoDialog.show(R.string.delete_avatar, R.string.error_delete_avatar, getActivity());
        }
    }

    @Override
    public void onSetDefaultAvatar(boolean done) {
        waitDialog.hide();
        if(done){
            InfoDialog.show(R.string.set_default_for_group, R.string.set_default_for_group_done, getActivity());
        }else{
            InfoDialog.show(R.string.set_default_for_group, R.string.set_default_for_group_error, getActivity());
        }
    }

    @Override
    public void onGetAvatars(boolean done, AvatarsResponse response) {
        pbLoading.setVisibility(View.INVISIBLE);
        if (done) {
            for (AvatarResponse a : response.getAvatars()) {
                adapter.add(a);
            }
        } else {
            Toast.makeText(getActivity(), R.string.not_load_avatars, Toast.LENGTH_LONG).show();
        }
    }
}
