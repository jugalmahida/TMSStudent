package com.example.tms;

import android.app.DownloadManager;
import android.content.Context;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import android.os.Environment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Toast;

import com.example.tms.databinding.FragmentAcademicsBinding;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;

import java.util.ArrayList;
import java.util.Collections;


public class AcademicsFragment extends Fragment {

    private FragmentAcademicsBinding binding;
    private DatabaseReference rootDatabaseReference;
    private SharedPreferences sharedPreferences;
    private String tcName;
    public static ArrayList<MaterialModel> lsMaterialModel = new ArrayList<>();
    private ArrayList<String > lsfileName =new ArrayList<>();
    private ArrayList<String >  lstime=new ArrayList<>();
    public AcademicsFragment() {}
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        rootDatabaseReference = FirebaseDatabase.getInstance().getReference().child("Teachers").child("Materials");
        sharedPreferences = getActivity().getSharedPreferences("SystemPre", Context.MODE_PRIVATE);
        tcName = sharedPreferences.getString("tcName","");

        //Get Materials
        rootDatabaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                lsMaterialModel.clear();
                lsfileName.clear();
                lstime.clear();
                for(DataSnapshot dataSnapshot : snapshot.getChildren()){
                    MaterialModel materialModel =new MaterialModel();
                    if(dataSnapshot.child("tcName").getValue().toString().equals(tcName)){

                        lsfileName.add( (String) dataSnapshot.child("fileName").getValue());
                        lstime.add((String)dataSnapshot.child("time").getValue());

                        materialModel.setFileName( (String) dataSnapshot.child("fileName").getValue());
                        materialModel.setTime( (String) dataSnapshot.child("time").getValue());
                        materialModel.setFileUri( (String) dataSnapshot.child("fileUri").getValue());
                        materialModel.setTcName( (String) dataSnapshot.child("tcName").getValue());
                        lsMaterialModel.add(materialModel);
                    }
                }
                if(lsfileName.isEmpty()){
                    binding.textViewNothingAca.setVisibility(View.VISIBLE);
                }else{
                    binding.textViewNothingAca.setVisibility(View.GONE);
                }
                Collections.reverse(lsMaterialModel);
                Collections.reverse(lsfileName);
                Collections.reverse(lstime);
                AcademicsAdapter adapter_academics = new AcademicsAdapter(getContext(),lsfileName,lstime);
                binding.lsMaterial.setAdapter(adapter_academics);
                binding.progressAcademics.setVisibility(View.GONE);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        binding = FragmentAcademicsBinding.inflate(inflater,container,false);
        binding.txtClassesName.setText(tcName);
        binding.progressAcademics.setVisibility(View.VISIBLE);
        return binding.getRoot();
    }


}