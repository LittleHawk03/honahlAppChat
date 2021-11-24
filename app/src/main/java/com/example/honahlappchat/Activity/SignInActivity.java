package com.example.honahlappchat.Activity;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Toast;

import com.example.honahlappchat.R;
import com.example.honahlappchat.Utilities.Constants;
import com.example.honahlappchat.Utilities.PreferenceManager;

import com.example.honahlappchat.databinding.ActivitySignInBinding;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;


public class SignInActivity extends AppCompatActivity {

    private ActivitySignInBinding binding;
    private PreferenceManager preferenceManager;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().requestFeature(Window.FEATURE_ACTION_BAR);
        getSupportActionBar().hide();

        preferenceManager = new PreferenceManager(getApplicationContext());

        if (preferenceManager.getBoolean(Constants.KEY_IS_SIGNS_IN)){
            Intent intent = new Intent(getApplicationContext(), MainActivity.class);
            startActivity(intent);
            finish();
        }

        binding = ActivitySignInBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());



        statusColor();
        SetListeners();

    }

     private void statusColor()
    {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M)
        {
            getWindow().setStatusBarColor(getResources().getColor(R.color.blue,this.getTheme()));
        }
        else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT)
        {
            getWindow().setStatusBarColor(getResources().getColor(R.color.blue,this.getTheme()));
        }
    }

    private void SetListeners() {
        binding.RegisterButton.setOnClickListener(v ->
                startActivity(new Intent(getApplicationContext(),SignUpActivity.class)));
        binding.SignInButton.setOnClickListener(v -> {
            if (IsValidSignInAccept()){
                SignIn();
            }
        });

    }

    private void SignIn(){
        loading(true);
        FirebaseFirestore database = FirebaseFirestore.getInstance();
        database.collection(Constants.KEY_COLLECTION_USERS)
                .whereEqualTo(Constants.KEY_EMAIL,binding.SignInInputEmail.getText().toString())
                .whereEqualTo(Constants.KEY_PASSWORD,binding.SignInInputPass.getText().toString())
                .get().addOnCompleteListener(task -> {
                    if (task.isSuccessful() && task.getResult() != null && task.getResult().getDocuments().size() > 0){

                        DocumentSnapshot documentSnapshot = task.getResult().getDocuments().get(0);
                        preferenceManager.putBoolean(Constants.KEY_IS_SIGNS_IN,true);
                        preferenceManager.putString(Constants.KEY_USER_ID,documentSnapshot.getId());
                        preferenceManager.putString(Constants.KEY_EMAIL,documentSnapshot.getString(Constants.KEY_EMAIL));
                        preferenceManager.putString(Constants.KEY_NAME,documentSnapshot.getString(Constants.KEY_NAME));
                        preferenceManager.putString(Constants.KEY_IMAGE,documentSnapshot.getString(Constants.KEY_IMAGE));
                        Intent intent = new Intent(getApplicationContext(), MainActivity.class);
                        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                        startActivity(intent);
                    }else {
                        loading(false);
                        binding.textErrorSignIn1.setText("your username or password is incorrect");
                    }

                });
    }


    private Boolean IsValidSignInAccept(){
       if (binding.SignInInputEmail.getText().toString().trim().isEmpty()){
           binding.textErrorSignIn1.setText("You need to enter your username or email");
           return false;
       }else if (binding.SignInInputPass.getText().toString().trim().isEmpty()){
           binding.textErrorSignIn2.setText("You need to enter your password");
           return false;
       }else{
           binding.textErrorSignIn1.setText(null);
           binding.textErrorSignIn2.setText(null);
           return true;
       }

    }

    private void loading(Boolean BeLoading){
        if (BeLoading){
            binding.SignInButton.setVisibility(View.INVISIBLE);
            binding.ProgressBar.setVisibility(View.VISIBLE);
        }else {
            binding.SignInButton.setVisibility(View.VISIBLE);
            binding.ProgressBar.setVisibility(View.INVISIBLE);
        }
    }




}