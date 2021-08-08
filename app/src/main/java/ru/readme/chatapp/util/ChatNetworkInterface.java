package ru.readme.chatapp.util;

import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.Header;
import retrofit2.http.Multipart;
import retrofit2.http.POST;
import retrofit2.http.Part;
import retrofit2.http.Path;

public interface ChatNetworkInterface {

    @POST("/friends")
    Call<String> getFriends(@Header("token") String token, @Body String request);

    @POST("/friends/add")
    Call<String> addFriend(@Header("token") String token, @Body String request);

    @POST("/friends/delete")
    Call<String> delFriend(@Header("token") String token, @Body String request);

    @POST("/login")
    Call<String> login(@Body String request,@Header("version") String version);

    @POST("/registration/checklogin")
    Call<String> checkLogin(@Body String request,@Header("version") String version);

    @POST("/registration/reg")
    Call<String> registration(@Body String request);

    @POST("/message/addattachment")
    Call<String> addAttachment(@Header("token") String token, @Body String request);

    @POST("/message/send")
    Call<String> sendMessage(@Header("token") String token, @Body String request);

    @POST("/message/pm")
    Call<String> getPersonalChat(@Header("token") String token, @Body String request);

    @POST("/message/pm/leave")
    Call<String> leavePersonalChat(@Header("token") String token, @Body String request);

    @POST("/message/delete")
    Call<String> deleteMessage(@Header("token") String token, @Body String request);

    @Multipart
    @POST("/message/addattachment/upload")
    Call<String> uploadAttachment(@Part("token") RequestBody token, @Part("id") RequestBody id, @Part MultipartBody.Part file);

    @POST("/rooms")
    Call<String> getRooms(@Header("token") String token, @Body String request);

    @POST("/rooms/room/kick")
    Call<String> kickUser(@Header("token") String token, @Body String request);

    @POST("/rooms/room/unkick")
    Call<String> unKickUser(@Header("token") String token, @Body String request);

    @POST("/rooms/room/kicked")
    Call<String> getKickedUsers(@Header("token") String token, @Body String request);

    @POST("/rooms/room/users")
    Call<String> getRoomUsers(@Header("token") String token, @Body String request);

    @POST("/rooms/create/category")
    Call<String> createCategory(@Header("token") String token, @Body String request);

    @POST("/rooms/update/category")
    Call<String> updateCategory(@Header("token") String token, @Body String request);

    @POST("/rooms/delete/category")
    Call<String> deleteCategory(@Header("token") String token, @Body String request);

    @POST("/rooms/lastroom")
    Call<String> getLastRoom(@Header("token") String token, @Body String request);

    @POST("/rooms/create/room")
    Call<String> createRoom(@Header("token") String token, @Body String request);

    @POST("/rooms/update/room")
    Call<String> updateRoom(@Header("token") String token, @Body String request);

    @POST("/rooms/delete/room")
    Call<String> deleteRoom(@Header("token") String token, @Body String request);

    @POST("/rooms/leave")
    Call<String> leaveRoom(@Header("token") String token, @Body String request);

    @POST("/rooms/room/messages")
    Call<String> getMessages(@Header("token") String token, @Body String request);

    @POST("/rooms/room/pm/messages")
    Call<String> getPMMessages(@Header("token") String token, @Body String request);

    @POST("/rooms/pms")
    Call<String> getPMs(@Header("token") String token, @Body String request);

    @POST("/user")
    Call<String> getUser(@Header("token") String token, @Body String request);

    @POST("/user/find")
    Call<String> findUsers(@Header("token") String token, @Body String request);

    @POST("/user/checknic")
    Call<String> checkNic(@Header("token") String token, @Body String request);

    @POST("/user/changepassword")
    Call<String> changePassword(@Header("token") String token, @Body String request);

    @POST("/user/update")
    Call<String> updateUser(@Header("token") String token, @Body String request);

    @POST("/user/set/type")
    Call<String> setUserType(@Header("token") String token, @Body String request);

    @POST("/ban/ban")
    Call<String> banUser(@Header("token") String token, @Body String request);

    @POST("/ban/superban")
    Call<String> superbanUser(@Header("token") String token, @Body String request);

    @POST("/ban/unban")
    Call<String> unbanUser(@Header("token") String token, @Body String request);

    @POST("/ban/users")
    Call<String> getBannedUsers(@Header("token") String token, @Body String request);

    @POST("/admins")
    Call<String> getAdmins(@Header("token") String token, @Body String request);

