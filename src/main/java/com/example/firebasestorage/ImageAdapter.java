package com.example.firebasestorage;

import android.content.Context;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.squareup.picasso.Picasso;

import java.util.List;

public class ImageAdapter extends RecyclerView.Adapter<ImageAdapter.ImageViewHolder> {

    private Context context;
    private List<Upload> uploads;

    public ImageAdapter(Context context, List<Upload> uploads){
        this.context = context;
        this.uploads = uploads;
    }

    @NonNull
    @Override
    public ImageViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.image_item, parent, false);
        return new ImageViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ImageViewHolder holder, int position) {
        Upload uploadCurrent = uploads.get(position);
        holder.textViewName.setText(uploadCurrent.getName());
        holder.textViewCountry.setText(uploadCurrent.getCountry());
        Picasso.with(context).load(uploadCurrent.getImageUrl()).fit().centerCrop().into(holder.imageView);
        //holder.imageView.setImageURI(Uri.parse(uploadCurrent.getImageUrl()));
    }

    @Override
    public int getItemCount() {
        return uploads.size();
    }

    public class ImageViewHolder extends RecyclerView.ViewHolder{
        public TextView textViewName, textViewCountry;
        public ImageView imageView;

        public ImageViewHolder(View view){
            super(view);

            textViewName = view.findViewById(R.id.nameCardViewTV);
            textViewCountry = view.findViewById(R.id.countryCardViewTV);
            imageView = view.findViewById(R.id.imageCardViewIV);
        }
    }
}
