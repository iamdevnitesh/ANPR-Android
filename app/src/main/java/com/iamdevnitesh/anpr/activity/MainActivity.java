package com.iamdevnitesh.anpr.activity;

import android.app.DatePickerDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.drawable.Drawable;
import android.graphics.pdf.PdfDocument;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.util.LruCache;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.DatePicker;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.datepicker.MaterialDatePicker;
import com.google.android.material.datepicker.MaterialPickerOnPositiveButtonClickListener;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.iamdevnitesh.anpr.R;
import com.iamdevnitesh.anpr.adapter.LicenseAdapter;
import com.iamdevnitesh.anpr.databinding.ActivityMainBinding;
import com.iamdevnitesh.anpr.dataclass.License;
import com.squareup.picasso.Picasso;
import com.squareup.picasso.Target;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;
import java.util.TimeZone;

public class MainActivity extends AppCompatActivity {
    ActivityMainBinding binding;
    FirebaseAuth mAuth ;
    DatabaseReference anprDB;
    RecyclerView recyclerView;
    StorageReference anprStorage;
    LicenseAdapter adapter;
    ArrayList<License> list;
    private String usingDate = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        mAuth = FirebaseAuth.getInstance();
        recyclerView = findViewById(R.id.recyclerView);
        // find todays date java
        SimpleDateFormat formatter = new SimpleDateFormat("dd-MM-yyyy");
        Date date = new Date();
        String current= formatter.format(date);
        usingDate = current;

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

        // use material date picker dialog
        MaterialDatePicker.Builder builder = MaterialDatePicker.Builder.datePicker();
        builder.setTitleText("Select Date");
        final MaterialDatePicker<Long> materialDatePicker = builder.build();


        binding.FBtndateSelecter.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                materialDatePicker.show(getSupportFragmentManager(),"DATE_PICKER");
            }
        });

        materialDatePicker.addOnPositiveButtonClickListener(new MaterialPickerOnPositiveButtonClickListener<Long>() {
            @Override
            public void onPositiveButtonClick(Long selection) {
                updateDate(selection);
            }
        });

        binding.FBtndownload.setOnClickListener(v -> {
            generatePDF(usingDate);
        });
    }

    public void generatePDF(String usingDate){
        // generate pdf from recycler view in LicenseAdapter
        PdfDocument pdfDocument = new PdfDocument();
        Paint paint = new Paint();
        PdfDocument.PageInfo pageInfo = new PdfDocument.PageInfo.Builder(1200,2010,1).create();
        PdfDocument.Page page = pdfDocument.startPage(pageInfo);
        Canvas canvas = page.getCanvas();
        paint.setColor(Color.parseColor("#FFFFFF"));
        canvas.drawPaint(paint);
        paint.setColor(Color.parseColor("#000000"));
        paint.setTextSize(35);
        canvas.drawText("License Plate",20,50,paint);
        canvas.drawText("Date",400,50,paint);
        canvas.drawText("Image",800,50,paint);
        paint.setTextSize(25);

        int i = 1;
        for(License license : list){
            //canvas.drawText(license.getImg_Url(),800,100*i,paint);
            canvas.drawText(license.getLicense_plate(),20,100*i,paint);
            canvas.drawText(license.getDate(),400,100*i,paint);
            // draw image
            int finalI = i;
            Picasso.get().load(license.getImg_Url()).into(new Target(){

                @Override
                public void onBitmapLoaded(Bitmap bitmap, Picasso.LoadedFrom from) {
                    //scale the image
                    Bitmap scaledBitmap = Bitmap.createScaledBitmap(bitmap, 400, 50, true);
                    canvas.drawBitmap(scaledBitmap,800,90* finalI,paint);
                }

                @Override
                public void onBitmapFailed(Exception e, Drawable errorDrawable) {

                }

                @Override
                public void onPrepareLoad(Drawable placeHolderDrawable) {

                }
            });
            i++;
        }
        pdfDocument.finishPage(page);
        File targetPdf=Environment.getExternalStoragePublicDirectory(
                Environment.DIRECTORY_DOWNLOADS);

        File filePath;
        filePath = new File(targetPdf, usingDate+".pdf");
        try {
            pdfDocument.writeTo(new FileOutputStream(filePath));
        } catch (IOException e) {
            e.printStackTrace();
            Toast.makeText(this, "Something wrong: " + e.toString(), Toast.LENGTH_LONG).show();
        }
        pdfDocument.close();
        Toast.makeText(this, "PDF is created!!!", Toast.LENGTH_SHORT).show();
    }

    public void updateDate(Long selection){
        // Get the offset from our timezone and UTC.
        TimeZone timeZoneUTC = TimeZone.getDefault();
        // It will be negative, so that's the -1
        int offsetFromUTC = timeZoneUTC.getOffset(new Date().getTime()) * -1;
        // Create a date format, then a date object with our offset
        SimpleDateFormat simpleFormat = new SimpleDateFormat("dd-MM-yyyy", Locale.US);
        Date date = new Date(selection + offsetFromUTC);
        usingDate = simpleFormat.format(date);

        // Reload the whole recycler view
        anprDB = FirebaseDatabase.getInstance().getReference(simpleFormat.format(date));
        anprStorage = FirebaseStorage.getInstance().getReference(simpleFormat.format(date));
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(MainActivity.this));

        list = new ArrayList<>();
        adapter = new LicenseAdapter(MainActivity.this,list);
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
                Toast.makeText(MainActivity.this, "Error in Updating", Toast.LENGTH_SHORT).show();
            }
        });
    }

    public void addUser(MenuItem menuItem){
        startActivity(new Intent(MainActivity.this, AddUserActivity.class));
        //finish();
    }

    public void logOut(MenuItem menuItem){
        mAuth.signOut();
        startActivity(new Intent(MainActivity.this, LoginActivity.class));
    }

    @Override
    protected void onStart() {
        super.onStart();
        FirebaseUser user = mAuth.getCurrentUser();
        if (user == null) {
            startActivity(new Intent(MainActivity.this, LoginActivity.class));
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_bar, menu);
        return true;
    }
}