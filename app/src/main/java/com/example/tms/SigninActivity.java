package com.example.tms;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.example.tms.databinding.ActivitySigninBinding;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class SigninActivity extends AppCompatActivity {

    private EditText ediEmail,ediPassword;
    private String email,password, ukey;

    private FirebaseAuth mAuth;
    private ProgressBar progress_signin;

    private DatabaseReference dbReference;
    StudentModel sm = new StudentModel();
    CheckInternet internet = new CheckInternet();
    private ActivitySigninBinding binding;
    private boolean isAllFilled=false;
    private String status;
    @Override
    public void onStart() {
        super.onStart();
        // Check if user is signed in (non-null) and update UI accordingly.
        FirebaseUser currentUser = mAuth.getCurrentUser();
        if(currentUser != null){
            currentUser.reload();
        }
    }
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);
        super.onCreate(savedInstanceState);
        binding = ActivitySigninBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        getSupportActionBar().hide();
        internet.InternetConnectivityChecker(this);
        internet.start();
        progress_signin = findViewById(R.id.progress_signin);
        mAuth = FirebaseAuth.getInstance();
        dbReference = FirebaseDatabase.getInstance().getReference();

        binding.txtFForgetPassword.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(SigninActivity.this, ForgetPassword.class);
                startActivity(intent);
                finish();
            }
        });
    }
    public void toSignup(View v){
        Intent intent = new Intent(SigninActivity.this, SignupActivity.class);
        startActivity(intent);
        finish();
    }

    public void Signin(View v){
        email = binding.edEmailLg.getText().toString().trim().toLowerCase();
        password = binding.edPasswordLg.getText().toString().trim();

        if(email.isBlank() && password.isBlank()){
            binding.tilEmail.setError("Email is Required");
            binding.tilPassword.setError("Password is Required");
            isAllFilled = false;
        }
        else {
            binding.tilEmail.setError("");
            binding.tilPassword.setError("");
            if(isAllFilled = CheckAllFields()) {

                mAuth.signInWithEmailAndPassword(email, password)
                        .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                            @Override
                            public void onComplete(@NonNull Task<AuthResult> task) {
                                if (task.isSuccessful()) {
                                    dbReference.child("Students").orderByChild("email").equalTo(email).addListenerForSingleValueEvent(new ValueEventListener() {
                                        @Override
                                        public void onDataChange(@NonNull DataSnapshot snapshot) {
                                            for (DataSnapshot childSnapshot : snapshot.getChildren()) {
                                                ukey = childSnapshot.getKey();
                                                status = childSnapshot.child("status").getValue().toString();
                                                sm.setEmail((String) childSnapshot.child("email").getValue());
                                                sm.setFullName((String) childSnapshot.child("fullName").getValue());
                                                sm.setPhoneNumber((String) childSnapshot.child("phoneNumber").getValue());
                                                sm.setStandards((String) childSnapshot.child("standards").getValue());
                                                sm.setSubjects((ArrayList<String>) childSnapshot.child("subjects").getValue());
                                                sm.setTcName((String) childSnapshot.child("tcName").getValue());
                                            }
                                            if(status.equals("Disable")){
                                                binding.progressSignin.setVisibility(View.GONE);
                                                Toast.makeText(SigninActivity.this, "Your Account is Disable, Please Contact Your Administration", Toast.LENGTH_SHORT).show();
                                            }else{
                                                SharedPreferences sharedPreferences = getSharedPreferences("SystemPre", MODE_PRIVATE);
                                                SharedPreferences.Editor editor = sharedPreferences.edit();
                                                editor.putBoolean("isLogin", true);
                                                editor.putString("email", email);
                                                editor.putString("tcName", sm.getTcName());
                                                editor.putString("Key", ukey);
                                                editor.putString("StudentName", sm.getFullName());
                                                editor.putString("StudentStd", sm.getStandards());
                                                editor.putString("StudentPhone", sm.getPhoneNumber());
//                                                Log.i("UKEY", "-_-_-_-_-_-_-_-_-_-_-_-_" + ukey);
                                                editor.commit();
                                                Intent intent = new Intent(SigninActivity.this, DashboardActivity.class);
                                                startActivity(intent);
                                                finish();
                                            }
                                        }

                                        @Override
                                        public void onCancelled(@NonNull DatabaseError error) {

                                        }
                                    });
                                    progress_signin.setVisibility(View.VISIBLE);

                                } else {
                                    // If sign in fails, display a message to the user.
                                    Toast.makeText(SigninActivity.this, "Authentication failed.",
                                            Toast.LENGTH_SHORT).show();
                                }
                            }
                        });
            }


        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        internet.stop();
    }

    private boolean CheckAllFields() {
        if(binding.edEmailLg.getText().toString().isBlank()){
            binding.tilEmail.setError("Email is Required");
            return false;
        }if(binding.edPasswordLg.getText().toString().isBlank()){
            binding.tilPassword.setError("Password is Required");
            return false;
        }
            binding.tilEmail.setError("");
            binding.tilPassword.setError("");
        return true;
    }

}