/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.mycompany.weatherloggerapp;

import java.sql.Timestamp;

/**
 *
 * @author MSI GF63
 */
public class WeatherData implements BaseEntity {
    private int id;
    private String city;
    private double temperature;
    private int humidity;
    private String kondisi;
    private String notesEncrypted;
    private Timestamp recordDate;

    public WeatherData(int id, String city, double temperature, int humidity, String kondisi, String notesEncrypted, Timestamp recordDate) {
        this.id = id;
        this.city = city;
        this.temperature = temperature;
        this.humidity = humidity;
        this.kondisi = kondisi;
        this.notesEncrypted = notesEncrypted;
        this.recordDate = recordDate;
    }
    
    // Konstruktor tanpa ID (untuk data baru)
    public WeatherData(String city, double temperature, int humidity, String kondisi, String notesEncrypted) {
        this.city = city;
        this.temperature = temperature;
        this.humidity = humidity;
        this.kondisi = kondisi;
        this.notesEncrypted = notesEncrypted;
    }

    // Getters and Setters
    
    @Override // Metode ini memenuhi kontrak dari BaseEntity
    public int getId() { return id; }
    
    public void setId(int id) { this.id = id; }
    public String getCity() { return city; }
    public void setCity(String city) { this.city = city; }
    public double getTemperature() { return temperature; }
    public void setTemperature(double temperature) { this.temperature = temperature; }
    public int getHumidity() { return humidity; }
    public void setHumidity(int humidity) { this.humidity = humidity; }
    public String getKondisi() { return kondisi; }
    public void setKondisi(String kondisi) { this.kondisi = kondisi; }
    public String getNotesEncrypted() { return notesEncrypted; }
    public void setNotesEncrypted(String notesEncrypted) { this.notesEncrypted = notesEncrypted; }
    public Timestamp getRecordDate() { return recordDate; }
    public void setRecordDate(Timestamp recordDate) { this.recordDate = recordDate; }
}
