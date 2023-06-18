package com.henriqueapps.administraoDeApartamentos.pages

import android.annotation.SuppressLint
import android.content.ContentValues.TAG
import android.graphics.Color
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.core.view.isVisible
import com.google.firebase.firestore.FirebaseFirestore
import com.henriqueapps.administraoDeApartamentos.databinding.ActivityApartmentRentBinding
import com.henriqueapps.administraoDeApartamentos.date.DatePickerFragment
import com.henriqueapps.administraoDeApartamentos.useful.decimalFormat
import com.henriqueapps.administraoDeApartamentos.useful.setOnEnterKeyListener

class ApartmentRent : AppCompatActivity() {

    private lateinit var binding: ActivityApartmentRentBinding

    private var price: String = ""
    private var date: String = ""
    private var newPrice = ""
    private var lateFee = ""

    @SuppressLint("SetTextI18n")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityApartmentRentBinding.inflate(layoutInflater)
        setContentView(binding.root)

        supportActionBar!!.hide()
        window.statusBarColor = Color.parseColor("#00A86B")

        binding.imageDate.setOnClickListener {
            DatePickerFragment { result ->
                binding.textEntryDate.text = result
                date = result
            }
                .show(supportFragmentManager, "datePicker")
        }

        binding.buttonBack.setOnClickListener {
            finish()
        }

        binding.buttonSave.setOnClickListener {
            saveData()
        }
        binding.imageSwich.setOnCheckedChangeListener { buttonView, isChecked ->
            if (isChecked) {
                binding.textInputLayoutDiscountValue.isVisible = true
                binding.textInputLayoutDiscountPercentage.isVisible = true
                binding.textRealValue.isVisible = true
                binding.textDiscountValue.isVisible = true
                binding.discountedValue.isVisible = true
                binding.valueReal.isVisible = true
            } else {
                binding.textInputLayoutDiscountValue.isVisible = false
                binding.textInputLayoutDiscountPercentage.isVisible = false
                binding.textRealValue.isVisible = false
                binding.textDiscountValue.isVisible = false
                binding.discountedValue.isVisible = false
                binding.valueReal.isVisible = false
            }
        }

        price = intent.getStringExtra("value")!!
        binding.valueReal.text = "R$ ${decimalFormat(price.toDouble())}"
        discountedValue(null)

        binding.editDiscountValue.setOnEnterKeyListener {
            discountedValue(true)
        }

        binding.editDiscountPercentage.setOnEnterKeyListener {
            discountedValue(false)
        }
    }

    @SuppressLint("SetTextI18n")
    private fun discountedValue(boolean: Boolean?) {
        val editDiscoutValue = binding.editDiscountValue.getNumericValue()
        val editDiscountPercentage = binding.editDiscountPercentage.text.toString()

        if (boolean == true) {
            if (editDiscoutValue == 0.0){
                binding.discountedValue.text = "R$ ${decimalFormat(price.toDouble())}"
            }else if (editDiscoutValue >= price.toDouble()) {
                binding.editDiscountValue.error =
                    "Insira um valor menor que o preço da propriedade!"
            } else {
                val percetageValue = ((editDiscoutValue * 100) / (price.toDouble()))
                val percetage = String.format("%.2f", percetageValue)
                binding.editDiscountPercentage.setText(percetage)
                binding.discountedValue.text = "R$ ${decimalFormat((price.toDouble() - editDiscoutValue))}"
                newPrice = (price.toDouble() - editDiscoutValue).toString()

            }
        }else if(boolean == false){
            val percentage = editDiscountPercentage.toDouble()
            if (editDiscountPercentage.isEmpty()){
                binding.discountedValue.text = "R$ ${decimalFormat(price.toDouble())}"
            }else if (percentage in 0.0..100.0){
                val value = (percentage*price.toDouble())/100
                binding.editDiscountValue.setText(decimalFormat(value))
                binding.discountedValue.text = "R$ ${decimalFormat(price.toDouble() - value)}"
                newPrice = (price.toDouble() - value).toString()
            }else{
                binding.editDiscountPercentage.error =
                    "Insira um valor entre 1 e 100!"
            }

        } else if (editDiscoutValue == 0.0 && editDiscountPercentage.isEmpty()){
            binding.discountedValue.text = "R$ ${decimalFormat(price.toDouble())}"
            newPrice = price
        }

    }

    private fun getLateFee(): Boolean {
        lateFee = binding.editLateFee.text.toString()

        var confirmed = true
        if(lateFee.isEmpty()) {
            lateFee = ""
        }else if (lateFee.toDouble() > 10.0){
            binding.editLateFee.error = "Insira um valor menor que 10%!"
            confirmed = false
        }else{
            val value = lateFee.toDouble()/30
            lateFee = value.toString()
        }
        return confirmed
    }

    private fun saveData(){
        val renter = binding.editRenter.text.toString()
        val observation = binding.editObservation.text.toString()

        val db = FirebaseFirestore.getInstance()
        val documentId = intent.getStringExtra("documentId")!!


        if (getLateFee()) {
            if (date.isEmpty()) {
                Toast.makeText(this, "Insira a data de entrada!", Toast.LENGTH_SHORT).show()
            } else {
                val rent: MutableMap<String, String> = HashMap()
                rent["renter"] = renter
                rent["date"] = date
                rent["confirmedPrice"] = newPrice
                rent["amountToPay"] = newPrice
                rent["observation"] = observation
                rent["numberOfMonth"] = "0"
                rent["lateFee"] = lateFee
                rent["lastPaymentDate"] = ""


                db.collection("Rent").document(documentId).set(rent)
                    .addOnSuccessListener {
                        Toast.makeText(this, "Aluguel salvo com sucesso!", Toast.LENGTH_SHORT)
                            .show()
                        finish()
                    }.addOnFailureListener {
                        Log.d(TAG, it.toString())
                    }
            }
        }
    }
}