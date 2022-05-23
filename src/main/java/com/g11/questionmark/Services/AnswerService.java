package com.g11.questionmark.Services;

import com.g11.questionmark.Models.Answer;
import com.g11.questionmark.Models.Question;
import com.g11.questionmark.Models.User;
import com.g11.questionmark.RequestBody.NotificationRequest;
import com.g11.questionmark.Utils.DatabaseConnection;
import org.springframework.util.StringUtils;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;

public class AnswerService {

    DatabaseConnection databaseConnection = new DatabaseConnection();
    LoginService loginService = new LoginService();
    QuestionService questionService = new QuestionService();
    NotificationService notificationService = new NotificationService();

    public Boolean submitAnswer(Answer answer, Integer userId) {
        Connection connection = databaseConnection.getConnection();
        Boolean returnVal = false;
        if (connection != null) {
            try {
                String insertQuery = "INSERT INTO answers (answer_text,op_user_id,votes,question_id) VALUES (?,?,?,?)";
                PreparedStatement preparedStatement = connection.prepareStatement(insertQuery);
                preparedStatement.setString(1, answer.getAnswerText());
                preparedStatement.setInt(2, userId);
                preparedStatement.setInt(3, 0);
                preparedStatement.setInt(4, answer.getQuestionId());
                preparedStatement.executeUpdate();
                returnVal = true;
                sendNotification(answer, userId);
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
        return returnVal;
    }

    public List<Answer> getAllAnswersOfQuestion(Integer questionId) {
        Connection connection = databaseConnection.getConnection();
        List<Answer> answerList = new ArrayList<>();
        if (connection != null) {
            try {
                String getQuery = "SELECT * FROM answers WHERE question_id="+questionId+" ORDER BY votes DESC";
                PreparedStatement preparedStatement = connection.prepareStatement(getQuery);
                ResultSet resultSet = preparedStatement.executeQuery();
                while (resultSet.next()) {
                    Answer answer = new Answer();
                    Integer userId = resultSet.getInt("op_user_id");
                    String username = loginService.getUsername(userId);
                    if (StringUtils.hasText(username)) {
                        answer.setUsername(username);
                    }
                    answer.setQuestionId(questionId);
                    answer.setAnswerText(resultSet.getString("answer_text"));
                    answer.setAnswerId(resultSet.getInt("answer_id"));
                    answer.setOpUserId(userId);
                    answer.setVotes(resultSet.getInt("votes"));
                    answerList.add(answer);
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
        return answerList;
    }

    private void sendNotification(Answer answer, Integer userId) {
        NotificationRequest notificationRequest = new NotificationRequest();
        Question question = questionService.getQuestionDetails(answer.getQuestionId().toString());
        User questionUser = loginService.getUserdetails(question.getOpUserId());
        if (questionUser != null) {
            notificationRequest.setEmail(questionUser.getEmail());
            notificationRequest.setPhoneNumber(questionUser.getMobileNo());
            notificationRequest.setUrl("http://questionmarkvpc-env.eba-q86vrrrm.us-east-1.elasticbeanstalk.com/question/"+question.getQuestionId());
            notificationRequest.setUserId(questionUser.getUserId().toString());
            String answerOp = loginService.getUsername(userId);
            if (StringUtils.hasText(answerOp)) {
                notificationRequest.setName(answerOp);
            }
            notificationService.sendNotification(notificationRequest);
        }
    }
}
