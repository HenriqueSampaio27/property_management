package com.henriqueapps.administraoDeApartamentos.ui.registrationApartament

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.snackbar.Snackbar
import com.henriqueapps.administraoDeApartamentos.api.ApiCep
import com.henriqueapps.administraoDeApartamentos.databinding.ActivityRegistrationApartamentBinding
import com.henriqueapps.administraoDeApartamentos.model.Address
import com.henriqueapps.administraoDeApartamentos.model.ApartamentRegistration
import retrofit2.*
import retrofit2.converter.gson.GsonConverterFactory

class RegistrationApartament : AppCompatActivity() {

    private lateinit var binding : ActivityRegistrationApartamentBinding
    lateinit var apartamentRegistration : ApartamentRegistration

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        title = ""

        binding = ActivityRegistrationApartamentBinding.inflate(layoutInflater)
        setContentView(binding.root)

        apartamentRegistration = ApartamentRegistration()

        binding.buttonContinued.setOnClickListener {
            continued(it)
        }

        binding.textSearch.setOnClickListener {
            getAddress()
        }

    }

    override fun onStart() {
        super.onStart()
    }

    private fun continued(view: View){
        val isDone = binding.editCep.unMasked
        val cep = binding.editCep.masked
        val logradouro = binding.editLogradouro.text.toString()
        val number = binding.editNumber.text.toString()
        val district = binding.editDistrict.text.toString()
        val city = binding.editCity.text.toString()
        val state = binding.editState.text.toString()
        val energy = binding.editEnergy.text.toString()
        val water = binding.editWater.text.toString()
        val price = binding.editPrice.getNumericValue()

        when{
            cep.isEmpty() && logradouro.isEmpty() && price.toString() == "0.0" && district.isEmpty() -> {
                val snackbar = Snackbar.make(view, "Preencha todos os campos!", Snackbar.LENGTH_SHORT)
                snackbar.show()
            }
            cep.isEmpty() -> {
                binding.editCep.error = "Insira o Cep!"
            }
            isDone.length < 8 ->{
                binding.editCep.error = "Insira um Cep válido!"
            }
            logradouro.isEmpty() -> {
                binding.editLogradouro.error = "Insira o Logradouro!"
            }
            district.isEmpty() -> {
                binding.editDistrict.error = "Insira o Bairro!"

            }
            city.isEmpty() -> {
                binding.editCity.error = "Insira a Cidade!"

            }
            state.isEmpty() -> {
                binding.editState.error = "Insira o Estado!"

            }
            price.toString() == "0.0" -> {
                binding.editPrice.error = "Insira o Valor!"
            }
            else -> {
                apartamentRegistration.getDate(cep, logradouro, district, city, state, number, price, energy, water)
                val intent = Intent(this@RegistrationApartament, RegistrationApartamentFinish::class.java)
                intent.putExtra("cep", cep)
                intent.putExtra("logradouro", logradouro)
                intent.putExtra("district", district)
                intent.putExtra("city", city)
                intent.putExtra("state", state)
                intent.putExtra("number", number)
                intent.putExtra("price", price.toString())
                intent.putExtra("energy", energy)
                intent.putExtra("water", water)
                startActivity(intent)
            }

        }
    }

    private fun getAddress(){
        val retrofit = Retrofit.Builder()
            .addConverterFactory(GsonConverterFactory.create())
            .baseUrl("https://viacep.com.br/ws/")
            .build()
            .create(ApiCep::class.java)

        val cep = binding.editCep.unMasked

        if(cep.length == 8){
            retrofit.setAddresss(cep).enqueue(object : Callback<Address>{
                override fun onResponse(call: Call<Address>, response: Response<Address>) {
                    if(response.code() == 200){
                        val logradouro = response.body()?.logradouro.toString()
                        val bairro = response.body()?.bairro.toString()
                        val localidade = response.body()?.localidade.toString()
                        val uf = response.body()?.uf.toString()
                        setAddressText(logradouro, bairro, localidade, uf)
                    }else{
                        binding.editCep.error = "Cep inválido!"
                    }
                }

                override fun onFailure(call: Call<Address>, t: Throwable) {
                    Toast.makeText(this@RegistrationApartament, "Cep inválido!", Toast.LENGTH_SHORT).show()
                }

            })
        }
    }

    private fun setAddressText(logradouro: String, bairro: String, localidade: String, uf: String){
        binding.editLogradouro.setText(logradouro)
        binding.editDistrict.setText(bairro)
        binding.editCity.setText(localidade)
        binding.editState.setText(uf)
    }
}