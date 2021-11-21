package com.example.honahlappchat.Adapter;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Base64;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.honahlappchat.Listener.Userlistener;
import com.example.honahlappchat.databinding.ItemUserBinding;
import com.example.honahlappchat.models.UsersM;

import java.util.List;

public class UserAdapter extends RecyclerView.Adapter<UserAdapter.UserViewHolder>{

    private final List<UsersM> users;
    private final Userlistener userlistener;

    public UserAdapter(List<UsersM> users, Userlistener userlistener) {
        this.users = users;
        this.userlistener = userlistener;
    }


    // đây là các phương thức bắt buộc của adapter recycle view
    @NonNull
    @Override
    public UserViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        ItemUserBinding itemUserBinding = ItemUserBinding.inflate(
                LayoutInflater.from(parent.getContext()),parent,false
        );
        return new UserViewHolder(itemUserBinding);
    }

    @Override
    public void onBindViewHolder(@NonNull UserViewHolder holder, int position) {
        holder.SetUserData(users.get(position));
    }

    @Override
    public int getItemCount() {
        return users.size();
    }


    //tao view holder cho recycle view
    class UserViewHolder extends RecyclerView.ViewHolder {

        ItemUserBinding binding;

        UserViewHolder(ItemUserBinding itemUserBinding){
            super(itemUserBinding.getRoot());
            binding = itemUserBinding;
        }

        void SetUserData(UsersM users){
            binding.TextName.setText(users.name);
            binding.textEmail.setText(users.email);
            binding.imageProfile.setImageBitmap(getImage(users.image));
            binding.getRoot().setOnClickListener(v -> userlistener.onUsetClicked(users));
        }
    }


    // de decode anh ve ding dang string cua bit map
    private Bitmap getImage(String encodeImage){
        byte[] bytes = Base64.decode(encodeImage, Base64.DEFAULT);
        return BitmapFactory.decodeByteArray(bytes,0,bytes.length);

    }
}
