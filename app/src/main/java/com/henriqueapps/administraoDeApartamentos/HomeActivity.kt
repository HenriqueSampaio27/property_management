package com.henriqueapps.administraoDeApartamentos

import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
//import android.widget.TextView
//import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.drawerlayout.widget.DrawerLayout
import androidx.navigation.findNavController
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.navigateUp
import androidx.navigation.ui.setupActionBarWithNavController
import androidx.navigation.ui.setupWithNavController
//import com.bumptech.glide.Glide
import com.google.android.material.navigation.NavigationView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.henriqueapps.administraoDeApartamentos.databinding.ActivityHomeBinding
import com.henriqueapps.administraoDeApartamentos.pages.Login
import com.henriqueapps.administraoDeApartamentos.pages.Notifications

class HomeActivity : AppCompatActivity() {

    private lateinit var appBarConfiguration: AppBarConfiguration
    private lateinit var binding: ActivityHomeBinding
    private lateinit var navView : NavigationView

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

    override fun onStart() {
        super.onStart()
        val db = FirebaseFirestore.getInstance()
        val usuarioId = FirebaseAuth.getInstance().currentUser!!.uid
        //val emailID = FirebaseAuth.getInstance().currentUser!!.email

        val documentReference = db.collection("Usuarios").document(usuarioId)
        documentReference.addSnapshotListener { value, error ->
            //if (value != null) {
                //Glide.with(applicationContext).load(value.getString("image")).into(findViewById(R.id.imageAvatar))
                //val name = findViewById<TextView>(R.id.nameUser)
                //name.setText(value.getString("name"))
                //val email = findViewById<TextView>(R.id.emailUser)
                //email.setText(emailID)
            //}
        }
    }
}