package edu.bluejack181.thedoctorcheckups

import android.content.Intent
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.support.design.widget.NavigationView
import android.support.v4.view.GravityCompat
import android.support.v4.widget.DrawerLayout
import android.support.v7.app.ActionBar
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.Toolbar
import android.view.MenuItem
import android.widget.Toast
import com.android.volley.Request
import com.android.volley.RequestQueue
import com.android.volley.Response
import com.android.volley.toolbox.StringRequest
import com.android.volley.toolbox.Volley
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*
import kotlinx.android.synthetic.main.activity_result_diagnosis.*
import org.json.JSONArray
import org.json.JSONObject
import java.text.SimpleDateFormat
import java.util.*
import kotlin.concurrent.schedule
import kotlin.math.max

class ResultDiagnosisActivity : AppCompatActivity() {

    private lateinit var api: AccessApi

    private lateinit var URL: String
    private lateinit var TOKEN: String
    private lateinit var LANGUAGE: String

    private lateinit var drawerLayout: DrawerLayout

    private val mAuth: FirebaseAuth = FirebaseAuth.getInstance()
    private val mDatabase: FirebaseDatabase = FirebaseDatabase.getInstance()
    private var databaseRef: DatabaseReference = mDatabase.getReference("users")
    private var databaseRefIllness: DatabaseReference = mDatabase.getReference("illness_history")
    private var userId: String = ""
    private var gender: String = ""
    private var year: Int = 0
    private var list_diagnosis = ArrayList<Diagnosis>()
    private lateinit var queue: VolleySingleton
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_result_diagnosis)

        api = AccessApi(applicationContext)
        URL = api.getUrl()
        LANGUAGE = api.getLang()
        queue = VolleySingleton.getInstance(applicationContext)

        val toolbar: Toolbar = findViewById(R.id.toolbar)
        setSupportActionBar(toolbar)

        val actionBar: ActionBar? = supportActionBar
        actionBar?.apply {
            setDisplayHomeAsUpEnabled(true)
            setHomeAsUpIndicator(R.drawable.ic_menu)
        }

        drawerLayout = findViewById(R.id.drawer_layout)
        val navView: NavigationView = findViewById(R.id.nav_view)

        navView.setNavigationItemSelectedListener { menuItem ->
            menuItem.isChecked = true
            when(menuItem.itemId){
                R.id.nav_diagnose ->{
                    startActivity(Intent(applicationContext, DiagnoseIllnessActivity::class.java))
                    finish()
                }
                R.id.nav_history -> {
                    startActivity(Intent(applicationContext, HealthHistoryActivity::class.java))
                    finish()
                }
                R.id.nav_forum ->{
//                    Toast.makeText(applicationContext, "Forum", Toast.LENGTH_SHORT).show()
                    startActivity(Intent(applicationContext, ForumActivity::class.java))
                    finish()
                }
                R.id.nav_profile -> {
                    Toast.makeText(applicationContext, "Profile", Toast.LENGTH_SHORT).show()
                }
                R.id.nav_logout ->{
                    mAuth.signOut()
                    startActivity(Intent(this, MainActivity::class.java))
                    finish()
                }
            }

            drawerLayout.closeDrawers()

            true
        }

        var id = intent.getStringExtra("ids")
        userId = mAuth.currentUser!!.uid

//        Toast.makeText(applicationContext, userId, Toast.LENGTH_SHORT).show()

        Timer("fetchData", true).schedule(5000){
            getGender(id)
        }
//        getYear(id)

