package com.example.honahlappchat.Adapter;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Base64;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.honahlappchat.Listener.ConversionListener;
import com.example.honahlappchat.databinding.ItemRecentChatBinding;
import com.example.honahlappchat.models.ChatMessage;
import com.example.honahlappchat.models.UsersM;

import java.util.List;

public class RecentConversationAdapte extends RecyclerView.Adapter<RecentConversationAdapte.ConversionVIewholder>{

    private final List<ChatMessage> chatMessages;
    private final ConversionListener conversionListener;

    public RecentConversationAdapte(List<ChatMessage> chatMessages, ConversionListener conversionListener) {
        this.chatMessages = chatMessages;
        this.conversionListener = conversionListener;
    }

    @NonNull
    @Override
    public ConversionVIewholder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        ItemRecentChatBinding itemRecentChatBinding = ItemRecentChatBinding.inflate(
            LayoutInflater.from(parent.getContext()), parent ,false
        );

        return new ConversionVIewholder(itemRecentChatBinding);
    }

    @Override
    public void onBindViewHolder(@NonNull ConversionVIewholder holder, int position) {
        holder.setData(chatMessages.get(position));
    }

    @Override
    public int getItemCount() {
        return chatMessages.size();
    }


    class ConversionVIewholder extends RecyclerView.ViewHolder {

        ItemRecentChatBinding binding;

        ConversionVIewholder(ItemRecentChatBinding itemRecentChatBinding){
            super(itemRecentChatBinding.getRoot());
            binding = itemRecentChatBinding;
        }

        void setData(ChatMessage chatMessage){
            binding.imageProfile.setImageBitmap(getConvertConversionImage(chatMessage.ConversionImage));
            binding.TextName.setText(chatMessage.ConversionName);
            binding.textRecentChat.setText(chatMessage.message);
            binding.getRoot().setOnClickListener(v -> {
                UsersM usersM = new UsersM();
                usersM.id = chatMessage.conversionId;
                usersM.name = chatMessage.ConversionName;
                usersM.image = chatMessage.ConversionImage;
                conversionListener.onConversionClicked(usersM);
            });
        }
    }

    private Bitmap getConvertConversionImage(String encodeImage){
        byte[] bytes = Base64.decode(encodeImage, Base64.DEFAULT);
        return BitmapFactory.decodeByteArray(bytes,0,bytes.length);
    }
}
