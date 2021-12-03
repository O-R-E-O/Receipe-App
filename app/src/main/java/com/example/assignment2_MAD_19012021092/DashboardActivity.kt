package com.example.assignment2_MAD_19012021092

import android.app.AlarmManager
import android.app.PendingIntent
import android.app.TimePickerDialog
import android.content.Intent
import android.graphics.Color
import android.graphics.drawable.AnimationDrawable
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.view.WindowManager
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.ktx.Firebase
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import java.text.SimpleDateFormat
import java.util.*

class DashboardActivity : AppCompatActivity() {
    lateinit var heart: AnimationDrawable
    private val users = FirebaseFirestore.getInstance().collection("users")
    private val receipes = FirebaseFirestore.getInstance().collection("receipes")


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_dashboard)

        setStatusBarTransparent()
        val tvUserName = findViewById<TextView>(R.id.tv_user_name)
        val tvUserEmail = findViewById<TextView>(R.id.tv_user_email)
        val tvName = findViewById<TextView>(R.id.tv_name)
        val tvNumber = findViewById<TextView>(R.id.tv_number)
        val tvUserBio = findViewById<TextView>(R.id.tv_bio)
        val tvEmailId = findViewById<TextView>(R.id.tv_email_id)
        val btnLogout = findViewById<TextView>(R.id.btn_logout)
        val ivHeart = findViewById<ImageView>(R.id.iv_heart)
        val tvReceipes = findViewById<TextView>(R.id.tv_reciepes_added)


        val bottomNavBar = findViewById<BottomNavigationView>(R.id.bottom_nav_bar)
        ivHeart.setBackgroundResource(R.drawable.heart_animation)
        heart = ivHeart.background as AnimationDrawable
        heart.start()

        CoroutineScope(Dispatchers.Main).launch {
            val user = users.document(Firebase.auth.currentUser!!.uid).get().await().toObject(User::class.java)!!

            tvUserName.text = user.userName
            tvUserEmail.text = Firebase.auth.currentUser!!.email
            tvName.text = user.userName
            tvNumber.text = user.contactNumber
            tvUserBio.text = user.bio
            tvEmailId.text = Firebase.auth.currentUser!!.email

            val receipe = receipes.whereEqualTo("owner", Firebase.auth.currentUser!!.uid).get().await().toObjects(Receipes::class.java)

            if (receipe.size >= 3) {

                val r =
                    "${receipe[0].receipeName}\n${receipe[1].receipeName}\n${receipe[2].receipeName}\n....."

                tvReceipes.text = r

            } else {
                var rp = ""
                for (t in receipe) {
                    rp = rp + t.receipeName + "\n"
                }

                    tvReceipes.text = rp


            }
        }


        btnLogout.setOnClickListener {

            Firebase.auth.signOut()

            Intent(this, LoginActivity::class.java).apply {
                startActivity(this)
                finish()
            }
        }


        bottomNavBar.setOnItemSelectedListener{
            if(it.itemId == R.id.notes){
                Intent(this, ReceipesActivity::class.java).apply {
                    startActivity(this)
                    finish()
                }
                return@setOnItemSelectedListener true
            }
            else{
                return@setOnItemSelectedListener true
            }
        }
    }

    private fun setStatusBarTransparent() {
        if (Build.VERSION.SDK_INT in 19..20) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
                setWindowFlag(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS, true)
            }
        }
        if (Build.VERSION.SDK_INT >= 19) {
            window.decorView.systemUiVisibility =
                View.SYSTEM_UI_FLAG_LAYOUT_STABLE or View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
        }
        if (Build.VERSION.SDK_INT >= 21) {
            setWindowFlag(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS, false)
            window.statusBarColor = Color.TRANSPARENT
        }
    }
    private fun setWindowFlag(bits: Int, on: Boolean) {
        val winParameters = window.attributes
        if (on) {
            winParameters.flags = winParameters.flags or bits
        } else {
            winParameters.flags = winParameters.flags and bits.inv()
        }
        window.attributes = winParameters
    }
    override fun onBackPressed() {
        super.onBackPressed()
        finishAffinity()
    }
}

