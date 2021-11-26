package com.example.honahlappchat.ActivitySetting;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.Window;
import android.widget.Toast;

import com.example.honahlappchat.R;
import com.example.honahlappchat.Utilities.Constants;
import com.example.honahlappchat.Utilities.PreferenceManager;
import com.example.honahlappchat.databinding.ActivityManagerPasswordBinding;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

public class ManagerPassword extends AppCompatActivity {

    private ActivityManagerPasswordBinding binding;
    private FirebaseFirestore database;
    private PreferenceManager preferenceManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().requestFeature(Window.FEATURE_ACTION_BAR);
        getSupportActionBar().hide();
        binding = ActivityManagerPasswordBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        database = FirebaseFirestore.getInstance();
        preferenceManager = new PreferenceManager(getApplicationContext());

        statusColor();
        setListener();

    }

    private void statusColor()
    {
        getWindow().setStatusBarColor(getResources().getColor(R.color.gainsBoro,this.getTheme()));
    }

    private void setListener(){
        binding.ManagerPassBack.setOnClickListener(v -> onBackPressed());
        binding.bottomConfirmChangePass.setOnClickListener(v -> {
            if(IsValidChange()){
                CheckPassword();
            }
        });

    }

    private void showToast(String message){
        Toast.makeText(getApplicationContext(),message,Toast.LENGTH_LONG).show();
    }

    private void CheckPassword(){

        database.collection(Constants.KEY_COLLECTION_USERS)
                .whereEqualTo(Constants.KEY_EMAIL,preferenceManager.getString(Constants.KEY_EMAIL))
                .whereEqualTo(Constants.KEY_PASSWORD,binding.recentPassEdittext.getText().toString())
                .get().addOnCompleteListener(task -> {
                    if (task.isSuccessful() && task.getResult() != null && task.getResult().getDocuments().size() > 0){
                        updatePass();
                    }
                    else {
                        showToast("your password is incorrect");
                    }
        });
    }

    private void updatePass(){
        DocumentReference documentReference = database.collection(Constants.KEY_COLLECTION_USERS).document(
            preferenceManager.getString(Constants.KEY_USER_ID)
        );
        documentReference.update(Constants.KEY_PASSWORD, binding.NewPassEdittext.getText().toString())
                .addOnSuccessListener(unused -> {
                    showToast("change successfully");
                    onBackPressed();
                });
    }



    private Boolean IsValidChange(){
        if (binding.recentPassEdittext.getText().toString().trim().isEmpty()){
            showToast("please enter your recent password");
            return false;
        } else if(binding.NewPassEdittext.getText().toString().trim().isEmpty()){
            showToast("please enter your new password");
            return false;
        }else if (binding.NewPassEdittext.getText().toString().length() < 6){
            showToast("Password must be more than 6 characters");
            return false;
        }else if (binding.confirmNewPass.getText().toString().trim().isEmpty()){
            showToast("please confirm your new password");
            return false;
        }else if(!binding.NewPassEdittext.getText().toString().equals(binding.confirmNewPass.getText().toString())){
            showToast("your confirm password is incorrect");
            return false;
        }
        else {
            return true;
        }
    }
}