//        insertNewDiagnosis(userId)
    }

    private fun insertNewDiagnosis(userId: String){
//        butuh id user, nama penyakit, dan tanggal
        var recAccuracy: Int = 0
        var maxAccuracy: Int = 0
        var nameIllness: String = ""
        for (i in 0 until list_diagnosis.size){
            val e = list_diagnosis.get(i)
            maxAccuracy = max(e.accuracy, maxAccuracy)
            if(maxAccuracy != recAccuracy){
                nameIllness = e.name
            }

            recAccuracy = maxAccuracy
        }

        var primaryKey = databaseRefIllness.push().key.toString()
        var simpledate = SimpleDateFormat("dd-MM-yyyy")
        val date = Date()

        var curr_date = simpledate.format(date)

        var ill_history = IllnessHistory(mAuth.currentUser!!.uid, nameIllness, curr_date)
//        Toast.makeText(applicationContext, nameIllness, Toast.LENGTH_SHORT).show()

        databaseRefIllness.child(primaryKey).setValue(ill_history).addOnCompleteListener{
            Toast.makeText(applicationContext, "Inserted into history", Toast.LENGTH_SHORT).show()
        }
    }

    private fun getGender(id: String){
        var data = databaseRef.child(userId).child("gender")
//        var gender: String = ""
        var listener = object: ValueEventListener{
            override fun onDataChange(dataSnapshot: DataSnapshot) {
//                Toast.makeText(applicationContext, dataSnapshot.getValue().toString(), Toast.LENGTH_SHORT).show()
                gender = dataSnapshot.getValue().toString()
//                Toast.makeText(applicationContext, gender, Toast.LENGTH_SHORT).show()
                getYear(id)
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
//                Toast.makeText(applicationContext, dataSnapshot.getValue().toString() + " " + gender, Toast.LENGTH_SHORT).show()
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
        TOKEN = api.getToken()
        var target = URL + "/diagnosis?token=" + TOKEN + "&language=" + LANGUAGE + "&symptoms=[" + id + "]&gender=" + gender + "&year_of_birth=" + year.toString()
        val request = StringRequest(Request.Method.GET, target, Response.Listener {response ->
            var stringResponse = response.toString()
            var jsonArr = JSONArray(stringResponse)
            for (i in 0 until jsonArr.length()){
                val item = jsonArr.getJSONObject(i)
                val issue = item.getJSONObject("Issue")
                val accuracy = issue.getInt("Accuracy")
                if(accuracy >= 75) {
                    list_diagnosis.add(Diagnosis(issue.getInt("ID"), issue.getString("Name"), issue.getInt("Accuracy")))
                }
            }
//            Toast.makeText(applicationContext, stringResponse, Toast.LENGTH_SHORT).show()
            getInfo()
        }, Response.ErrorListener { error ->
            Toast.makeText(applicationContext, error.toString(), Toast.LENGTH_SHORT).show()
        })

        queue.addToRequestQueue(request)
    }

    fun getInfo(){
        for (i in 0 until list_diagnosis.size){
            var diagnosis = list_diagnosis.get(i)
            var id = diagnosis.id
            TOKEN = api.getToken()
            var targetUrl = URL + "/issues/" + id + "/info?token=" + TOKEN + "&language=" + LANGUAGE
            val request = StringRequest(Request.Method.GET,targetUrl, Response.Listener { response ->
                val stringResponse = response.toString()
                val jsonObject = JSONObject(stringResponse)
                diagnosis.prof_name = jsonObject.getString("ProfName")
                diagnosis.medical_condition = jsonObject.getString("MedicalCondition")
                diagnosis.treatment = jsonObject.getString("TreatmentDescription")
                list_diagnosis.set(i, diagnosis)

                if(i == list_diagnosis.size - 1) setDiagnosis()
            }, Response.ErrorListener { error ->
                Toast.makeText(applicationContext, error.toString(), Toast.LENGTH_SHORT).show()
            })

            queue.addToRequestQueue(request)
        }
    }

    fun setDiagnosis(){
        val adapterConfirmSymptoms = ResultDiagnosisAdapter(list_diagnosis, this)
        list_diagnose.layoutManager = LinearLayoutManager(this)
        list_diagnose.adapter = adapterConfirmSymptoms


        insertNewDiagnosis(userId)
    }

    override fun onOptionsItemSelected(item: MenuItem?): Boolean {
        return when(item!!.itemId){
            android.R.id.home -> {
                drawerLayout.openDrawer(GravityCompat.START)
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }
}
