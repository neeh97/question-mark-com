package com.g11.questionmark.Services;

import com.g11.questionmark.Models.Answer;
import com.g11.questionmark.Models.Question;
import com.g11.questionmark.Models.Vote;
import com.g11.questionmark.Utils.DatabaseConnection;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class VoteService {
    DatabaseConnection databaseConnection = new DatabaseConnection();

    private  Boolean decreaseTotalVotes(String Id, String table, Integer count ){
        Connection connection = databaseConnection.getConnection();
        Boolean returnVal = false;
        if (connection != null) {
            try {
                String whereCondition = " ";
                if (table.equals("answers")){
                    whereCondition = " WHERE answer_id=" + Id;
                }
                else{
                    whereCondition = " WHERE question_id=" + Id;
                }
                String updateSQL = "UPDATE "+table+" SET votes = votes - "+count+ whereCondition;
                PreparedStatement preparedStatement = connection.prepareStatement(updateSQL);
                preparedStatement.executeUpdate();
                returnVal = true;
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

    private Boolean increaseTotalVotes(String Id, String table, Integer count){
        Connection connection = databaseConnection.getConnection();
        Boolean returnVal = false;
        if (connection != null) {
            try {
                String whereCondition = " ";
                if (table.equals("answers")){
                    whereCondition = " WHERE answer_id=" + Id;
                }
                else{
                    whereCondition = " WHERE question_id=" + Id;
                }
                String updateSQL = "UPDATE "+table+" SET votes = votes + "+count+ whereCondition;
                PreparedStatement preparedStatement = connection.prepareStatement(updateSQL);
                preparedStatement.executeUpdate();
                returnVal = true;
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

    private Boolean updateVote( Vote vote, Integer userId){
        Connection connection = databaseConnection.getConnection();
        Boolean returnVal = false;
        if (connection != null) {
            try {
                String updateSQL = "UPDATE votes SET vote='"+vote.getVote()+"' WHERE entity_id="+vote.getEntityId()+" AND entity='"+vote.getEntity()+"' AND user_id="+ userId ;
                System.out.println(updateSQL);
                PreparedStatement preparedStatement = connection.prepareStatement(updateSQL);
                preparedStatement.executeUpdate();
                returnVal = true;
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

    public Boolean upVote(Vote vote, Integer userId) {
        Connection connection = databaseConnection.getConnection();
        Boolean returnVal = false;
        if (connection != null) {
            try {
                String getQuery = "SELECT * FROM votes WHERE entity_id="+vote.getEntityId()+" AND entity='"+vote.getEntity()+"' AND user_id="+ userId ;
                PreparedStatement preparedStatement = connection.prepareStatement(getQuery);
                ResultSet resultSet = preparedStatement.executeQuery();
                if (resultSet.next()) {
                    String type = resultSet.getString("vote");
                    if(type.equals("UP")){
                        String deleteQuery = "DELETE FROM votes WHERE entity_id="+vote.getEntityId()+" AND entity='"+vote.getEntity()+"' AND user_id="+ userId ;
                        preparedStatement = connection.prepareStatement(deleteQuery);
                        preparedStatement.executeUpdate();
                        if(decreaseTotalVotes(String.valueOf(vote.getEntityId()), vote.getEntity(), 1)){
                            returnVal = true;
                        }
                        else {
                            returnVal = false;
                        }
                    }
                    else{
                        if(updateVote(vote, userId) && increaseTotalVotes(String.valueOf(vote.getEntityId()), vote.getEntity(), 2)){
                            returnVal = true;
                        }
                        else {
                            returnVal = false;
                        }
                    }
                }
                else {
                    String insertQuery = "INSERT INTO votes (entity_id,entity,user_id,vote) VALUES (?,?,?,?)";
                    preparedStatement = connection.prepareStatement(insertQuery);
                    preparedStatement.setInt(1, vote.getEntityId());
                    preparedStatement.setString(2, vote.getEntity());
                    preparedStatement.setInt(3, userId);
                    preparedStatement.setString(4, vote.getVote());
                    preparedStatement.executeUpdate();

                    if(increaseTotalVotes(String.valueOf(vote.getEntityId()), vote.getEntity(), 1)){
                        returnVal = true;
                    }
                    else {
                        returnVal = true;
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
        return returnVal;
    }

    public Boolean downVote(Vote vote, Integer userId) {
        Connection connection = databaseConnection.getConnection();
        Boolean returnVal = false;
        if (connection != null) {
            try {
                String getQuery = "SELECT * FROM votes WHERE entity_id="+vote.getEntityId()+" AND entity='"+vote.getEntity()+"' AND user_id="+ userId ;
                PreparedStatement preparedStatement = connection.prepareStatement(getQuery);
                ResultSet resultSet = preparedStatement.executeQuery();
                if (resultSet.next()) {
                    String type = resultSet.getString("vote");
                    if(type.equals("DOWN")){
                        String deleteQuery = "DELETE FROM votes WHERE entity_id="+vote.getEntityId()+" AND entity='"+vote.getEntity()+"' AND user_id="+ userId ;
                        preparedStatement = connection.prepareStatement(deleteQuery);
                        preparedStatement.executeUpdate();
                        if(increaseTotalVotes(String.valueOf(vote.getEntityId()), vote.getEntity(), 1)){
                            returnVal = true;
                        }
                        else {
                            returnVal = false;
                        }
                    }
                    else{
                        if(updateVote(vote, userId) && decreaseTotalVotes(String.valueOf(vote.getEntityId()), vote.getEntity(), 2)){
                            returnVal = true;
                        }
                        else {
                            returnVal = false;
                        }
                    }
                }
                else {
                    String insertQuery = "INSERT INTO votes (entity_id,entity,user_id,vote) VALUES (?,?,?,?)";
                    preparedStatement = connection.prepareStatement(insertQuery);
                    preparedStatement.setInt(1, vote.getEntityId());
                    preparedStatement.setString(2, vote.getEntity());
                    preparedStatement.setInt(3, userId);
                    preparedStatement.setString(4, vote.getVote());
                    preparedStatement.executeUpdate();

                    if(decreaseTotalVotes(String.valueOf(vote.getEntityId()), vote.getEntity(), 1)){
                        returnVal = true;
                    }
                    else {
                        returnVal = true;
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
        return returnVal;
    }
}
