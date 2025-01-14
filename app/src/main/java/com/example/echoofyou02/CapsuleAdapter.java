package com.example.echoofyou02;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Base64;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

public class CapsuleAdapter extends RecyclerView.Adapter<CapsuleAdapter.CapsuleViewHolder> {

    private final List<Capsule> capsuleList;

    public CapsuleAdapter(List<Capsule> capsuleList) {
        this.capsuleList = capsuleList;
    }

    @NonNull
    @Override
    public CapsuleViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_capsule, parent, false);
        return new CapsuleViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull CapsuleViewHolder holder, int position) {
        Capsule capsule = capsuleList.get(position);
        holder.title.setText(capsule.getTitle());
        holder.description.setText(capsule.getDescription());
        holder.dateTime.setText(capsule.getDateTime().toString());

        // Decode and set the image
        String base64Image = capsule.getFileBase64();
        if (base64Image != null) {
            byte[] decodedString = Base64.decode(base64Image, Base64.DEFAULT);
            Bitmap decodedBitmap = BitmapFactory.decodeByteArray(decodedString, 0, decodedString.length);
            holder.image.setImageBitmap(decodedBitmap);
        }
    }

    @Override
    public int getItemCount() {
        return capsuleList.size();
    }

    static class CapsuleViewHolder extends RecyclerView.ViewHolder {

        TextView title, description, dateTime;
        ImageView image;

        public CapsuleViewHolder(@NonNull View itemView) {
            super(itemView);
            title = itemView.findViewById(R.id.capsuleTitle);
            description = itemView.findViewById(R.id.capsuleDescription);
            dateTime = itemView.findViewById(R.id.capsuleDateTime);
            image = itemView.findViewById(R.id.capsuleImage);
        }
    }
}


