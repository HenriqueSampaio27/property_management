package com.henriqueapps.administraoDeApartamentos.pages

import android.annotation.SuppressLint
import android.content.ContentValues.TAG
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import androidx.core.net.toUri
import androidx.core.view.isVisible
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.henriqueapps.administraoDeApartamentos.adapter.AdapterNotifications
import com.henriqueapps.administraoDeApartamentos.databinding.ActivityNotificationsBinding
import com.henriqueapps.administraoDeApartamentos.model.NotificationsModel
import com.henriqueapps.administraoDeApartamentos.useful.decimalFormat
import java.text.SimpleDateFormat
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.time.temporal.ChronoUnit
import java.util.Calendar
import kotlin.math.abs

class Notifications : AppCompatActivity() {

    private lateinit var binding: ActivityNotificationsBinding

    private lateinit var adapterNotifications: AdapterNotifications
    private var listNotifications: MutableList<NotificationsModel> = mutableListOf()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityNotificationsBinding.inflate(layoutInflater)
        setContentView(binding.root)

        supportActionBar!!.hide()

        val recyclerView = binding.recyclerViewNotifications
        recyclerView.layoutManager = LinearLayoutManager(this.applicationContext)
        adapterNotifications = AdapterNotifications(this.applicationContext, listNotifications)
        recyclerView.adapter = adapterNotifications
        recyclerView.hasFixedSize()

        binding.buttonBack.setOnClickListener {
            finish()
        }

    }

    override fun onStart() {
        super.onStart()
        getNotifications()
    }

    private fun getNotifications(){
        val db = FirebaseFirestore.getInstance()
        val userUUID = FirebaseAuth.getInstance().currentUser!!.uid
        db.collection("Properties").whereEqualTo("owner", userUUID)
            .get().addOnSuccessListener {documents ->
                for (document in documents){
                    val documentId = document.id
                    var image = document.data["imageOne"].toString()
                        if(document.data["imageOne"].toString().isEmpty()){
                            db.collection("Aplication").document("aplicationLogo").get()
                                .addOnSuccessListener { document ->
                                    image = document.data!!["logo"].toString()
                                }
                    }
                    val logradouro = document.data["logradouro"].toString()
                    val number = document.data["number"].toString()
                    db.collection("Rent").document(documentId).get()
                        .addOnSuccessListener { result ->
                            if (result.exists()) {
                                val price = result.data!!["amountToPay"].toString()
                                val date = result.data!!["date"].toString()
                                val numberOfMonth = result.data!!["numberOfMonth"].toString()
                                val lateFee = result.data!!["lateFee"].toString()

                                insertNotification(
                                    image,
                                    logradouro,
                                    number,
                                    price,
                                    date,
                                    numberOfMonth,
                                    lateFee,
                                    documentId
                                )
                            }
                        }
                }
                if(listNotifications.toString().isEmpty()){
                    binding.textNotifi.isVisible = true
                }
            }.addOnFailureListener {
                Log.d(TAG, it.toString())
            }
    }

    @SuppressLint("SimpleDateFormat", "NotifyDataSetChanged")
    private fun insertNotification(image: String, logradouro: String, numberOrigin: String, price: String, date: String, numberOfMonth: String, lateFee: String, documentID: String){
        val dataFormat = SimpleDateFormat("dd/MM/yyyy")

        val calendar = Calendar.getInstance()
        calendar.set("${date[6]}${date[7]}${date[8]}${date[9]}".toInt(), "${date[3]}${date[4]}".toInt(), "${date[0]}${date[1]}".toInt())
        calendar.add(Calendar.MONTH, numberOfMonth.toInt())
        val dueDate = dataFormat.format(calendar.time)

        val currentCalendar = Calendar.getInstance().time
        val currentData = dataFormat.format(currentCalendar)

        val dueDateTwo = LocalDate.from(DateTimeFormatter.ofPattern("dd/MM/yyyy").parse(dueDate))
        val dateCurrent = LocalDate.from(DateTimeFormatter.ofPattern("dd/MM/yyyy").parse(currentData))

        val days = abs(ChronoUnit.DAYS.between(currentCalendar.toInstant(), calendar.toInstant()))
        val number: String = if (numberOrigin == "Sem número"){
            ", Sem número"
        }else{
            " Nº$numberOrigin"
        }

        if(dateCurrent.isAfter(dueDateTwo)){
            val lateFeeValue: String = if(lateFee.isNotEmpty()){
                (price.toDouble() + (price.toDouble()*(days * lateFee.toDouble()/100))).toString()
            }else{
                price
            }
            listNotifications.add(NotificationsModel(image, "Aluguel atrasado", logradouro, number, lateFeeValue, dueDate, documentID))
            adapterNotifications.notifyDataSetChanged()

        }

    }

    override fun onPause() {
        super.onPause()
        listNotifications = mutableListOf()
    }

}