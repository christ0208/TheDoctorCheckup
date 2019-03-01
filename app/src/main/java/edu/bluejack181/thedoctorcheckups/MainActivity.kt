/*
Created by CP18-1
16:05 28/02/2019
 */

package edu.bluejack181.thedoctorcheckups

import android.content.Intent
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity() {

    private val mAuth: FirebaseAuth = FirebaseAuth.getInstance()
    private val mDatabase: FirebaseDatabase = FirebaseDatabase.getInstance()
    private val mDatabaseReference: DatabaseReference? = mDatabase.getReference("users")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        btn_login.setOnClickListener{
//            Toast.makeText(this, "Testing Login", Toast.LENGTH_SHORT).show()
            val email = txt_email.text.toString()
            val password = txt_password.text.toString()
            mAuth.signInWithEmailAndPassword(email, password)
                    .addOnCompleteListener(this){task->
                        if(task.isSuccessful){
                            Toast.makeText(applicationContext, "Success Login!", Toast.LENGTH_SHORT).show()
                        }
                        else{
                            Toast.makeText(applicationContext, "Wrong email or password", Toast.LENGTH_SHORT).show()
                        }
                    }
        }

        btn_register.setOnClickListener{
//            Toast.makeText(this, "Testing Register", Toast.LENGTH_SHORT).show()
            startActivity(Intent(this, RegisterActivity::class.java))
        }
    }
}
