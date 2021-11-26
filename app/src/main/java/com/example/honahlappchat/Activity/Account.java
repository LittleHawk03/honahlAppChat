package com.example.honahlappchat.Activity;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;

import android.app.Dialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Base64;
import android.view.View;
import android.view.Window;

import android.widget.Button;
import android.widget.Toast;

import com.example.honahlappchat.ActivitySetting.ManagerName;
import com.example.honahlappchat.ActivitySetting.ManagerPassword;
import com.example.honahlappchat.ActivitySetting.ManagerUsername;
import com.example.honahlappchat.R;
import com.example.honahlappchat.Utilities.Constants;
import com.example.honahlappchat.Utilities.PreferenceManager;
import com.example.honahlappchat.databinding.ActivityAccountBinding;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

import java.io.ByteArrayOutputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;


public class Account extends BaseActivity {

    private ActivityAccountBinding binding;
    private PreferenceManager preferenceManager;
    private Dialog dialog;
    private String encodeImage;
    private FirebaseFirestore database;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().requestFeature(Window.FEATURE_ACTION_BAR);
        getSupportActionBar().hide();
        binding = ActivityAccountBinding.inflate(getLayoutInflater());
        preferenceManager = new PreferenceManager(getApplicationContext());
        database = FirebaseFirestore.getInstance();
        dialog = new Dialog(this);

        setContentView(binding.getRoot());
        statusColor();
        setListener();

    }

    /** Load du lieu lien tuc moi khi gia tri thay doi */
    @Override
    protected void onResume() {
        super.onResume();
        Load();
    }



    private void statusColor()
    {
        getWindow().setStatusBarColor(getResources().getColor(R.color.gainsBoro,this.getTheme()));
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
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
                startActivity(intent);
            }
        });

        binding.SignOutFrame.setOnClickListener(v -> {
           Dialog();
        });

        binding.NameFrame.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getApplicationContext(), ManagerName.class);
                startActivity(intent);

            }
        });


        binding.AccountFrame.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getApplicationContext(), ManagerUsername.class);
                startActivity(intent);
            }
        });

        binding.PassChangeFrame.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getApplicationContext(), ManagerPassword.class);
                startActivity(intent);
            }
        });

        binding.imageAvt.setOnClickListener(v -> {
            Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
            intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
            pickImage.launch(intent);
        });

    }

    private void showToast(String message){
        Toast.makeText(getApplicationContext(),message,Toast.LENGTH_LONG).show();
    }



    /**
     *
     * phần này copy tu cái activity sign in và khong hieu gi
     *
     * dinh dang lai cai anh*/
    private String encodeImage(Bitmap bitmap){
        int previewWidth = 150;
        int previewHeight = bitmap.getHeight() * previewWidth / bitmap.getWidth();
        Bitmap previewBitmap = Bitmap.createScaledBitmap(bitmap,previewWidth,previewHeight,false);
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        previewBitmap.compress(Bitmap.CompressFormat.JPEG,50,byteArrayOutputStream);
        byte[] bytes = byteArrayOutputStream.toByteArray();
        return Base64.encodeToString(bytes,Base64.DEFAULT);

    }


    /** lay code tren mang nen khong hieu gi luon
     *
     * nhưng co thể hiểu đây là hàm lấy ảnh từ trong thư viện rồi add lên
     * */
    private final ActivityResultLauncher<Intent> pickImage = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(), result -> {
                if (result.getResultCode() == RESULT_OK){
                    if (result.getData() != null){
                        Uri ImageUri = result.getData().getData();
                        try {
                            InputStream inputStream = getContentResolver().openInputStream(ImageUri);
                            Bitmap bitmap = BitmapFactory.decodeStream(inputStream);
                            encodeImage = encodeImage(bitmap);
                            updateImage();
                        }catch (FileNotFoundException e){
                            e.printStackTrace();
                        }
                    }
                }
            });

    /**
     *  cap nhat avata phan tren
     * */

    private void updateImage(){
        DocumentReference documentReference = database.collection(Constants.KEY_COLLECTION_USERS)
                .document( preferenceManager.getString(Constants.KEY_USER_ID));

        documentReference.update(Constants.KEY_IMAGE,encodeImage).addOnSuccessListener(unused -> {
            preferenceManager.putString(Constants.KEY_IMAGE , encodeImage);
            showToast("update image is successfully");
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


    /** thoát cứng không cần xóa token */
    private void LogOut(){
        preferenceManager.putBoolean(Constants.KEY_IS_SIGNS_IN,false);
        Intent intent = new Intent(getApplicationContext(),SignInActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(intent);
        finish();

    }
}