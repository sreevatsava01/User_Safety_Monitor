package com.example.myapplication

import android.content.Context
import android.widget.Toast
import com.amazonaws.auth.BasicAWSCredentials
import com.amazonaws.regions.Region
import com.amazonaws.regions.Regions
import com.amazonaws.services.dynamodbv2.AmazonDynamoDBClient
import com.amazonaws.services.dynamodbv2.model.AttributeValue
import com.amazonaws.services.dynamodbv2.model.PutItemRequest
import com.amazonaws.services.dynamodbv2.model.ScanRequest
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.util.*
import kotlin.collections.HashMap

class SymptomsDBManager(private val context: Context) {
    private val dynamoDBClient: AmazonDynamoDBClient
    private val coroutineScope = CoroutineScope(Dispatchers.IO)
    init {
        val awsCredentials = BasicAWSCredentials(
            context.getString(R.string.aws_access_key),
            context.getString(R.string.aws_secret_key)
        )
        dynamoDBClient = AmazonDynamoDBClient(awsCredentials).apply {
            setRegion(Region.getRegion(Regions.US_EAST_1))
        }
    }

    fun uploadSymptomsToDynamoDB(symptomsMap: Map<String, Float>) {
        coroutineScope.launch {
            try {
                val item = HashMap<String, AttributeValue>()

                symptomsMap.forEach { (key, value) ->
                    item[key] = AttributeValue().withN(value.toString())
                }
                item["id"] = AttributeValue().withS(UUID.randomUUID().toString())

                val putItemRequest = PutItemRequest()
                    .withTableName("user_symptoms")
                    .withItem(item)

                dynamoDBClient.putItem(putItemRequest)

                withContext(Dispatchers.Main) {
                    Toast.makeText(context, "Uploaded signs to DynamoDB", Toast.LENGTH_LONG).show()
                }
            } catch (e: Exception) {
                withContext(Dispatchers.Main) {
                    Toast.makeText(context, "Error uploading to DynamoDB: ${e.message}", Toast.LENGTH_LONG).show()
                }
            }
        }
    }


    fun fetchLastSymptomsEntry(onResult: (Map<String, AttributeValue>) -> Unit) {
        CoroutineScope(Dispatchers.IO).launch {
            try {
                val scanRequest = ScanRequest().withTableName("user_symptoms")
                val result = dynamoDBClient.scan(scanRequest)
                val items = result.items

                if (items.isNotEmpty()) {
                    val lastItem = items[0]
                    withContext(Dispatchers.Main) {
                        onResult(lastItem)
                    }
                }
            } catch (e: Exception) {
                withContext(Dispatchers.Main) {
                    Toast.makeText(context, "Error fetching from DynamoDB: ${e.message}", Toast.LENGTH_LONG).show()
                }
            }
        }
    }


}
