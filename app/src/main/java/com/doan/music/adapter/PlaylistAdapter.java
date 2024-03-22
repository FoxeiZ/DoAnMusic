package com.doan.music.adapter;


import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.Context;
import android.graphics.Color;
import android.util.Log;
import android.view.ContextMenu;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.doan.music.R;
import com.doan.music.models.ModelManager;
import com.doan.music.models.PlaylistModel;
import com.doan.music.utils.General;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;

import java.lang.ref.WeakReference;
import java.util.ArrayList;

interface DeleteListener {
    void onDelete(int position);
}

public class PlaylistAdapter extends BaseItemAdapter<PlaylistAdapter.ViewHolder> {

    private final ArrayList<PlaylistModel> playlistModels = new ArrayList<>();
    public static WeakReference<ArrayList<PlaylistModel>> playlistModelsRef;

    public static ArrayList<PlaylistModel> getPlaylistModels() {
        return playlistModelsRef.get();
    }

    public PlaylistAdapter(ModelManager modelManager) {
        super(modelManager);
    }

    public void destroy() {
        playlistModelsRef = null;
    }

    @SuppressLint("NotifyDataSetChanged")
    public void setData(DataSnapshot snapshot) {
        playlistModels.clear();
        snapshot.getChildren().forEach(data -> {
            Log.d("", "setData: " + data);
            playlistModels.add(data.getValue(PlaylistModel.class));
        });
        notifyDataSetChanged();
        playlistModelsRef = new WeakReference<>(playlistModels);
    }

    public void populateData(Context context) {
        if (!playlistModels.isEmpty())
            return;

        General.getCurrentUserQuery(context).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()) {
                    DataSnapshot snapshot1 = snapshot.child(General.getCurrentUser(context)).child("playlists");
                    if (snapshot1.exists()) {
                        setData(snapshot1);
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    public void addData(PlaylistModel playlistModel) {
        playlistModels.add(playlistModel);
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new ViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.list_song_item, parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        PlaylistModel playlistModel = playlistModels.get(position);
        holder.playlistTitle.setText(playlistModel.getTitle());

        holder.setDeleteListener(pos -> {
            playlistModels.remove(pos);
            notifyItemRemoved(pos);
        });

        String desc = playlistModel.getDescription();
        if (desc == null || desc.isEmpty()) {
            desc = "<no description>";
            holder.playlistDesc.setTextColor(Color.argb(50, 255, 255, 255));
        }
        if (desc.length() > 50) {
            desc = desc.substring(0, 50) + "...";
        }
        holder.playlistDesc.setText(desc);
    }

    @Override
    public int getItemCount() {
        return playlistModels.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder implements View.OnCreateContextMenuListener {

        public final RelativeLayout adapterRoot;
        public final TextView playlistTitle;
        public final TextView playlistDesc;

        public void setDeleteListener(DeleteListener deleteListener) {
            this.deleteListener = deleteListener;
        }

        private DeleteListener deleteListener;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            adapterRoot = itemView.findViewById(R.id.adapterRootLayout);
            adapterRoot.setOnCreateContextMenuListener(this);

            playlistTitle = itemView.findViewById(R.id.songTitle);
            playlistDesc = itemView.findViewById(R.id.songArtist);

            ((TextView) itemView.findViewById(R.id.songDuration)).setText("");
        }

        @Override
        public void onCreateContextMenu(ContextMenu contextMenu, View view, ContextMenu.ContextMenuInfo contextMenuInfo) {
            contextMenu.add(0, 0, 0, "Delete").setOnMenuItemClickListener(menuItem -> {
                General.getCurrentUserRef(view.getContext()).child("playlists").child(playlistTitle.getText().toString()).removeValue();
                deleteListener.onDelete(getLayoutPosition());
                return true;
            });
            contextMenu.add(0, 1, 0, "Edit").setOnMenuItemClickListener(menuItem -> {
                showDialog();
                return true;
            });
        }

        private void showDialog() {
            Context context = itemView.getContext();
            View view = LayoutInflater.from(context).inflate(R.layout.dialog_add_playlist, null);

            EditText playlist_name = view.findViewById(R.id.playlist_name);
            EditText playlist_desc = view.findViewById(R.id.playlist_desc);

            playlist_name.setText(playlistTitle.getText());
            playlist_desc.setText(playlistDesc.getText());

            AlertDialog alertDialog = new AlertDialog.Builder(context)
                    .setView(view)
                    .setTitle("Update playlist info")
                    .setPositiveButton("Update", (dialog, which) -> {
                        String playlistName = playlist_name.getText().toString();
                        if (playlistName.isEmpty()) {
                            ((EditText) view.findViewById(R.id.playlist_name)).setError("Playlist name can not be empty");
                            return;
                        }

                        String playlistDescription = playlist_desc.getText().toString();
                        General.getCurrentUserRef(context).child("playlists").child(playlistTitle.getText().toString()).setValue(new PlaylistModel(playlistName, playlistDescription));

                        playlistTitle.setText(playlistName);
                        playlistDesc.setText(playlistDescription);
                    })
                    .setNegativeButton("Cancel", (dialog, which) -> dialog.dismiss())
                    .create();

            alertDialog.show();
        }
    }
}