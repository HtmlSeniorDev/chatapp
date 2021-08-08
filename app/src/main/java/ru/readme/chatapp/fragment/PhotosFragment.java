package ru.readme.chatapp.fragment;

import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;

import java.io.File;
import java.io.FileOutputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.List;

import ru.readme.chatapp.activity.ImageViewActivity;
import ru.readme.chatapp.activity.MainActivity;
import ru.readme.chatapp.R;
import ru.readme.chatapp.adapter.PhotosAdapter;
import ru.readme.chatapp.dialog.AddPhotoDialog;
import ru.readme.chatapp.dialog.AddPhotoFolderDialog;
import ru.readme.chatapp.dialog.AskDialog;
import ru.readme.chatapp.dialog.EditPhotoFolderDialog;
import ru.readme.chatapp.dialog.InfoDialog;
import ru.readme.chatapp.dialog.SelectFileDialog;
import ru.readme.chatapp.dialog.WaitDialog;
import ru.readme.chatapp.object.requests.FolderRequest;
import ru.readme.chatapp.object.requests.PhotoRequest;
import ru.readme.chatapp.object.responses.FolderResponse;
import ru.readme.chatapp.object.responses.PhotoResponse;
import ru.readme.chatapp.object.responses.PhotosResponse;
import ru.readme.chatapp.util.ImagePicker;
import ru.readme.chatapp.util.Network;
import ru.readme.chatapp.util.SettingsHelper;
import ru.readme.chatapp.helper.PhotosHelper;

import static android.app.Activity.RESULT_OK;
import static ru.readme.chatapp.activity.MainActivity.TAG_CHAT;

/**
 * Created by dima on 03.01.17.
 */

public class PhotosFragment extends MyBaseFragment implements AddPhotoFolderDialog.OnAddPhotoFolderListener, AddPhotoDialog.OnAddPhotoListener, PhotosAdapter.OnPhotoClickListener, PhotosHelper.OnPhotoHelperListener {

    private PhotosAdapter adapter;
    private RecyclerView rvPhotos;

    private FolderResponse folder;
    private String user;

