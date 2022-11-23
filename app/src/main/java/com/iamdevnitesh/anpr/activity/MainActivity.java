package com.iamdevnitesh.anpr.activity;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.StrictMode;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.datepicker.MaterialDatePicker;
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
import com.itextpdf.io.image.ImageData;
import com.itextpdf.io.image.ImageDataFactory;
import com.itextpdf.kernel.color.DeviceGray;
import com.itextpdf.kernel.geom.PageSize;
import com.itextpdf.kernel.pdf.PdfDocument;
import com.itextpdf.kernel.pdf.PdfWriter;
import com.itextpdf.layout.Document;
import com.itextpdf.layout.element.Cell;
import com.itextpdf.layout.element.Image;
import com.itextpdf.layout.element.Paragraph;
import com.itextpdf.layout.element.Table;
import com.itextpdf.layout.property.TextAlignment;
import com.itextpdf.layout.property.UnitValue;
import com.squareup.picasso.Picasso;
import com.squareup.picasso.Target;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.net.MalformedURLException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Locale;
import java.util.TimeZone;

public class MainActivity extends AppCompatActivity {
    ActivityMainBinding binding;
    FirebaseAuth mAuth;
    DatabaseReference anprDB;
    RecyclerView recyclerView;
    StorageReference anprStorage;
    LicenseAdapter adapter;
    License license;
    ArrayList<License> list;
    private String usingDate = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        // check writing permission and request if not granted
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            int permission = ActivityCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE);
            if (permission != PackageManager.PERMISSION_GRANTED) {
                int REQUEST_CODE = 3434;
                ActivityCompat.requestPermissions(MainActivity.this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, REQUEST_CODE);
            }
        }

        mAuth = FirebaseAuth.getInstance();
        recyclerView = findViewById(R.id.recyclerView);
        // find todays date java
        @SuppressLint("SimpleDateFormat") SimpleDateFormat formatter = new SimpleDateFormat("dd-MM-yyyy");
        Date date = new Date();
        String current = formatter.format(date);
        usingDate = current;

        anprDB = FirebaseDatabase.getInstance().getReference(current);
        anprStorage = FirebaseStorage.getInstance().getReference(current);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        list = new ArrayList<>();
        adapter = new LicenseAdapter(this, list);
        recyclerView.setAdapter(adapter);

        anprDB.addValueEventListener(new ValueEventListener() {
            @SuppressLint("NotifyDataSetChanged")
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                list.clear();
                for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                    license = dataSnapshot.getValue(License.class);
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
        MaterialDatePicker.Builder<Long> builder = MaterialDatePicker.Builder.datePicker();
        builder.setTitleText("Select Date");
        final MaterialDatePicker<Long> materialDatePicker = builder.build();


        binding.FBtndateSelecter.setOnClickListener(v -> materialDatePicker.show(getSupportFragmentManager(), "DATE_PICKER"));

        materialDatePicker.addOnPositiveButtonClickListener(this::updateDate);

        binding.FBtndownload.setOnClickListener(v -> {
            binding.pdfGenerationProgressBar.setVisibility(View.VISIBLE);
            try {
                generatePDF(usingDate);
            } catch (FileNotFoundException | MalformedURLException e) {
                e.printStackTrace();
            }
        });
    }

    public void generatePDF(String usingDate) throws FileNotFoundException, MalformedURLException {
        String destinationPath = Environment.getExternalStoragePublicDirectory(
                Environment.DIRECTORY_DOWNLOADS) + "/" + usingDate + ".pdf";

        float[] columnWidths = {1, 5, 5, 5};
        Table table = new Table(UnitValue.createPercentArray(columnWidths));

        PdfDocument pdfDoc = new PdfDocument(new PdfWriter(destinationPath));
        Document doc = new Document(pdfDoc, PageSize.A4.rotate());

        Cell cell = new Cell(1,6)
                .add(new Paragraph("ANPR Report"))
                .setFontSize(13)
                .setFontColor(DeviceGray.WHITE)
                .setBackgroundColor(DeviceGray.BLACK)
                .setTextAlignment(TextAlignment.CENTER);

        table.addHeaderCell(cell);

        for(int i=0;i<2;i++) {
            Cell[] headerFooter = new Cell[]{
                    new Cell()
                            .setTextAlignment(TextAlignment.CENTER)
                            .setBackgroundColor(new DeviceGray(0.75f))
                            .add(new Paragraph("S No.")),
                    new Cell()
                            .setTextAlignment(TextAlignment.CENTER)
                            .setBackgroundColor(new DeviceGray(0.75f))
                            .add(new Paragraph("License Plate")),
                    new Cell()
                            .setTextAlignment(TextAlignment.CENTER)
                            .setBackgroundColor(new DeviceGray(0.75f))
                            .add(new Paragraph("Date")),
                    new Cell()
                            .setTextAlignment(TextAlignment.CENTER)
                            .setBackgroundColor(new DeviceGray(0.75f))
                            .add(new Paragraph("Image"))
            };
            for(Cell hfCell : headerFooter) {
                if(i==0) {
                    table.addHeaderCell(hfCell);
                }
            }
        }

        int i=0;
        for(License lt:list){
            table.addCell(new Cell().add(new Paragraph(String.valueOf(i+1))));
            table.addCell(new Cell().add(new Paragraph(lt.getLicense_plate())));
            Date date1 = new java.util.Date(lt.getDate()* 1000L);
            @SuppressLint("SimpleDateFormat") SimpleDateFormat sdf = new SimpleDateFormat("dd-MM-yyyy HH:mm:ss");
            sdf.setTimeZone(java.util.TimeZone.getTimeZone("GMT+5:30"));
            String formattedDate = sdf.format(date1);
            table.addCell(new Cell().add(new Paragraph(formattedDate)));
            // avoid network on main thread exception using StrictMode
            StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
            StrictMode.setThreadPolicy(policy);
            ImageData imageData = ImageDataFactory.create(lt.getImg_Url());
            Image image = new Image(imageData);
            image.scaleToFit(100, 100);
            table.addCell(new Cell().add(image));
            i++;
        }

        doc.add(table);
        doc.close();

        binding.pdfGenerationProgressBar.setVisibility(View.GONE);
        Toast.makeText(this, "PDF Generated", Toast.LENGTH_SHORT).show();

    }

    public void updateDate(Long selection) {
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
        adapter = new LicenseAdapter(MainActivity.this, list);
        recyclerView.setAdapter(adapter);

        anprDB.addValueEventListener(new ValueEventListener() {
            @SuppressLint("NotifyDataSetChanged")
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                list.clear();
                for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
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

    public void addUser(MenuItem menuItem) {
        startActivity(new Intent(MainActivity.this, AddUserActivity.class));
        //finish();
    }

    public void logOut(MenuItem menuItem) {
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