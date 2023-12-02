package org.example;

import java.io.Serializable;
import java.sql.Blob;

public class AlbumInfo implements Serializable {
  private String id;
  private byte[] image;
  private String info;

  public AlbumInfo() {
  }

  public String getId() {
    return id;
  }

  public void setId(String id) {
    this.id = id;
  }

  public byte[] getImage() {
    return image;
  }

  public void setImage(byte[] image) {
    this.image = image;
  }

  public String getInfo() {
    return info;
  }

  public void setInfo(String info) {
    this.info = info;
  }
}
