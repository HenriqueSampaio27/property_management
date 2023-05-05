package com.henriqueapps.administraoDeApartamentos.pages

import android.content.Intent
import android.graphics.Color
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.henriqueapps.administraoDeApartamentos.R
import com.henriqueapps.administraoDeApartamentos.databinding.ActivityForgotPasswordBinding

class ForgotPassword : AppCompatActivity() {

    private lateinit var binding: ActivityForgotPasswordBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityForgotPasswordBinding.inflate(layoutInflater)
        setContentView(binding.root)

        supportActionBar!!.hide()
        window.statusBarColor = Color.parseColor("#00A86B")

        binding.buttonBack.setOnClickListener{
            finish()
        }

        binding.buttonRecovery.setOnClickListener{
            val email = binding.editEmail.text.toString()
            FirebaseAuth.getInstance().sendPasswordResetEmail(email)
            Toast.makeText(this, "E-mail para redefinição de senha enviado!", Toast.LENGTH_SHORT).show()
        }
    }
}