    private PhotosHelper helper;
    private AddPhotoDialog photoDialog;
    private WaitDialog waitDialog;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.photos, null);
        waitDialog = new WaitDialog(getActivity());
        helper = new PhotosHelper(getActivity(), this);
        rvPhotos = (RecyclerView) v.findViewById(R.id.rv_photos_list);
        if (savedInstanceState != null) {
            if (savedInstanceState.containsKey("user")) {
                user = savedInstanceState.getString("user");
            }
            if (savedInstanceState.containsKey("folder")) {
                folder = (FolderResponse) savedInstanceState.getSerializable("folder");
            }
        } else if (getArguments() != null) {
            if (getArguments().containsKey("user")) {
                user = getArguments().getString("user");
            }
            if (getArguments().containsKey("folder")) {
                folder = (FolderResponse) getArguments().getSerializable("folder");
            }
        }
        RecyclerView.LayoutManager manager = new GridLayoutManager(getActivity(), 3);
        rvPhotos.setLayoutManager(manager);
        if (user == null || user.equals(new SettingsHelper(getActivity()).getUserId())) {
            setHasOptionsMenu(true);
            adapter = new PhotosAdapter(getActivity(), this, true, true);
        } else {
            adapter = new PhotosAdapter(getActivity(), this, false, true);
        }
        rvPhotos.setAdapter(adapter);
        if (savedInstanceState != null) {
            if (savedInstanceState.containsKey("data")) {
                PhotosResponse rsp = (PhotosResponse) savedInstanceState.getSerializable("data");
                for (FolderResponse folder : rsp.getFolders()) {
                    adapter.add(folder);
                }
                for (PhotoResponse photo : rsp.getPhotos()) {
                    adapter.add(photo);
                }
            }
        }

        if (adapter.getPhotos().size() == 0 && adapter.getFolders().size() == 0) {
            if (folder != null) {
                helper.getFolder(user, folder.getId());
            } else {
                helper.getFolder(user, null);
            }
        }

        if (getActivity() instanceof MainActivity) {
            ((AppCompatActivity) getActivity()).getSupportActionBar().setTitle(R.string.photos);
            if (folder != null) {
                ((AppCompatActivity) getActivity()).getSupportActionBar().setTitle(folder.getName());
            }
        }
        return v;
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        if (folder != null) {
            outState.putSerializable("folder", folder);
        }
        outState.putString("user", user);
        PhotosResponse rsp = new PhotosResponse();
        rsp.setPhotos(adapter.getPhotos());
        rsp.setFolders(adapter.getFolders());
        outState.putSerializable("data", rsp);
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        if (user == null || user.equals(new SettingsHelper(getActivity()).getUserId())) {
            inflater.inflate(R.menu.photos, menu);
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == R.id.menu_photos_add_folder) {
            new AddPhotoFolderDialog(getActivity(), this);
        } else if (item.getItemId() == R.id.menu_photos_delete_folder) {
            AskDialog.show(getActivity(), R.string.delete_folder_ask_title, R.string.delete_folder_ask, new AskDialog.OnAskDialogClickListener() {
                @Override
                public void onAskClick(boolean acept) {
                    if(acept) {
                        waitDialog.show();
                        if (folder != null) {
                            helper.deleteFolder(folder.getId());
                        }
                    }
                }
            });
        } else if (item.getItemId() == R.id.menu_photos_edit_folder && folder != null) {
            new EditPhotoFolderDialog(getActivity(), new EditPhotoFolderDialog.OnEditPhotoFolderListener() {
                @Override
                public void onPreEditPhotoFolder(FolderRequest request) {
                    waitDialog.show();
                    helper.updateFolder(request);
                }
            }, folder);
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == SelectFileDialog.CODE_GALLERY) {
            if (resultCode == RESULT_OK) {
                Bitmap bitmap = ImagePicker.getImageFromResult(getActivity(), resultCode, data);
                File subdir = new File(getActivity().getFilesDir(), "tmp");
                if (!subdir.exists()) {
                    subdir.mkdirs();
                }
                String name = System.currentTimeMillis() + ".jpeg";
                try {
                    File img = new File(getActivity().getFilesDir() + "/tmp/", name);
                    OutputStream os = new FileOutputStream(img);
                    bitmap.compress(Bitmap.CompressFormat.JPEG, 85, os);
                    os.flush();
                    os.close();
                    if (photoDialog != null) {
                        photoDialog.setImage(img);
                    }
                } catch (Exception e) {
                    Log.e("App", "create photo", e);
                }
            }
        }
    }

    @Override
    public void onAddPhoto(PhotoResponse photoResponse) {
        waitDialog.hide();
        if (photoResponse != null) {
            adapter.add(photoResponse);
        } else {
            InfoDialog.show(R.string.photo_add, R.string.photo_add_error, getActivity());
        }
    }

    @Override
    public void onFolderDelete(boolean done) {
        waitDialog.hide();
        if (done) {
            doBack();
        } else {
            InfoDialog.show(R.string.delete_folder_title, R.string.delete_folder_error, getActivity());
        }
    }

    @Override
    public void onDeletePhoto(boolean done, String id) {
        waitDialog.hide();
        if (done) {
            PhotoResponse pr = new PhotoResponse();
            pr.setId(id);
            adapter.delete(pr);
        } else {
            InfoDialog.show(R.string.delete_photo_title, R.string.delete_photo_error, getActivity());
        }
    }

    @Override
    public void onSetForProfile(boolean done) {
        waitDialog.hide();
        if (done) {
            InfoDialog.show(R.string.set_image_for_profile, R.string.set_image_for_profile_done, getActivity());
        } else {
            InfoDialog.show(R.string.set_image_for_profile, R.string.set_image_for_profile_error, getActivity());
        }
    }

    @Override
    public void onPhotoClick(PhotoResponse photoResponse) {
        if (photoResponse == null && user.equals(new SettingsHelper(getActivity()).getUserId())) {
            photoDialog = new AddPhotoDialog(this, null, this);
        } else {
            Intent i = new Intent(getActivity(), ImageViewActivity.class);
            i.putExtra("url", Network.photoLink(photoResponse.getId()));
            i.putExtra("description", photoResponse.getDescription());
            startActivityForResult(i,1);
        }
    }

    @Override
    public void onFolderClick(FolderResponse folderResponse) {
        Bundle args = new Bundle();
        args.putString("user", user);
        args.putSerializable("folder", folderResponse);
        PhotosFragment fragment = new PhotosFragment();
        fragment.setArguments(args);
        FragmentTransaction fragmentTransaction = getFragmentManager().beginTransaction();
        fragmentTransaction.setCustomAnimations(android.R.anim.fade_in,
                android.R.anim.fade_out);
        if (getActivity() instanceof MainActivity) {
            fragmentTransaction
                    .replace(R.id.ll_base_main_layout_content, fragment, TAG_CHAT)
                    .addToBackStack(null)
                    .commitAllowingStateLoss();
        } else {
            fragmentTransaction
                    .replace(R.id.ll_base_layout_content, fragment, TAG_CHAT)
                    .addToBackStack(null)
                    .commitAllowingStateLoss();
        }
    }

    @Override
    public void onFolderLongClick(FolderResponse folderResponse) {

    }

    @Override
    public void onLongPhotoClick(final PhotoResponse photoResponse) {
        if (user.equals(new SettingsHelper(getActivity()).getUserId())) {
            final List<Integer> actionsIds = new ArrayList<>();
            List<String> actions = new ArrayList<>();
            actions.add(getString(R.string.set_my_photo));
            actionsIds.add(1);
            actionsIds.add(2);
            actions.add(getString(R.string.delete_photo));
            String title = getString(R.string.photo);
            if (photoResponse.getDescription() != null && photoResponse.getDescription().length() > 0) {
                title = photoResponse.getDescription();
            }
            new AlertDialog.Builder(getActivity())
                    .setTitle(title)
                    .setAdapter(new ArrayAdapter<>(getActivity(), android.R.layout.simple_list_item_1, actions),
                            new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialogInterface, int i) {
                                    if (actionsIds.get(i) == 1) {
                                        waitDialog.show();
                                        helper.setForProfile(photoResponse.getId());
                                    } else if (actionsIds.get(i) == 2) {
                                        AskDialog.show(getActivity(), R.string.delete_photo, R.string.delete_photo_ask, new AskDialog.OnAskDialogClickListener() {
                                            @Override
                                            public void onAskClick(boolean acept) {
                                                if (acept) {
                                                    waitDialog.show();
                                                    helper.deletePhoto(photoResponse.getId());
                                                }
                                            }
                                        });
                                    }
                                }
                            })
                    .show();
        }
    }

    @Override
    public void onGetPhotos(boolean done, PhotosResponse response) {
        if (done) {
            for (FolderResponse folder : response.getFolders()) {
                adapter.add(folder);
            }
            for (PhotoResponse photo : response.getPhotos()) {
                adapter.add(photo);
            }
        }
    }

    @Override
    public void onAddPhotoFolder(boolean done, FolderResponse response) {
        waitDialog.hide();
        if (done) {
            adapter.add(response);
        } else {
            InfoDialog.show(R.string.add_folder, R.string.add_folder_error, getActivity());
        }
    }

    @Override
    public void onUpdatePhotoFolder(boolean done, FolderResponse response) {
        waitDialog.hide();
        if (done) {
            this.folder = response;
        } else {
            InfoDialog.show(R.string.edit_folder, R.string.edit_folder_error, getActivity());
        }
    }


    @Override
    public void onPrepearAddPhoto(File file, PhotoRequest request) {
        if (file != null) {
            waitDialog.show();
            if (folder != null) {
                request.setParent(folder.getId());
            }
            helper.addPhoto(file, request);
        } else {
            InfoDialog.show(R.string.add_photo, R.string.add_photo_error, getActivity());
        }
    }

    @Override
    public void onPreCreatePhotoFolder(FolderRequest request) {
        if (request != null && request.getName() != null && request.getName().length() > 0) {
            waitDialog.show();
            if (folder != null) {
                request.setParent(folder.getId());
            }
            helper.addFolder(request);
        } else {
            InfoDialog.show(R.string.add_folder, R.string.add_folder_error, getActivity());
        }
    }
}
