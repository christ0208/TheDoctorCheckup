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
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.RadioGroup
import android.widget.Toast
import com.android.volley.Request
import com.android.volley.Response
import com.android.volley.toolbox.StringRequest
import com.android.volley.toolbox.Volley
import com.google.firebase.auth.FirebaseAuth
import kotlinx.android.synthetic.main.activity_diagnose_illness.*
import org.json.JSONArray

class DiagnoseIllnessActivity : AppCompatActivity() {

    private val api: AccessApi = AccessApi()

    private val mAuth: FirebaseAuth = FirebaseAuth.getInstance()

    private val URL: String = api.getUrl()
    private val TOKEN = api.getToken()
    private val LANGUAGE = api.getLang()

    private lateinit var drawerLayout: DrawerLayout
    private lateinit var genderChosen: String

    private var idSymptoms: ArrayList<Int> = ArrayList<Int>()
    private var symptoms: ArrayList<String> = ArrayList<String>()
    private var idConfirmedSymptoms: ArrayList<Int> = ArrayList<Int>()
    private var confirmedSymptoms: ArrayList<String> = ArrayList<String>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_diagnose_illness)

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
                }
                R.id.nav_forum ->{
//                    Toast.makeText(applicationContext, "Forum", Toast.LENGTH_SHORT).show()
                    startActivity(Intent(applicationContext, ForumActivity::class.java))
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

        getSymptoms()

        val adapterSearch: ArrayAdapter<String> = ArrayAdapter<String>(this, android.R.layout.simple_dropdown_item_1line, symptoms)
        val adapterConfirmSymptoms = ConfirmSymptomsAdapter(confirmedSymptoms, this)

        txt_search.threshold = 1
        txt_search.setAdapter(adapterSearch)
        txt_search.onItemClickListener = AdapterView.OnItemClickListener { parent, view, position, id ->
            val selectedItem = parent.getItemAtPosition(position)
            val index = symptoms.indexOf(selectedItem)
            idConfirmedSymptoms.add(idSymptoms.get(index))
            confirmedSymptoms.add(symptoms.get(index))
            adapterConfirmSymptoms.notifyDataSetChanged()
        }

        btn_proceed.setOnClickListener {
            var ids: String = ""
            for (i in 0 until idConfirmedSymptoms.size){
                if(ids === ""){
                    ids = idConfirmedSymptoms.get(i).toString()
                }
                else{
                    ids += "," + idConfirmedSymptoms.get(i).toString()
                }
            }
            getDiagnosis(ids)

        }

        btn_reset.setOnClickListener{
            idConfirmedSymptoms.clear()
            confirmedSymptoms.clear()
            adapterConfirmSymptoms.notifyDataSetChanged()
        }

        list_symptoms.layoutManager = LinearLayoutManager(this)
        list_symptoms.adapter = adapterConfirmSymptoms
    }

    fun getDiagnosis(id: String){
        var i = Intent(this, ResultDiagnosisActivity::class.java)
        i.putExtra("ids", id)
        startActivity(i)
    }

    fun getSymptoms(){
        var queue = Volley.newRequestQueue(applicationContext)
        val currentUrl = URL + "/symptoms?token=" + TOKEN + "&language=" + LANGUAGE
        var request = StringRequest(Request.Method.GET, currentUrl, Response.Listener { response ->
            var stringResponse = response.toString()
            var jsonArr = JSONArray(stringResponse)
            for (i in 0 until jsonArr.length()){
                var jsonInner = jsonArr.getJSONObject(i)
                symptoms.add(jsonInner.getString("Name"))
                idSymptoms.add(jsonInner.getInt("ID"))
            }
        }, Response.ErrorListener { error ->
            Toast.makeText(applicationContext, error.toString(), Toast.LENGTH_SHORT).show()
        })

        queue.add(request)
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
