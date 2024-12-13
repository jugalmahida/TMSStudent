package com.example.tms;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;

import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.example.tms.databinding.ActivitySignupBinding;
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
import com.google.i18n.phonenumbers.NumberParseException;
import com.google.i18n.phonenumbers.PhoneNumberUtil;
import com.google.i18n.phonenumbers.Phonenumber;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class SignupActivity extends AppCompatActivity {

    private String name,email,password,tcname,phoneNumber,stStandard;
    private boolean isResEmail,isRedPhoneNumber;
    private ActivitySignupBinding binding;
    private String[] std = {"Select Standard", "1","2","3","4","5","6","7","8","9","10"};

//    private String[] tclasses = {"Select Tution Classes","ABC","DEF","GHI","JKL","MNO","PQR","STU","VWX","YZ"};
    private List<String> tclasses = new ArrayList<>();
    ArrayList<Integer> sublist = new ArrayList<>();
    boolean[] selectedSub;
    String[] subArray = {"Gujarati", "English", "Maths", "Science", "Social Science", "Hindi", "Environment","Sanskrit","Computer"};

    private FirebaseAuth mAuth;

    private DatabaseReference rootDatabaseref;

    private DatabaseReference teacherDatabaseref;

    private int finalSelectedStd;
    private DatabaseReference validationDataBaseRef = FirebaseDatabase.getInstance().getReference("Students");
    private String finalSelectedSub;
    private boolean isAllFields = false;
    private EditText txtEmail;
    private Button btnEditEmail;
    CheckInternet internet = new CheckInternet();

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
        binding = ActivitySignupBinding.inflate(getLayoutInflater());
        internet.InternetConnectivityChecker(this);
        internet.start();
        setContentView(binding.getRoot());
        getSupportActionBar().hide();
        tclasses.add("Select Tuition Classes");

        mAuth = FirebaseAuth.getInstance();
        rootDatabaseref = FirebaseDatabase.getInstance().getReference().child("Students");
        teacherDatabaseref = FirebaseDatabase.getInstance().getReference("Teachers");

        // Get TC Name from Firebase
        teacherDatabaseref.addValueEventListener(new ValueEventListener() {
            String tcname1;
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for(DataSnapshot dataSnapshot : snapshot.getChildren()){
                    tcname1 = (String) dataSnapshot.child("tcName").getValue();
                    tclasses.add(tcname1);
                    tclasses.removeAll(Collections.singletonList(null));
                }
                Log.i("List:--- ", ""+tclasses);

            }
            @Override
            public void onCancelled(@NonNull DatabaseError error) {}
        });
        ArrayAdapter<String> adapterTc=new ArrayAdapter<>(getApplicationContext(), android.R.layout.simple_list_item_1,tclasses);
        binding.sptcName.setSelection(1);
        binding.sptcName.setAdapter(adapterTc);

        selectedSub = new boolean[subArray.length];
        ArrayAdapter<String> adapter=new ArrayAdapter<>(getApplicationContext(), android.R.layout.simple_list_item_1,std);
        binding.spStd.setSelection(1);
        binding.spStd.setAdapter(adapter);

        binding.spStd.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                if(i>0) {
                    finalSelectedStd = Integer.parseInt(std[i]);
                    Toast.makeText(getApplicationContext(), "Your standard is " + std[i], Toast.LENGTH_SHORT).show();
                }

            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {
                Toast.makeText(getApplicationContext(), "Nothing selected..", Toast.LENGTH_SHORT).show();
            }
        });

        binding.sptcName.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                if(i>0) {
                    tcname = tclasses.get(i);
                }
            }
            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {
                Toast.makeText(getApplicationContext(), "Nothing selected..", Toast.LENGTH_SHORT).show();
            }
        });

        binding.spSub.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                AlertDialog.Builder builder = new AlertDialog.Builder(SignupActivity.this);
                // set title
                builder.setTitle("Select Std");

                // set dialog non cancelable
                builder.setMultiChoiceItems(subArray, selectedSub, new DialogInterface.OnMultiChoiceClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i, boolean b) {
                        // check condition
                        if (b) {
                            // when checkbox selected
                            // Add position  in lang list
                            sublist.add(i);
                            // Sort array list
                            Collections.sort(sublist);
                        } else {
                            // when checkbox unselected
                            // Remove position from langList
                            sublist.remove(Integer.valueOf(i));
                        }
                    }
                });

                builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        // Initialize string builder
                        StringBuilder stringBuilder = new StringBuilder();
                        // use for loop
                        for (int j = 0; j < sublist.size(); j++) {
                            // concat array value
                            stringBuilder.append(subArray[sublist.get(j)]);
                            // check condition
                            if (j != sublist.size() - 1) {
                                // When j value  not equal
                                // to lang list size - 1
                                // add comma
                                stringBuilder.append(", ");
                            }
                        }
                        // set text on textView
                        binding.spSub.setText(stringBuilder.toString());
                        finalSelectedSub = stringBuilder.toString();
                    }
                });

                builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        // dismiss dialog
                        dialogInterface.dismiss();
                    }
                });
                builder.setNeutralButton("Clear All", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        // use for loop
                        for (int j = 0; j < selectedSub.length; j++) {
                            // remove all selection
                            selectedSub[j] = false;
                            // clear language list
                            sublist.clear();
                            // clear text view value
                            binding.spSub.setText("");
                        }
                    }
                });
                // show dialog
                builder.show();
            }
        });
    }
    public void toSignin(View v){
        Intent intent = new Intent(SignupActivity.this, SigninActivity.class);
        startActivity(intent);
        finish();
    }

    public void Signup(View v){
        name = binding.edName.getText().toString().trim();
        email = binding.edEmail.getText().toString().trim().toLowerCase();
        password = binding.edPassword.getText().toString();
        phoneNumber = binding.edPhoneNumber.getText().toString().trim();
        tcname = binding.sptcName.getSelectedItem().toString().trim();

        if(email.isBlank() && password.isBlank() && name.isBlank() && tcname.equals("Select Tuition Classes") && phoneNumber.isBlank()){
            binding.tilEmailReg.setError("* This Field is Required");
            binding.tilPasswordReg.setError("* This Field is Required");
            binding.tilName.setError("* This Field is Required");
            binding.tilPhoneNumber.setError("* This Field is Required");
            Toast.makeText(this, "Please Select Tuition Classes Name", Toast.LENGTH_SHORT).show();
            isAllFields = false;
        }else{
            binding.tilName.setError("");
            binding.tilEmailReg.setError("");
            binding.tilPhoneNumber.setError("");
            binding.tilPasswordReg.setError("");
            isAllFields = CheckAllFields();
            if (isAllFields) {
                if(isValidEmail(email)) {
                    if (password.length() >= 6) {
                        if (phoneNumber.length() == 10) {
                            if(isValidPhoneNumber(phoneNumber)){
                                ArrayList<String> phoneNumbers = new ArrayList<>();
                                ArrayList<String> emails = new ArrayList<>();
                                validationDataBaseRef.addListenerForSingleValueEvent(new ValueEventListener() {
                                    @Override
                                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                                        String dbemail;
                                        String dbphonenumber;
                                        for(DataSnapshot dataSnapshot : snapshot.getChildren()){
                                            dbemail = (String) dataSnapshot.child("email").getValue();
                                            dbphonenumber = (String) dataSnapshot.child("phoneNumber").getValue();
                                            emails.add(dbemail);
                                            phoneNumbers.add(dbphonenumber);
                                            if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.N) {
                                                isResEmail = emails.stream().anyMatch(email::equalsIgnoreCase);
                                                isRedPhoneNumber = phoneNumbers.stream().anyMatch(phoneNumber::equalsIgnoreCase);
                                            }
                                            if(!isResEmail){
                                                if(!isRedPhoneNumber){
                                                    binding.progressSignup.setVisibility(View.VISIBLE);
                                                    // Start
                                                    mAuth.createUserWithEmailAndPassword(email, password)
                                                            .addOnCompleteListener( new OnCompleteListener<AuthResult>() {
                                                                @Override
                                                                public void onComplete(@NonNull Task<AuthResult> task) {

                                                                    LayoutInflater inflater = LayoutInflater.from(getApplicationContext());
                                                                    View view = inflater.inflate(R.layout.alert_dialog, null);
                                                                    AlertDialog builder = new AlertDialog.Builder(SignupActivity.this).create();
                                                                    builder.setCancelable(false);
                                                                    builder.setView(view);
                                                                    builder.setTitle("Email Verification");
                                                                    builder.setMessage("A Email has been send on below email \nYou will be redirected to Dashboard in few moments...\nNot You? Type New Email to Resend\n");
                                                                    txtEmail = view.findViewById(R.id.edemailforalert);
                                                                    btnEditEmail = view.findViewById(R.id.btneditemail);
                                                                    txtEmail.setEnabled(false);
                                                                    txtEmail.setText(email);
                                                                    btnEditEmail.setOnClickListener(new View.OnClickListener() {
                                                                        @Override
                                                                        public void onClick(View view) {
                                                                            txtEmail.setEnabled(true);
                                                                        }
                                                                    });
                                                                    builder.setButton(Dialog.BUTTON_POSITIVE, "Done", new DialogInterface.OnClickListener() {
                                                                        @Override
                                                                        public void onClick(DialogInterface dialogInterface, int i) {
                                                                            builder.dismiss();
                                                                        }
                                                                    });
                                                                    builder.show();
                                                                    if (task.isSuccessful()) {
                                                                        mAuth.getCurrentUser().sendEmailVerification().addOnCompleteListener(new OnCompleteListener<Void>() {
                                                                            @Override
                                                                            public void onComplete(@NonNull Task<Void> task) {
                                                                                if (task.isSuccessful()) {
                                                                                    Timer timer = new Timer();
                                                                                    timer.schedule(new TimerTask() {
                                                                                        @Override
                                                                                        public void run() {
                                                                                            mAuth.getCurrentUser().reload();
                                                                                            if (mAuth.getCurrentUser().isEmailVerified()) {
                                                                                                String ukey = rootDatabaseref.push().getKey();
                                                                                                timer.cancel();
                                                                                                builder.dismiss();
                                                                                                String[] finalsubs = finalSelectedSub.split(", ");
                                                                                                ArrayList<String> sublist = new ArrayList<String>(Arrays.asList(finalsubs));
                                                                                                String std_final = String.valueOf(finalSelectedStd);
                                                                                                StudentModel sm = new StudentModel(tcname, name, email, phoneNumber, std_final, sublist, "Active");
                                                                                                rootDatabaseref.child(ukey).setValue(sm);
                                                                                                SharedPreferences sharedPreferences = getSharedPreferences("SystemPre", MODE_PRIVATE);
                                                                                                SharedPreferences.Editor editor = sharedPreferences.edit();
                                                                                                editor.putBoolean("isLogin", true);
                                                                                                editor.putString("email", email);
                                                                                                editor.putString("tcName", tcname);
                                                                                                editor.putString("Key", ukey);
                                                                                                editor.putString("StudentName", name);
                                                                                                editor.putString("StudentStd", std_final);
                                                                                                editor.putString("StudentPhone", phoneNumber);
                                                                                                editor.apply();
                                                                                                // Sign in success, update UI with the signed-in user's information
                                                                                                Intent intent = new Intent(SignupActivity.this, DashboardActivity.class);
                                                                                                startActivity(intent);
                                                                                                finish();
                                                                                            } else {
                                                                                                Log.i("EM", "Email Not Verified");
                                                                                            }
                                                                                        }
                                                                                    }, 0, 800);
                                                                                    binding.progressSignup.setVisibility(View.GONE);
                                                                                } else {
                                                                                    Toast.makeText(SignupActivity.this, "Please Verify Your Email (" + email + ")", Toast.LENGTH_SHORT).show();
                                                                                }
                                                                            }
                                                                        });
                                                                    }
                                                                }
                                                            });

                                                    // Finished

                                                }else{
                                                    binding.tilPhoneNumber.setError("Phone Number is Already Registered");
                                                    binding.progressSignup.setVisibility(View.GONE);
                                                }
                                            }else{
                                                binding.tilEmailReg.setError("Email is Already Registered");
                                                binding.progressSignup.setVisibility(View.GONE);
                                            }
                                        }
                                    }
                                    @Override
                                    public void onCancelled(@NonNull DatabaseError error) {

                                    }
                                });

                            }
                            else{
                                Toast.makeText(this, "Phone Number Not Valid !", Toast.LENGTH_SHORT).show();

                            }
                        }else {
                            Toast.makeText(this, "Phone Number Must be 10 Characters Long", Toast.LENGTH_SHORT).show();
                        }
                    } else {
                        Toast.makeText(this, "Password Must be 6 Atleast Characters Long", Toast.LENGTH_SHORT).show();
                    }
                }
                else{
                    Toast.makeText(this, "Email Not Valid !", Toast.LENGTH_SHORT).show();
                }
            }
        }
    }


    private boolean CheckAllFields() {
        if(binding.sptcName.getSelectedItem().toString().equals("Select Tuition Classes")){
            Toast.makeText(this, "Please Select Tuition Classes Name", Toast.LENGTH_SHORT).show();
            return false;
        }if(binding.edName.getText().toString().isBlank()){
            binding.tilName.setError("Full Name is Required");
            return false;
        }
        if(binding.edEmail.getText().toString().trim().toLowerCase().isBlank()){
            binding.tilEmailReg.setError("Email is Required");
            return false;
        }
        if(binding.edPhoneNumber.getText().toString().isBlank()){
            binding.tilPhoneNumber.setError("Phone Number is Required");
            return false;
        }if(binding.edPassword.getText().toString().isBlank()){
            binding.tilPasswordReg.setError("Password is Required");
            return false;
        }if(binding.spStd.getSelectedItem().toString().equals("Select Standard")){
            Toast.makeText(this, "Please Select Standards", Toast.LENGTH_SHORT).show();
            return false;
        }if(binding.spSub.getText().toString().isBlank()){
            Toast.makeText(this, "Please Select Subjects", Toast.LENGTH_SHORT).show();
            return false;
        }
        binding.tilName.setError("");
        binding.tilEmailReg.setError("");
        binding.tilPhoneNumber.setError("");
        binding.tilPasswordReg.setError("");
        return true;
    }

    public static boolean isValidEmail(String email) {
        // Regular expression pattern for email validation
        String emailPattern = "[a-zA-Z0-9._-]+@[a-z]+\\.+[a-z]+";

        // Compile the regular expression pattern
        Pattern pattern = Pattern.compile(emailPattern);

        // Match the email address against the regular expression pattern
        Matcher matcher = pattern.matcher(email);

        // Return true if the email address matches the pattern, false otherwise
        return matcher.matches();
    }

    public static boolean isValidPhoneNumber(String phoneNumber) {
        // Create an instance of PhoneNumberUtil
        PhoneNumberUtil phoneNumberUtil = PhoneNumberUtil.getInstance();

        try {
            // Parse the phone number with the country code for India
            Phonenumber.PhoneNumber number = phoneNumberUtil.parse(phoneNumber, "IN");

            // Check if the phone number is valid
            return phoneNumberUtil.isValidNumber(number);
        } catch (NumberParseException e) {
            // Invalid phone number format
            return false;
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        internet.stop();
    }
}