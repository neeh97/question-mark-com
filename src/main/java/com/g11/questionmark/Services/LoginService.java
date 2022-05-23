package com.g11.questionmark.Services;

import com.g11.questionmark.Models.User;
import com.g11.questionmark.Utils.DatabaseConnection;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

public class LoginService {

    DatabaseConnection databaseConnection = new DatabaseConnection();

    public Integer performLogin(User user) {
        Connection connection = databaseConnection.getConnection();
        BCryptPasswordEncoder bCryptPasswordEncoder = new BCryptPasswordEncoder();
        Integer userId = 0;
        if (connection != null) {
            try {
                String query = "SELECT * FROM users WHERE email='" + user.getEmail() + "'";
                PreparedStatement preparedStatement = connection.prepareStatement(query);
                ResultSet resultSet = preparedStatement.executeQuery();
                if (resultSet.next()) {
                    if (bCryptPasswordEncoder.matches(user.getPassword(), resultSet.getString("password"))) {
                        userId = resultSet.getInt("userid");
                    }
                }
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
        return userId;
    }

    public String getUsername(Integer userId) {
        Connection connection = databaseConnection.getConnection();
        String username = "";
        if (connection != null) {
            try {
                String query = "SELECT * FROM users WHERE userid="+userId;
                PreparedStatement preparedStatement = connection.prepareStatement(query);
                ResultSet resultSet = preparedStatement.executeQuery();
                if (resultSet.next()) {
                    username = resultSet.getString("username");
                }
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
        return username;
    }

    public User getUserdetails(Integer userId) {
        Connection connection = databaseConnection.getConnection();
        User user = null;
        if (connection != null) {
            try {
                String query = "SELECT * FROM users WHERE userid="+userId;
                PreparedStatement preparedStatement = connection.prepareStatement(query);
                ResultSet resultSet = preparedStatement.executeQuery();
                if (resultSet.next()) {
                    user = new User();
                    user.setUserId(userId);
                    user.setUsername(resultSet.getString("username"));
                    user.setEmail(resultSet.getString("email"));
                    user.setMobileNo(resultSet.getString("mob_no"));
                }
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
        return user;
    }
}
