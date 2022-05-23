#Author : Simar Saggu
#Created on 19th July, 2021
#Referred boto3 documentation for AWS SNS
#Listing topics was referred from: https://boto3.amazonaws.com/v1/documentation/api/latest/reference/services/sns.html#SNS.Client.list_topics
#Creating topics was referred from: https://boto3.amazonaws.com/v1/documentation/api/latest/reference/services/sns.html#SNS.Client.create_topic
#Adding subscribers to the topic was referred from here: https://boto3.amazonaws.com/v1/documentation/api/latest/reference/services/sns.html#SNS.Client.subscribe
#Publishing messages was referred from: https://boto3.amazonaws.com/v1/documentation/api/latest/reference/services/sns.html#SNS.Client.publish

import json
import boto3

def lambda_handler(event, context):
    topic_name=event['userId']
    email_id =event['email']
    phone_number=event['phoneNumber']
    user_name=event['name']
    
    try:
        message="QuestionMark: {0} commented on your post.\n\nClick on the link the below to see.\nLink: {1} ".format(user_name,event['url'])
        sns_client= boto3.client('sns')
        #get cuurent account id
        account_id = context.invoked_function_arn.split(":")[4]
        print(account_id)
        #SNS Topic ARN
        topic_arn="arn:aws:sns:us-east-1:"+account_id+":"+topic_name
        topic_list = sns_client.list_topics()
        topics = topic_list["Topics"]
    
        arn_exist=list(filter(lambda x: (x['TopicArn']==topic_arn),topics))
        
        if len(arn_exist)==0:
            
            print("User topic does not exist")
            create_topic_response = sns_client.create_topic(Name=topic_name)
            print("Created topic"+topic_name)
            
            responses = sns_client.subscribe(TopicArn=topic_arn, Protocol="SMS", Endpoint=phone_number)
            subscription_arn = responses["SubscriptionArn"]
            print("Created subscription"+subscription_arn)
            
            response = sns_client.subscribe(TopicArn=topic_arn, Protocol="email", Endpoint=email_id)
            subscription_arns = response["SubscriptionArn"]
            print("Created subscription"+subscription_arns)
           
            sns_client.publish(TopicArn=topic_arn,Subject="Comment", Message=message)
            print("Notification sent")
            
        else:
            
            print("User topic existing")
            sns_client.publish(TopicArn=topic_arn,Subject="Comment", Message=message)
            print("Notification sent")
            
        return {'statusCode': 200,'body': {'message':'Notification sent'}}

    except Exception as e:
        errorMessage=e
        print(errorMessage)
        
        return {'statusCode': 500,'body': {'message':'Internal Server Error','errorMessage':repr(errorMessage)}}
