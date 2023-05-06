package com.henriqueapps.administraoDeApartamentos.pages

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.henriqueapps.administraoDeApartamentos.databinding.ActivityNotificationsBinding

class Notifications : AppCompatActivity() {

    private lateinit var binding: ActivityNotificationsBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityNotificationsBinding.inflate(layoutInflater)
        setContentView(binding.root)

        supportActionBar!!.hide()

        binding.buttonBack.setOnClickListener {
            finish()
        }

    }
}