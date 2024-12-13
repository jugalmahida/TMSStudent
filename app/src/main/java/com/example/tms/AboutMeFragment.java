package com.example.tms;

import static android.app.Activity.RESULT_OK;

import android.app.ProgressDialog;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.graphics.Rect;
import android.graphics.RectF;
import android.net.Uri;
import android.os.Bundle;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.MimeTypeMap;
import android.widget.ImageView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.example.tms.databinding.FragmentAboutMeBinding;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.time.Year;
import java.util.UUID;


public class AboutMeFragment extends Fragment {
    private FragmentAboutMeBinding binding;
    private DatabaseReference rootDatabaseReference;
    private DatabaseReference StudentsReference;

    private StorageReference storageReference;
    public Uri imageUri;
    public String key;


    public AboutMeFragment() {
    }

    @Override

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        StudentsReference = FirebaseDatabase.getInstance().getReference().child("Students");

    }

    private void uploadToFirebase(Uri uri){
        final ProgressDialog pd = new ProgressDialog(getContext());
        pd.setTitle("Uploading Image...");
        final StorageReference imgReference = storageReference.child(System.currentTimeMillis() + "."+ getFileExtension(uri));

        imgReference.putFile(uri).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                imgReference.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                    @Override
                    public void onSuccess(Uri uri) {
                        key = rootDatabaseReference.push().getKey();
                        rootDatabaseReference.child(key).setValue(uri.toString());
                        pd.hide();
                    }
                });
            }
        }).addOnProgressListener(new OnProgressListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onProgress(@NonNull UploadTask.TaskSnapshot snapshot) {
                double prograssper = (100.00* snapshot.getBytesTransferred() / snapshot.getTotalByteCount());
                pd.setMessage("Percentage: "+(int) prograssper +"%");
                pd.show();
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                pd.hide();
                Toast.makeText(getContext(), "Failed to Upload...", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private String getFileExtension(Uri fileUri){
        ContentResolver contentResolver = getActivity().getContentResolver();
        MimeTypeMap mime = MimeTypeMap.getSingleton();
        return mime.getExtensionFromMimeType(contentResolver.getType(fileUri));
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        binding = FragmentAboutMeBinding.inflate(inflater, container, false);
        binding.progressBarAbt.setVisibility(View.VISIBLE);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        SharedPreferences sp = getActivity().getSharedPreferences("SystemPre", Context.MODE_PRIVATE);

        String stName = sp.getString("StudentName","");
        String stPhone = sp.getString("StudentPhone","");
        String stStd = sp.getString("StudentStd","");
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
            Year thisYear = Year.now();
            binding.txtStudentYear.setText(thisYear.toString());
//            Toast.makeText(getContext(), "Your Current Year :- "+thisYear, Toast.LENGTH_SHORT).show();
        }

//        Log.i("STNAME-----","_________"+ stName);
//        Log.i("STSTD-----","_________"+ stStd);
//        Log.i("STPHONE-----","_________"+ stPhone);
//
//
//        binding.txtStudentName.setText(stName);
//        binding.txtStudentStandard.setText(stStd);
//        binding.txtStudentPhoneNumber.setText(stPhone);


        String uKey = sp.getString("Key","");
        Log.i("Key-----","_________"+ uKey);

        rootDatabaseReference = FirebaseDatabase.getInstance().getReference().child("Students").child(uKey).child("Image");
//        Log.i("Students Standars",""+StudentsReference);
        StudentsReference.addListenerForSingleValueEvent(new ValueEventListener() {

            String keyDB;
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for(DataSnapshot studentSnapshot : snapshot.getChildren()){
                    keyDB = studentSnapshot.getKey();
                    if(keyDB.equals(uKey)){
                        binding.txtStudentName.setText(""+studentSnapshot.child("fullName").getValue().toString());
                        binding.txtStudentStandard.setText(""+studentSnapshot.child("standards").getValue().toString());
                        binding.txtStudentPhoneNumber.setText(""+studentSnapshot.child("phoneNumber").getValue().toString());
                    }

                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
        storageReference = FirebaseStorage.getInstance().getReference();
        rootDatabaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if(getContext()==null){
                    return;
                }
                for(DataSnapshot dataSnapshot: snapshot.getChildren()){
                    String uri = dataSnapshot.getValue().toString();
                    Glide.with(getContext()).load(uri).into(binding.imgUser);
                }
                binding.progressBarAbt.setVisibility(View.GONE);
            }
            @Override
            public void onCancelled(@NonNull DatabaseError error) {
            }
        });

        ActivityResultLauncher<Intent> activityResultLauncher = registerForActivityResult(new ActivityResultContracts.StartActivityForResult(), new ActivityResultCallback<ActivityResult>() {
            @Override
            public void onActivityResult(ActivityResult result) {
                if (result.getResultCode() == RESULT_OK){
                    Intent data = result.getData();
                    imageUri = data.getData();
                    binding.imgUser.setImageURI(imageUri);
                }
                else {
                    Toast.makeText(getContext(), "No Image Selected", Toast.LENGTH_SHORT).show();
                }
            }
        });
        binding.imgUser.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent photoPicker = new Intent();
                photoPicker.setAction(Intent.ACTION_GET_CONTENT);
                photoPicker.setType("image/*");
                activityResultLauncher.launch(photoPicker);
            }
        });

        binding.btnSaveImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(imageUri != null){
                    uploadToFirebase(imageUri);
                }
                else {
                    Toast.makeText(getContext(), "Please Select Image", Toast.LENGTH_SHORT).show();
                }
            }
        });

        binding.btnBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                replaceFragment(new AccountFragment());
            }
        });
    }

    private void replaceFragment(Fragment fragment){
        FragmentManager fragmentManager = getActivity().getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.replace(R.id.frame_layout,fragment);
        fragmentTransaction.commit();
    }
}

