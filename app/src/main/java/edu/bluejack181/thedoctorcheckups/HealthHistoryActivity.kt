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
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*
import kotlinx.android.synthetic.main.activity_health_history.*
import org.apache.poi.hssf.usermodel.HSSFWorkbook
import org.apache.poi.ss.usermodel.Cell
import org.apache.poi.ss.usermodel.Workbook
import org.apache.poi.hssf.usermodel.HSSFCellStyle
import org.apache.poi.hssf.util.HSSFColor
import org.apache.poi.ss.usermodel.CellStyle
import org.apache.poi.ss.usermodel.Sheet
import org.apache.poi.hssf.record.aggregates.RowRecordsAggregate.createRow
import java.io.File
import java.io.FileOutputStream
import java.text.SimpleDateFormat
import java.util.*


class HealthHistoryActivity : AppCompatActivity() {

    private lateinit var drawerLayout: DrawerLayout

    private val mDatabase: FirebaseDatabase = FirebaseDatabase.getInstance()
    private val mAuth: FirebaseAuth = FirebaseAuth.getInstance()
    private val databaseRef: DatabaseReference = mDatabase.getReference("illness_history")
    private val databaseRefUser: DatabaseReference = mDatabase.getReference("users")

    private var list_illness_history: ArrayList<IllnessHistory> = ArrayList()
    private var userId: String = ""
    private var userData: User = User()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_health_history)

        userId = mAuth.currentUser!!.uid

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
                R.id.nav_logout ->{
                    mAuth.signOut()
                    startActivity(Intent(this, MainActivity::class.java))
                    finish()
                }
            }

            drawerLayout.closeDrawers()

            true
        }

        btn_export.setOnClickListener {
            exportToExcel()
        }

        getIllnessHistory()
    }

    fun exportToExcel(){
        getUser()

        var simpleDate = SimpleDateFormat("dd-MM-yyyy")
        var current_date = Date()

        var filename: String = userData.name + "-" + simpleDate.format(current_date) + ".xlsx"
        var wb = HSSFWorkbook()

        var c: Cell? = null
        val cs = wb.createCellStyle()
        cs.fillForegroundColor = HSSFColor.LIME.index
        cs.fillPattern = HSSFCellStyle.SOLID_FOREGROUND

        var sheet1: Sheet? = null
        sheet1 = wb.createSheet("myOrder")

        var line = 0
        var row = sheet1.createRow(line++)

        c = row.createCell(0)
        c.setCellValue("Name")
        c.setCellStyle(cs)

        c = row.createCell(1)
        c.setCellValue(userData.name)
        c.setCellStyle(cs)

        row = sheet1.createRow(line++)

        c = row.createCell(0)
        c.setCellValue("Gender")
        c.setCellStyle(cs)

        c = row.createCell(1)
        c.setCellValue(userData.gender)
        c.setCellStyle(cs)

        row = sheet1.createRow(line++)

        c = row.createCell(0)
        c.setCellValue("Date of Birth")
        c.setCellStyle(cs)

        c = row.createCell(1)
        c.setCellValue(userData.day.toString() + "/" + userData.month.toString() + "/" + userData.year.toString())
        c.setCellStyle(cs)

        row = sheet1.createRow(line++)
        c = row.createCell(0)
        c.setCellValue("History of Illness:")
        c.setCellStyle(cs)

        for( illness in list_illness_history ){
            c = row.createCell(1)
            c.setCellValue(illness.illness)
            c.setCellStyle(cs)
            row = sheet1.createRow(line++)
        }

        val file = File(this.getExternalFilesDir(null), filename)
        var os: FileOutputStream? = null

        os = FileOutputStream(file)
        wb.write(os)

        os.close()
        Toast.makeText(applicationContext, "Saved to " + this.getExternalFilesDir(null), Toast.LENGTH_SHORT).show()
    }

    fun getUser(){
        var reference = databaseRefUser.child(userId)
        var listener = object: ValueEventListener{
            override fun onDataChange(p0: DataSnapshot) {
                userData = p0.getValue(User::class.java)!!
            }

            override fun onCancelled(p0: DatabaseError) {

            }
        }

        reference.addValueEventListener(listener)
    }

    fun getIllnessHistory(){
        var listener = object: ValueEventListener{
            override fun onDataChange(p0: DataSnapshot) {
                for (snapshot in p0.children){
                    var element = snapshot.getValue(IllnessHistory::class.java)!!
                    if(element.userid.equals(userId, true)){
                        list_illness_history.add(snapshot.getValue(IllnessHistory::class.java)!!)
                    }
                }
                setHealthHistory()
            }

            override fun onCancelled(p0: DatabaseError) {

            }
        }
        databaseRef.addValueEventListener(listener)
    }

    fun setHealthHistory(){
        var adapterHealthHistory = HealthHistoryAdapter(list_illness_history, this)
        list_illness.layoutManager = LinearLayoutManager(this)
        list_illness.adapter = adapterHealthHistory
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
