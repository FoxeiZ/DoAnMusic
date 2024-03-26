package com.doan.music.models;

import android.content.ContentUris;
import android.net.Uri;
import android.os.Parcel;
import android.os.Parcelable;
import android.provider.MediaStore;

import androidx.annotation.NonNull;

public class MusicModel implements Parcelable {
    private final String title, artist, duration;
    private final long songId, albumId;
    private boolean isPlaying;

    public long getSongId() {
        return songId;
    }

    public MusicModel(
            String title,
            String artist,
            String duration,
            boolean isPlaying,
            long songId,
            long albumId
    ) {
        this.title = title;
        this.artist = artist;
        this.duration = duration;
        this.isPlaying = isPlaying;
        this.songId = songId;
        this.albumId = albumId;
    }

    protected MusicModel(Parcel in) {
        title = in.readString();
        artist = in.readString();
        duration = in.readString();
        isPlaying = in.readByte() != 0;
        songId = in.readLong();
        albumId = in.readLong();
    }

    public static final Parcelable.Creator<MusicModel> CREATOR = new Parcelable.Creator<MusicModel>() {
        @Override
        public MusicModel createFromParcel(Parcel in) {
            return new MusicModel(in);
        }

        @Override
        public MusicModel[] newArray(int size) {
            return new MusicModel[size];
        }
    };

    public boolean isContain(String query) {
        query = query.toLowerCase();
        if (title.toLowerCase().contains(query)) return true;
        return artist.toLowerCase().contains(query);
    }

    public String getTitle() {
        return title;
    }

    public String getArtist() {
        return artist;
    }

    public String getDuration() {
        return duration;
    }

    public boolean isPlaying() {
        return isPlaying;
    }

    public Uri getFileUri() {
        return ContentUris.withAppendedId(MediaStore.Audio.Media.EXTERNAL_CONTENT_URI, songId);
    }

    public Uri getAlbumArtUri() {
        return ContentUris.withAppendedId(
                Uri.parse("content://media/external/audio/albumart"), albumId
        );
    }

    public void setPlaying(boolean playing) {
        isPlaying = playing;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(@NonNull Parcel parcel, int i) {
        parcel.writeString(title);
        parcel.writeString(artist);
        parcel.writeString(duration);
        parcel.writeByte((byte) (isPlaying ? 1 : 0));
        parcel.writeLong(songId);
        parcel.writeLong(albumId);
    }
}
