package com.example.tms;

import android.content.Context;
import android.content.SharedPreferences;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.fragment.app.Fragment;

import com.bumptech.glide.Glide;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class AnnouncementAdapter extends BaseAdapter {

    private Context context;

    private ArrayList<String> FullName,Time,Announce_content;

    private String url_new;


    public AnnouncementAdapter(Context context, ArrayList<String> fullName, ArrayList<String> time, ArrayList<String> announce_content) {
        this.context = context;
        FullName = fullName;
        Time = time;
        Announce_content = announce_content;

    }




    @Override
    public int getCount() {
        return FullName.size();
    }

    @Override
    public Object getItem(int position) {
        return null;
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        LayoutInflater inflater = LayoutInflater.from(context);
        View v1 = inflater.inflate(R.layout.card, null);
        TextView textFname = v1.findViewById(R.id.txtName);
        TextView textUTime = v1.findViewById(R.id.txtUTime);
        TextView textAnn = v1.findViewById(R.id.txtAnn);
        ImageView imTutImg = v1.findViewById(R.id.ivDp);
        ConstraintLayout csAnn = v1.findViewById(R.id.consAnnouncement);

        SharedPreferences sharedPreferences = context.getSharedPreferences("SystemPre",Context.MODE_PRIVATE);
        String tc_Name = sharedPreferences.getString("tcName","");
        DatabaseReference teachersRef = FirebaseDatabase.getInstance().getReference().child("Teachers");

        teachersRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (context == null) {
                    return;
                }
                for (DataSnapshot teacherSnapshot : dataSnapshot.getChildren()) {
                    if(context==null){
                        return;
                    }
                    // retrieve the data nested inside the child node
                    String name = teacherSnapshot.child("tcName").getValue(String.class);
                    // do something with the data
                    String key1 =teacherSnapshot.getKey();
                    if(!key1.equals("Announcements") && !key1.equals("Materials")) {
                        if(tc_Name.equals(name)){
                            Object obj = teacherSnapshot.child("ImageUrl").getValue();
                            if(obj==null){
                                Glide.with(context.getApplicationContext()).load(R.mipmap.file);

                            }
                            else{
                                String ImageUrlTut = teacherSnapshot.child("ImageUrl").getValue().toString();
//                            Toast.makeText(context, "This is Url"+ImageUrlTut, Toast.LENGTH_SHORT).show();
                                ImageUrlTut = ImageUrlTut.replace("{","");
                                ImageUrlTut = ImageUrlTut.replace("}","");
                                url_new = ImageUrlTut.substring(ImageUrlTut.indexOf("https"));
                                Glide.with(context.getApplicationContext()).load(url_new).into(imTutImg);
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

        csAnn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AppCompatActivity activity = (AppCompatActivity) v.getContext();
                Fragment myFragment = new AnnouncementFragment(FullName.get(position),Time.get(position) , Announce_content.get(position),url_new);
                activity.getSupportFragmentManager().beginTransaction().replace(R.id.frame_layout, myFragment).addToBackStack(null).commit();


            }
        });

        textFname.setText(FullName.get(position));
        textUTime.setText(Time.get(position));



        textAnn.setText(Announce_content.get(position));

        return v1;
    }
}
