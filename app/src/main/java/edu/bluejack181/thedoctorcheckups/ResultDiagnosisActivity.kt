package edu.bluejack181.thedoctorcheckups

import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import com.android.volley.Request
import com.android.volley.Response
import com.android.volley.toolbox.StringRequest
import com.android.volley.toolbox.Volley
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*

class ResultDiagnosisActivity : AppCompatActivity() {

    private var api: AccessApi = AccessApi()

    private val URL: String = api.getUrl()
    private val TOKEN: String = api.getToken()
    private val LANGUAGE: String = api.getLang()

    private val mAuth: FirebaseAuth = FirebaseAuth.getInstance()
    private val mDatabase: FirebaseDatabase = FirebaseDatabase.getInstance()
    private var databaseRef: DatabaseReference = mDatabase.getReference("users")
    private var userId: String = ""
    private var gender: String = ""
    private var year: Int = 0
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_result_diagnosis)

        var id = intent.getStringExtra("ids")
        userId = mAuth.currentUser!!.uid
//        var year: Int = getYear()
//        var gender: String = getGender()

        getGender()
        getYear(id)
//        yearThread.start()
//        genderThread.start()
//
//        yearThread.join()
//        genderThread.join()

//        getDiagnosis(id, year, gender)
    }

    private fun getGender(){
        var data = databaseRef.child(userId).child("gender")
//        var gender: String = ""
        var listener = object: ValueEventListener{
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                gender = dataSnapshot.getValue().toString()
//                Toast.makeText(applicationContext, gender, Toast.LENGTH_SHORT).show()
            }

            override fun onCancelled(error: DatabaseError) {
                Toast.makeText(applicationContext, error.toString(), Toast.LENGTH_SHORT).show()
            }
        }
        data.addValueEventListener(listener)
//        while(gender === ""){}
//        return gender
    }

    private fun getYear(id: String){
        var data = databaseRef.child(userId).child("year")
//        var year: Int = 0
        var listener = object: ValueEventListener{
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                year = dataSnapshot.getValue().toString().toInt()
//                Toast.makeText(applicationContext, year.toString(), Toast.LENGTH_SHORT).show()
//                Toast.makeText(applicationContext, id + " " + year.toString() + " " + gender, Toast.LENGTH_SHORT).show()
                getDiagnosis(id, year, gender)
            }

            override fun onCancelled(error: DatabaseError) {
                Toast.makeText(applicationContext, error.toString(), Toast.LENGTH_SHORT).show()
            }
        }

        data.addValueEventListener(listener)
//        while(year == 0){ }
//        return year
    }

    fun getDiagnosis(id: String, year: Int, gender: String){
//        Toast.makeText(applicationContext, year.toString() + " " + gender, Toast.LENGTH_SHORT).show()
        var queue = Volley.newRequestQueue(this)
        var target = URL + "/diagnosis?token=" + TOKEN + "&language=" + LANGUAGE + "&symptoms=[" + id + "]&gender=" + gender + "&year_of_birth=" + year.toString()
        val request = StringRequest(Request.Method.GET, target, Response.Listener {response ->
            var stringResponse = response.toString()
            Toast.makeText(applicationContext, stringResponse, Toast.LENGTH_SHORT).show()
        }, Response.ErrorListener { error ->
            Toast.makeText(applicationContext, error.toString(), Toast.LENGTH_SHORT).show()
        })

        queue.add(request)
    }
}
