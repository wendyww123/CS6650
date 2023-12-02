package org.example;

import java.io.IOException;
import java.io.PrintWriter;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import com.google.gson.Gson;

@WebServlet("/album/*")
public class AlbumServlet extends HttpServlet {

  private final MongoTool mongoTool = new MongoTool();

  private final Gson gson = new Gson();

  /**
   *
   * @param request
   * @param response
   * @throws IOException
   */
  @Override
  protected void doPost(HttpServletRequest request, HttpServletResponse response) throws IOException {
    response.setContentType("application/json");
    try {
      String id = mongoTool.add();
      ImageMetaData metaData = new ImageMetaData(id,100L);
      String metaDataJString = this.gson.toJson(metaData);
      PrintWriter out = response.getWriter();
      response.setCharacterEncoding("UTF-8");
      out.print(metaDataJString);
      out.flush();
    } catch (Exception e) {
      throw new RuntimeException(e);
    }
  }

  /**
   *
   * @param request
   * @param response
   * @throws IOException
   */
  @Override
  protected void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException {
    if (request.getPathInfo() != null && request.getPathInfo().length() > 1) {
      try {
        String albumID = request.getPathInfo().split("/")[1];
        AlbumInfo albumInfo = mongoTool.getById(albumID);
        String albumJsonString = this.gson.toJson(albumInfo);
        PrintWriter out = response.getWriter();
        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");
        out.print(albumJsonString);
        out.flush();
      } catch (Exception e) {
        throw new RuntimeException(e);
      }
    }
  }
}

