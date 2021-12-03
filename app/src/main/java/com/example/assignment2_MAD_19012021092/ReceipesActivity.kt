package com.example.assignment2_MAD_19012021092

import android.app.Dialog
import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.view.View
import android.view.WindowManager
import android.widget.ListView
import android.widget.TextView
import android.widget.TimePicker
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.android.material.switchmaterial.SwitchMaterial
import com.google.android.material.textfield.TextInputEditText
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.ktx.Firebase
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import java.util.*
import kotlin.collections.ArrayList

class ReceipesActivity : AppCompatActivity() {

    private val receipes = FirebaseFirestore.getInstance().collection("receipes")
    private val users = FirebaseFirestore.getInstance().collection("users")

    private lateinit var adapter: ReceipesAdapter


    override fun onStart() {

        val bottomNavBar = findViewById<BottomNavigationView>(R.id.bottom_nav_bar)
        bottomNavBar.menu.findItem(R.id.notes).isChecked = true
        getUpdatedList()
        super.onStart()
    }


    @RequiresApi(Build.VERSION_CODES.M)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_receipes)
        //createNotificationChannel()
        setStatusBarTransparent()

        CoroutineScope(Dispatchers.Main).launch {
            val abc = receipes.get().await().toObjects(Receipes::class.java) as ArrayList
            adapter = ReceipesAdapter(application, abc)
            val lvNotes: ListView = findViewById(R.id.lv_notes)
            lvNotes.adapter = adapter

            lvNotes.setOnItemClickListener { adapterView, view, i, l ->
                val link = abc[i].link

                Intent(Intent.ACTION_VIEW, Uri.parse(link)).apply {
                    startActivity(this)
                }
            }



        }

        //lvNotes.adapter = adapter



        val bottomNavBar = findViewById<BottomNavigationView>(R.id.bottom_nav_bar)
        bottomNavBar.setOnItemSelectedListener {
            if (it.itemId == R.id.dashboard) {
                Intent(this, DashboardActivity::class.java).apply {
                    startActivity(this)
                    finish()
                }
                return@setOnItemSelectedListener true
            } else {
                return@setOnItemSelectedListener true
            }
        }
        val fabAddNote = findViewById<FloatingActionButton>(R.id.fab_add_note)
        fabAddNote.setOnClickListener {
            val dialog = Dialog(this)
            dialog.setContentView(R.layout.add_edit_note_dialog)
            val noteTitle = dialog.findViewById<TextInputEditText>(R.id.et_note_title)
            val noteSubTitle = dialog.findViewById<TextInputEditText>(R.id.et_note_sub_title)
            val noteDescription = dialog.findViewById<TextInputEditText>(R.id.et_note_description)
            val reciepeLink = dialog.findViewById<TextInputEditText>(R.id.et_note_link)
            val btnOk = dialog.findViewById<TextView>(R.id.btn_ok)
            btnOk.setOnClickListener {
                if (noteTitle.text.toString().isEmpty() or noteSubTitle.text.toString()
                        .isEmpty() or noteDescription.text.toString().isEmpty() or reciepeLink.text!!.isBlank()
                ) {
                    Toast.makeText(
                        this,
                        "Please enter all fields\nAll fields are required",
                        Toast.LENGTH_SHORT
                    ).show()
                } else {
                    val id = receipes.document().id
                    val receipe = Receipes(
                        uid = id,
                        receipeName = noteTitle.text.toString(),
                        typeOfReceipe = noteSubTitle.text.toString(),
                        receipeDescription = noteDescription.text.toString(),
                        owner = Firebase.auth.currentUser!!.uid,
                        link = reciepeLink.text.toString()
                    )

                    CoroutineScope(Dispatchers.Main).launch {

                        receipes.document(id).set(receipe).await()

                        users.document(Firebase.auth.currentUser!!.uid).update("createdReceipes", FieldValue.arrayUnion(id))

                    }
                    getUpdatedList()
                    dialog.dismiss()
                }
            }
            dialog.show()

    }}

    private fun getUpdatedList() {
        CoroutineScope(Dispatchers.Main).launch {
            val abc = receipes.get().await().toObjects(Receipes::class.java) as ArrayList
            adapter = ReceipesAdapter(application, abc)
            val lvNotes: ListView = findViewById(R.id.lv_notes)
            lvNotes.adapter = adapter
        }
    }

//    private fun createNotificationChannel() {
//        val notificationManager =
//            getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
//        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
//            val description = "Channel for sending notes notification"
//            val importance = NotificationManager.IMPORTANCE_HIGH
//            val channel = NotificationChannel(channelId, channelName, importance)
//            channel.description = description
//            channel.vibrationPattern = longArrayOf(0, 1000, 500, 1000)
//            channel.enableVibration(true)
//            notificationManager.createNotificationChannel(channel)
//        }
//    }

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

    override fun onDestroy() {
        super.onDestroy()
        finishAffinity()
    }
}

const val channelId = "notesChannel"
const val channelName = "Notes Channel"

