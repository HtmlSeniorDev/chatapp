package ru.readme.chatapp.util;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonParseException;
import com.google.gson.JsonSerializationContext;
import com.google.gson.JsonSerializer;

import java.lang.reflect.Type;
import java.util.Date;
import java.util.concurrent.TimeUnit;

import okhttp3.OkHttpClient;
import retrofit2.Retrofit;
import retrofit2.converter.scalars.ScalarsConverterFactory;

public class Network {

    public final static String LINK = "http://82.146.46.204:8080/";
    private static ChatNetworkInterface chatNetworkInterface;
    public static String avatarLink(String id) {
        return LINK + "avatars/get/" + id + ".png";
    }
    public static String photoLink(String id) {
        return LINK + "photos/get/" + id + ".jpeg";
    }
    public static String giftLink(String id) {
        return LINK + "gifts/get/" + id + ".png";
    }

    public static synchronized ChatNetworkInterface getChatNetworkInterface() {
        if (chatNetworkInterface == null) {

            final OkHttpClient okHttpClient = new OkHttpClient.Builder()
                    .connectTimeout(20, TimeUnit.SECONDS)
                    .writeTimeout(20, TimeUnit.SECONDS)
                    .readTimeout(30, TimeUnit.SECONDS)
                    .build();

            Retrofit retrofit = new Retrofit.Builder()
                    .baseUrl(LINK)
                    .addConverterFactory(ScalarsConverterFactory.create())
                    .client(okHttpClient)
                    .build();
            chatNetworkInterface = retrofit.create(ChatNetworkInterface.class);
        }
        return chatNetworkInterface;
    }

    public static Gson gson() {
        GsonBuilder builder = new GsonBuilder();
        builder.registerTypeAdapter(Date.class, new JsonDeserializer<Date>() {
            public Date deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) throws JsonParseException {
                return new Date(json.getAsJsonPrimitive().getAsLong());
            }
        });
        builder.registerTypeAdapter(Date.class, new JsonSerializer<Date>() {

            @Override
            public JsonElement serialize(Date src, Type typeOfSrc, JsonSerializationContext context) {
                return context.serialize(src.getTime());
            }
        });
        return builder.create();
    }
}
