package com.example.honahlappchat.Activity;

import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.widget.Toast;

import com.example.honahlappchat.R;
import com.example.honahlappchat.databinding.ActivitySignInBinding;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;

public class SignInActivity extends AppCompatActivity {

    private ActivitySignInBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivitySignInBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        SetListeners();

    }

    private void SetListeners() {
        binding.RegisterButton.setOnClickListener(v ->
                startActivity(new Intent(getApplicationContext(),SignUpActivity.class)));
        binding.SignInButton.setOnClickListener(v -> AddDataToFireBase());

    }

    private void AddDataToFireBase(){
        FirebaseFirestore database = FirebaseFirestore.getInstance();
        HashMap<String, Object> data = new HashMap<>();
        data.put("first_name","Manh Duc");
        data.put("last_name", "Nguyen");
        database.collection("users")
                .add(data).addOnSuccessListener(documentReference -> {
                    Toast.makeText(getApplicationContext(),"DataInsert",Toast.LENGTH_LONG).show();
                }).addOnFailureListener(exception -> {
                    Toast.makeText(getApplicationContext(),exception.getMessage(),Toast.LENGTH_LONG).show();
        });

    }

}