    @POST("/moders")
    Call<String> getModers(@Header("token") String token, @Body String request);

    @POST("/rules")
    Call<String> getRules(@Header("token") String token, @Body String request);

    @GET("/avatars/getlastupdate")
    Call<String> getAvatarsLastUpdate();

    @GET("/avatars/getlist")
    Call<String> getAvatarsList();

    @GET("/avatars/get/{id}.png")
    Call<ResponseBody> getAvatar(@Path("id") String id);

    @POST("/avatars/add")
    Call<String> addAvatar(@Header("token") String token, @Body String request);

    @POST("/avatars/update")
    Call<String> updateAvatar(@Header("token") String token, @Body String request);

    @POST("/avatars/remove")
    Call<String> removeAvatar(@Header("token") String token, @Body String request);

    @Multipart
    @POST("/avatars/upload")
    Call<String> uploadAvatar(@Part("token") RequestBody token, @Part("id") RequestBody id, @Part MultipartBody.Part file);

    @POST("/avatars/setdefault")
    Call<String> setDefaultAvatars(@Header("token") String token, @Body String request);

    @POST("/avatars/getdescriptions")
    Call<String> getAvatarsDescription(@Header("token") String token, @Body String request);

    @POST("/avatars/purchese")
    Call<String> purcheseAvatar(@Header("token") String token, @Body String request);

    @POST("/invisible/set")
    Call<String> setInvisible(@Header("token") String token, @Body String request);

    @POST("/invisible/get")
    Call<String> getInvisibles(@Header("token") String token, @Body String request);

    @POST("/invisible/unset")
    Call<String> unsetInvisible(@Header("token") String token, @Body String request);

    @POST("/notice/add")
    Call<String> sendNotice(@Header("token") String token, @Body String request);

    @POST("/notice/read")
    Call<String> readNotice(@Header("token") String token, @Body String request);

    @GET("/photos/get/{id}.jpeg")
    Call<ResponseBody> getPhoto(@Path("id") String id);

    @Multipart
    @POST("/photos/add")
    Call<String> addPhoto(@Part("token") RequestBody token, @Part MultipartBody.Part file, @Part("request") RequestBody request);

    @POST("/photos")
    Call<String> getPhotos(@Header("token") String token, @Body String request);

    @POST("/photos/delete")
    Call<String> deletePhoto(@Header("token") String token, @Body String request);

    @POST("/photos/setforuser")
    Call<String> setForUser(@Header("token") String token, @Body String request);

    @POST("/photos/folder/add")
    Call<String> addPhotoFolder(@Header("token") String token, @Body String request);

    @POST("/photos/folder/delete")
    Call<String> deletePhotoFolder(@Header("token") String token, @Body String request);

    @POST("/photos/folder/update")
    Call<String> updatePhotoFolder(@Header("token") String token, @Body String request);

    @POST("/photos/folder")
    Call<String> getPhotoFolder(@Header("token") String token, @Body String request);

    @Multipart
    @POST("/gifts/add")
    Call<String> addGift(@Part("token") RequestBody token, @Part MultipartBody.Part file, @Part("request") RequestBody request);

    @Multipart
    @POST("/gifts/update")
    Call<String> updateGift(@Part("token") RequestBody token, @Part MultipartBody.Part file, @Part("request") RequestBody request);

    @POST("/gifts/delete")
    Call<String> deleteGift(@Header("token") String token, @Body String request);

    @POST("/gifts")
    Call<String> getGifts(@Header("token") String token, @Body String request);

    @POST("/gifts/send")
    Call<String> sendGift(@Header("token") String token, @Body String request);

    @POST("/zags/price/set")
    Call<String> setZagsPrice(@Header("token") String token, @Body String request);

    @POST("/zags/price")
    Call<String> getZagsPrice(@Header("token") String token, @Body String request);

    @POST("/zags")
    Call<String> zagsRequest(@Header("token") String token, @Body String request);

    @POST("/zags/cancel")
    Call<String> cancelZagsRequest(@Header("token") String token, @Body String request);

    @POST("/deviceban/list")
    Call<String> getBannedDevices(@Header("token") String token, @Body String request);

    @POST("/deviceban/ban")
    Call<String> banDevice(@Header("token") String token, @Body String request);

    @POST("/deviceban/unban")
    Call<String> unbanDevice(@Header("token") String token, @Body String request);

    @POST("/user/addmoney")
    Call<String> AddMoney(@Header("token") String token, @Body String request);

}
