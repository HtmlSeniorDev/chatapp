package ru.readme.chatapp.fragment;

import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import androidx.annotation.Nullable;
import androidx.fragment.app.FragmentTransaction;
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
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import java.io.File;
import java.io.FileOutputStream;
import java.io.OutputStream;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;

import ru.readme.chatapp.activity.AnotherBalanceActivity;
import ru.readme.chatapp.activity.ImageViewActivity;
import ru.readme.chatapp.activity.MainActivity;
import ru.readme.chatapp.R;
import ru.readme.chatapp.activity.ZagsActivity;
import ru.readme.chatapp.adapter.GiftsAdapter;
import ru.readme.chatapp.adapter.PhotosAdapter;
import ru.readme.chatapp.adapter.ProfileAdapter;
import ru.readme.chatapp.dialog.AddMoneyDialog;
import ru.readme.chatapp.dialog.AddPhotoDialog;
import ru.readme.chatapp.dialog.AskDialog;
import ru.readme.chatapp.dialog.AskSendGiftDialog;
import ru.readme.chatapp.dialog.ChangePasswordDialog;
import ru.readme.chatapp.dialog.GiftDialog;
import ru.readme.chatapp.dialog.GiftsDialog;
import ru.readme.chatapp.dialog.InfoDialog;
import ru.readme.chatapp.dialog.SelectFileDialog;
import ru.readme.chatapp.dialog.WaitDialog;
import ru.readme.chatapp.object.ProfileElement;
import ru.readme.chatapp.object.UserType;
import ru.readme.chatapp.object.requests.PhotoRequest;
import ru.readme.chatapp.object.responses.FolderResponse;
import ru.readme.chatapp.object.responses.GiftResponse;
import ru.readme.chatapp.object.responses.PMChatResponse;
import ru.readme.chatapp.object.responses.PhotoResponse;
import ru.readme.chatapp.object.responses.PhotosResponse;
import ru.readme.chatapp.object.responses.UserResponse;
import ru.readme.chatapp.util.ImagePicker;
import ru.readme.chatapp.util.ImageSetter;
import ru.readme.chatapp.util.Network;
import ru.readme.chatapp.util.SettingsHelper;
import ru.readme.chatapp.helper.ProfileHelper;

import static android.app.Activity.RESULT_OK;
import static ru.readme.chatapp.activity.MainActivity.TAG_CHAT;
import static ru.readme.chatapp.activity.MainActivity.TAG_EDIT_PROFILE;

/**
 * Created by dima on 20.11.16.
 */

public class ProfileFragment extends MyBaseFragment implements GiftsAdapter.OnGiftElementClickListener, AddPhotoDialog.OnAddPhotoListener, ProfileHelper.OnProfileActionListener, View.OnClickListener, PhotosAdapter.OnPhotoClickListener {

    private TextView tvNic;
    private ImageView ivPhoto;
    private ProfileAdapter adapter;
    private RecyclerView rvList;
    private Button OpenAllPhotos;
    private RelativeLayout l4 , l5;


