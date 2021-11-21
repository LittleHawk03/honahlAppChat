package com.example.honahlappchat.Adapter;

import android.graphics.Bitmap;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.honahlappchat.databinding.ItemContainerReceiverBinding;
import com.example.honahlappchat.databinding.ItemContainerSendchatBinding;
import com.example.honahlappchat.models.ChatMessage;

import java.util.List;

public class ChatAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder>{

    private final List<ChatMessage> chatMessages;
    private final Bitmap recivedBitmap;
    private final String senderId;

    private static final int VIEW_TYPE_SENT = 1;
    private static final int VIEW_TYPE_RECEIVED = 2;

    public ChatAdapter(List<ChatMessage> chatMessages, Bitmap recivedBitmap, String senderId) {
        this.chatMessages = chatMessages;
        this.recivedBitmap = recivedBitmap;
        this.senderId = senderId;
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        if (viewType == VIEW_TYPE_SENT){
           return new SentMessageViewHolder(ItemContainerSendchatBinding.inflate(
                    LayoutInflater.from(parent.getContext()),parent,false
                )
           );
        }else {
            return new ReceiverMessageViewHolder(ItemContainerReceiverBinding.inflate(
                    LayoutInflater.from(parent.getContext()),parent,false
                )
            );
        }

    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        if (getItemViewType(position) == VIEW_TYPE_SENT){
            ((SentMessageViewHolder) holder).setData(chatMessages.get(position));
        }else {
            ((ReceiverMessageViewHolder) holder).setData(chatMessages.get(position),recivedBitmap);
        }
    }

    @Override
    public int getItemCount() {
        return chatMessages.size();
    }

    public int getItemViewType(int position){
        if (chatMessages.get(position).senderID.equals(senderId)){
            return VIEW_TYPE_SENT;
        } else {
            return VIEW_TYPE_RECEIVED;
        }
    }


    static class SentMessageViewHolder extends RecyclerView.ViewHolder{


        private final ItemContainerSendchatBinding binding;

        SentMessageViewHolder(ItemContainerSendchatBinding itemContainerSendchatBinding){
            super(itemContainerSendchatBinding.getRoot());
            binding = itemContainerSendchatBinding;
        }

        void setData(ChatMessage chatMessage){
            binding.textSendMess.setText(chatMessage.message);
            binding.textDateTime.setText(chatMessage.dateTime);
        }

    }

    static class ReceiverMessageViewHolder extends RecyclerView.ViewHolder{

        private final ItemContainerReceiverBinding binding;

        ReceiverMessageViewHolder(ItemContainerReceiverBinding itemContainerReceiverBinding){
            super(itemContainerReceiverBinding.getRoot());
            binding = itemContainerReceiverBinding;
        }

        private void setData(ChatMessage chatMessage,Bitmap receiverPhoto){
            binding.textRecMess.setText(chatMessage.message);
            binding.textRecDateTime.setText(chatMessage.dateTime);
            binding.imageForProfile.setImageBitmap(receiverPhoto);
        }
    }


}
