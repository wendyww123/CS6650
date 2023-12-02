package org.example;


import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoClients;
import com.mongodb.client.MongoDatabase;
import com.mongodb.client.model.Filters;
import com.mongodb.client.result.InsertOneResult;
import org.bson.Document;
import org.bson.types.ObjectId;

import java.io.BufferedInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.sql.*;

public class MongoTool {

    private static final MongoClient mongoClient = MongoClients.create("mongodb://localhost:27017");

    // 选择要操作的数据库
    private static final MongoDatabase database = mongoClient.getDatabase("album");

    public MongoTool(){

    }

    public void close(Connection connection) throws SQLException {
        if(mongoClient != null){
            mongoClient.close();
        }
    }

    /**
     * find album data by id
     * @param id
     * @return
     * @throws SQLException
     * @throws ClassNotFoundException
     */
    public AlbumInfo getById(String id) throws Exception {
        Document document = database.getCollection("album").find(Filters.eq("_id", new ObjectId(id))).first();
        if(document == null){
            return null;
        }
        String _id = document.getObjectId("_id").toString();
        String info = document.getString("info");
        AlbumInfo albumInfo = new AlbumInfo();
        albumInfo.setId(_id);
        albumInfo.setInfo(info);
        return albumInfo;
    }

    /**
     * save data
     * @return
     * @throws SQLException
     * @throws ClassNotFoundException
     * @throws IOException
     */
    public String add() throws Exception {
        Document doc = new Document("info", "{'artists': 'Sex Pistols', 'year': '1977', 'title': 'Sex Pistols'}")
                .append("image", "nmtb.png");
        InsertOneResult result = database.getCollection("album").insertOne(doc);
        if(result.wasAcknowledged()){
            return result.getInsertedId().asObjectId().getValue().toString();
        }
        return null;
    }

    public static void main(String[] args) throws Exception {
        MongoTool mongoTool = new MongoTool();
        String id = mongoTool.add();
        System.out.println(id);
        AlbumInfo albumInfo = mongoTool.getById(id);
        System.out.println(albumInfo);
    }
}
