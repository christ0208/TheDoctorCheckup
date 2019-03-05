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
import android.widget.Toast
import com.android.volley.Request
import com.android.volley.Response
import com.android.volley.toolbox.StringRequest
import com.android.volley.toolbox.Volley
import kotlinx.android.synthetic.main.activity_diagnose_illness.*
import org.json.JSONArray

class DiagnoseIllnessActivity : AppCompatActivity() {

    private val URL: String = "https://sandbox-healthservice.priaid.ch"
    private val TOKEN = "eyJ0eXAiOiJKV1QiLCJhbGciOiJIUzI1NiJ9.eyJlbWFpbCI6InRkdW1teTczOUBnbWFpbC5jb20iLCJyb2xlIjoiVXNlciIsImh0dHA6Ly9zY2hlbWFzLnhtbHNvYXAub3JnL3dzLzIwMDUvMDUvaWRlbnRpdHkvY2xhaW1zL3NpZCI6IjQ2ODgiLCJodHRwOi8vc2NoZW1hcy5taWNyb3NvZnQuY29tL3dzLzIwMDgvMDYvaWRlbnRpdHkvY2xhaW1zL3ZlcnNpb24iOiIyMDAiLCJodHRwOi8vZXhhbXBsZS5vcmcvY2xhaW1zL2xpbWl0IjoiOTk5OTk5OTk5IiwiaHR0cDovL2V4YW1wbGUub3JnL2NsYWltcy9tZW1iZXJzaGlwIjoiUHJlbWl1bSIsImh0dHA6Ly9leGFtcGxlLm9yZy9jbGFpbXMvbGFuZ3VhZ2UiOiJlbi1nYiIsImh0dHA6Ly9zY2hlbWFzLm1pY3Jvc29mdC5jb20vd3MvMjAwOC8wNi9pZGVudGl0eS9jbGFpbXMvZXhwaXJhdGlvbiI6IjIwOTktMTItMzEiLCJodHRwOi8vZXhhbXBsZS5vcmcvY2xhaW1zL21lbWJlcnNoaXBzdGFydCI6IjIwMTktMDItMjYiLCJpc3MiOiJodHRwczovL3NhbmRib3gtYXV0aHNlcnZpY2UucHJpYWlkLmNoIiwiYXVkIjoiaHR0cHM6Ly9oZWFsdGhzZXJ2aWNlLnByaWFpZC5jaCIsImV4cCI6MTU1MTc2NjYxMCwibmJmIjoxNTUxNzU5NDEwfQ.mczi-3Gqb2wBXYtQw6QBos4xTbfQZRmDZS4q6M5eE5w"
    private val LANGUAGE = "en-gb"

    private lateinit var drawerLayout: DrawerLayout

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

        btn_reset.setOnClickListener{
            idConfirmedSymptoms.clear()
            confirmedSymptoms.clear()
            adapterConfirmSymptoms.notifyDataSetChanged()
        }

        list_symptoms.layoutManager = LinearLayoutManager(this)
        list_symptoms.adapter = adapterConfirmSymptoms
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
