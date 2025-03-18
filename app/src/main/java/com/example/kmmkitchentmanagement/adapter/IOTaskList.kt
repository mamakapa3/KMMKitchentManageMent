package com.example.kmmkitchentmanagement.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageButton;
import android.widget.ProgressBar;

import androidx.annotation.NonNull;

import com.example.kmmkitchentmanagement.R;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.UploadTask;

import java.util.ArrayList;

public class IOTaskList extends BaseAdapter {
    ArrayList<UploadTask> uploadTasks;
    Context context;

    public IOTaskList(ArrayList<UploadTask> uploadTasks, Context context) {
        this.uploadTasks = uploadTasks;
        this.context = context;
    }

    @Override
    public int getCount() {
        return uploadTasks.size();
    }

    @Override
    public Object getItem(int i) {
        return uploadTasks.get(i);
    }

    @Override
    public long getItemId(int i) {
        return i;
    }

    @Override
    public View getView(int i, View view, ViewGroup viewGroup) {
        view = LayoutInflater.from(context).inflate(R.layout.onprogress,null);
        ProgressBar progressBar = (ProgressBar) view.findViewById(R.id.progressBar2);
        ImageButton btn=view.findViewById(R.id.state);
        UploadTask uploadTask=uploadTasks.get(i);
        uploadTask.addOnProgressListener(new OnProgressListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onProgress(@NonNull UploadTask.TaskSnapshot snapshot) {
                    Long progress = snapshot.getBytesTransferred()/ snapshot.getTotalByteCount();
                    progressBar.setProgress(Math.toIntExact(progress*100));
            }
        });
        uploadTask.addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                btn.setImageResource(R.drawable.check);
            }
        });
        uploadTask.addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                btn.setImageResource(R.drawable.error);
            }
        });
        return view;
    }
}
