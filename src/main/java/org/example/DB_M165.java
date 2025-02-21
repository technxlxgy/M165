package org.example;

import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoClients;
import com.mongodb.client.MongoDatabase;
import org.bson.Document;

public class DB_M165 {

    public static void main(String[] args) {
        // Create a new Mongo Client
        /*
        MongoClient client = MongoClients.create()) {
        MongoDatabase db = client.getDatabase("M165_DB");
        System.out.println("Got database: " + db.getName());*/

        //MongoCollection<Document> collection = db.getCollection("chat");
        //collection.insertOne(Document.parse("{ name: \"Iva\" }"));

        //System.out.println("number of documents: " + collection.countDocuments());


        // Make connection and create database
        String uri = "mongodb://localhost:27017";
        MongoClient mongoClient = MongoClients.create(uri);

        // Accessing the database
        MongoDatabase db = mongoClient.getDatabase("discount-moebel");

        // Create collection
        var posts = db.getCollection("posts");

        for (int i = 0; i < 2; i++) {
            String json = API.simulateRandomPost();

            //JsonObject jsonObject = JsonParse.parseString(json).getAsJsonObject();

            //System.out.println("JSONObj: " + jsonObject);
            //json.getString("username");

            //String userName = "username";

            //JsonObject userNameObject = new JsonObject(userName);


            //userNameObject. (userName, jsonObject.remove(userNameObject));

            System.out.println(json);
            posts.insertOne(Document.parse(json));

            //System.out.println("Username: " + userName);
            //System.out.println("Username Obj: " + userNameObject);

        }
    }
}
