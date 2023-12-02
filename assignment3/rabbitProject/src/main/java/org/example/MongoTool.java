package org.example;


import com.mongodb.BasicDBObject;
import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoClients;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import com.mongodb.client.model.Filters;
import com.mongodb.client.result.InsertOneResult;
import org.bson.Document;
import org.bson.types.ObjectId;

import java.io.IOException;
import java.sql.Connection;
import java.sql.SQLException;

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
     * save data
     * @param mqMessage
     * @return
     * @throws Exception
     */
    public void saveMsg(MqMessage mqMessage) throws Exception {
        String id = mqMessage.getId();
        Integer action = mqMessage.getAction();
        MongoCollection<Document> collection =  database.getCollection("album_like");
        Document document = collection.find(Filters.eq("_id", new ObjectId(id))).first();
        if(document == null){
            int like = action == 1 ? 1 : 0;
            int dislike = action != 1 ? 1 : 0;
            Document doc = new Document("_id", new ObjectId(id))
                    .append("like", like)
                    .append("dislike", dislike);
            database.getCollection("album_like").insertOne(doc);
        }else{
            int like = action == 1 ? 1 : 0;
            int dislike = action != 1 ? 1 : 0;
            like += document.getInteger("like");
            dislike += document.getInteger("dislike");
            BasicDBObject updateFields = new BasicDBObject();
            updateFields.append("like", like);
            updateFields.append("dislike", dislike);

            BasicDBObject setQuery = new BasicDBObject();
            setQuery.append("$set", updateFields);
            BasicDBObject searchQuery = new BasicDBObject("_id", new ObjectId(id));
            collection.updateOne(searchQuery, updateFields);
        }

    }
}
