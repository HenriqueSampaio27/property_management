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
import com.henriqueapps.administraoDeApartamentos.R
import com.henriqueapps.administraoDeApartamentos.databinding.ActivityPaymentBinding
import com.henriqueapps.administraoDeApartamentos.useful.decimalFormat
import java.text.SimpleDateFormat
import java.util.Calendar

class Payment : AppCompatActivity() {

    private lateinit var binding: ActivityPaymentBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityPaymentBinding.inflate(layoutInflater)
        setContentView(binding.root)

        supportActionBar!!.hide()
        window.statusBarColor = Color.parseColor("#00A86B")

        binding.buttonBack.setOnClickListener{
            finish()
        }

        val amountToPay = intent.getStringExtra("amountToPay")!!
        binding.buttonConfirm.setOnClickListener {
            paymentConfirmed(amountToPay)
        }

        binding.editValue.setText(amountToPay)
        binding.valueReal.text = decimalFormat(amountToPay.toDouble())
        getCurrentDate()
    }

    @SuppressLint("SimpleDateFormat")
    private fun getCurrentDate(){
        val calendar = Calendar.getInstance()
        val dataFormat = SimpleDateFormat("dd/MM/yyyy")
        binding.textPayDay.text = dataFormat.format(calendar.time)

        if (intent.getStringExtra("lastPaymentDate")!!.isNotEmpty()){
            binding.lastDate.isVisible = true
            binding.textLastDate.isVisible = true
            binding.lastDate.text = intent.getStringExtra("lastPaymentDate")!!
        }

    }

    private fun paymentConfirmed(amountToPay: String){
        val db = FirebaseFirestore.getInstance()
        val documentId = intent.getStringExtra("documentId")!!

        val date = binding.textPayDay.text.toString()
        val valuePay = binding.editValue.getNumericValue().toString()
        val value = amountToPay.toDouble() - valuePay.toDouble()
        val numberOfMonth = intent.getStringExtra("numberOfMonth")!!
        val price = intent.getStringExtra("value")!!

        if (date.isEmpty()){
            Toast.makeText(this, "Preencha a data do pagamento!", Toast.LENGTH_SHORT).show()
        }
        if (valuePay.isEmpty()){
            binding.editValue.error = "Insira o valor ser pago!"
        }
        if (valuePay > amountToPay){
            binding.editValue.error = "Insira um valor menor ou igual ao valor em aberto!"
        }else if ((date.isNotEmpty() && valuePay.isNotEmpty()) && (valuePay == amountToPay)){
            db.collection("Rent").document(documentId).update(
                mapOf(
                    "lastPaymentDate" to date,
                    "amountToPay" to price,
                    "numberOfMonth" to (numberOfMonth.toInt() + 1)
                )
            )
                .addOnSuccessListener {
                    Toast.makeText(this, "Pagamento concluido com sucesso!", Toast.LENGTH_SHORT).show()
                    finish()
                }.addOnFailureListener {
                    Log.d(TAG, it.toString())
                }
        }else if(date.isNotEmpty() && valuePay.isNotEmpty()){
            db.collection("Rent").document(documentId).update(
                mapOf(
                    "lastPaymentDate" to date,
                    "amountToPay" to value.toString()
                )
            )
                .addOnSuccessListener {
                    Toast.makeText(this, "Pagamento atualizado com sucesso!", Toast.LENGTH_SHORT).show()
                    finish()
                }.addOnFailureListener {
                    Log.d(TAG, it.toString())
                }
        }


    }
}