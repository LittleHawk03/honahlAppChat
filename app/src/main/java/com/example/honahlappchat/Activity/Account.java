package com.example.honahlappchat.Activity;

import androidx.appcompat.app.AppCompatActivity;

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
import android.view.WindowManager;
import android.widget.Button;

import com.example.honahlappchat.R;
import com.example.honahlappchat.Utilities.Constants;
import com.example.honahlappchat.Utilities.PreferenceManager;
import com.example.honahlappchat.databinding.ActivityAccountBinding;
import com.example.honahlappchat.databinding.ActivityMainBinding;
import com.example.honahlappchat.databinding.SignoutDialogBinding;

public class Account extends AppCompatActivity {

    private ActivityAccountBinding binding;
    private PreferenceManager preferenceManager;
    private Dialog dialog;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().requestFeature(Window.FEATURE_ACTION_BAR);
        getSupportActionBar().hide();
        binding = ActivityAccountBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        statusColor();

        preferenceManager = new PreferenceManager(getApplicationContext());
        dialog = new Dialog(this);

        setListener();
        Load();


    }


//    private void statusColor(){
//        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
//            Window w = getWindow();
//            w.setFlags(WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS, WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS);
//        }
//        else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M)
//        {
//            Window w = getWindow();
//            w.setFlags(WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS, WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS);
//        }
//    }

    private void statusColor()
    {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M)
        {
            getWindow().setStatusBarColor(getResources().getColor(R.color.gainsBoro,this.getTheme()));
        }
        else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP)
        {
            getWindow().setStatusBarColor(getResources().getColor(R.color.gainsBoro,this.getTheme()));
        }
    }


    private void Load(){
        binding.OutputTextName.setText(preferenceManager.getString(Constants.KEY_NAME));
        binding.OutPutUserName.setText(preferenceManager.getString(Constants.KEY_EMAIL));
        byte[] bytes = Base64.decode(preferenceManager.getString(Constants.KEY_IMAGE),Base64.DEFAULT);
        Bitmap bitmap = BitmapFactory.decodeByteArray(bytes,0, bytes.length);
        binding.imageAvt.setImageBitmap(bitmap);
    }

    private void setListener(){

        binding.ButtonBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getApplicationContext(),MainActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                startActivity(intent);
            }
        });

        binding.SignOutFrame.setOnClickListener(v -> {
           Dialog();
        });

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
        preferenceManager.putBoolean(Constants.KEY_IS_SIGNS_IN,false);
        Intent intent = new Intent(getApplicationContext(),SignInActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(intent);
        finish();

    }
}