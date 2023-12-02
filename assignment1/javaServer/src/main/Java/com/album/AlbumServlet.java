package com.album;

import java.io.IOException;
import java.io.PrintWriter;
import javax.servlet.ServletException;
import javax.servlet.annotation.MultipartConfig;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.Part;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

@WebServlet("/album/*")
@MultipartConfig(fileSizeThreshold = 1024 * 1024 * 10,    // 10 MB
        maxFileSize = 1024 * 1024 * 50,        // 50 MB
        maxRequestSize = 1024 * 1024 * 100)
public class AlbumServlet extends HttpServlet {

  private final String JSON = "application/json";

  private final String CODE = "UTF-8";

  private final Gson gson = new GsonBuilder().setPrettyPrinting().create();

  @Override
  protected void doPost(HttpServletRequest request, HttpServletResponse response) throws IOException {
    response.setContentType(JSON);
    try {
      Part imagePart = request.getPart("image");
//            String albumId = request.getParameter("albumId");
      long imageSize = imagePart.getSize();
      ImageMetaData metaData = new ImageMetaData(1L,imageSize);
      PrintWriter out = response.getWriter();
      response.setCharacterEncoding(CODE);
      out.print(gson.toJson(metaData));
      out.flush();
    } catch (Exception e) {
      throw new IOException(e.getMessage());
    }
  }

  @Override
  protected void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException {
    try {
      AlbumInfo album = new AlbumInfo("Blue Doe", "Album Title", "2023");
      PrintWriter out = response.getWriter();
      response.setContentType(JSON);
      response.setCharacterEncoding(CODE);
      out.print(gson.toJson(album));
      out.flush();
    } catch (Exception e) {
      throw new IOException(e.getMessage());
    }
  }
}

