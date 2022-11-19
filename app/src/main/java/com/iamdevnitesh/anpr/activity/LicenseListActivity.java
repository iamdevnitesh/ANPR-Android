package com.iamdevnitesh.anpr.activity;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.net.Uri;
import android.os.Bundle;
import android.util.Log;

import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.ListResult;
import com.google.firebase.storage.StorageReference;
import com.iamdevnitesh.anpr.R;
import com.iamdevnitesh.anpr.adapter.LicenseAdapter;
import com.iamdevnitesh.anpr.dataclass.License;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

public class LicenseListActivity extends AppCompatActivity {

    RecyclerView recyclerView;
    DatabaseReference anprDB;
    StorageReference anprStorage;
    LicenseAdapter adapter;
    ArrayList<License> list;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_license_list);

        recyclerView = findViewById(R.id.recyclerView);

        SimpleDateFormat formatter = new SimpleDateFormat("dd-MM-yyyy");
        Date date = new Date();
        String current = formatter.format(date);

        anprDB = FirebaseDatabase.getInstance().getReference(current);
        anprStorage = FirebaseStorage.getInstance().getReference(current);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        list = new ArrayList<>();
        adapter = new LicenseAdapter(this,list);
        recyclerView.setAdapter(adapter);

        anprDB.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                list.clear();
                for(DataSnapshot dataSnapshot : snapshot.getChildren()){
                    License license = dataSnapshot.getValue(License.class);
                    // empty the list
                    list.add(license);
                }
                adapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }
}