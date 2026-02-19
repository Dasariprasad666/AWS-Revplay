package com.example.demo.dto;

public class SongPerformanceDTO {
    private Long songId;
    private String title;
    private Long playCount;

    // Getters and Setters
    public Long getSongId() { return songId; }
    public void setSongId(Long songId) { this.songId = songId; }
    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }
    public Long getPlayCount() { return playCount; }
    public void setPlayCount(Long playCount) { this.playCount = playCount; }
}