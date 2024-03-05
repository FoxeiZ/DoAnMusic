package com.doan.music.models;

import android.content.ContentUris;
import android.net.Uri;
import android.os.Parcel;
import android.os.Parcelable;

public class ArtistModel implements Parcelable {
    private final long artistId;
    private final String artistName;
    private final long albumId;

    protected ArtistModel(Parcel in) {
        artistId = in.readLong();
        artistName = in.readString();
        albumId = in.readLong();
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeLong(artistId);
        dest.writeString(artistName);
        dest.writeLong(albumId);
    }

    @Override
    public int describeContents() {
        return 0;
    }

    public static final Creator<ArtistModel> CREATOR = new Creator<ArtistModel>() {
        @Override
        public ArtistModel createFromParcel(Parcel in) {
            return new ArtistModel(in);
        }

        @Override
        public ArtistModel[] newArray(int size) {
            return new ArtistModel[size];
        }
    };

    public long getArtistId() {
        return artistId;
    }

    public String getArtistName() {
        return artistName;
    }

    public ArtistModel(long artistId, long albumId, String artistName) {
        this.artistId = artistId;
        this.artistName = artistName;
        this.albumId = albumId;
    }

    public Uri getAlbumArtUri() {
        return ContentUris.withAppendedId(
                Uri.parse("content://media/external/audio/albumart"), albumId
        );
    }
}
