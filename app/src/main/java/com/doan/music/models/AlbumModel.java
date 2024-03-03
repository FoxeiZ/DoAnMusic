package com.doan.music.models;

import android.os.Parcel;
import android.os.Parcelable;

public class AlbumModel implements Parcelable {
    private final long albumId;
    private final String albumName;

    public String getAlbumName() {
        return albumName;
    }

    public long getAlbumId() {
        return albumId;
    }

    public AlbumModel(long albumId, String albumName) {
        this.albumId = albumId;
        this.albumName = albumName;
    }

    protected AlbumModel(Parcel in) {
        albumId = in.readLong();
        albumName = in.readString();
    }

    public static final Creator<AlbumModel> CREATOR = new Creator<AlbumModel>() {
        @Override
        public AlbumModel createFromParcel(Parcel in) {
            return new AlbumModel(in);
        }

        @Override
        public AlbumModel[] newArray(int size) {
            return new AlbumModel[size];
        }
    };

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeLong(albumId);
    }
}
