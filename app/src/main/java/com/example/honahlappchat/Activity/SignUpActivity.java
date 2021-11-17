package com.example.honahlappchat.Activity;

import androidx.activity.result.ActivityResultCaller;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Base64;
import android.view.View;
import android.view.Window;
import android.widget.Toast;

import com.example.honahlappchat.R;
import com.example.honahlappchat.Utilities.Constants;
import com.example.honahlappchat.Utilities.PreferenceManager;
import com.example.honahlappchat.databinding.ActivitySignUpBinding;
import com.google.firebase.firestore.FirebaseFirestore;

import java.io.ByteArrayOutputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.util.HashMap;

public class SignUpActivity extends AppCompatActivity {

    private ActivitySignUpBinding binding;
    private PreferenceManager preferenceManager;
    private String encodeImage;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().requestFeature(Window.FEATURE_ACTION_BAR);
        getSupportActionBar().hide();

        binding = ActivitySignUpBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        preferenceManager = new PreferenceManager(getApplicationContext());


        statuscolor();
        SetListener();

    }

    private void statuscolor()
    {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M)
        {
            getWindow().setStatusBarColor(getResources().getColor(R.color.gainsboro,this.getTheme()));
        }
        else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP)
        {
            getWindow().setStatusBarColor(getResources().getColor(R.color.gainsboro,this.getTheme()));
        }
    }

    private void SetListener(){
        binding.signInText.setOnClickListener(v -> onBackPressed());
        binding.SignUpButton.setOnClickListener(v -> {
            if (IsValidSignUpAccept()){
                Register();
            }
        });
        binding.layoutImage.setOnClickListener(v -> {
            Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
            intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
            pickImage.launch(intent);
        });
    }

    private void Register(){
        loading(true);
        FirebaseFirestore database = FirebaseFirestore.getInstance();
        HashMap<String, Object> users = new HashMap<>();
        users.put(Constants.KEY_NAME,binding.RegisterInputName.getText().toString());
        users.put(Constants.KEY_EMAIL,binding.InputEmail.getText().toString());
        users.put(Constants.KEY_PASSWORD,binding.InputPassWord.getText().toString());
        users.put(Constants.KEY_IMAGE, encodeImage);
        database.collection(Constants.KEY_COLLECTION_USERS).add(users)
                .addOnSuccessListener(documentReference -> {
                    loading(false);
                    preferenceManager.putBoolean(Constants.KEY_IS_SIGNS_IN,true);
                    preferenceManager.putString(Constants.KEY_USER_ID,documentReference.getId());
                    preferenceManager.putString(Constants.KEY_NAME,binding.RegisterInputName.getText().toString());
                    preferenceManager.putString(Constants.KEY_IMAGE,encodeImage);
                    Intent intent = new Intent(getApplicationContext(), MainActivity.class);
                    intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                    startActivity(intent);
                })
                .addOnFailureListener(exception -> {
                    loading(false);
                    showToast(exception.getMessage());
                });


    }

    //dinh dang lai cai anh
    private String encodeImage(Bitmap bitmap){
        int previewWidth = 150;
        int previewHeight = bitmap.getHeight() * previewWidth / bitmap.getWidth();
        Bitmap previewBitmap = Bitmap.createScaledBitmap(bitmap,previewWidth,previewHeight,false);
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        previewBitmap.compress(Bitmap.CompressFormat.JPEG,50,byteArrayOutputStream);
        byte[] bytes = byteArrayOutputStream.toByteArray();
        return Base64.encodeToString(bytes,Base64.DEFAULT);

    }


    // lay code tren mang nen khong hieu gi luon
    private final ActivityResultLauncher<Intent> pickImage = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(), result -> {
                if (result.getResultCode() == RESULT_OK){
                    if (result.getData() != null){
                        Uri ImageUri = result.getData().getData();
                        try {
                            InputStream inputStream = getContentResolver().openInputStream(ImageUri);
                            Bitmap bitmap = BitmapFactory.decodeStream(inputStream);
                            binding.imageProfile.setImageBitmap(bitmap);
                            binding.ImageText.setVisibility(View.GONE);
                            encodeImage = encodeImage(bitmap);
                        }catch (FileNotFoundException e){
                            e.printStackTrace();
                        }
                    }
                }
            });

    private void showToast(String message){
        Toast.makeText(getApplicationContext(), message, Toast.LENGTH_SHORT).show();
    }

    private Boolean IsValidSignUpAccept(){
        if (encodeImage == null){
            showToast("select photo");
            return false;
        }else if (binding.RegisterInputName.getText().toString().trim().isEmpty()){
            showToast("Name cant be empty");
            return false;
        }else if (binding.InputEmail.getText().toString().trim().isEmpty()){
            showToast("Email or username cant be empty ");
            return false;
        }else if (binding.InputPassWord.getText().toString().trim().isEmpty()){
            showToast("password cant be empty ");
            return false;
        }else if(binding.InputConfirmPassWord.getText().toString().trim().isEmpty()){
            showToast("you must confirm your password ");
            return false;
//        }else if (binding.InputConfirmPassWord.getText().toString().trim().equals(binding.InputPassWord.getText().toString())){
//            showToast(" confirm password is incorrect ");
//            return false;
        }else {

            return true;
        }
    }

    private void loading(Boolean BeLoading){
        if (BeLoading){
           binding.SignUpButton.setVisibility(View.INVISIBLE);
           binding.ProgressBar.setVisibility(View.VISIBLE);
        }else{
            binding.SignUpButton.setVisibility(View.VISIBLE);
            binding.ProgressBar.setVisibility(View.INVISIBLE);
        }
    }


}