package com.g11.questionmark.Services;

import com.amazonaws.auth.AWSStaticCredentialsProvider;
import com.amazonaws.auth.BasicSessionCredentials;
import com.amazonaws.regions.Regions;
import com.amazonaws.services.secretsmanager.AWSSecretsManager;
import com.amazonaws.services.secretsmanager.AWSSecretsManagerClientBuilder;
import com.amazonaws.services.secretsmanager.model.AWSSecretsManagerException;
import com.amazonaws.services.secretsmanager.model.GetSecretValueRequest;
import com.amazonaws.services.secretsmanager.model.GetSecretValueResult;
import com.g11.questionmark.Utils.ApplicationConfiguration;
import org.json.JSONObject;
// Referred AWS SDK for Java documentation to create Secrets Manager Service
// https://docs.aws.amazon.com/code-samples/latest/catalog/javav2-secretsmanager-src-main-java-com-example-secrets-GetSecretValue.java.html
public class SecretsManagerService {

    public static JSONObject databaseCredentialJson = null;

    public static JSONObject getSecret() {

        ApplicationConfiguration applicationConfiguration = ApplicationConfiguration.instance();
        String secretKey = applicationConfiguration.getAws_secret_access_key();
        String accessKey = applicationConfiguration.getAws_access_key_id();
        String token = applicationConfiguration.getAws_session_token();

        try {
            BasicSessionCredentials sessionCredentials = new BasicSessionCredentials(accessKey, secretKey,token);
            AWSSecretsManager secretsClient  = AWSSecretsManagerClientBuilder.standard()
                    .withCredentials(new AWSStaticCredentialsProvider(sessionCredentials))
                    .withRegion(Regions.US_EAST_1).build();

            String dbCredentials;
            GetSecretValueRequest getSecretValueRequest = new GetSecretValueRequest()
                    .withSecretId("mysql");
            GetSecretValueResult getSecretValueResult = null;
            getSecretValueResult = secretsClient.getSecretValue(getSecretValueRequest);
            if (getSecretValueResult.getSecretString() != null) {
                dbCredentials = getSecretValueResult.getSecretString();
                databaseCredentialJson = new JSONObject(dbCredentials);
            }
        } catch (AWSSecretsManagerException e) {
            System.out.println(e);
        }

        return databaseCredentialJson;
    }
}
