/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.mycompany.weatherloggerapp;

import com.mongodb.client.MongoClients;
import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoDatabase;
import com.mongodb.client.MongoCollection;
import org.bson.Document;
import java.util.Date;


/**
 *
 * @author MSI GF63
 */
public class AuditLogger {
    private static final String CONNECTION_STRING = "mongodb://localhost:27017";
    private static final String DATABASE_NAME = "weather_app_logs";
    private static final String COLLECTION_NAME = "audit_logs";
    
    public static void logAction(String username, String action, String details) {
        // 'try-with-resources' ini penting untuk membuat dan menutup koneksi secara otomatis
        try (MongoClient mongoClient = MongoClients.create(CONNECTION_STRING)) {
            MongoDatabase database = mongoClient.getDatabase(DATABASE_NAME);
            MongoCollection<Document> collection = database.getCollection(COLLECTION_NAME);

            Document log = new Document("username", username)
                    .append("action", action)
                    .append("details", details)
                    .append("timestamp", new Date());

            collection.insertOne(log);
            System.out.println("Audit log saved to MongoDB.");
            
        } catch (Exception e) {
            System.err.println("Failed to log to MongoDB: " + e.getMessage());
        }
    }
}
