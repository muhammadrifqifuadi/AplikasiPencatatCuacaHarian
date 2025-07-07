/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.mycompany.weatherloggerapp;


import java.sql.*;
import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author MSI GF63
 */
public class DatabaseManager {
    // SESUAIKAN DENGAN PENGATURAN DATABASE ANDA
    private static final String DB_URL = "jdbc:mysql://localhost:3306/weather_db";
    private static final String USER = "root";
    private static final String PASSWORD = ""; 
    
    private static int loggedInUserId = -1; // Menyimpan ID user yang login

    public static Connection getConnection() throws SQLException {
        return DriverManager.getConnection(DB_URL, USER, PASSWORD);
    }

    public static boolean validateUser(String username, String hashedPassword) {
        String sql = "SELECT id FROM users WHERE username = ? AND password_hash = ?";
        try (Connection conn = getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, username);
            pstmt.setString(2, hashedPassword);
            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    loggedInUserId = rs.getInt("id"); // Simpan ID user
                    return true;
                }
            }
        } catch (SQLException e) {
            System.err.println("Database error during validation: " + e.getMessage());
        }
        return false;
    }
    
    public static int getLoggedInUserId() {
        return loggedInUserId;
    }
    
    public static void logout() {
        loggedInUserId = -1;
    }

    public List<WeatherData> getAllWeatherData() {
        List<WeatherData> dataList = new ArrayList<>();
        String sql = "SELECT * FROM weather_data WHERE user_id = ? ORDER BY record_date DESC";
        try (Connection conn = getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setInt(1, getLoggedInUserId());
            ResultSet rs = pstmt.executeQuery();

            while (rs.next()) {
                WeatherData data = new WeatherData(
                    rs.getInt("id"),
                    rs.getString("city"),
                    rs.getDouble("temperature"),
                    rs.getInt("humidity"),
                    rs.getString("kondisi"),
                    rs.getString("notes_encrypted"),
                    rs.getTimestamp("record_date")
                );
                dataList.add(data);
            }
        } catch (SQLException e) {
            System.err.println("Error fetching weather data: " + e.getMessage());
        }
        return dataList;
    }

    public boolean addWeatherData(WeatherData data) {
        String sql = "INSERT INTO weather_data(city, temperature, humidity, kondisi, notes_encrypted, user_id) VALUES(?, ?, ?, ?, ?, ?)";
        try (Connection conn = getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, data.getCity());
            pstmt.setDouble(2, data.getTemperature());
            pstmt.setInt(3, data.getHumidity());
            pstmt.setString(4, data.getKondisi());
            pstmt.setString(5, data.getNotesEncrypted());
            pstmt.setInt(6, getLoggedInUserId());
            return pstmt.executeUpdate() > 0;
        } catch (SQLException e) {
            System.err.println("Error adding weather data: " + e.getMessage());
            return false;
        }
    }
    
    public boolean updateWeatherData(WeatherData data) {
        String sql = "UPDATE weather_data SET city = ?, temperature = ?, humidity = ?, kondisi = ?, notes_encrypted = ? WHERE id = ? AND user_id = ?";
         try (Connection conn = getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, data.getCity());
            pstmt.setDouble(2, data.getTemperature());
            pstmt.setInt(3, data.getHumidity());
            pstmt.setString(4, data.getKondisi());
            pstmt.setString(5, data.getNotesEncrypted());
            pstmt.setInt(6, data.getId());
            pstmt.setInt(7, getLoggedInUserId());
            return pstmt.executeUpdate() > 0;
        } catch (SQLException e) {
            System.err.println("Error updating weather data: " + e.getMessage());
            return false;
        }
    }
    
    public boolean deleteWeatherData(int id) {
        String sql = "DELETE FROM weather_data WHERE id = ? AND user_id = ?";
        try (Connection conn = getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, id);
            pstmt.setInt(2, getLoggedInUserId());
            return pstmt.executeUpdate() > 0;
        } catch (SQLException e) {
            System.err.println("Error deleting weather data: " + e.getMessage());
            return false;
        }
    }
    
    
    public boolean userExists(String username) {
        String sql = "SELECT id FROM users WHERE username = ?";
        try (Connection conn = getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, username);
            try (ResultSet rs = pstmt.executeQuery()) {
                return rs.next(); // true jika user sudah ada
            }
        } catch (SQLException e) {
            System.err.println("Error checking user existence: " + e.getMessage());
            return true; // Anggap ada error sebagai user sudah ada untuk keamanan
        }
    }

    public boolean addUser(String username, String fullName, String hashedPassword) {
        String sql = "INSERT INTO users(username, full_name, password_hash) VALUES(?, ?, ?)";
        try (Connection conn = getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, username);
            pstmt.setString(2, fullName);
            pstmt.setString(3, hashedPassword);
            int rowsAffected = pstmt.executeUpdate();
            return rowsAffected > 0;
        } catch (SQLException e) {
            System.err.println("Error adding user: " + e.getMessage());
            return false;
        }
    }
    
}
