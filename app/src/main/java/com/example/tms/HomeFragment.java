package com.example.tms;

import static android.content.Context.MODE_PRIVATE;
import static androidx.constraintlayout.helper.widget.MotionEffect.TAG;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.example.tms.databinding.FragmentHomeBinding;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;


public class HomeFragment extends Fragment {

    private FragmentHomeBinding binding;
    String DBtcName;

    private DatabaseReference annReference;
    private DatabaseReference getNamesReference;
    private String teacherKey;

    public static List<AnnouncementModel> annList = new ArrayList<>();
    private ArrayList<String> FullNameList = new ArrayList<>();
    private ArrayList<String> AnnouncementList = new ArrayList<>();
    private ArrayList<String> TimeList = new ArrayList<>();
    private static String tcName;
    private static String newtcName;
    private String status;
    private SharedPreferences sharedPreferences;

    public HomeFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        sharedPreferences = getActivity().getSharedPreferences("SystemPre", MODE_PRIVATE);

        super.onCreate(savedInstanceState);
        getNamesReference = FirebaseDatabase.getInstance().getReference().child("Students");
        SharedPreferences sharedPreferences = getActivity().getSharedPreferences("SystemPre", MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        String email = sharedPreferences.getString("email","");

        getNamesReference.orderByChild("email").equalTo(email).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for (DataSnapshot childSnapshot : snapshot.getChildren()) {
                    status = childSnapshot.child("status").getValue().toString();
                }
                if(status.equals("Disable")){
                    Toast.makeText(getContext(), "Your Account is Disable, Please Contact Your Administration", Toast.LENGTH_SHORT).show();
                    editor.clear();
                    editor.commit();
                    Intent intent = new Intent(getContext(), MainActivity.class);
                    startActivity(intent);
                    getActivity().finish();
                }
            }
            @Override
            public void onCancelled(@NonNull DatabaseError error) {
            }
        });

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        binding = FragmentHomeBinding.inflate(inflater,container,false);
        binding.progressHomeFrag.setVisibility(View.VISIBLE);
        annReference = FirebaseDatabase.getInstance().getReference().child("Teachers").child("Announcements");

        return binding.getRoot();
    }
    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        getNamesReference.addListenerForSingleValueEvent(new ValueEventListener() {


            String sysEmail = sharedPreferences.getString("email","");
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {


                for (DataSnapshot dataSnapshot:snapshot.getChildren()){
                    String key = dataSnapshot.getKey();
                    if(sysEmail.equals(dataSnapshot.child("email").getValue().toString())){
                        tcName = dataSnapshot.child("tcName").getValue().toString();
                        newtcName = tcName;
                        SharedPreferences.Editor editor = sharedPreferences.edit();
                        editor.putString("tcName",""+newtcName);
                        binding.txtTCName.setText(tcName);
                        editor.apply();
                    }
                }
            }


            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
        annReference.addValueEventListener(new ValueEventListener() {


            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                FullNameList.clear();
                AnnouncementList.clear();
                TimeList.clear();
                annList.clear();
                for (DataSnapshot dataSnapshot:snapshot.getChildren()){
                    AnnouncementModel an = new AnnouncementModel();
                    if(dataSnapshot.child("tcName").getValue().toString().equals(newtcName)){
                        FullNameList.add(dataSnapshot.child("fullName").getValue().toString());
                        AnnouncementList.add(dataSnapshot.child("announcement").getValue().toString());
                        TimeList.add(dataSnapshot.child("time").getValue().toString());
                    }
                }
                if(FullNameList.isEmpty()){
                    binding.textViewNothingHome.setVisibility(View.VISIBLE);
                }else{
                    binding.textViewNothingHome.setVisibility(View.GONE);
                }
                Collections.reverse(annList);
                Collections.reverse(FullNameList);
                Collections.reverse(AnnouncementList);
                Collections.reverse(TimeList);

                AnnouncementAdapter anAdapter = new AnnouncementAdapter(getContext(), FullNameList, TimeList, AnnouncementList);
                binding.lsAnn.setAdapter(anAdapter);
                binding.progressHomeFrag.setVisibility(View.GONE);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });





    }
}

//chamanshah@yopmail.com