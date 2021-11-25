package com.example.honahlappchat.Activity;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Build;
import android.os.Bundle;
import android.util.Base64;
import android.view.View;
import android.view.Window;
import android.widget.Toast;

import com.example.honahlappchat.Adapter.ChatAdapter;

import com.example.honahlappchat.NetWork.ApiClient;
import com.example.honahlappchat.NetWork.ApiService;
import com.example.honahlappchat.R;
import com.example.honahlappchat.Utilities.Constants;
import com.example.honahlappchat.Utilities.PreferenceManager;
import com.example.honahlappchat.databinding.ActivityChatBinding;
import com.example.honahlappchat.models.ChatMessage;
import com.example.honahlappchat.models.UsersM;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.firebase.firestore.DocumentChange;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;


import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Objects;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class activity_chat extends BaseActivity {

    private ActivityChatBinding binding;
    private UsersM receiverUsers;
    private List<ChatMessage> chatMessages;
    private ChatAdapter chatAdapter;
    private PreferenceManager preferenceManager;
    private FirebaseFirestore database;
    private String conversionId = null;
    private Boolean beReceiverAvailable = false;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().requestFeature(Window.FEATURE_ACTION_BAR);
        getSupportActionBar().hide();
        binding = ActivityChatBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());


        statusColor();
        setListeners();
        LoadReceiverDetail();
        dataInit();
        listenMessage();



    }

    private void statusColor()
    {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M)
        {
            getWindow().setStatusBarColor(getResources().getColor(R.color.blue,this.getTheme()));
        }
        else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP)
        {
            getWindow().setStatusBarColor(getResources().getColor(R.color.blue,this.getTheme()));
        }
    }

    private void dataInit(){
        preferenceManager = new PreferenceManager(getApplicationContext());
        chatMessages = new ArrayList<>();
        chatAdapter = new ChatAdapter(chatMessages,
                getBitmapFromString(receiverUsers.image),
                preferenceManager.getString(Constants.KEY_USER_ID)
        );
        binding.chatRecyclerView.setAdapter(chatAdapter);
        database = FirebaseFirestore.getInstance();
    }

    private void sendMessage(){
        HashMap<String, Object> message = new HashMap<>();
        message.put(Constants.KEY_SENDER_ID,preferenceManager.getString(Constants.KEY_USER_ID));
        message.put(Constants.KEY_RECEIVER_ID,receiverUsers.id);
        message.put(Constants.KEY_MESSAGE,binding.inputMess.getText().toString());
        message.put(Constants.KEY_TIMESTAMP,new Date());
        database.collection(Constants.KEY_COLLECTION_CHAT).add(message);
        if (conversionId != null){
            updateConversion(binding.inputMess.getText().toString());
        }
        else {
            HashMap<String, Object> conversion = new HashMap<>();
            conversion.put(Constants.KEY_SENDER_ID,preferenceManager.getString(Constants.KEY_USER_ID));
            conversion.put(Constants.KEY_SENDER_NAME,preferenceManager.getString(Constants.KEY_NAME));
            conversion.put(Constants.KEY_RECEIVER_ID,receiverUsers.id);
            conversion.put(Constants.KEY_RECEIVER_NAME,receiverUsers.name);
            conversion.put(Constants.KEY_RECEIVER_IMAGE,receiverUsers.image);
            conversion.put(Constants.KEY_LAST_MESSAGE,binding.inputMess.getText().toString());
            conversion.put(Constants.KEY_TIMESTAMP,new Date());
            addConversion(conversion);
        }


        /** lay tren mang gan nhu la khong hieu gi*/
        if (!beReceiverAvailable){
            try{
                JSONArray tokens = new JSONArray();
                tokens.put(receiverUsers.token);

                JSONObject data = new JSONObject();
                data.put(Constants.KEY_USER_ID, preferenceManager.getString(Constants.KEY_USER_ID));
                data.put(Constants.KEY_NAME, preferenceManager.getString(Constants.KEY_NAME));
                data.put(Constants.KEY_FCM_TOKEN, preferenceManager.getString(Constants.KEY_FCM_TOKEN));
                data.put(Constants.KEY_MESSAGE, binding.inputMess.getText().toString());

                JSONObject body = new JSONObject();
                body.put(Constants.REMOTE_MSG_DATA, data);
                body.put(Constants.REMOTE_MSG_REGISTRATION_IDS, tokens);
                sendNotification(body.toString());
            } catch (Exception exception){
                showToast(exception.getMessage());
            }
        }
        binding.inputMess.setText(null);
    }

    private void showToast(String message){
        Toast.makeText(getApplicationContext(), message, Toast.LENGTH_SHORT).show();
    }

    /** phan gui thong bao sieu kho hieu no se bao gom ca API (ApiClient trong package NetWork) */
    private void sendNotification(String messageBody){
        ApiClient.getClient().create(ApiService.class).sendMessage(
                Constants.getRemoteMsgHeaders(),messageBody
        ).enqueue(new Callback<String>() {
            @Override
            public void onResponse(@NonNull Call<String> call,@NonNull Response<String> response) {
                if (response.isSuccessful()){
                    try {
                        if (response.body() != null){
                            JSONObject responseJson = new JSONObject(response.body());
                            JSONArray results = responseJson.getJSONArray("results");
                            if (responseJson.getInt("failure") == 1){
                                JSONObject error = (JSONObject) results.get(0);
                                showToast(error.getString("error"));
                                return;
                            }
                        }
                    }catch (JSONException e){
                        e.printStackTrace();
                    }
                    showToast("Notification is successful");
                }else {
                    showToast("error: " + response.code());
                }
            }

            @Override
            public void onFailure( @NonNull  Call<String> call,@NonNull Throwable t) {
                showToast(t.getMessage());
            }
        });


    }

    /**de lay su kien availability cua mot user nao do co dan online hay khong*/
    private void listenAvailabilityOfReceiver(){
        database.collection(Constants.KEY_COLLECTION_USERS).document(
                receiverUsers.id
        ).addSnapshotListener(activity_chat.this, (value, error) -> {
           if (error != null){
               return;
           }
           if (value != null){
               if (value.getLong(Constants.KEY_AVAILABILITY)  != null){
                   int availability = Objects.requireNonNull(
                           value.getLong(Constants.KEY_AVAILABILITY)
                   ).intValue();
                   beReceiverAvailable = availability == 1;
               }
               receiverUsers.token = value.getString(Constants.KEY_FCM_TOKEN);
               if (receiverUsers.image == null){
                   receiverUsers.image = value.getString(Constants.KEY_IMAGE);
                   chatAdapter.setRecivedBitmap(getBitmapFromString(receiverUsers.image));
                   chatAdapter.notifyItemRangeChanged(0,chatMessages.size());
               }
           }
            if (beReceiverAvailable){
                binding.textOnline.setVisibility(View.VISIBLE);
            }else {
                binding.textOnline.setVisibility(View.GONE);
            }
        });
    }

    /** ham de lay su kien 2 chieu mot chieu nguoi nhan mot chieu la nguoi gui de day len firebase  */
    private void listenMessage() {
        database.collection(Constants.KEY_COLLECTION_CHAT)
                .whereEqualTo(Constants.KEY_SENDER_ID,preferenceManager.getString(Constants.KEY_USER_ID))
                .whereEqualTo(Constants.KEY_RECEIVER_ID,receiverUsers.id)
                .addSnapshotListener(eventListener);
        database.collection(Constants.KEY_COLLECTION_CHAT)
                .whereEqualTo(Constants.KEY_SENDER_ID,receiverUsers.id)
                .whereEqualTo(Constants.KEY_RECEIVER_ID, preferenceManager.getString(Constants.KEY_USER_ID))
                .addSnapshotListener(eventListener);
    }

    /** tao recycle view cho chat them vao recycleView*/
    private final EventListener<QuerySnapshot> eventListener = (value, error) -> {
        if (error != null){
            return;
        }
        if (value != null){
            int count = chatMessages.size();

            for (DocumentChange documentChange : value.getDocumentChanges()){
                if (documentChange.getType() == DocumentChange.Type.ADDED){
                    ChatMessage chatMessage = new ChatMessage();
                    chatMessage.senderID = documentChange.getDocument().getString(Constants.KEY_SENDER_ID);
                    chatMessage.receiverId = documentChange.getDocument().getString(Constants.KEY_RECEIVER_ID);
                    chatMessage.message = documentChange.getDocument().getString(Constants.KEY_MESSAGE);
                    chatMessage.dateTime = getReadTime(documentChange.getDocument().getDate(Constants.KEY_TIMESTAMP));
                    chatMessage.dateObject = documentChange.getDocument().getDate(Constants.KEY_TIMESTAMP);
                    chatMessages.add(chatMessage);
                }
            }
            Collections.sort(chatMessages, (obj1,obj2) -> obj1.dateObject.compareTo(obj2.dateObject));
            if (count == 0){
                chatAdapter.notifyDataSetChanged();
            }
            else{
                chatAdapter.notifyItemRangeChanged(chatMessages.size(),chatMessages.size());
                binding.chatRecyclerView.smoothScrollToPosition(chatMessages.size() - 1);
            }
            binding.chatRecyclerView.setVisibility(View.VISIBLE);
        }
        binding.SendProgress.setVisibility(View.GONE);
        if (conversionId == null){
            checkForConversion();
        }
    };



    private Bitmap getBitmapFromString(String encodedImage){
        if (encodedImage != null){
            byte[] bytes = Base64.decode(encodedImage, Base64.DEFAULT);
            return BitmapFactory.decodeByteArray(bytes,0,bytes.length);
        }else {
            return null;
        }

    }

    /** chuyen anh tu ding dang String song kieu bitmap */
    private void LoadReceiverDetail(){
        receiverUsers = (UsersM) getIntent().getSerializableExtra(Constants.KEY_USER);
        binding.textNameSend.setText(receiverUsers.name);
        binding.imageRecProfile.setImageBitmap(getBitmapFromString(receiverUsers.image));
    }

    /**Tao su kien khi nhan nut gui*/
    private void setListeners(){
        binding.imageBack2.setOnClickListener(v -> onBackPressed());
        binding.buttonSend.setOnClickListener(v -> sendMessage());
    }

    /** dinh danh ngay thang nam*/
    private String getReadTime(Date date){
        return new SimpleDateFormat("MMMM dd, yyyy - hh:mm a", Locale.getDefault()).format(date);
    }

    /**them conversion xem o ham sendMessage()*/
    private void addConversion(HashMap<String, Object> conversion){
        database.collection(Constants.KEY_COLLECTION_CONVERSATIONS)
                .add(conversion)
                .addOnSuccessListener(documentReference -> conversionId = documentReference.getId());
    }

    /**cap nhat 2 khoa tin nhan cuoi va thoi gian len database (collection)*/
    private void updateConversion(String message){
        DocumentReference documentReference = database.collection(Constants.KEY_COLLECTION_CONVERSATIONS)
                .document(conversionId);
        documentReference.update(
          Constants.KEY_LAST_MESSAGE,message,
          Constants.KEY_TIMESTAMP,new Date()
        );
    }

    /** neu conversion co gia tri la null no se gui xuong ham nay */
    private void checkForConversion(){
        if (chatMessages.size() != 0){
            checkForConversionRemotely(
                    preferenceManager.getString(Constants.KEY_USER_ID),receiverUsers.id
            );
            checkForConversionRemotely(
                    receiverUsers.id,preferenceManager.getString(Constants.KEY_USER_ID)
            );
        }
    }

    // tim trong collection conversatons nowi ma co hai khoa sender va receiver
    private void checkForConversionRemotely(String senderId,String receiverId){
        database.collection(Constants.KEY_COLLECTION_CONVERSATIONS)
                .whereEqualTo(Constants.KEY_SENDER_ID,senderId)
                .whereEqualTo(Constants.KEY_RECEIVER_ID,receiverId)
                .get()
                .addOnCompleteListener(ConversionOnCompleteListener);
    }


    private final OnCompleteListener<QuerySnapshot> ConversionOnCompleteListener = task -> {
        if (task.isSuccessful() && task.getResult() != null && task.getResult().getDocuments().size() > 0){
            DocumentSnapshot documentSnapshot = task.getResult().getDocuments().get(0);
            conversionId = documentSnapshot.getId();
        }
    };



    @Override
    protected void onResume() {
        super.onResume();
        listenAvailabilityOfReceiver();
    }
}