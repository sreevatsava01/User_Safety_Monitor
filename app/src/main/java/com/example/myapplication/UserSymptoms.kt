package com.example.myapplication
import android.os.Bundle
import android.widget.RatingBar
import androidx.activity.ComponentActivity
import com.amazonaws.services.dynamodbv2.model.AttributeValue
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class UserSymptoms : ComponentActivity() {

    private lateinit var symptomsDBManager: SymptomsDBManager


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_user_symptoms)

        symptomsDBManager = SymptomsDBManager(this)
        symptomsDBManager.fetchLastSymptomsEntry { item ->
            updateUIWithSymptoms(item)
        }
    }


    private fun updateUIWithSymptoms(item: Map<String, AttributeValue>) {
        CoroutineScope(Dispatchers.Main).launch {
            findViewById<RatingBar>(R.id.usernausearating).rating = item["nausea"]?.n?.toFloat() ?: 0f
            findViewById<RatingBar>(R.id.userheadAcheRating).rating = item["headAche"]?.n?.toFloat() ?: 0f
            findViewById<RatingBar>(R.id.userdiarrheaRating).rating = item["diarrhea"]?.n?.toFloat() ?: 0f
            findViewById<RatingBar>(R.id.usersoar_throatRating).rating = item["soar_throat"]?.n?.toFloat() ?: 0f
            findViewById<RatingBar>(R.id.userfeverRating).rating = item["fever"]?.n?.toFloat() ?: 0f
            findViewById<RatingBar>(R.id.usermuscle_acheRating).rating = item["muscle_ache"]?.n?.toFloat() ?: 0f
            findViewById<RatingBar>(R.id.userloss_of_smell_tasteRating).rating = item["loss_of_smell_taste"]?.n?.toFloat() ?: 0f
            findViewById<RatingBar>(R.id.usercoughRating).rating = item["cough"]?.n?.toFloat() ?: 0f
            findViewById<RatingBar>(R.id.usershortness_breathRating).rating = item["shortness_breath"]?.n?.toFloat() ?: 0f
            findViewById<RatingBar>(R.id.userfeeling_tiredRating).rating = item["feeling_tired"]?.n?.toFloat() ?: 0f
        }
    }

}
