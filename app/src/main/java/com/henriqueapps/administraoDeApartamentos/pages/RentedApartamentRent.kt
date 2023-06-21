package com.henriqueapps.administraoDeApartamentos.pages

import android.annotation.SuppressLint
import android.content.ContentValues
import android.content.ContentValues.TAG
import android.content.Intent
import android.graphics.Color
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.Editable
import android.util.Log
import android.widget.Toast
import androidx.core.view.isVisible
import com.google.firebase.firestore.FirebaseFirestore
import com.henriqueapps.administraoDeApartamentos.R
import com.henriqueapps.administraoDeApartamentos.databinding.ActivityRentedApartamentRentBinding
import com.henriqueapps.administraoDeApartamentos.useful.decimalFormat
import com.henriqueapps.administraoDeApartamentos.useful.setOnEnterKeyListener

@SuppressLint("SetTextI18n")
class RentedApartamentRent : AppCompatActivity() {

    private lateinit var binding: ActivityRentedApartamentRentBinding

    private var price: String = ""
    private var newPrice = ""
    private var renter = ""
    private var observation = ""
    private var lateFee = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityRentedApartamentRentBinding.inflate(layoutInflater)
        setContentView(binding.root)

        supportActionBar!!.hide()
        window.statusBarColor = Color.parseColor("#00A86B")
        binding.buttonBack.setOnClickListener {
            finish()
        }

        binding.buttonSave.setOnClickListener {
            saveData()
        }

        binding.buttonRelease.setOnClickListener {
            val db = FirebaseFirestore.getInstance()
            val documentId = intent.getStringExtra("documentId")!!

            db.collection("Rent").document(documentId).delete()
                .addOnSuccessListener {
                    finish()
                }.addOnFailureListener {
                    Log.d(TAG, it.toString())
                }
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
        getInfo()

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

    private fun saveData(){
        renter = binding.editRenter.text.toString()
        observation = binding.editObservation.text.toString()

        val db = FirebaseFirestore.getInstance()
        val documentId = intent.getStringExtra("documentId")!!

        verifyinformation(db, documentId)

        db.collection("Rent").document(documentId).update(mapOf(
            "renter" to renter,
            "confirmedPrice" to newPrice,
            "observation" to observation,
            "lateFee" to lateFee
        ))
            .addOnSuccessListener {
                Toast.makeText(this, "Aluguel salvo com sucesso!", Toast.LENGTH_SHORT).show()
            }.addOnFailureListener {
                Log.d(TAG, it.toString())
            }
    }

    private fun verifyinformation(db: FirebaseFirestore, documentId: String) {
        db.collection("Rent").document(documentId).get().addOnSuccessListener { result ->

            if(renter.isEmpty()){
                renter = result.data!!["renter"].toString()
            }
            if (observation.isEmpty()){
                observation = result.data!!["observation"].toString()
            }
            if (newPrice.isEmpty()){
                newPrice = result.data!!["confirmedPrice"].toString()
            }
            if (lateFee.isEmpty()){
                lateFee = result.data!!["lateFee"].toString()
            }
        }.addOnFailureListener {
            Log.d(TAG, it.toString())
        }
    }

    private fun getInfo(){
        val db = FirebaseFirestore.getInstance()
        val documentId = intent.getStringExtra("documentId")!!

        db.collection("Rent").document(documentId).get().addOnSuccessListener { result ->
            binding.editRenter.setText(result.data!!["renter"].toString())
            binding.editObservation.setText(result.data!!["observation"].toString())
            binding.valueReal.setText(decimalFormat((intent.getStringExtra("value")!!).toDouble()))
            binding.discountedValue.setText(decimalFormat((result.data!!["confirmedPrice"].toString().toDouble())))
            binding.editLateFee.setText(result.data!!["lateFee"].toString())
            verificationDiscount(result.data!!["confirmedPrice"].toString())
        }
    }

    private fun verificationDiscount(discountedPrice: String){
        val priceDiscount = price.toDouble() - discountedPrice.toDouble()
        val discountPercetage = ((priceDiscount)/price.toDouble())*100
        binding.editDiscountPercentage.setText(discountPercetage.toString())
        binding.editDiscountValue.setText(decimalFormat(priceDiscount))
    }

}