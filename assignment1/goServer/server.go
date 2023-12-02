package main

import (
	"github.com/gin-gonic/gin"
	"net/http"
)

type AlbumInfo struct {
	Artists string `json:"artists,omitempty"`
	Title   string `json:"title,omitempty"`
	Year    string `json:"year,omitempty"`
}

type ImageMetaData struct {
	AlbumID   int64 `json:"albumID,omitempty"`
	ImageSize int64 `json:"imageSize,omitempty"`
}

func newAlbum() AlbumInfo {
	albumInfo := AlbumInfo{
		Artists: "Sex Pistols",
		Title:   "Never Mind the Bollocks",
		Year:    "1997",
	}
	return albumInfo
}

func newImage() ImageMetaData {
	imageMetaData := ImageMetaData{
		AlbumID:   1,
		ImageSize: 100,
	}
	return imageMetaData
}

func main() {
	r := gin.Default()
	r.GET("/album/:id", func(c *gin.Context) {
		c.JSON(http.StatusOK, newAlbum())
	})
	r.POST("/album", func(c *gin.Context) {
		c.JSON(http.StatusOK, newImage())
	})
	r.Run("0.0.0.0:8081") // listen and serve on 0.0.0.0:8080 (for windows "localhost:8080")
}
