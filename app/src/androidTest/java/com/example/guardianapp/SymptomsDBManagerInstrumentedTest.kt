package com.example.guardianapp

import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.platform.app.InstrumentationRegistry
import com.amazonaws.services.dynamodbv2.AmazonDynamoDBClient
import com.amazonaws.services.dynamodbv2.model.*
import org.junit.Assert.*
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.Mockito.*
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.runBlockingTest
import java.util.UUID

@RunWith(AndroidJUnit4::class)
@ExperimentalCoroutinesApi
class SymptomsDBManagerInstrumentedTest {

    private lateinit var mockDynamoDBClient: AmazonDynamoDBClient
    private lateinit var symptomsDBManager: SymptomsDBManager

    @Before
    fun setUp() {
        val appContext = InstrumentationRegistry.getInstrumentation().targetContext
        mockDynamoDBClient = mock(AmazonDynamoDBClient::class.java)
        symptomsDBManager = SymptomsDBManager(appContext).apply {
            this.dynamoDBClient = mockDynamoDBClient
        }
    }

    @Test
    fun uploadSymptomsToDynamoDB_Success() = runBlockingTest {
        doNothing().`when`(mockDynamoDBClient).putItem(any(PutItemRequest::class.java))

        val symptomsMap = mapOf("cough" to 5.0f, "fever" to 6.0f)
        symptomsDBManager.uploadSymptomsToDynamoDB(symptomsMap)
    }

    @Test
    fun fetchLastSymptomsEntry_Success() = runBlockingTest {
        val fakeScanResult = createMockScanResult()
        `when`(mockDynamoDBClient.scan(any(ScanRequest::class.java))).thenReturn(fakeScanResult)

        var lastSymptoms: Map<String, AttributeValue>? = null
        symptomsDBManager.fetchLastSymptomsEntry { symptoms ->
            lastSymptoms = symptoms
        }

        assertNotNull(lastSymptoms)
    }

    private fun createMockScanResult(): ScanResult {
        val fakeItems = mutableListOf<Map<String, AttributeValue>>()
        val item = hashMapOf(
            "cough" to AttributeValue().withN("5.0"),
            "fever" to AttributeValue().withN("6.0"),
            "id" to AttributeValue().withS(UUID.randomUUID().toString())
        )
        fakeItems.add(item)

        return ScanResult().withItems(fakeItems)
    }
}
