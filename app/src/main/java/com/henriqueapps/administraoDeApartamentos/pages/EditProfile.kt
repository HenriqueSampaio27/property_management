package com.henriqueapps.administraoDeApartamentos.pages

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.henriqueapps.administraoDeApartamentos.databinding.ActivityEditProfileBinding

class EditProfile : AppCompatActivity() {

    private lateinit var binding: ActivityEditProfileBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityEditProfileBinding.inflate(layoutInflater)
        setContentView(binding.root)
    }
}