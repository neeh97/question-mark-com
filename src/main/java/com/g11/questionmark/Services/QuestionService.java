package com.g11.questionmark.Services;

import com.g11.questionmark.Models.Question;
import com.g11.questionmark.Utils.DatabaseConnection;
import org.springframework.util.StringUtils;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

public class QuestionService {

    DatabaseConnection databaseConnection = new DatabaseConnection();
    LoginService loginService = new LoginService();

    public Question getQuestionDetails(String questionId) {
        Connection connection = databaseConnection.getConnection();
        Question question = null;
        if (connection != null) {
            try {
                String getQuery = "SELECT * FROM questions WHERE question_id="+questionId;
                PreparedStatement preparedStatement = connection.prepareStatement(getQuery);
                ResultSet resultSet = preparedStatement.executeQuery();
                if (resultSet.next()) {
                    question = new Question();
                    question.setQuestionId(Integer.parseInt(questionId));
                    Integer userId = resultSet.getInt("op_user_id");
                    String username = loginService.getUsername(userId);
                    if (StringUtils.hasText(username)) {
                        question.setUsername(username);
                    }
                    question.setQuestionText(resultSet.getString("question_text"));
                    question.setQuestionTitle(resultSet.getString("question_title"));
                    question.setOpUserId(userId);
                    question.setVotes(resultSet.getInt("votes"));
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
        return question;
    }
}
