/*
Created by CP18-1
16:05 28/02/2019
 */

package edu.bluejack181.thedoctorcheckups

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.Build
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.support.v4.app.NotificationCompat
import android.support.v4.app.NotificationManagerCompat
import android.util.Log
import android.widget.Toast
import com.facebook.*
import com.facebook.appevents.AppEventsLogger;
import com.facebook.login.LoginResult
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.api.ApiException
import com.google.android.gms.tasks.Task
import com.google.firebase.auth.FacebookAuthProvider
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.GoogleAuthProvider
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import kotlinx.android.synthetic.main.activity_main.*
import kotlin.math.PI

class MainActivity : AppCompatActivity() {

    val RC_SIGN_IN: Int = 1
    lateinit var mGoogleSignInClient: GoogleSignInClient
    lateinit var mGoogleSignInOptions: GoogleSignInOptions

    private fun configureGoogleSignIn() {
        mGoogleSignInOptions = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestIdToken(getString(R.string.default_web_client_id))
            .requestEmail()
            .build()
        mGoogleSignInClient = GoogleSignIn.getClient(this, mGoogleSignInOptions)
    }
    private fun signIn() {
        val signInIntent: Intent = mGoogleSignInClient.signInIntent
        startActivityForResult(signInIntent, RC_SIGN_IN)
    }

    private val mAuth: FirebaseAuth = FirebaseAuth.getInstance()
    private val mDatabase: FirebaseDatabase = FirebaseDatabase.getInstance()
    private val mDatabaseReference: DatabaseReference? = mDatabase.getReference("users")
    private lateinit var callbackManager: CallbackManager

    private fun firebaseAuthWithGoogle(acct: GoogleSignInAccount) {
        val credential = GoogleAuthProvider.getCredential(acct.idToken, null)
        Toast.makeText(applicationContext, "Test", Toast.LENGTH_SHORT).show()
        mAuth.signInWithCredential(credential).addOnCompleteListener {
            if (it.isSuccessful) {

                startActivity(Intent(applicationContext, DiagnoseIllnessActivity::class.java))
                finish()
            } else {
                Toast.makeText(this, "2 failed:(", Toast.LENGTH_LONG).show()
            }
        }
    }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        createNotificationChannel()

        configureGoogleSignIn()
        google_button.setOnClickListener {
            Toast.makeText(this, "Test 1", Toast.LENGTH_LONG).show()
            signIn()
        }

        FacebookSdk.sdkInitialize(getApplicationContext());
        AppEventsLogger.activateApp(this);

        callbackManager = CallbackManager.Factory.create();

        login_button.setReadPermissions("email", "public_profile")
        login_button.registerCallback(callbackManager, object : FacebookCallback<LoginResult> {
            override fun onSuccess(loginResult: LoginResult) {
                handleFacebookAccessToken(loginResult.accessToken)
            }

            override fun onCancel() {
                // ...
            }

            override fun onError(error: FacebookException) {
                // ...
            }
        })

        btn_login.setOnClickListener{
//            Toast.makeText(this, "Testing Login", Toast.LENGTH_SHORT).show()
            val email = txt_email.text.toString()
            val password = txt_password.text.toString()
            mAuth.signInWithEmailAndPassword(email, password)
                    .addOnCompleteListener(this){task->
                        if(task.isSuccessful){
                            Toast.makeText(applicationContext, "Success Login!", Toast.LENGTH_SHORT).show()
                            var diagnoseIntent = Intent(this, DiagnoseIllnessActivity::class.java)
                            var diagnosePendingIntent = PendingIntent.getActivity(this, 0, diagnoseIntent, 0)
                            var builder = NotificationCompat.Builder(this, "default")
                                .setSmallIcon(R.drawable.ic_menu)
                                .setContentTitle("The Doctor Checkup")
                                .setContentText("You have logged on to application")
                                .setPriority(NotificationCompat.PRIORITY_DEFAULT)
                                .addAction(R.drawable.ic_menu, "Go To Diagnosing Illness", diagnosePendingIntent)

                            with(NotificationManagerCompat.from(this)){
                                notify(1, builder.build())
                            }
                            startActivity(Intent(applicationContext, DiagnoseIllnessActivity::class.java))
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

    private fun createNotificationChannel(){
        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.O){
            val importance = NotificationManager.IMPORTANCE_DEFAULT
            val channel = NotificationChannel("default", "testing", importance).apply {
                description = "testing"
            }
            val notificationManager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            notificationManager.createNotificationChannel(channel)
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        callbackManager.onActivityResult(requestCode, resultCode, data)

        if (requestCode == RC_SIGN_IN) {
            val task: Task<GoogleSignInAccount> = GoogleSignIn.getSignedInAccountFromIntent(data)
//            Toast.makeText(this, "Test 2", Toast.LENGTH_LONG).show()

            try {
                val account = task.getResult(ApiException::class.java)
                firebaseAuthWithGoogle(account)
            } catch (e: ApiException) {
//                Log.w("TAG", "signInResult:failed code=" + e.getStatusCode());
                Toast.makeText(this, "1 failed:(", Toast.LENGTH_LONG).show()
            }
        }
    }

    private fun handleFacebookAccessToken(token: AccessToken) {
//        Log.d(TAG, "handleFacebookAccessToken:$token")

        val credential = FacebookAuthProvider.getCredential(token.token)
        mAuth.signInWithCredential(credential)
            .addOnCompleteListener(this) { task ->
                if (task.isSuccessful) {
                    val user = mAuth.currentUser
                    startActivity(Intent(applicationContext, DiagnoseIllnessActivity::class.java))
                } else {
                    Toast.makeText(baseContext, "Authentication failed.",
                        Toast.LENGTH_SHORT).show()
                }

                // ...
            }
    }

}
