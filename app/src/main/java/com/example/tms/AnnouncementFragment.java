package com.example.tms;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.bumptech.glide.Glide;
import com.example.tms.databinding.FragmentAnnouncementBinding;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;


public class AnnouncementFragment extends Fragment {

    private FragmentAnnouncementBinding binding;
    private String Name , Time , Announcement, imgUrl;
    public AnnouncementFragment() {
        // Required empty public constructor
    }
    public AnnouncementFragment(String Name , String Time , String Announcement, String UserImgUrl) {
        // Required empty public constructor
        this.Name = Name;
        this.Time = Time;
        this.Announcement = Announcement;
        this.imgUrl = UserImgUrl;
    }
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        binding = FragmentAnnouncementBinding.inflate(inflater,container,false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        SharedPreferences sharedPreferences = getActivity().getSharedPreferences("SystemPre", Context.MODE_PRIVATE);
        String tc_Name = sharedPreferences.getString("tcName","");
        DatabaseReference teachersRef = FirebaseDatabase.getInstance().getReference().child("Teachers");
        teachersRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for (DataSnapshot teacherSnapshot : dataSnapshot.getChildren()) {
                    // retrieve the data nested inside the child node
                    String name = teacherSnapshot.child("tcName").getValue(String.class);
                    String email = teacherSnapshot.child("email").getValue(String.class);
                    // do something with the data
                    String key1 =teacherSnapshot.getKey();
                    if(!key1.equals("Announcements") && !key1.equals("Materials")) {
                        if(tc_Name.equals(name)){
                            if (getContext() == null) {
                                return;
                            }
                            Object obj = teacherSnapshot.child("ImageUrl").getValue();
                            if(obj==null){
                                Glide.with(getContext()).load(R.mipmap.userprofile).into(binding.imgUserAnnouncementFragment);
                            }
                            else{
                                String ImageUrlTut = teacherSnapshot.child("ImageUrl").getValue().toString();
                                ImageUrlTut = ImageUrlTut.replace("{","");
                                ImageUrlTut = ImageUrlTut.replace("}","");
                                imgUrl = ImageUrlTut.substring(ImageUrlTut.indexOf("https"));
                                Glide.with(getContext()).load(imgUrl).into(binding.imgUserAnnouncementFragment);
                            }
                        }
                    }
                }
            }
            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                // handle any errors
            }
        });
        binding.txtUserNameAnnouncementFragment.setText(Name);
        binding.txtTimeAnnouncementFragment.setText(Time);
        binding.txtAnnouncements.setText(Announcement);
        binding.txtBackAnnouncement.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                replaceFragment(new HomeFragment());
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