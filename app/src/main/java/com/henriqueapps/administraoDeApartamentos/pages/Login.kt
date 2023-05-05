package com.henriqueapps.administraoDeApartamentos.pages

import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.isVisible
import com.google.android.material.snackbar.Snackbar
import com.google.firebase.FirebaseNetworkException
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException
import com.google.firebase.auth.FirebaseAuthWeakPasswordException
import com.henriqueapps.administraoDeApartamentos.HomeActivity
import com.henriqueapps.administraoDeApartamentos.databinding.ActivityLoginBinding

class Login : AppCompatActivity() {

    private lateinit var binding: ActivityLoginBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityLoginBinding.inflate(layoutInflater)
        setContentView(binding.root)

        supportActionBar!!.hide()
        window.statusBarColor = Color.parseColor("#00A86B")

        binding.buttonEntrar.setOnClickListener{    view ->
            val email = binding.editEmail.text.toString()
            val password = binding.editPassword.text.toString()

            when{
                email.isEmpty() && password.isEmpty() -> {
                    val snackbar =
                        Snackbar.make(view, "Preencha todos os campos!", Snackbar.LENGTH_SHORT)
                    snackbar.show()
                }
                email.isEmpty() -> {
                    binding.editEmail.error = "Preencha o E-mail!"
                }
                password.isEmpty() -> {
                    binding.editPassword.error = "Preencha a Senha!"
                }else -> {
                    logar(email, password, view)
                }
            }

        }

        binding.txtForgotPassword.setOnClickListener{
            val intent = Intent(this, ForgotPassword::class.java)
            startActivity(intent)
        }

        binding.buttonCadastrar.setOnClickListener {
            val intent = Intent(this, Registration::class.java)
            startActivity(intent)
        }

    }

    override fun onBackPressed() {
        super.onBackPressed()
        finish()
    }

    private fun logar(email: String, password: String, view: View){
        binding.progressCircularLogin.isVisible = true
        FirebaseAuth.getInstance().signInWithEmailAndPassword(email, password).addOnSuccessListener {
            val snackbar = Snackbar.make(view, "Conta logada com sucesso!", Snackbar.LENGTH_SHORT)
            snackbar.show()
            val intent = Intent(this, HomeActivity::class.java)
            startActivity(intent)
            finish()
        }.addOnFailureListener {
            binding.progressCircularLogin.isVisible = false
            val error = it

            when {
               error is FirebaseAuthWeakPasswordException -> {
                    binding.editPassword.error = "Senha incorreta!"
               }
                error is FirebaseAuthInvalidCredentialsException -> {
                    binding.editEmail.error = "E-mail inválido!"
                }
                error is FirebaseNetworkException -> {
                    Toast.makeText(this, "Sem conexão com a internet!", Toast.LENGTH_SHORT).show()
                }
                else -> {
                    Toast.makeText(this, "Erro ao logar usuário!", Toast.LENGTH_SHORT).show()
                }
            }

        }
    }

    override fun onStart() {
        super.onStart()

        val currentUser = FirebaseAuth.getInstance().currentUser
        if(currentUser != null){
            val intent = Intent(this, HomeActivity::class.java)
            startActivity(intent)
            finish()
        }
    }
}