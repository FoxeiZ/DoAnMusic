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
import com.doan.music.models.AlbumModel;
import com.doan.music.models.ModelManager;

public class AlbumItemAdapter extends BaseItemAdapter<AlbumItemAdapter.ViewHolder> {
    public AlbumItemAdapter(ModelManager modelManager) {
        super(modelManager);
    }

    @NonNull
    @Override
    public AlbumItemAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new ViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.list_album_item, null));
    }

    @Override
    public void onBindViewHolder(@NonNull AlbumItemAdapter.ViewHolder holder, int position) {
        AlbumModel albumModel = modelManager.getAlbumModels().get(position);
        Glide.with(holder.itemView)
                .load(albumModel.getAlbumArtUri())
                .transition(DrawableTransitionOptions.withCrossFade())
                .error(R.drawable.audiotrack_icon)
                .into(holder.ivAlbumCover);
        holder.tvAlbumName.setText(albumModel.getAlbumName());
        holder.itemView.setOnClickListener(view -> {
            Context context = view.getContext();
            Intent i = new Intent(context, DetailAlbumActivity.class);
            i.putExtra("pos", position);
            i.putExtra("albumId", albumModel.getAlbumId());
            context.startActivity(i);
        });
    }

    @Override
    public int getItemCount() {
        return modelManager.getAlbumModels().size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        public final ImageView ivAlbumCover;
        public final TextView tvAlbumName;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            ivAlbumCover = itemView.findViewById(R.id.ivAlbumCover);
            tvAlbumName = itemView.findViewById(R.id.tvAlbumName);
        }
    }
}
