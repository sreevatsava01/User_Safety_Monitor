package com.example.myapplication

import com.amazonaws.auth.BasicAWSCredentials
import com.amazonaws.regions.Region
import com.amazonaws.regions.Regions
import com.amazonaws.services.sqs.AmazonSQSClient
import com.amazonaws.services.sqs.model.SendMessageRequest
import org.json.JSONObject

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

fun sendToSQS(jsonData: JSONObject) {
    CoroutineScope(Dispatchers.IO).launch {
        try {
            val awsCredentials = BasicAWSCredentials("AKIAQKCB34WRBO6ZH3C2", "kqOqBVIIlfwyQInhAvs2nVizbnejSI5ToB3H7Oxf")
            val sqsClient = AmazonSQSClient(awsCredentials)

            // Specify your region if necessary
            sqsClient.setRegion(Region.getRegion(Regions.US_EAST_1))

            val queueUrl = "https://sqs.us-east-1.amazonaws.com/021612979618/MC_project5"

            val sendMessageRequest = SendMessageRequest(queueUrl, jsonData.toString())
            sqsClient.sendMessage(sendMessageRequest)
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

}
