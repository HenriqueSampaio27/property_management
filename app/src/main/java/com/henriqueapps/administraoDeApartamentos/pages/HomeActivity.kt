package com.henriqueapps.administraoDeApartamentos.pages

import android.annotation.SuppressLint
import android.content.ContentValues
import android.content.ContentValues.TAG
import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.core.net.toUri
import androidx.core.view.isVisible
import androidx.drawerlayout.widget.DrawerLayout
import androidx.navigation.findNavController
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.navigateUp
import androidx.navigation.ui.setupActionBarWithNavController
import androidx.navigation.ui.setupWithNavController
import com.bumptech.glide.Glide
import com.google.android.material.badge.BadgeDrawable
import com.google.android.material.badge.BadgeUtils
import com.google.android.material.navigation.NavigationView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.henriqueapps.administraoDeApartamentos.R
import com.henriqueapps.administraoDeApartamentos.databinding.ActivityHomeBinding
import com.henriqueapps.administraoDeApartamentos.model.Apartament
import com.henriqueapps.administraoDeApartamentos.useful.decimalFormat
import de.hdodenhof.circleimageview.CircleImageView
import java.text.SimpleDateFormat
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.time.temporal.ChronoUnit
import java.util.Calendar
import kotlin.math.abs
import kotlin.math.roundToInt

class HomeActivity : AppCompatActivity() {

    private lateinit var appBarConfiguration: AppBarConfiguration
    private lateinit var binding: ActivityHomeBinding
    private lateinit var navView : NavigationView
    private lateinit var headerView: View

    private var mNotifiItemCount: Int = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityHomeBinding.inflate(layoutInflater)
        setContentView(binding.root)

        window.statusBarColor = Color.parseColor("#00A86B")
        setSupportActionBar(binding.appBarHome.toolbar)

        val drawerLayout: DrawerLayout = binding.drawerLayout
        navView = binding.navView
        val navController = findNavController(R.id.nav_host_fragment_content_home)
        // Passing each menu ID as a set of Ids because each
        // menu should be considered as top level destinations.
        appBarConfiguration = AppBarConfiguration(
            setOf(
                R.id.nav_apartamentList, R.id.nav_registrationApartament, R.id.nav_calendar
            ), drawerLayout
        )
        setupActionBarWithNavController(navController, appBarConfiguration)
        navView.setupWithNavController(navController)

        val navigationView : NavigationView  = findViewById(R.id.nav_view)
        headerView = navigationView.getHeaderView(0)
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        // Inflate the menu; this adds items to the action bar if it is present.
        menuInflater.inflate(R.menu.home, menu)

        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        val id = item.itemId
        if (id == R.id.action_exit) {
            FirebaseAuth.getInstance().signOut()
            val intent = Intent(this@HomeActivity, Login::class.java)
            startActivity(intent)
            finish()
        }
        if (id == R.id.button_notifications){
            val intent = Intent(this, Notifications::class.java)
            startActivity(intent)
        }
        return super.onOptionsItemSelected(item)
    }

    override fun onSupportNavigateUp(): Boolean {
        val navController = findNavController(R.id.nav_host_fragment_content_home)
        return navController.navigateUp(appBarConfiguration) || super.onSupportNavigateUp()
    }

    private fun getNotifications(){
        val db = FirebaseFirestore.getInstance()
        val userUUID = FirebaseAuth.getInstance().currentUser!!.uid
        mNotifiItemCount = 0
        db.collection("Properties").whereEqualTo("owner", userUUID)
            .get().addOnSuccessListener {documents ->
                for (document in documents){
                    val documentId = document.id
                    db.collection("Rent").document(documentId).get()
                        .addOnSuccessListener { result ->
                            if (result.exists()) {
                                val dateRent = result.data!!["date"].toString()
                                val numberOfMonth = result.data!!["numberOfMonth"].toString().toInt()
                                notificationsAmount(dateRent, numberOfMonth)
                            }
                        }
                }
            }.addOnFailureListener {
                Log.d(ContentValues.TAG, it.toString())
            }
    }

    @SuppressLint("SimpleDateFormat")
    private fun notificationsAmount(date: String, numberOfMonth: Int) {

        val dataFormat = SimpleDateFormat("dd/MM/yyyy")

        val calendar = Calendar.getInstance()
        calendar.set(
            "${date[6]}${date[7]}${date[8]}${date[9]}".toInt(),
            "${date[3]}${date[4]}".toInt(),
            "${date[0]}${date[1]}".toInt()
        )
        calendar.add(Calendar.MONTH, numberOfMonth)
        val dueDate = dataFormat.format(calendar.time)

        val currentCalendar = Calendar.getInstance().time
        val currentData = dataFormat.format(currentCalendar)

        val dueDateTwo = LocalDate.from(DateTimeFormatter.ofPattern("dd/MM/yyyy").parse(dueDate))
        val dateCurrent =
            LocalDate.from(DateTimeFormatter.ofPattern("dd/MM/yyyy").parse(currentData))

        if (dateCurrent.isAfter(dueDateTwo)) {
            mNotifiItemCount += 1
        }
    }

        @SuppressLint("SuspiciousIndentation")
    override fun onStart() {
        super.onStart()
        getNotifications()
        val db = FirebaseFirestore.getInstance()
        val usuarioId = FirebaseAuth.getInstance().currentUser!!.uid
        val emailID = FirebaseAuth.getInstance().currentUser!!.email
        val navUsername : TextView = headerView.findViewById(R.id.nameUser)
        val navUserEmail : TextView = headerView.findViewById(R.id.emailUser)
        val navUserImage: CircleImageView = headerView.findViewById(R.id.imageAvatar)

        val documentReference = db.collection("Usuarios").document(usuarioId)

        documentReference.addSnapshotListener { value, _ ->
            if (value != null) {
                Log.d(TAG, value.getString("image").toString())
                if(value.getString("image")!!.isNotEmpty() || value.getString("image") != null){
                    Glide.with(applicationContext).load(value.getString("image")).into(navUserImage)
                }else if(value.getString("image").toString().isEmpty()){
                    db.collection("Aplication").document("aplicationLogo").get()
                        .addOnSuccessListener { document ->
                            val image = document.data!!["logo"].toString().toUri()
                                Glide.with(applicationContext).load(image)
                                    .into(navUserImage)
                        }
                }

                navUsername.text = value.getString("name")!!
                navUserEmail.text = emailID
            }
        }
    }

    @SuppressLint("UnsafeOptInUsageError")
    override fun onPrepareOptionsMenu(menu: Menu): Boolean {
        Handler(Looper.getMainLooper()).postDelayed({
            val badge = BadgeDrawable.create(this)
            badge.number = mNotifiItemCount
            badge.backgroundColor = Color.parseColor("#FFFFFF")
            badge.badgeTextColor = Color.parseColor("#00A86B")
            BadgeUtils.attachBadgeDrawable(badge, binding.appBarHome.toolbar,
                R.id.button_notifications
            )
        }, 2000)
        return super.onPrepareOptionsMenu(menu)
    }
}