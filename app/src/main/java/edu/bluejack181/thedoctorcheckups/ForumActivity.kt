package edu.bluejack181.thedoctorcheckups

import android.content.Intent
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.support.design.widget.NavigationView
import android.support.design.widget.TabLayout
import android.support.v4.view.GravityCompat
import android.support.v4.view.ViewPager
import android.support.v4.widget.DrawerLayout
import android.support.v7.app.ActionBar
import android.support.v7.widget.Toolbar
import android.view.MenuItem
import android.widget.Toast
import com.google.firebase.auth.FirebaseAuth

class ForumActivity : AppCompatActivity() {

    private lateinit var drawerLayout: DrawerLayout

    private val mAuth: FirebaseAuth = FirebaseAuth.getInstance()
    private val adapter: ForumTabAdapter = ForumTabAdapter(supportFragmentManager)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_forum)

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

        var viewPager: ViewPager = findViewById(R.id.forum_pager)
        var tabLayout: TabLayout = findViewById(R.id.tab_layout)

        adapter.addFragment(ViewForumFragment(), "View Forum")
        adapter.addFragment(CreateForumFragment(), "Create Forum")

        viewPager.adapter = adapter
        tabLayout.setupWithViewPager(viewPager)
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
