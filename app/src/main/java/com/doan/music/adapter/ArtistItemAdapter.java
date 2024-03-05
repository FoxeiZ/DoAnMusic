package com.doan.music.adapter;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions;
import com.doan.music.R;
import com.doan.music.activities.DetailAlbumActivity;
import com.doan.music.models.ArtistModel;
import com.doan.music.models.ModelManager;

public class ArtistItemAdapter extends BaseItemAdapter<ArtistItemAdapter.ViewHolder> {
    public ArtistItemAdapter(ModelManager modelManager) {
        super(modelManager);
    }

    @NonNull
    @Override
    public ArtistItemAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new ViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.list_grid_item, null));
    }

    @Override
    public void onBindViewHolder(@NonNull ArtistItemAdapter.ViewHolder holder, int position) {
        ArtistModel artistModel = modelManager.getArtistModels().get(position);
        Glide.with(holder.itemView)
                .load(artistModel.getAlbumArtUri())
                .transition(DrawableTransitionOptions.withCrossFade())
                .error(R.drawable.audiotrack_icon)
                .into(holder.ivCover);
        holder.tvName.setText(artistModel.getArtistName());
        holder.itemView.setOnClickListener(view -> {
            Context context = view.getContext();
            Intent i = new Intent(context, DetailAlbumActivity.class);
            i.putExtra("pos", position);
            i.putExtra("artistId", artistModel.getArtistId());
            context.startActivity(i);
        });
    }

    @Override
    public int getItemCount() {
        return modelManager.getAlbumModels().size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        public final ImageView ivCover;
        public final TextView tvName;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            ivCover = itemView.findViewById(R.id.ivCover);
            tvName = itemView.findViewById(R.id.tvName);
        }
    }
}
