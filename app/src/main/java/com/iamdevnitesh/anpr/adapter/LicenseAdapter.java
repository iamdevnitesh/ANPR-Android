package com.iamdevnitesh.anpr.adapter;

import android.annotation.SuppressLint;
import android.content.Context;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.iamdevnitesh.anpr.R;
import com.iamdevnitesh.anpr.dataclass.License;
import com.squareup.picasso.Picasso;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

public class LicenseAdapter extends RecyclerView.Adapter<LicenseAdapter.MyViewHolder> {

    Context context;
    ArrayList<License> list;

    public LicenseAdapter(Context context, ArrayList<License> list) {
        this.context = context;
        this.list = list;
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_holder, parent, false);
        //LicenseAdapter.MyViewHolder holder = new LicenseAdapter.MyViewHolder(view);
        return new MyViewHolder(view);
    }

    @SuppressLint("ResourceType")
    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {
        // calculate todays date
        SimpleDateFormat formatter = new SimpleDateFormat("dd-MM-yyyy");
        Date date = new Date();
        String current = formatter.format(date);
        // storage refernce
        StorageReference anprStorage = FirebaseStorage.getInstance().getReference(current);
        // license position
        License license = list.get(position);

        // SETTING UP VALUES
        holder.license.setText("license :" + license.getLicense_plate());
        holder.date.setText("date :" + license.getDate());
        //Using Glide to load image from firebase
        String url = license.getImg_Url();
        Picasso.get().load(url).into(holder.image);
    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    public static class MyViewHolder extends RecyclerView.ViewHolder {
        TextView license, date;
        ImageView image;

        public MyViewHolder(@NonNull View itemView) {
            super(itemView);

            license = itemView.findViewById(R.id.licenseTextView);
            date = itemView.findViewById(R.id.dateTextView);
            image = itemView.findViewById(R.id.licenseImageView);
        }
    }
}
