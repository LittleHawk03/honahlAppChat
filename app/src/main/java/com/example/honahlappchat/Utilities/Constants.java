package com.example.honahlappchat.Utilities;

import java.util.HashMap;

public class Constants {
    public static final String KEY_COLLECTION_USERS = "users";
    public static final String KEY_NAME = "name";
    public static final String KEY_EMAIL = "email";
    public static final String KEY_PASSWORD = "password";
    public static final String KEY_PREFERENCE_NAME = "chatAppPreference";
    public static final String KEY_IS_SIGNS_IN = "IsSignIn";
    public static final String KEY_USER_ID = "userId";
    public static final String KEY_IMAGE = "image";
    public static final String KEY_FCM_TOKEN = "fcmToken";
    public static final String KEY_USER = "user";
    public static final String KEY_COLLECTION_CHAT = "chat";
    public static final String KEY_SENDER_ID = "senderId";
    public static final String KEY_RECEIVER_ID = "receivedId";
    public static final String KEY_MESSAGE = "message";
    public static final String KEY_TIMESTAMP = "timestamp";
    public static final String KEY_COLLECTION_CONVERSATIONS = "conversations";
    public static final String KEY_SENDER_NAME = "senderImage";
    public static final String KEY_RECEIVER_NAME = "receiverName";
    public static final String KEY_SENDER_IMAGE = "senderImage";
    public static final String KEY_RECEIVER_IMAGE = "receiverImage";
    public static final String KEY_LAST_MESSAGE = "lastMessage";
    public static final String KEY_AVAILABILITY = "availability";

    /**key su uy quyen tu xa*/
    public static final String REMOTE_MSG_AUTHORIZATION = "Authorization";
    /**key kieu du lieu tin nhan */
    public static final String REMOTE_MSG_CONTENT_TYPE = "Content-Type";

    public static final String REMOTE_MSG_DATA = "data";

    /** mot key de dang ky ids */
    public static final String REMOTE_MSG_REGISTRATION_IDS = "registration_ids";


    public static HashMap<String, String> remoteMsgHeaders = null;
    public static HashMap<String, String> getRemoteMsgHeaders(){
        if (remoteMsgHeaders == null){
            remoteMsgHeaders = new HashMap<>();
            remoteMsgHeaders.put(
                    REMOTE_MSG_AUTHORIZATION,
                    "key=AAAAVqep3-k:APA91bHDABSh0RA8izeIiJPzHAzLJhoKg4PcVvU2-WG92FL9PuPpEPKgRwFTEmr_ACCIn3k5aD1ejZn6UGXWlI49WZAq576N6vA2dND57n1SBGanANgM2Je5XF29U804ao1o1DkWzKvJ"
            );
            remoteMsgHeaders.put(
                    REMOTE_MSG_CONTENT_TYPE,
                    "application/json"
            );
        }
        return remoteMsgHeaders;
    }




}
