package ru.readme.chatapp.activity;

import android.annotation.SuppressLint;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.graphics.BitmapFactory;
import android.media.RingtoneManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.os.PowerManager;
import android.os.Vibrator;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import com.google.android.material.navigation.NavigationView;
import androidx.fragment.app.FragmentManager;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import android.util.Log;
import android.view.Gravity;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

import com.google.android.gms.ads.AdView;
import com.google.android.gms.ads.MobileAds;
import com.google.android.gms.ads.initialization.InitializationStatus;
import com.google.android.gms.ads.initialization.OnInitializationCompleteListener;

import java.util.ArrayList;
import java.util.List;
import ru.readme.chatapp.R;
import ru.readme.chatapp.dialog.AskDialog;
import ru.readme.chatapp.dialog.NoticeInfoDialog;
import ru.readme.chatapp.dialog.WaitDialog;
import ru.readme.chatapp.fragment.ChatFragment;
import ru.readme.chatapp.fragment.ChatsFragment;
import ru.readme.chatapp.fragment.SettingsFragment;
import ru.readme.chatapp.object.UserType;
import ru.readme.chatapp.object.responses.NoticeResponse;
import ru.readme.chatapp.object.responses.RoomResponse;
import ru.readme.chatapp.object.responses.UserResponse;
import ru.readme.chatapp.util.BackListener;
import ru.readme.chatapp.util.SettingsHelper;
import ru.readme.chatapp.helper.AvatarsLoader;
import ru.readme.chatapp.helper.MainActivityHelper;

public class MainActivity extends AppCompatActivity implements MainActivityHelper.OnMainHelperActionListener {
    protected PowerManager.WakeLock mWakeLock;
    public final static String TAG_CHATS = "chats";
    private AdView mAdView;
    public final static String TAG_CHAT = "chat";
    public final static String TAG_PROFILE = "profile";
    public final static String TAG_EDIT_PROFILE = "editprofile";
    public final static String TAG_BANNED = "banned";
    public static final String TAG_PM_CHATS = "personal";
    public static final String TAG_AVATARS = "avatars";
    public static final String TAG_INVISIBLE = "invisible";
    public static final String TAG_GIFTS = "gifts";
    public static final String TAG_VIORT_ZAGS_ADMIN = "virt_zags_admin";
    public static final String TAG_VIORT_ZAGS = "virt_zags";
    public static final String TAG_BALANCE = "balance";
    public static final String TAG_SETTINGS = "settings";
    public static final String TAG_DEVICES_BAN = "devicesban";
    public static final String TAG_RULES = "rules";
    public static final String TAG_MODERS = "moders";
    public static final String TAG_ADMINS = "admins";
    public static final String TAG_FRIENDS = "friends";
    public static MainActivity ma = null;
    private static boolean network = true;
    private NoticeInfoDialog noticeInfoDialog = new NoticeInfoDialog();
    private NavigationView nvNavigation;
    private Toolbar toolbar;
    private WaitDialog waitDialog;
    private DrawerLayout drawerLayout;
    private View navHeader;
    private MainActivityHelper helper;
    private boolean profieLoad = false;
    Handler handler = new Handler(new Handler.Callback() {
        @Override
        public boolean handleMessage(Message message) {
            if (message.what == 1) {//загрузка профиля каждые 5 секунд
                if (MainActivity.this != null) {
                    if (!profieLoad) {
                        profieLoad = true;
                        helper.loadProfile();
                    }
                    handler.sendEmptyMessageDelayed(1, 15000);
                }
            } else if (message.what == 4) {//проверка состояния сести каждые 2.5 секунд
                if (MainActivity.this != null) {
                    checkNetwork();
                    if (network) {
                        handler.sendEmptyMessageDelayed(4, 7000);
                    }
                }

            }
            return true;
        }
    });
    private List<NoticeResponse> notices = new ArrayList<>();
    private SettingsHelper settingsHelper;
    private BackListener backListener;
    private Vibrator vibrator;
    private UserResponse user;
    private int lastMessages = 0;
    private boolean nShow = false;

    @SuppressLint("InvalidWakeLockTag")
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ma = this;
        new AvatarsLoader(this).check();
        waitDialog = new WaitDialog(this);
        helper = new MainActivityHelper(this, this);
        vibrator = (Vibrator) getSystemService(Context.VIBRATOR_SERVICE);
        settingsHelper = new SettingsHelper(this);
        setContentView(R.layout.drawer_activity);
        MobileAds.initialize(this, new OnInitializationCompleteListener() {
            @Override
            public void onInitializationComplete(@NonNull InitializationStatus initializationStatus) {
            }
        });

