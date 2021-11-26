package com.example.honahlappchat.ActivitySetting;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.Window;
import android.widget.Toast;

import com.example.honahlappchat.R;
import com.example.honahlappchat.Utilities.Constants;
import com.example.honahlappchat.Utilities.PreferenceManager;
import com.example.honahlappchat.databinding.ActivityManagerUsernameBinding;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QuerySnapshot;

public class ManagerUsername extends AppCompatActivity {

    private ActivityManagerUsernameBinding binding;
    private FirebaseFirestore database;
    private PreferenceManager preferenceManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().requestFeature(Window.FEATURE_ACTION_BAR);
        getSupportActionBar().hide();
        binding = ActivityManagerUsernameBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        database = FirebaseFirestore.getInstance();
        preferenceManager = new PreferenceManager(getApplicationContext());

        statusColor();
        SetListener();

    }

    private void statusColor()
    {
        getWindow().setStatusBarColor(getResources().getColor(R.color.gainsBoro,this.getTheme()));
    }

    private void SetListener(){
        binding.ManagerUserBack.setOnClickListener(v -> onBackPressed());

        binding.bottomConfirmChangeUser.setOnClickListener(v -> {
            if (IsValidChange()){
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
                .whereEqualTo(Constants.KEY_PASSWORD,binding.confirmPass.getText().toString())
                .get().addOnCompleteListener(task -> {
                    if (task.isSuccessful() && task.getResult() != null && task.getResult().getDocuments().size() > 0){
                        checkUserAndEmail();
                    }
                    else {
                        showToast("you password is incorrect");
                    }
        });
    }


    private void checkUserAndEmail(){

        CollectionReference UsersReference = database.collection(Constants.KEY_COLLECTION_USERS);
        Query query = UsersReference.whereEqualTo(Constants.KEY_EMAIL,binding.yourNewUsernameEditText.getText().toString());
        query.get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if (task.isSuccessful()){
                    for (DocumentSnapshot documentSnapshot : task.getResult()){
                        String user = documentSnapshot.getString(Constants.KEY_EMAIL);

                        if (user.equals(binding.yourNewUsernameEditText.getText().toString())){
                            showToast("UserName is exit");
                        }
                    }
                }
                if (task.getResult().size() == 0){
                    updateUsername();
                }
            }
        });


    }

    private void updateUsername(){

        DocumentReference documentReference = database.collection(Constants.KEY_COLLECTION_USERS).document(
                preferenceManager.getString(Constants.KEY_USER_ID)
        );
        documentReference.update(Constants.KEY_EMAIL,binding.yourNewUsernameEditText.getText().toString())
                .addOnSuccessListener(unused -> {
                    showToast("change successfully");
                    preferenceManager.putString(Constants.KEY_EMAIL,binding.yourNewUsernameEditText.getText().toString());
                    onBackPressed();
                });
    }




    private Boolean IsValidChange(){
        if (binding.yourNewUsernameEditText.getText().toString().trim().isEmpty()){
            binding.textSupport.setTextColor(getColor(R.color.redError));
            return false;
        }
        else if (binding.confirmPass.getText().toString().trim().isEmpty()){
            binding.textSupport2.setTextColor(getColor(R.color.redError));
            return false;
        }
        else {
            return true;
        }
    }


}