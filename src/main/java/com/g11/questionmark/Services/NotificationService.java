//Author : Simar Saggu
// Created on 19th July, 2021
package com.g11.questionmark.Services;

import com.amazonaws.auth.AWSStaticCredentialsProvider;
import com.amazonaws.auth.BasicAWSCredentials;
import com.amazonaws.auth.BasicSessionCredentials;
import com.amazonaws.auth.profile.ProfileCredentialsProvider;
import com.amazonaws.regions.Regions;
import com.amazonaws.services.lambda.AWSLambda;
import com.amazonaws.services.lambda.AWSLambdaClientBuilder;
import com.amazonaws.services.lambda.model.InvokeRequest;
import com.amazonaws.services.lambda.model.InvokeResult;
import com.amazonaws.services.lambda.model.ServiceException;
import com.g11.questionmark.RequestBody.NotificationRequest;
import com.g11.questionmark.Utils.ApplicationConfiguration;
import org.json.JSONObject;

import java.nio.charset.StandardCharsets;

//Referred AWS SDK for Java Documentation for invoking AWS Lambda function
//https://docs.aws.amazon.com/sdk-for-java/v1/developer-guide/examples-lambda.html

public class NotificationService {

    ApplicationConfiguration applicationConfiguration = ApplicationConfiguration.instance();
    public JSONObject createPayload(NotificationRequest notificationRequest){

        JSONObject payload = new JSONObject();
        payload.put("name",notificationRequest.getName());
        payload.put("userId",notificationRequest.getUserId());
        payload.put("email",notificationRequest.getEmail());
        payload.put("phoneNumber",notificationRequest.getPhoneNumber());
        payload.put("url",notificationRequest.getUrl());
        return payload;
    }

    public void sendNotification(NotificationRequest notificationRequest){
        JSONObject payload = createPayload(notificationRequest);
        System.out.println(payload);
        InvokeRequest invokeRequest = new InvokeRequest().withFunctionName("arn:aws:lambda:us-east-1:680470716395:function:notificationService")
                .withPayload(String.valueOf(payload));
        InvokeResult invokeResult = null;
        try {
            String secretKey = applicationConfiguration.getAws_secret_access_key();
            String accessKey = applicationConfiguration.getAws_access_key_id();
            String token = applicationConfiguration.getAws_session_token();
            System.out.println(secretKey);
            System.out.println(accessKey);
            System.out.println(token);
            BasicSessionCredentials sessionCredentials = new BasicSessionCredentials(accessKey, secretKey,token);
            AWSLambda lambdaClient = AWSLambdaClientBuilder.standard()
                    .withCredentials(new AWSStaticCredentialsProvider(sessionCredentials))
                    .withRegion(Regions.US_EAST_1).build();

            invokeResult = lambdaClient.invoke(invokeRequest);

            String response = new String(invokeResult.getPayload().array(), StandardCharsets.UTF_8);
            System.out.println(response);

        } catch (ServiceException e) {
            System.out.println(e);
        }
    }

}