    private UserResponse user;
    private String userId;
    private ProfileHelper helper;
    private WaitDialog waitDialog;
    private SettingsHelper settingsHelper;
    private LinearLayout llPhotos;
    private RecyclerView rvPhotos, rvGifts;
    private ProgressBar pbPhotos;
    private TextView tvNoPhotos, tvNoGifts;
    private PhotosAdapter photosAdapter;
    private AddPhotoDialog photoDialog;
    private GiftsAdapter giftsAdapter;


    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.profile, null);
        settingsHelper = new SettingsHelper(getActivity());
        helper = new ProfileHelper(getActivity(), this);
        waitDialog = new WaitDialog(getActivity());
        tvNic = (TextView) v.findViewById(R.id.tv_profile_user_nic);
        ivPhoto = (ImageView) v.findViewById(R.id.iv_profile_photo);
        ivPhoto.setOnClickListener(this);
        adapter = new ProfileAdapter(getActivity());
        rvList = (RecyclerView) v.findViewById(R.id.rv_profile_list);
        rvPhotos = (RecyclerView) v.findViewById(R.id.rv_profile_photos);
        pbPhotos = (ProgressBar) v.findViewById(R.id.pb_profile_photos);
        tvNoPhotos = (TextView) v.findViewById(R.id.tv_profile_no_photos);
        llPhotos = (LinearLayout) v.findViewById(R.id.ll_profile_photos);
        l4 = (RelativeLayout) v.findViewById(R.id.l4);
        l5 = (RelativeLayout) v.findViewById(R.id.l5);

        rvGifts = (RecyclerView) v.findViewById(R.id.rv_profile_gifts);
        tvNoGifts = (TextView) v.findViewById(R.id.tv_profile_no_gifts);
        OpenAllPhotos = (Button) v.findViewById(R.id.open_all_photo);

        //llPhotos.setOnClickListener(this);
        OpenAllPhotos.setOnClickListener(this);
        RecyclerView.LayoutManager manager1 = new LinearLayoutManager(getActivity(), LinearLayoutManager.HORIZONTAL, false);
        rvPhotos.setLayoutManager(manager1);

        RecyclerView.LayoutManager manager2 = new LinearLayoutManager(getActivity(), LinearLayoutManager.HORIZONTAL, false);
        rvGifts.setLayoutManager(manager2);

        tvNoPhotos.setVisibility(View.INVISIBLE);
        tvNoGifts.setVisibility(View.INVISIBLE);

        giftsAdapter = new GiftsAdapter(getActivity(), this, true);
        rvGifts.setAdapter(giftsAdapter);

        RecyclerView.LayoutManager manager = new LinearLayoutManager(getActivity());
        DividerItemDecoration dividerItemDecoration = new DividerItemDecoration(rvList.getContext(),
                DividerItemDecoration.VERTICAL);

        rvList.addItemDecoration(dividerItemDecoration);
        rvList.setLayoutManager(manager);
        rvList.setAdapter(adapter);
        if (savedInstanceState != null) {
            user = (UserResponse) savedInstanceState.getSerializable("user");
            if (user != null) {
                userId = user.getId();
                if (!isDetached()) {
                    setUser();
                }
            }

        } else if (getArguments() != null) {
            userId = getArguments().getString("user", "-1");
            waitDialog.show();
            helper.loadUser(userId);
        }
        if (userId == null) {
            userId = settingsHelper.getUserId();
            waitDialog.show();
            helper.loadUser(userId);
        }
        if (getActivity() instanceof MainActivity) {
            ((AppCompatActivity) getActivity()).getSupportActionBar().setTitle(R.string.profile);
        }
        photosAdapter = new PhotosAdapter(getActivity(), this, settingsHelper.getUserId().equals(userId));

        rvPhotos.setAdapter(photosAdapter);
        if (savedInstanceState != null && savedInstanceState.containsKey("photos")) {
            PhotosResponse photos = (PhotosResponse) savedInstanceState.getSerializable("photos");
            for (PhotoResponse pr : photos.getPhotos()) {
                photosAdapter.add(pr);
            }
            if (photosAdapter.getPhotos().size() == 0) {
               // tvNoPhotos.setVisibility(View.VISIBLE);
              //  helper.getPhotos(userId);
                l4.setVisibility(View.GONE);
            } else {
                pbPhotos.setVisibility(View.INVISIBLE);
            }
        } else {
            helper.getPhotos(userId);
        }

        return v;
    }

    private void setUser() {

        List<ProfileElement> elements = new ArrayList<>();
        adapter.clear();
        tvNic.setText(user.getNic());
        tvNic.setTextColor(user.getColor());

        SimpleDateFormat sdf = new SimpleDateFormat("dd.MM.yyyy");
        if (user.getSex() != -1) {
            elements.add(new ProfileElement(getString(R.string.sextext), UserType.getSex(user.getSex())));
        }
        if (user.getFirstName() != null) {
            elements.add(new ProfileElement(getString(R.string.user_first_name), user.getFirstName()));
        }
        if (user.getLastName() != null) {
            elements.add(new ProfileElement(getString(R.string.user_last_name), user.getLastName()));
        }

        if (user.getZags() != null) {
            elements.add(new ProfileElement(getString(R.string.in_brack), user.getZags().getNic()));
        }

        if (user.getBday() != null) {
            elements.add(new ProfileElement(getString(R.string.user_bday), sdf.format(user.getBday())));
        }

        if (user.getCity() != null) {
            elements.add(new ProfileElement(getString(R.string.user_city), user.getCity()));
        }
        if (user.getAbout() != null) {
            elements.add(new ProfileElement(getString(R.string.user_about), user.getAbout()));
        }
        if (settingsHelper.getUserType() == UserType.TYPE_ADMIN) {
            elements.add(new ProfileElement(getString(R.string.user_type), getString(UserType.getTypeString(user.getType()))));
        }
        if (user.getLastVisit() != null && user.getLastVisit().getTime() > 1000) {
            SimpleDateFormat sdf1 = new SimpleDateFormat("dd-MM-yyyy HH:mm");
            elements.add(new ProfileElement(getString(R.string.user_last_visit), sdf1.format(user.getLastVisit())));
        }
        for (ProfileElement el : elements) {
            adapter.add(el);
        }

        try {
            if (user.getAvatarLink() != null && user.getAvatarLink().length() > 0) {
                ImageSetter.setAvatar(user.getAvatarLink(), getActivity(), ivPhoto);
            }

        } catch (Exception e) {
        }
        Picasso.with(getActivity())
                .load(Network.photoLink(user.getPhoto()))
                .error(R.drawable.ic_block_grey_700_48dp)
                .into(ivPhoto);
        if (giftsAdapter == null) {
            giftsAdapter = new GiftsAdapter(getActivity(), this, true);
        }
        giftsAdapter.clear();
        for (GiftResponse giftResponse : user.getGifts()) {
            giftsAdapter.add(giftResponse);
        }
        if (giftsAdapter.getItemCount() == 0) {
          //  tvNoGifts.setVisibility(View.VISIBLE);
            l5.setVisibility(View.GONE);
        } else {
            tvNoGifts.setVisibility(View.INVISIBLE);
        }

    }


    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        if (userId.equals(settingsHelper.getUserId())) {
            inflater.inflate(R.menu.profile_my, menu);
        } else if (settingsHelper.getUserType() == UserType.TYPE_ADMIN) {
            inflater.inflate(R.menu.profile_admin, menu);
        } else {
            inflater.inflate(R.menu.profile, menu);
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == R.id.menu_profile_edit) {
            Bundle args = new Bundle();
            args.putSerializable("user", user);
            EditProfileFragment fragment = new EditProfileFragment();
            fragment.setArguments(args);
            FragmentTransaction fragmentTransaction = getFragmentManager().beginTransaction();
            fragmentTransaction.setCustomAnimations(android.R.anim.fade_in,
                    android.R.anim.fade_out);

            if (getActivity() instanceof MainActivity) {
                fragmentTransaction
                        .replace(R.id.ll_base_main_layout_content, fragment, TAG_EDIT_PROFILE)
                        .addToBackStack(null)
                        .commitAllowingStateLoss();

            } else {
                fragmentTransaction
                        .replace(R.id.ll_base_layout_content, fragment, TAG_EDIT_PROFILE)
                        .addToBackStack(null)
                        .commitAllowingStateLoss();

            }

        } else if (item.getItemId() == R.id.menu_profile_messages) {
            waitDialog.show();
            helper.startDialog(user.getId());
      //  } else if (item.getItemId() == R.id.menu_profile_add_friends) {
         //   helper.onAdd(user.getId(), userId);
              } else if (item.getItemId() == R.id.menu_profile_add_money) {
            new AddMoneyDialog(getActivity(), new AddMoneyDialog.OnAddMoneyListener() {
                @Override
                public void onAddMoney(String sum) {
                    if (sum.length() > 0 && sum.length() <= 7) {
                        waitDialog.show();
                        helper.AddMoney(user.getId(), sum);
                    } else {
                        InfoDialog.show("Ошибка", "сумма должна быть больше 0 и не длиннее 7 цифр", getActivity());
                    }
                }
            });

        } else if (item.getItemId() == R.id.menu_profile_change_password) {
            new ChangePasswordDialog(getActivity(), new ChangePasswordDialog.OnChangePasswordListener() {
                @Override
                public void onChangePassword(String oldPassword, String newPassword, String retypePassword) {
                    if (newPassword.equals(retypePassword) && newPassword.length() >= 6) {
                        waitDialog.show();
                        helper.changePassword(oldPassword, newPassword);
                    } else {
                        InfoDialog.show(R.string.change_password, R.string.change_password_error, getActivity());
                    }
                }
            });
        } else if (item.getItemId() == R.id.menu_profile_gift) {
            GiftsDialog gdialog = new GiftsDialog();
            gdialog.setOnSelectGiftListener(new GiftsDialog.OnSelectGiftListener() {
                @Override
                public void onSelectGift(final GiftResponse gift) {
                    String ms = getString(R.string.send_gift_ask);
                    ms = ms.replaceAll(Pattern.quote("%"), gift.getName());
                    ms = ms.replaceAll(Pattern.quote("*"), (gift.getPrice() / 100) + "");
                    AskSendGiftDialog.show(getActivity(), R.string.send_gift_title, ms, new AskSendGiftDialog.OnAskSendGiftDialogClickListener() {
                        @Override
                        public void onAskClick(boolean acept, boolean anon) {
                            if (acept) {
                                waitDialog.show();
                                helper.sendGift(gift.getId(), userId, anon);
                            }
                        }
                    });
                }
            });
            gdialog.show(getFragmentManager(), "gifts_dialog");
        } else if (item.getItemId() == R.id.menu_profile_zags) {
            Intent intent = new Intent(getActivity(), ZagsActivity.class);
            intent.putExtra("user", user);
            startActivityForResult(intent, 1);
        } else if (item.getItemId() == R.id.menu_profile_add_balance) {
            Intent intent = new Intent(getActivity(), AnotherBalanceActivity.class);
            intent.putExtra("id", user.getId());
            startActivityForResult(intent, 1);
        } else if (item.getItemId() == R.id.menu_profile_device_ban) {
            AskDialog.show(getActivity(), R.string.device_ban, R.string.device_ban_ask, new AskDialog.OnAskDialogClickListener() {
                @Override
                public void onAskClick(boolean acept) {
                    if (acept) {
                        waitDialog.show();
                        helper.banDevice(user.getId());
                    }
                }

            });
        }
        return super.onOptionsItemSelected(item);
    }


    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 1) {
            if (resultCode == 2) {
                getActivity().setResult(2);
                getActivity().finish();
            } else {
                waitDialog.show();
                helper.loadUser(userId);
            }
        } else if (requestCode == SelectFileDialog.CODE_GALLERY) {
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
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putSerializable("user", user);
        PhotosResponse dt = new PhotosResponse();
        if (photosAdapter != null && photosAdapter.getPhotos() != null) {
            dt.setPhotos(photosAdapter.getPhotos());
        } else {
            dt.setPhotos(new ArrayList<PhotoResponse>());
        }
        outState.putSerializable("photos", dt);
    }

    @Override
    public void onUserLoad(UserResponse user) {
        waitDialog.hide();
        if (user != null) {
            this.user = user;
            setUser();
        } else {
            InfoDialog.show(R.string.load_user, R.string.load_user_error, getActivity());
        }
    }

    @Override
    public void onGetPhotos(boolean done, List<PhotoResponse> photos) {
        pbPhotos.setVisibility(View.INVISIBLE);
        if (done) {
            for (PhotoResponse pr : photos) {
                photosAdapter.add(pr);
            }
            if (photosAdapter.getElements().size() == 0) {
                tvNoPhotos.setVisibility(View.VISIBLE);
                l4.setVisibility(View.GONE);
            }
        } else {
            tvNoPhotos.setVisibility(View.VISIBLE);
            l4.setVisibility(View.GONE);
        }
    }

    @Override
    public void onAddPhoto(PhotoResponse photoResponse) {
        waitDialog.hide();
        if (photoResponse != null) {
            photosAdapter.add(photoResponse);
        } else {
            InfoDialog.show(R.string.photo_add, R.string.photo_add_error, getActivity());
        }
    }

    @Override
    public void onChangePassword(boolean done, String newPassword) {
        waitDialog.hide();
        if (done) {
            InfoDialog.show(R.string.change_password, R.string.change_password_done, getActivity());
            settingsHelper.setPassword(newPassword);
        } else {
            InfoDialog.show(R.string.change_password, R.string.change_password_error, getActivity());
        }
    }

    @Override
    public void onAddMoney(boolean done) {
        waitDialog.hide();
        if (done) {
            InfoDialog.show("Баланс", "Баланс успешно добавлен", getActivity());
        } else {
            InfoDialog.show("Баланс", "При добавлении баланса произошла ошибка", getActivity());
        }
    }

    @Override
    public void onStartDialog(boolean status, PMChatResponse response) {
        waitDialog.hide();
        if (status) {
            Bundle args = new Bundle();
            args.putSerializable("chat", response);
            ChatFragment fragment = new ChatFragment();
            fragment.setArguments(args);
            FragmentTransaction fragmentTransaction = getFragmentManager().beginTransaction();
            fragmentTransaction.setCustomAnimations(android.R.anim.fade_in,
                    android.R.anim.fade_out);
            if (getActivity() instanceof MainActivity) {
                fragmentTransaction
                        .replace(R.id.ll_base_main_layout_content, fragment, TAG_CHAT)
                        .addToBackStack(TAG_CHAT)
                        .commitAllowingStateLoss();
            } else {
                fragmentTransaction
                        .replace(R.id.ll_base_layout_content, fragment, TAG_CHAT)
                        .addToBackStack(TAG_CHAT)
                        .commitAllowingStateLoss();
            }

        }
    }

    @Override
    public void onSendGift(boolean done) {
        if (done) {
            waitDialog.hide();
            InfoDialog.show(R.string.send_gift_title, R.string.send_gift_done, getActivity());
            helper.loadUser(userId);
        } else {
            InfoDialog.show(R.string.send_gift_title, R.string.send_gift_error, getActivity());
        }
    }

    @Override
    public void onDeviceBan(boolean done) {
        if (done) {
            waitDialog.hide();
            InfoDialog.show(R.string.device_ban, R.string.device_ban_done, getActivity());
            helper.loadUser(userId);
        } else {
            InfoDialog.show(R.string.device_ban, R.string.device_ban_error, getActivity());
        }
    }


    @Override
    public void onClick(View view) {
        if (view.getId() == R.id.open_all_photo) {
            Bundle args = new Bundle();
            args.putString("user", userId);
            args.putSerializable("folder", null);
            PhotosFragment fragment = new PhotosFragment();
            fragment.setArguments(args);
            FragmentTransaction fragmentTransaction = getFragmentManager().beginTransaction();
            fragmentTransaction.setCustomAnimations(android.R.anim.fade_in,
                    android.R.anim.fade_out);
            if (getActivity() instanceof MainActivity) {
                fragmentTransaction
                        .replace(R.id.ll_base_main_layout_content, fragment, TAG_CHAT)
                        .addToBackStack(TAG_CHAT)
                        .commitAllowingStateLoss();
            } else {
                fragmentTransaction
                        .replace(R.id.ll_base_layout_content, fragment, TAG_CHAT)
                        .addToBackStack(TAG_CHAT)
                        .commitAllowingStateLoss();
            }
        }
    }

    @Override
    public void onPhotoClick(PhotoResponse photoResponse) {
        if (photoResponse == null && userId.equals(settingsHelper.getUserId())) {
            photoDialog = new AddPhotoDialog(this, null, this);
        } else {
            Intent i = new Intent(getActivity(), ImageViewActivity.class);
            i.putExtra("url", Network.photoLink(photoResponse.getId()));
            i.putExtra("description", photoResponse.getDescription());
            startActivityForResult(i, 1);
        }
    }

    @Override
    public void onFolderClick(FolderResponse folderResponse) {

    }

    @Override
    public void onFolderLongClick(FolderResponse folderResponse) {

    }

    @Override
    public void onLongPhotoClick(PhotoResponse photoResponse) {

    }

    @Override
    public void onPrepearAddPhoto(File file, PhotoRequest request) {
        if (file != null) {
            waitDialog.show();
            helper.addPhoto(file, request);
        } else {
            InfoDialog.show(R.string.add_photo, R.string.add_photo_error, getActivity());
        }
    }

    @Override
    public void onGiftElementClick(GiftResponse avatarResponse) {
        new GiftDialog(this, avatarResponse);
    }




}
