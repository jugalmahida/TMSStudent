package com.example.tms;

import static androidx.core.view.ViewKt.isVisible;

import android.app.DownloadManager;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Environment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;
import androidx.core.view.ViewCompat;

import androidx.annotation.NonNull;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FileDownloadTask;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.ListResult;
import com.google.firebase.storage.StorageReference;

import java.io.File;
import java.util.ArrayList;

public class AcademicsAdapter extends BaseAdapter {

    private Context context;
    private ArrayList<Uri> myFileUri = new ArrayList<>();
    private ArrayList<MaterialModel> materialModel = new ArrayList<>();
    private ArrayList<String> fileName;
    private ArrayList<String> uploadTime;
    private ImageButton downloadButton;
    DatabaseReference rootDatabaseReference = FirebaseDatabase.getInstance().getReference();
    DatabaseReference subDirRef = rootDatabaseReference.child("Teachers").child("Materials");
    private SharedPreferences sharedPreferences;

    public AcademicsAdapter(Context context, ArrayList<String> fileName, ArrayList<String> uploadTime) {
        this.context = context;
        this.fileName = fileName;
        this.uploadTime = uploadTime;
    }

    @Override
    public int getCount() {
        return fileName.size();
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
        View view1 = inflater.inflate(R.layout.materiallist, null);

        TextView txtFileName = view1.findViewById(R.id.txtfilename);
        TextView txtTime = view1.findViewById(R.id.txttime);
        downloadButton = view1.findViewById(R.id.ibDownload);
//        String tcName = sharedPreferences.getString("tcName","");
        downloadButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                Toast.makeText(context, "Clicked......."+fileName.get(position), Toast.LENGTH_SHORT).show();
                subDirRef.addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        for (DataSnapshot subDirSnapshot : dataSnapshot.getChildren()) {
                            if(subDirSnapshot.child("fileName").getValue().equals(fileName.get(position))){
                                String dbFileName = subDirSnapshot.child("fileName").getValue().toString();
                                DatabaseReference urlRef = subDirSnapshot.getRef().child("fileUri");
                                urlRef.addListenerForSingleValueEvent(new ValueEventListener() {
                                    @Override
                                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                        String url = dataSnapshot.getValue(String.class);
                                        downloadFile(url,dbFileName);
                                    }
                                    @Override
                                    public void onCancelled(@NonNull DatabaseError databaseError) {
                                        Log.e("Error-----", databaseError.getMessage());
                                    }
                                });
                            }
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {
                        Log.e("Error-----", databaseError.getMessage());
                    }
                });
            }
        });

        txtFileName.setText(fileName.get(position));
        txtTime.setText(uploadTime.get(position));


        return view1;
    }


    private void downloadFile(String url, String fileName) {
        File file = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS), fileName);
        if (file.exists()) {
            Toast.makeText(context, "File Already Downloaded", Toast.LENGTH_SHORT).show();
            return;
        }
        else{
            Toast.makeText(context, "File Downloaded", Toast.LENGTH_SHORT).show();
        }

//        Toast.makeText(context, "Download File Called", Toast.LENGTH_SHORT).show();
        DownloadManager.Request request = new DownloadManager.Request(Uri.parse(url));
        request.setTitle(fileName);
        request.setDescription("Downloading file...");

        // set the destination folder and file name
        request.setDestinationInExternalPublicDir(Environment.DIRECTORY_DOWNLOADS, fileName);

        // get download service and enqueue file
        DownloadManager downloadManager = (DownloadManager) context.getSystemService(Context.DOWNLOAD_SERVICE);
        downloadManager.enqueue(request);
    }


}
