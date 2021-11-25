package com.example.honahlappchat.Activity;


import android.app.Dialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Build;
import android.os.Bundle;
import android.util.Base64;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.Toast;

import com.example.honahlappchat.Adapter.RecentConversationAdapte;
import com.example.honahlappchat.Listener.ConversionListener;
import com.example.honahlappchat.R;
import com.example.honahlappchat.Utilities.Constants;
import com.example.honahlappchat.Utilities.PreferenceManager;
import com.example.honahlappchat.databinding.ActivityMainBinding;
import com.example.honahlappchat.databinding.SignoutDialogBinding;
import com.example.honahlappchat.models.ChatMessage;
import com.example.honahlappchat.models.UsersM;
import com.google.firebase.firestore.DocumentChange;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.messaging.FirebaseMessaging;


import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;


public class MainActivity extends BaseActivity implements ConversionListener {

    private ActivityMainBinding binding;
    private PreferenceManager preferenceManager;
    private Dialog dialog;
    private List<ChatMessage> Conversation;
    private RecentConversationAdapte conversationAdapte;
    private FirebaseFirestore database;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().requestFeature(Window.FEATURE_ACTION_BAR);
        getSupportActionBar().hide();
        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        preferenceManager = new PreferenceManager(getApplicationContext());
        dialog = new Dialog(this);

