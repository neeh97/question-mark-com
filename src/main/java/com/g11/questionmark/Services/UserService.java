package com.g11.questionmark.Services;

import com.g11.questionmark.Models.Question;
import com.g11.questionmark.Models.User;
import com.g11.questionmark.Utils.DatabaseConnection;
import org.springframework.util.StringUtils;

import java.sql.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class UserService {

    DatabaseConnection databaseConnection = new DatabaseConnection();

    public List<Question> performGetQuestions() {
        Connection connection = databaseConnection.getConnection();
        Question question = null;
        List<Question> questions = new ArrayList<>();
        if (connection != null) {
            try {
                String query = "SELECT * FROM questions ORDER BY votes DESC";
                PreparedStatement preparedStatement = connection.prepareStatement(query);
                ResultSet resultSet = preparedStatement.executeQuery();
                while (resultSet.next()) {
                    question = new Question();
                    question.setQuestionId(resultSet.getInt("question_id"));
                    Integer userId = resultSet.getInt("op_user_id");

                    question.setQuestionText(resultSet.getString("question_text"));
                    question.setQuestionTitle(resultSet.getString("question_title"));
                    question.setOpUserId(userId);
                    question.setVotes(resultSet.getInt("votes"));
                    questions.add(question);
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
        try {
            connection.close();
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }
        return questions;

    }

    public Boolean performSubmitQuestion(Question question, Integer userId) {
        Connection connection = databaseConnection.getConnection();
        Boolean flag = false;
        if (connection != null) {
            try {
                System.out.println("Inside try of submit question");
                String query = "INSERT INTO questions(op_user_id,question_title,question_text,dateofposting) VALUES (?,?,?,?)";
                PreparedStatement preparedStatement = connection.prepareStatement(query);
                preparedStatement.setInt(1, userId);
                preparedStatement.setString(2, question.getQuestionTitle());
                preparedStatement.setString(3, question.getQuestionText());
                preparedStatement.setString(4, String.valueOf(Timestamp.valueOf(LocalDateTime.now())));
                preparedStatement.executeUpdate();
                System.out.println("Inside try, after execution of execute update");
                flag = true;
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
        return flag;
    }

    public List<Question> performSearchQuestion(String searchString) {
        Connection connection = databaseConnection.getConnection();
        Question question = null;
        List<Question> questions = new ArrayList<>();
        System.out.println("Inside perform search");
        if (connection != null) {
           /* if (searchString.isEmpty()) {
                questions=performGetQuestions();
            } else {*/
                try {
                    System.out.println("Inside try block");
                    String query = "SELECT * FROM questions where question_title like ?;";
                    String searchText = "%" + searchString + "%";
                    PreparedStatement preparedStatement = connection.prepareStatement(query);
                    preparedStatement.setString(1, searchText);
                    ResultSet resultSet = preparedStatement.executeQuery();
                    System.out.println("The query is:" + query);
                    System.out.println("after result set");
                    while (resultSet.next()) {
                        System.out.println("In while loop");
                        question = new Question();
                        question.setQuestionId(resultSet.getInt("question_id"));
                        Integer userId = resultSet.getInt("op_user_id");

                        question.setQuestionText(resultSet.getString("question_text"));
                        question.setQuestionTitle(resultSet.getString("question_title"));
                        question.setOpUserId(userId);
                        question.setVotes(resultSet.getInt("votes"));
                        questions.add(question);
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
            try {
                connection.close();
            } catch (SQLException throwables) {
                throwables.printStackTrace();
            }
        //}

        return questions;

    }
}

