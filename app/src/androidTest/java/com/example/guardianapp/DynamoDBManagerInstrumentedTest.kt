package com.example.guardianapp

import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.platform.app.InstrumentationRegistry
import com.amazonaws.services.dynamodbv2.AmazonDynamoDBClient
import com.amazonaws.services.dynamodbv2.model.*
import com.google.android.gms.maps.model.LatLng
import org.junit.Assert.*
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.Mockito.*
import kotlinx.coroutines.runBlocking

@RunWith(AndroidJUnit4::class)
class DynamoDBManagerInstrumentedTest {

    private lateinit var mockDynamoDBClient: AmazonDynamoDBClient
    private lateinit var dynamoDBManager: DynamoDBManager

    @Before
    fun setUp() {
        val appContext = InstrumentationRegistry.getInstrumentation().targetContext
        assertEquals("com.example.guardianapp", appContext.packageName)

        mockDynamoDBClient = mock(AmazonDynamoDBClient::class.java)
        dynamoDBManager = DynamoDBManager(appContext).apply {
            this.dynamoDBClient = mockDynamoDBClient
        }
    }

    @Test
    fun fetchAllPolygons_Success() = runBlocking {
        val fakeScanResult = createMockScanResult()
        `when`(mockDynamoDBClient.scan(any(ScanRequest::class.java))).thenReturn(fakeScanResult)

        var polygonsResult: List<List<LatLng>>? = null
        dynamoDBManager.fetchAllPolygons(
            onResult = { polygons -> polygonsResult = polygons },
            onError = { fail("Error fetching polygons: ${it.message}") }
        )

        assertNotNull(polygonsResult)
        assertTrue("Polygon list should not be empty", polygonsResult!!.isNotEmpty())
    }

    @Test
    fun fetchAllPolygons_Failure() = runBlocking {
        `when`(mockDynamoDBClient.scan(any(ScanRequest::class.java))).thenThrow(RuntimeException("Network error"))

        var errorOccurred = false
        dynamoDBManager.fetchAllPolygons(
            onResult = { fail("Expected an error, but got success") },
            onError = { errorOccurred = true }
        )

        assertTrue("Error callback should be triggered", errorOccurred)
    }

    @Test
    fun savePolygonToDatabase_Success() = runBlocking {
        doNothing().`when`(mockDynamoDBClient).putItem(any(PutItemRequest::class.java))
        dynamoDBManager.savePolygonToDatabase(mutableListOf(LatLng(40.7128, -74.0060)))
    }

    private fun createMockScanResult(): ScanResult {
        val fakeItems = mutableListOf<Map<String, AttributeValue>>()
        val item = hashMapOf(
            "Points" to AttributeValue().withL(
                listOf(
                    AttributeValue().withM(
                        hashMapOf(
                            "Latitude" to AttributeValue().withN("40.7128"),
                            "Longitude" to AttributeValue().withN("-74.0060")
                        )
                    )
                )
            )
        )
        fakeItems.add(item)
        return ScanResult().withItems(fakeItems)
    }

}
