/*
Created by CP18-1
16:05 28/02/2019
 */


package edu.bluejack181.thedoctorcheckups

import android.content.Intent
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import kotlinx.android.synthetic.main.activity_register.*

class RegisterActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_register)

        btn_register.setOnClickListener{
            Toast.makeText(this, "Testing Register", Toast.LENGTH_SHORT).show()
        }

        btn_login.setOnClickListener{
//            Toast.makeText(this, "Testing login", Toast.LENGTH_SHORT).show()
            finish()
        }
    }
}
