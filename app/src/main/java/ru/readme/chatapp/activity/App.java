package ru.readme.chatapp.activity;

import android.app.Application;
import android.os.Build;
import android.os.StrictMode;
import android.util.Log;

import java.lang.reflect.Method;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import ru.readme.chatapp.fragment.ChatFragment;
import ru.readme.chatapp.object.CoderUser;
import ru.readme.chatapp.object.requests.BaseRequest;
import ru.readme.chatapp.object.requests.RoomRequest;
import ru.readme.chatapp.util.CheckNick;
import ru.readme.chatapp.util.DID;
import ru.readme.chatapp.util.Network;
import ru.readme.chatapp.util.SettingsHelper;
import ru.readme.chatapp.util.TokenHelper;
import ru.readme.chatapp.helper.AvatarsLoader;

public class App extends Application {
    public static String avasPath = "";
    public static String generateSystemCode = "-1";
    private static App app;
   // public static String sID = null;

    public static void leave(final String room) {
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    Thread.sleep(3000);
                } catch (Exception e) {

                }
                if (ChatFragment.curRoom == null || !ChatFragment.curRoom.equals(room)) {
                    final RoomRequest request = new RoomRequest();
                    request.setRoom(room);
                    new SettingsHelper(App.app).setLastChat(null);
                    Log.i("Chat Leave", "leave from " + room);
                    new TokenHelper(app).getToken(new TokenHelper.OnGetTokenListener() {
                        @Override
                        public void onGetToken(String token) {
                            Call<String> call = Network.getChatNetworkInterface().leaveRoom(token, BaseRequest.code(request, CoderUser.get(app)));
                            if (call != null) {
                                call.enqueue(new Callback<String>() {
                                    @Override
                                    public void onResponse(Call<String> call, Response<String> response) {

                                    }

                                    @Override
                                    public void onFailure(Call<String> call, Throwable t) {

                                    }
                                });
                            } else {

                            }
                        }
                    });
                }
            }
        }).start();

    }

    @Override
    public void onCreate() {
        super.onCreate();
        app = this;
        if (Build.VERSION.SDK_INT >= 24) {
            try {
                Method m = StrictMode.class.getMethod("disableDeathOnFileUriExposure");
                m.invoke(null);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        generateSystemCode = CheckNick.getID();
        avasPath = getFilesDir() + "/avatars/";
        new AvatarsLoader(this).check();
    }

}
