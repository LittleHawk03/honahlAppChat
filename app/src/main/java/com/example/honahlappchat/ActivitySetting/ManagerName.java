package com.example.honahlappchat.ActivitySetting;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.widget.Toast;

import com.example.honahlappchat.Activity.BaseActivity;
import com.example.honahlappchat.R;
import com.example.honahlappchat.Utilities.Constants;
import com.example.honahlappchat.Utilities.PreferenceManager;
import com.example.honahlappchat.databinding.ActivityManagerNameBinding;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QuerySnapshot;

public class ManagerName extends BaseActivity {

    private ActivityManagerNameBinding binding;
    private FirebaseFirestore database;
    private PreferenceManager preferenceManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().requestFeature(Window.FEATURE_ACTION_BAR);
        getSupportActionBar().hide();

        binding = ActivityManagerNameBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        database = FirebaseFirestore.getInstance();
        preferenceManager = new PreferenceManager(getApplicationContext());

        setListener();
        statusColor();


    }


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


    private void setListener(){
        binding.ManagerNameBack.setOnClickListener(v -> onBackPressed());
        binding.bottomConfirmChangeName.setOnClickListener(v -> {
            if (IsValidChange()){
                checkPassword();
            }
        });
    }

    private void showToast(String message){
        Toast.makeText(getApplicationContext(),message,Toast.LENGTH_LONG).show();
    }

    /** check mat khau  */
    private void checkPassword(){

        database.collection(Constants.KEY_COLLECTION_USERS)
                .whereEqualTo(Constants.KEY_EMAIL,preferenceManager.getString(Constants.KEY_EMAIL))
                .whereEqualTo(Constants.KEY_PASSWORD,binding.confirmPass.getText().toString())
                .get().addOnCompleteListener(task -> {
                    if (task.isSuccessful() && task.getResult() != null && task.getResult().getDocuments().size() > 0){
                        updateName();
                    }else{
                        showToast("your password is incorrect");
                    }
        });


    }

    private void updateName(){

        DocumentReference documentReference = database.collection(Constants.KEY_COLLECTION_USERS).document(
                preferenceManager.getString(Constants.KEY_USER_ID)
        );
        documentReference.update(Constants.KEY_NAME,binding.yourNewNameEditText.getText().toString())
                .addOnSuccessListener(unused -> {
                    preferenceManager.putString(Constants.KEY_NAME, binding.yourNewNameEditText.getText().toString());
                    showToast("change successfully");
                    onBackPressed();
                });
    }

    private Boolean IsValidChange(){
        if (binding.yourNewNameEditText.getText().toString().trim().isEmpty()){
            binding.textSupport.setTextColor(getColor(R.color.redError));
            return false;
        } else if (binding.confirmPass.getText().toString().isEmpty()){
            binding.textSupport2.setTextColor(getColor(R.color.redError));
            return false;
        }
        else {
            return true;
        }
    }


}