package com.example.myapp3;

public class MyDataModel {
    private int id;
    private String artist;
    private String trackTitle;
    private String entryTime;

    public MyDataModel(int id, String artist, String trackTitle, String entryTime) {
        this.id = id;
        this.artist = artist;
        this.trackTitle = trackTitle;
        this.entryTime = entryTime;
    }

    public String getId() {
        return String.valueOf(id);
    }

    public String getArtist() {
        return artist;
    }

    public String getTrackTitle() {
        return trackTitle;
    }

    public String getEntryTime() {
        return entryTime;
    }

    @Override
    public String toString() {
        return "ID: " + id + "\nArtist: " + artist + "\nTrack Title: " + trackTitle + "\nEntry Time: " + entryTime;
    }
}
