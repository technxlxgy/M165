package org.example;

import com.mongodb.client.MongoClients;
import org.bson.BsonArray;
import org.bson.BsonDocument;
import org.bson.Document;
import org.bson.json.JsonObject;

import java.util.List;

import static com.mongodb.client.model.Filters.eq;

public class Solution {

    public static void main(String[] args) {
        var client = MongoClients.create();

        var db = client.getDatabase("testdb");

        var posts = db.getCollection("posts");

        var json = API.simulateRandomPost();
        System.out.println(json);

        var doc = BsonDocument.parse(json);
        System.out.println(doc);
        var username = doc.getString("username").getValue();
        System.out.println("username: " + username);
        var message = doc.getString("message").getValue();
        System.out.println("message: " + message);
        var attachments = doc.getArray("attachments").getValues();
        System.out.println("attachments: " + attachments);
        for (var attachment : attachments) {
            System.out.println(" * " + attachment);
            var name = attachment.asDocument().getString("name").getValue();
            var size = attachment.asDocument().getInt32("size").getValue();
            System.out.println(" --> " + name + " : " + size);
        }

        long matches = posts.countDocuments(eq("username", username));
        System.out.println("matches = " + matches);
        if (matches > 0) {
            // username existiert bereits
            // ...

            var foundDoc = posts.find(eq("username", username)).first();
            var documents = foundDoc.getList("posts", Document.class);
            documents.add(new Document().append("message", message));
            //foundDoc.put("posts", documents);
            foundDoc.append("posts", documents);
        } else {
            var newMessage = new Document().append("message", message);
            var messageList = List.of(newMessage);
            var attachmentList = doc.getArray("attachments");
            BsonArray postArray = new BsonArray();

            // username existiert noch nicht
            var newDoc = new Document()
                    .append("username", username)
                    .append("posts", postArray);
            System.out.println(newDoc);
            posts.insertOne(newDoc);
        }
    }
}
