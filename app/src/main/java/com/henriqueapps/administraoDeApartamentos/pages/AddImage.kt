package com.henriqueapps.administraoDeApartamentos.pages

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.henriqueapps.administraoDeApartamentos.R
import com.henriqueapps.administraoDeApartamentos.databinding.ActivityAddImageBinding
import com.henriqueapps.administraoDeApartamentos.databinding.DialogWaterUpdateBinding

class AddImage : AppCompatActivity() {

    private lateinit var binding: ActivityAddImageBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityAddImageBinding.inflate(layoutInflater)
        setContentView(binding.root)


    }
}