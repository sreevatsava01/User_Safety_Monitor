package com.example.guardianapp
import android.content.Context
import android.util.Log
import android.widget.Toast
import com.amazonaws.auth.BasicAWSCredentials
import com.amazonaws.regions.Region
import com.amazonaws.regions.Regions
import com.amazonaws.services.dynamodbv2.AmazonDynamoDBClient
import com.amazonaws.services.dynamodbv2.model.AttributeValue
import com.amazonaws.services.dynamodbv2.model.PutItemRequest
import com.amazonaws.services.dynamodbv2.model.ScanRequest
import com.google.android.gms.maps.model.LatLng
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.util.UUID

class DynamoDBManager(private val context: Context) {

    private val awsCredentials = BasicAWSCredentials(
        context.getString(R.string.aws_access_key),
        context.getString(R.string.aws_secret_key)
    )

    var dynamoDBClient: AmazonDynamoDBClient  =
        AmazonDynamoDBClient(awsCredentials).apply {
            setRegion(Region.getRegion(Regions.US_EAST_1))
        }


    fun fetchAllPolygons(onResult: (List<List<LatLng>>) -> Unit, onError: (Exception) -> Unit) {
        CoroutineScope(Dispatchers.IO).launch {
            try {
                val scanRequest = ScanRequest().withTableName("Polygons")
                val result = dynamoDBClient.scan(scanRequest)
                val items = result.items

                val polygons = items.mapNotNull { item ->
                    convertItemToPolygon(item)
                }

                withContext(Dispatchers.Main) {
                    onResult(polygons)
                }
            } catch (e: Exception) {
                withContext(Dispatchers.Main) {
                    onError(e)
                }
                Log.e("DynamoDBManager", "Error fetching from DynamoDB", e)
            }
        }
    }

    fun savePolygonToDatabase(points: MutableList<LatLng>) {
        CoroutineScope(Dispatchers.IO).launch {
            try {

                val tableName = "Polygons"
                val uniqueID = UUID.randomUUID().toString()
                val item = HashMap<String, AttributeValue>()
                item["PolygonID"] = AttributeValue().withS(uniqueID) // Replace with a unique identifier for your polygon
                item["Points"] = AttributeValue().withL(points.map { point ->
                    AttributeValue().withM(mapOf(
                        "Latitude" to AttributeValue().withN(point.latitude.toString()),
                        "Longitude" to AttributeValue().withN(point.longitude.toString())
                    ))
                })

                val putItemRequest = PutItemRequest()
                    .withTableName(tableName)
                    .withItem(item)

                dynamoDBClient.putItem(putItemRequest)
                withContext(Dispatchers.Main) {
                    Toast.makeText(context, "Polygon saved to DynamoDB", Toast.LENGTH_SHORT).show()
                }
            } catch (e: Exception) {
                Log.e("DatabaseHelper", "Error saving to DynamoDB", e)
                withContext(Dispatchers.Main) {
                    Toast.makeText(context, "Error saving to DynamoDB: ${e.message}", Toast.LENGTH_LONG).show()
                }
            }
        }
    }

    private fun convertItemToPolygon(item: Map<String, AttributeValue>): List<LatLng>? {
        val pointsList = item["Points"]?.l ?: return null
        return pointsList.mapNotNull { pointMap ->
            val latitude = pointMap.m["Latitude"]?.n?.toDoubleOrNull()
            val longitude = pointMap.m["Longitude"]?.n?.toDoubleOrNull()
            if (latitude != null && longitude != null) LatLng(latitude, longitude) else null
        }
    }


}
