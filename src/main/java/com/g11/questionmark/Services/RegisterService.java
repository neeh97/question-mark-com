package com.g11.questionmark.Services;

import com.g11.questionmark.Models.User;
import com.g11.questionmark.Utils.DatabaseConnection;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

public class RegisterService {
    DatabaseConnection databaseConnection = new DatabaseConnection();

    public void performRegistration(User user){
        Connection connection = databaseConnection.getConnection();
        if (connection != null) {
            try {
                String query = "INSERT INTO users(email,password,username,mob_no) VALUES (?,?,?,?)";
                String mobno = user.getCountryCode()+user.getMobileNo();
                String password = user.getPassword();
                BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();
                String encryptpassword = encoder.encode(password);
                PreparedStatement preparedStatement = connection.prepareStatement(query);
                preparedStatement.setString(1,user.getEmail());
                preparedStatement.setString(2,encryptpassword);
                preparedStatement.setString(3,user.getUsername());
                preparedStatement.setString(4,mobno);
                preparedStatement.executeUpdate();
            } catch (Exception e) {
                e.printStackTrace();
            } finally {
                try {
                    connection.close();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
    }
}
