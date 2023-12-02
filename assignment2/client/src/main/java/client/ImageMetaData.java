package client;

public class ImageMetaData {

    private String albumId;

    private Long imageSize;

    public ImageMetaData(String albumId, Long imageSize) {
        this.albumId = albumId;
        this.imageSize = imageSize;
    }

    public String getAlbumId() {
        return albumId;
    }

    public void setAlbumId(String albumId) {
        this.albumId = albumId;
    }

    public Long getImageSize() {
        return imageSize;
    }

    public void setImageSize(Long imageSize) {
        this.imageSize = imageSize;
    }
}
