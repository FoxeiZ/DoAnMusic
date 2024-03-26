package com.doan.music.models;

import java.util.ArrayList;

public class PlaylistModel {
    private String title;
    private String description;
    private ArrayList<Long> items;

    public void setTitle(String title) {
        this.title = title;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public PlaylistModel() {

    }

    public PlaylistModel(String title, String description, ArrayList<Long> items) {
        this.title = title;
        this.description = description;
        this.items = items;
    }

    public ArrayList<Long> getItems() {
        if (items == null) items = new ArrayList<>();
        return items;
    }

    public void addItem(long item) {
        getItems().add(item);
    }

    public void removeItem(int index) {
        getItems().remove(index);
    }

    public void setItems(ArrayList<Long> items) {
        this.items = items;
    }

    public String getTitle() {
        return title;
    }

    public String getDescription() {
        return description;
    }

    public boolean isExist(long id) {
        return getItems().contains(id);
    }
}