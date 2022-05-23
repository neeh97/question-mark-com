package com.g11.questionmark.Utils;

import com.g11.questionmark.Services.SecretsManagerService;
import org.json.JSONObject;
import java.sql.Connection;
import java.sql.DriverManager;

public class DatabaseConnection {

    private String DB_URL;
    private String DB_USERNAME;
    private String DB_PASSWORD;
    private String DB_NAME;
    JSONObject secret = SecretsManagerService.getSecret();

    public DatabaseConnection(){
        this.DB_URL= (String) secret.get("host");
        this.DB_NAME = (String) secret.get("dbname");
        this.DB_USERNAME = (String) secret.get("username");
        this.DB_PASSWORD = (String) secret.get("password");
//        this.DB_URL= "group11-database.c8wuqih51ara.us-east-1.rds.amazonaws.com";
//        this.DB_NAME = "questionmark";
//        this.DB_USERNAME = "group11";
//        this.DB_PASSWORD = "group11csci5409";
    }
    public Connection getConnection() {
        Connection connection = null;
        System.out.println(this.DB_NAME+" "+this.DB_PASSWORD+" "+this.DB_URL+" "+this.DB_USERNAME);
        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
            connection = DriverManager.getConnection("jdbc:mysql://"+this.DB_URL+"/"+this.DB_NAME+"?user="+this.DB_USERNAME+"&password="+this.DB_PASSWORD);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return connection;
    }
}