        initData();
        LoadProfile();
        statusColor();
        SetListeners();
        getToken();
        listenConversation();


    }

    @Override
    public void onBackPressed() {
        Intent intent = new Intent(Intent.ACTION_MAIN);
        intent.addCategory(Intent.CATEGORY_HOME);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent);
    }

    private void SetListeners(){
        binding.imageProfile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getApplicationContext(),Account.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                startActivity(intent);
            }
        });
        binding.SignOutButton.setOnClickListener(v -> {
            Dialog();
        });
        binding.newChat.setOnClickListener(v ->
                startActivity(new Intent(getApplicationContext(),userAcivity.class))
        );
    }

    private void initData(){
        Conversation = new ArrayList<>();
        conversationAdapte = new RecentConversationAdapte(Conversation, this);
        binding.RecentConversationRecyclerView.setAdapter(conversationAdapte);
        database = FirebaseFirestore.getInstance();
    }


    private void statusColor()
    {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M)
        {
            getWindow().setStatusBarColor(getResources().getColor(R.color.MainStatus,this.getTheme()));
        }
        else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP)
        {
            getWindow().setStatusBarColor(getResources().getColor(R.color.MainStatus,this.getTheme()));
        }
    }



    private void LoadProfile(){
        if (Constants.KEY_IMAGE != null){
            byte[] bytes = Base64.decode(preferenceManager.getString(Constants.KEY_IMAGE),Base64.DEFAULT);
            Bitmap bitmap = BitmapFactory.decodeByteArray(bytes,0,bytes.length);
            binding.imageProfile.setImageBitmap(bitmap);
        }

    }

    private void showToast(String message){
        Toast.makeText(getApplicationContext(),message,Toast.LENGTH_LONG).show();
    }

    private void listenConversation(){
        database.collection(Constants.KEY_COLLECTION_CONVERSATIONS)
                .whereEqualTo(Constants.KEY_SENDER_ID,preferenceManager.getString(Constants.KEY_USER_ID))
                .addSnapshotListener(eventListener);
        database.collection(Constants.KEY_COLLECTION_CONVERSATIONS)
                .whereEqualTo(Constants.KEY_RECEIVER_ID,preferenceManager.getString(Constants.KEY_USER_ID))
                .addSnapshotListener(eventListener);

    }

    private final EventListener<QuerySnapshot> eventListener = (value, error) -> {
        if (error != null){
            return;
        }
        if (value != null){
            for (DocumentChange documentChange : value.getDocumentChanges()){
                if (documentChange.getType() == DocumentChange.Type.ADDED){
                    String senderId = documentChange.getDocument().getString(Constants.KEY_SENDER_ID);
                    String receiverId = documentChange.getDocument().getString(Constants.KEY_RECEIVER_ID);
                    ChatMessage chatMessage = new ChatMessage();
                    chatMessage.receiverId = receiverId;
                    chatMessage.senderID = senderId;

                    if (preferenceManager.getString(Constants.KEY_USER_ID).equals(senderId)){
                        chatMessage.ConversionImage = documentChange.getDocument().getString(Constants.KEY_RECEIVER_IMAGE);
                        chatMessage.ConversionName = documentChange.getDocument().getString(Constants.KEY_RECEIVER_NAME);
                        chatMessage.conversionId = documentChange.getDocument().getString(Constants.KEY_RECEIVER_ID);
                    }
                    else {
                        chatMessage.ConversionImage = documentChange.getDocument().getString(Constants.KEY_SENDER_IMAGE);
                        chatMessage.ConversionName = documentChange.getDocument().getString(Constants.KEY_SENDER_NAME);
                        chatMessage.conversionId = documentChange.getDocument().getString(Constants.KEY_SENDER_ID);
                    }
                    chatMessage.message = documentChange.getDocument().getString(Constants.KEY_LAST_MESSAGE);
                    chatMessage.dateObject = documentChange.getDocument().getDate(Constants.KEY_TIMESTAMP);
                    Conversation.add(chatMessage);
                }else if (documentChange.getType() == DocumentChange.Type.MODIFIED){
                    for (int i = 0; i <= Conversation.size(); i++){
                        String senderId = documentChange.getDocument().getString(Constants.KEY_SENDER_ID);
                        String receiverId = documentChange.getDocument().getString(Constants.KEY_RECEIVER_ID);
                        if (Conversation.get(i).senderID.equals(senderId) && Conversation.get(i).receiverId.equals(receiverId)){
                            Conversation.get(i).message = documentChange.getDocument().getString(Constants.KEY_LAST_MESSAGE);
                            Conversation.get(i).dateObject = documentChange.getDocument().getDate(Constants.KEY_TIMESTAMP);
                            break;
                        }

                    }
                }
            }
            Collections.sort(Conversation, (obj1,obj2) -> obj2.dateObject.compareTo(obj1.dateObject));
            conversationAdapte.notifyDataSetChanged();
            binding.RecentConversationRecyclerView.smoothScrollToPosition(0);
            binding.RecentConversationRecyclerView.setVisibility(View.VISIBLE);
            binding.recentProgress.setVisibility(View.GONE);
        }
    };


    private void getToken(){
        FirebaseMessaging.getInstance().getToken().addOnSuccessListener(this::updateToken);
    }

    private void updateToken(String token){
        preferenceManager.putString(Constants.KEY_FCM_TOKEN, token);
        FirebaseFirestore database = FirebaseFirestore.getInstance();
        DocumentReference documentReference = database.collection(Constants.KEY_COLLECTION_USERS).document(
            preferenceManager.getString(Constants.KEY_USER_ID)
        );
        documentReference.update(Constants.KEY_FCM_TOKEN,token)
                .addOnFailureListener(e -> showToast("unsuccessfully"));
    }

    private void Dialog(){


        dialog.setContentView(R.layout.signout_dialog);
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));


        Button buttonSignOut = dialog.findViewById(R.id.ButtonSignOut);
        Button buttonCancel = dialog.findViewById(R.id.ButtonCancel);

        buttonCancel.setOnClickListener(v -> {
            dialog.dismiss();
        });

        buttonSignOut.setOnClickListener(v -> {
            LogOut();
        });

        dialog.show();


    }

    private void LogOut(){
        FirebaseFirestore database = FirebaseFirestore.getInstance();
        DocumentReference documentReference = database.collection(Constants.KEY_COLLECTION_USERS).document(
                preferenceManager.getString(Constants.KEY_USER_ID)
        );
        HashMap<String ,Object> updates = new HashMap<>();
        updates.put(Constants.KEY_FCM_TOKEN, FieldValue.delete());
        documentReference.update(updates).addOnSuccessListener(unused -> {
           preferenceManager.clear();
           startActivity(new Intent(getApplicationContext(),SignInActivity.class));
           finish();
        });

    }

    @Override
    public void onConversionClicked(UsersM usersM) {
        Intent intent = new Intent(getApplicationContext(),activity_chat.class);
        intent.putExtra(Constants.KEY_USER,usersM);
        startActivity(intent);
    }
}