      if(settingsHelper.getSL() == true) {
         final PowerManager pm = (PowerManager) getSystemService(Context.POWER_SERVICE);
         this.mWakeLock = pm.newWakeLock(PowerManager.SCREEN_DIM_WAKE_LOCK, "My Tag");
         this.mWakeLock.acquire();
      }

        toolbar = (Toolbar) findViewById(R.id.toolbar);
        drawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);
        nvNavigation = (NavigationView) findViewById(R.id.nav_view);
        if (settingsHelper.getUserType() == UserType.TYPE_ADMIN) {
            getMenuInflater().inflate(R.menu.drawer_admin, nvNavigation.getMenu());
        } else if (settingsHelper.getUserType() == UserType.TYPE_MODER) {
            getMenuInflater().inflate(R.menu.drawer_moder, nvNavigation.getMenu());
        } else {
            getMenuInflater().inflate(R.menu.drawer, nvNavigation.getMenu());
        }
        navHeader = nvNavigation.getHeaderView(0);
        setSupportActionBar(toolbar);

        toolbar.setNavigationIcon(R.drawable.ic_menu_white_24dp);

        if (savedInstanceState == null) {
            if (settingsHelper.getLastChat() == null) {

                waitDialog.show();
                helper.getLastRoom();
            } else {
                setChatFragment(settingsHelper.getLastChat());
                settingsHelper.setLastChat(null);
            }
        } else {
            user = (UserResponse) savedInstanceState.getSerializable("user");
        }
        if (user == null) {
            helper.loadProfile();
        } else {
        }

        getSupportFragmentManager().addOnBackStackChangedListener(new FragmentManager.OnBackStackChangedListener() {
            @Override
            public void onBackStackChanged() {
                if (getSupportFragmentManager().getBackStackEntryCount() == 1) {
                    toolbar.setNavigationIcon(R.drawable.ic_menu_white_24dp);
                } else {
                    toolbar.setNavigationIcon(R.drawable.ic_arrow_back_white_24dp);
                }
            }
        });

        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (getSupportFragmentManager().getBackStackEntryCount() == 1) {
                    drawerLayout.openDrawer(Gravity.LEFT);
                } else {
                    doBack();
                }
            }
        });
        network = true;
        handler.sendEmptyMessageDelayed(1, 100);
        handler.sendEmptyMessage(4);
        setupNavigation();
    }

    @Override
    protected void onStart() {
        ma = this;
        super.onStart();

    }

    @Override
    protected void onPause() {
        ma = null;
        super.onPause();
    }

    @Override
    protected void onDestroy() {

        this.mWakeLock.release();

        ma = null;
        super.onDestroy();
    }

    public void forceReloadProfile() {
        helper.loadProfile();
    }

    private void checkNetwork() {
        ConnectivityManager cm = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetwork = cm.getActiveNetworkInfo();
        boolean isConnected = activeNetwork != null && activeNetwork.isConnectedOrConnecting();
        if (!isConnected) {
            network = false;
            Intent login = new Intent(MainActivity.this, LoginActivity.class);
            login.putExtra("network", true);
            startActivity(login);
            finishActivity(1);
            finish();
        }
    }

    private void setupNavigation() {
        nvNavigation.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                if (item.getItemId() == R.id.nav_chats) {
                    setChatsFragment();
                } else if (item.getItemId() == R.id.nav_profile) {
                    setupProfileFragment();
                } else if (item.getItemId() == R.id.nav_photos) {
                    setupPhotosFragment();
                } else if (item.getItemId() == R.id.nav_log_out) {
                    logOut();
                } else if (item.getItemId() == R.id.nav_pms) {
                    setupPMChatsFragment();
                } else if (item.getItemId() == R.id.nav_banned) {
                    setupBannedFragment();
                } else if (item.getItemId() == R.id.nav_avatars) {
                    setupAvatarsFragment();
                } else if (item.getItemId() == R.id.nav_set_avatar) {
                    setupPurcheseAvatarFragment();
                } else if (item.getItemId() == R.id.nav_invisibles) {
                    setupInvisibleFragment();
                } else if (item.getItemId() == R.id.nav_gifts) {
                    setupGiftsFragment();
                } else if (item.getItemId() == R.id.nav_virt_zags_admin) {
                    setupVirtZagsAdminFragment();
                } else if (item.getItemId() == R.id.nav_virt_zags) {
                    setupVirtZagsFragment();
                } else if (item.getItemId() == R.id.nav_virt_balance) {
                    setupBalanceFragment();
                } else if (item.getItemId() == R.id.nav_settings) {
                    setupSettingsFragment();
                } else if (item.getItemId() == R.id.nav_banned_device) {
                    setupDevicesBanFragment();
                }else if (item.getItemId() == R.id.nav_rules) {
                    setupRulesFragment();
                }else if (item.getItemId() == R.id.nav_admins) {
                    setupAdminsFragment();
                }else if (item.getItemId() == R.id.nav_moders) {
                    setupModersFragment();
              //  }else if (item.getItemId() == R.id.nav_friends) {
                   // setupFriendsFragment();
              //  }else if (item.getItemId() == R.id.nav_my_gifts) {
               //     setupMyGiftsFragment();
                }
                drawerLayout.closeDrawers();
                return false;
            }
        });
    }

    private void logOut() {
        AskDialog.show(this, getString(R.string.log_out_title), getString(R.string.log_out_ask), new AskDialog.OnAskDialogClickListener() {
            @Override
            public void onAskClick(boolean acept) {
                if (acept) {
                    settingsHelper.clear();
                    startActivity(new Intent(MainActivity.this, LoginActivity.class));
                    setResult(2);
                    finish();
                }
            }
        });
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putSerializable("user", user);
    }

    @Override
    public void onGetProfile(UserResponse user) {
        profieLoad = false;
        if (user != null) {
            settingsHelper.setUserNic(user.getNic());
            if (settingsHelper.getUserType() != user.getType()) {
                settingsHelper.setUserType(user.getType());
                if (user.getType() == UserType.TYPE_BANNED) {
                    setChatsFragment();
                }
            }
            this.user = user;
            if(lastMessages<user.getUnreadedMessages()){
                boolean push = settingsHelper.getPush();
                if(push) {
                    Intent intent = new Intent(this, PMChatsActivity.class);
                    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                    PendingIntent pendingIntent = PendingIntent.getActivity(this, 0 /* Request code */, intent,
                            PendingIntent.FLAG_ONE_SHOT);
                    Uri defaultSoundUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
                    Notification.Builder notificationBuilder = new Notification.Builder(this)
                            .setSmallIcon(R.drawable.ic_message_white_18dp)
                            .setContentTitle(getString(R.string.app_name))
                            .setContentText(getString(R.string.new_pm_message))
                            .setLargeIcon(BitmapFactory.decodeResource(getResources(), R.mipmap.ic_launcher))
                            .setAutoCancel(true)
                            .setSound(defaultSoundUri)
                            .setContentIntent(pendingIntent);
                    NotificationManager notificationManager =
                            (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
                    notificationManager.notify(567, notificationBuilder.build());
                }
            }
            lastMessages = (int) user.getUnreadedMessages();
            TextView tvMs = (TextView) nvNavigation.getMenu().findItem(R.id.nav_pms).getActionView().findViewById(R.id.tv_drewer_messages_count);
            if (user.getUnreadedMessages() > 0) {
                tvMs.setText(user.getUnreadedMessages() + "");
                tvMs.setVisibility(View.VISIBLE);
            } else {
                tvMs.setText("");
                tvMs.setVisibility(View.INVISIBLE);
            }
            for (NoticeResponse nr : user.getNotices()) {
                boolean add = true;
                for (NoticeResponse nrr : notices) {
                    if (nr.getId().equals(nrr.getId())) {
                        add = false;
                        break;
                    }
                }
                if (add) {
                    notices.add(nr);
                }
            }
            showNotice();


        }
    }

    private void showNotice() {
        if (notices.size() > 0) {
            final NoticeResponse nr = notices.get(0);
            if (noticeInfoDialog.getDialog() == null || !noticeInfoDialog.getDialog().isShowing()) {
                noticeInfoDialog.show(R.string.notice, nr.getMessage(), this, new NoticeInfoDialog.OnAceptListener() {
                    @Override
                    public void onAcept() {
                        nShow = false;
                        helper.readNotice(nr.getId());
                    }
                });
            }
        }
    }

    @Override
    public void onGetLastRoom(RoomResponse roomResponse) {
        waitDialog.hide();
        if (roomResponse != null && roomResponse.getId() != null && roomResponse.getId().length() > 1) {
            setChatFragment(roomResponse);
        } else {
            setChatsFragment();
        }
    }

    @Override
    public void onReadNotice(boolean done, String id) {
        if (done) {
            for (int i = 0; i < notices.size(); i++) {
                if (notices.get(i).getId().equals(id)) {
                    notices.remove(i);

                }
            }
        }
        showNotice();
    }

    public void doBack() {
        if (getSupportFragmentManager().getBackStackEntryCount() > 1) {
            getSupportFragmentManager().popBackStack();
        } else if (getSupportFragmentManager().getBackStackEntryCount() == 1) {
            try {
                String name = getSupportFragmentManager().getBackStackEntryAt(0).getName();
                if (name != null && (name.equals(TAG_CHAT) || name.equals(TAG_PM_CHATS) || name.equals(TAG_CHATS))) {
                    App.leave(ChatFragment.curRoom);
                    AskDialog.show(this, R.string.close_app, R.string.cloase_app_ask, new AskDialog.OnAskDialogClickListener() {
                        @Override
                        public void onAskClick(boolean acept) {
                            if (acept) {
                                finish();
                            }
                        }
                    });
                } else {
                    finish();
                }

            } catch (Exception e) {
                Log.e("Chat Error", "do back", e);
                finish();
            }
        } else {
            finish();
        }
    }

    public void setBackListener(BackListener backListener) {
        this.backListener = backListener;
    }

    @Override
    public void onBackPressed() {
        if (backListener != null) {
            backListener.doBack();
        } else {
            doBack();
        }

    }

    private void setChatFragment(RoomResponse chat) {
        if (!isFinishing()) {
        Bundle args = new Bundle();
        args.putSerializable("room", chat);
        ChatFragment fragment = new ChatFragment();
        fragment.setArguments(args);
        FragmentManager fm = getSupportFragmentManager();
        for (int i = 0; i < fm.getBackStackEntryCount(); ++i) {
            fm.popBackStack();
        }
            getSupportFragmentManager().beginTransaction()
                    .replace(R.id.ll_base_main_layout_content, fragment, TAG_CHAT)
                    .addToBackStack(TAG_CHAT)
                    .commit();
        }
    }

    public void setChatsFragment() {
        if (!isFinishing()) {
            App.leave(ChatFragment.curRoom);
            Bundle args = new Bundle();
            args.putString("category", "-1");
            args.putSerializable("cat", null);
            ChatsFragment fragment = new ChatsFragment();
            fragment.setArguments(args);
            FragmentManager fm = getSupportFragmentManager();
            for (int i = 0; i < fm.getBackStackEntryCount(); ++i) {
                fm.popBackStack();
            }
            getSupportFragmentManager().beginTransaction()
                    .replace(R.id.ll_base_main_layout_content, fragment, TAG_CHATS)
                    .addToBackStack(TAG_CHATS)
                    .commit();
        }
    }

    private void setupMyGiftsFragment() {
        startActivityForResult(new Intent(this, UserGiftsActivity.class),1);
    }

    private void setupProfileFragment() {
        startActivityForResult(new Intent(this, ProfileActivity.class),1);
    }

    private void setupPhotosFragment() {
        startActivityForResult(new Intent(this, PhotosActivity.class),1);
    }

    private void setupPMChatsFragment() {
        startActivityForResult(new Intent(this, PMChatsActivity.class),1);
    }

    private void setupRulesFragment() {
        startActivityForResult(new Intent(this, RulesActivity.class),1);
    }
    private void setupAdminsFragment() {
        startActivityForResult(new Intent(this, AdminsActivity.class),1);
    }

    private void setupFriendsFragment() {
       // startActivityForResult(new Intent(this, FriendsActivity.class),1);
    }


    private void setupModersFragment() {
        startActivityForResult(new Intent(this, ModersActivity.class),1);
    }

    private void setupBannedFragment() {
        startActivityForResult(new Intent(this, BannedActivity.class),1);
    }

    private void setupInvisibleFragment() {
        startActivityForResult(new Intent(this, InvisibleActivity.class),1);
    }

    private void setupPurcheseAvatarFragment() {
        startActivityForResult(new Intent(this, SetAvatarActivity.class),1);
    }

    private void setupAvatarsFragment() {
        startActivityForResult(new Intent(this, AvatarsActivity.class),1);
    }

    private void setupVirtZagsAdminFragment() {
        startActivityForResult(new Intent(this, ZagsAdminActivity.class),1);
    }

    public void setupVirtZagsFragment() {
        startActivityForResult(new Intent(this, ZagsActivity.class),1);
    }

    public void setupBalanceFragment() {
        startActivityForResult(new Intent(this, AnotherBalanceActivity.class),1);
    }

    private void setupGiftsFragment() {
        startActivityForResult(new Intent(this, GiftsActivity.class),1);
    }

    private void setupSettingsFragment() {
        Bundle args = new Bundle();
        SettingsFragment fragment = new SettingsFragment();
        fragment.setArguments(args);
        FragmentManager fm = getSupportFragmentManager();
        for (int i = 0; i < fm.getBackStackEntryCount(); ++i) {
            fm.popBackStack();
        }
        getSupportFragmentManager().beginTransaction()
                .replace(R.id.ll_base_main_layout_content, fragment, TAG_SETTINGS)
                .addToBackStack(TAG_GIFTS)
                .commit();
    }

    private void setupDevicesBanFragment() {
        startActivityForResult(new Intent(this, DeviceBanActivity.class),1);
    }

    public void unlogin() {
        network = false;
        Intent login = new Intent(MainActivity.this, LoginActivity.class);
        login.putExtra("login", true);
        startActivity(login);
        finishActivity(1);
        finish();
    }

}
