package com.album;

import java.io.Serializable;

public class AlbumInfo implements Serializable {
  private String artists;
  private String title;
  private String year;

  public AlbumInfo(String artists, String title, String year) {
    this.artists = artists;
    this.title = title;
    this.year = year;
  }

  public AlbumInfo() {
  }

  public String getArtists() {
    return artists;
  }

  public String getTitle() {
    return title;
  }

  public String getYear() {
    return year;
  }
}
