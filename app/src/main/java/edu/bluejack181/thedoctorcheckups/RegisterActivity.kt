/*
Created by CP18-1
16:05 28/02/2019
 */


package edu.bluejack181.thedoctorcheckups

import android.app.DatePickerDialog
import android.content.Intent
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.widget.RadioButton
import android.widget.RadioGroup
import android.widget.Toast
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import kotlinx.android.synthetic.main.activity_register.*
import java.util.*

class RegisterActivity : AppCompatActivity() {

    private val mAuth: FirebaseAuth = FirebaseAuth.getInstance()
    private val mDatabase: FirebaseDatabase = FirebaseDatabase.getInstance()
    private val mDatabaseReference: DatabaseReference? = mDatabase.getReference("users")
    private val c = Calendar.getInstance()
    private var picked_year = c.get(Calendar.YEAR)
    private var picked_month = c.get(Calendar.MONTH)
    private var picked_day = c.get(Calendar.DAY_OF_MONTH)
    private var gender:String = ""
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_register)

        gender_group.setOnCheckedChangeListener(RadioGroup.OnCheckedChangeListener { group, checkedId ->
            val radio: RadioButton = findViewById(checkedId)
//            Toast.makeText(applicationContext, radio.text, Toast.LENGTH_SHORT).show()
            gender = radio.text.toString()
        })

        btn_dob.setOnClickListener {
            val dpd = DatePickerDialog(this, DatePickerDialog.OnDateSetListener { view, year, month, dayOfMonth ->
                picked_day = dayOfMonth
                picked_month = month
                picked_year = year
            }, picked_year, picked_month, picked_day)

            dpd.show()
        }

        btn_register.setOnClickListener{
//            Toast.makeText(this, "Testing Register", Toast.LENGTH_SHORT).show()
            val name = txt_name.text.toString()
            val email = txt_email.text.toString()
            val address = txt_address.text.toString()
            val password = txt_password.text.toString()
            val cpassword = txt_confirm_password.text.toString()

            if(password.length < 6){
                Toast.makeText(applicationContext, "Password must be more than 6 characters", Toast.LENGTH_SHORT).show()
            }
            else if(password != cpassword){
                Toast.makeText(applicationContext, "Password and Confirm password must be the same", Toast.LENGTH_SHORT).show()
            }
            else{
                mAuth.createUserWithEmailAndPassword(email, password)
                        .addOnCompleteListener(this){task->
                            if(task.isSuccessful){
                                val userId = mAuth.currentUser!!.uid
                                val currentUser = mDatabaseReference!!.child(userId)
                                currentUser.child("name").setValue(name)
                                currentUser.child("email").setValue(email)
                                currentUser.child("address").setValue(address)
                                currentUser.child("gender").setValue(gender)
                                currentUser.child("day").setValue(picked_day)
                                currentUser.child("month").setValue(picked_month)
                                currentUser.child("year").setValue(picked_year)
                            }
                            else{
                                Toast.makeText(applicationContext, "Something's wrong with server. Please try again later.", Toast.LENGTH_SHORT).show()
                            }
                        }
            }
        }

        btn_login.setOnClickListener{
//            Toast.makeText(this, "Testing login", Toast.LENGTH_SHORT).show()
            finish()
        }
    }
}
