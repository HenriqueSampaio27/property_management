package com.henriqueapps.administraoDeApartamentos.pages

import android.annotation.SuppressLint
import android.content.ContentValues.TAG
import android.content.DialogInterface
import android.content.Intent
import android.graphics.Color
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.core.view.isVisible
import com.denzcoskun.imageslider.models.SlideModel
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.henriqueapps.administraoDeApartamentos.R
import com.henriqueapps.administraoDeApartamentos.databinding.ActivityDetailBinding
import com.henriqueapps.administraoDeApartamentos.useful.decimalFormat
import java.text.SimpleDateFormat
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.time.temporal.ChronoUnit
import java.util.Calendar
import kotlin.math.abs
import kotlin.math.roundToInt

@SuppressLint("SetTextI18n")
class Detail : AppCompatActivity() {

    private lateinit var binding: ActivityDetailBinding
    private val slidesList : MutableList<SlideModel> = mutableListOf()
    private var stateInfoProperty : Boolean = false
    private lateinit var dialog: AlertDialog
    private lateinit var energy: String
    private lateinit var water : String
    private lateinit var price : String
    private lateinit var rentPrice: String
    private lateinit var amountToPay: String
    private lateinit var numberOfMonth: String
    private lateinit var lastPaymentDate: String
    private var state: Boolean = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityDetailBinding.inflate(layoutInflater)
        setContentView(binding.root)

        window.statusBarColor = Color.parseColor("#00A86B")
        title = ""
        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        getDocuments()
        visibleInfoRent()
        binding.txtInfo.setOnClickListener {
            stateInfoProperty = stateInfoProperty != true
            visibleInfoProperty()
        }
        binding.imageInfoProperty.setOnClickListener {
            stateInfoProperty = stateInfoProperty != true
            visibleInfoProperty()
        }

        binding.cardView.isVisible = true

        Handler(Looper.getMainLooper()).postDelayed({
            binding.cardView.isVisible = false
        }, 500)

        rentData()
    }


    @SuppressLint("SetTextI18n", "SimpleDateFormat")
    private fun rentedOrLeased(rent: String, date: String, price: String, observation: String, numberOfMonth: Int, lateFee: Double, amountToPay : String, lastPaymentDate: String) {

        binding.statusInfo.text = "Alugado"
        state = true
        rentPrice = price
        this.amountToPay = amountToPay
        this.numberOfMonth = numberOfMonth.toString()
        this.lastPaymentDate = lastPaymentDate

        if (rent.isNotEmpty()){
            binding.txtRenter.isVisible = true
            binding.renter.isVisible = true
            binding.renter.text = rent
        }

        val dataFormat = SimpleDateFormat("dd/MM/yyyy")

        val calendar = Calendar.getInstance()
        calendar.set("${date[6]}${date[7]}${date[8]}${date[9]}".toInt(), "${date[3]}${date[4]}".toInt(), "${date[0]}${date[1]}".toInt())
        calendar.add(Calendar.MONTH, numberOfMonth)
        val dueDate = dataFormat.format(calendar.time)

        binding.dueDate.text = dueDate
        binding.dueDate.isVisible = true
        binding.txtDate.isVisible = true

        val currentCalendar = Calendar.getInstance().time
        val currentData = dataFormat.format(currentCalendar)

        val dueDateTwo = LocalDate.from(DateTimeFormatter.ofPattern("dd/MM/yyyy").parse(dueDate))
        val dateCurrent = LocalDate.from(DateTimeFormatter.ofPattern("dd/MM/yyyy").parse(currentData))
        val days = abs(ChronoUnit.DAYS.between(currentCalendar.toInstant(), calendar.toInstant()))

        if (dateCurrent.isAfter(dueDateTwo)){
            binding.daysDelay.isVisible = true
            binding.txtDaysDelay.isVisible = true
            if (days.toInt() == 1){
                binding.daysDelay.text = "$days dia"
            }else{
                binding.daysDelay.text = "$days dias"
            }
        }

        if(amountToPay.toDouble() == price.toDouble()){
            if (dateCurrent.isAfter(dueDateTwo)){
                binding.txtRentPrice.isVisible = true
                binding.rentPrice.isVisible = true
                val lateFeeValue = rentPrice.toDouble() + (rentPrice.toDouble()*(days * lateFee/100))

                val dd = (lateFeeValue * 100.0).roundToInt()/100.0
                this.amountToPay = dd.toString()
                binding.rentPrice.text = "R$ ${decimalFormat(lateFeeValue)}"
            }else{
                binding.txtRentPrice.isVisible = true
                binding.rentPrice.isVisible = true
                binding.rentPrice.text = "R$ $rentPrice"
            }
        }else{
            if (dateCurrent.isAfter(dueDateTwo)){
                binding.txtRentPrice.isVisible = true
                binding.rentPrice.isVisible = true
                val lateFeeValue = amountToPay.toDouble() + (amountToPay.toDouble()*(days * lateFee/100))

                val dd = (lateFeeValue * 100.0).roundToInt()/100.0
                this.amountToPay = dd.toString()
                binding.rentPrice.text = "R$ ${decimalFormat(lateFeeValue)}"
            }else{
                binding.txtRentPrice.isVisible = true
                binding.rentPrice.isVisible = true
                binding.rentPrice.text = "R$ $amountToPay"
            }
        }


        if (observation.isNotEmpty()){
            binding.txtObservation.isVisible = true
            binding.observation.isVisible = true
            binding.observation.text = observation
        }

    }

    private fun rentData(){
        val db = FirebaseFirestore.getInstance()
        val documentId = intent.getStringExtra("documentId")!!

        db.collection("Rent").document(documentId).get()
            .addOnSuccessListener { result ->
                if (result.exists()){
                    val rent = result.data!!["renter"].toString()
                    val date = result.data!!["date"].toString()
                    val price = result.data!!["confirmedPrice"].toString()
                    val observation = result.data!!["observation"].toString()
                    val numberOfMonth = result.data!!["numberOfMonth"].toString().toInt()
                    val lateFee = result.data!!["lateFee"].toString().toDouble()
                    val amountToPay = result.data!!["amountToPay"].toString()
                    val lastPaymentDate = result.data!!["lastPaymentDate"].toString()

                    rentedOrLeased(rent, date, price, observation, numberOfMonth, lateFee, amountToPay, lastPaymentDate)
                }else{
                    binding.statusInfo.text = "Livre"
                    state = false
                }
            }.addOnFailureListener {
                Log.d(TAG, it.toString())
            }
    }

    override fun onStart() {
        super.onStart()
        getDocuments()
    }

    @SuppressLint("SetTextI18n")
    private fun getDocuments(){
        val db = FirebaseFirestore.getInstance()
        val documentId = intent.getStringExtra("documentId")

        db.collection("Properties").document(documentId!!)
            .get().addOnSuccessListener {   document ->
                val imageOne = document.data?.get("imageOne").toString()
                val imageTwo = document.data?.get("imageTwo").toString()
                val imageTree = document.data?.get("imageTree").toString()
                val type = document.data?.get("type").toString()
                val cep = document.data?.get("cep").toString()
                val logradouro = document.data?.get("logradouro").toString()
                val district = document.data?.get("district").toString()
                val state = document.data?.get("state").toString()
                val city = document.data?.get("city").toString()
                water = document.data?.get("water").toString()
                energy = document.data?.get("energy").toString()
                price = document.data?.get("price").toString()
                var number = document.data?.get("number").toString()

                if (number == "Sem número"){
                    number = ", Sem número"
                }else{
                    number = " Nº$number"
                }
                if (imageOne.isNotEmpty() && slidesList.size <1){
                    val slideImageOne = SlideModel(imageOne)
                    slidesList.add(slideImageOne)
                }
                if (imageTwo.isNotEmpty() && slidesList.size <2){
                    val slideImageTwo = SlideModel(imageTwo)
                    slidesList.add(slideImageTwo)
                }
                if (imageTree.isNotEmpty() && slidesList.size <3){
                    val slideImageTree = SlideModel(imageTree)
                    slidesList.add(slideImageTree)
                }
                if(slidesList.size != 0){
                    binding.viewSlider.setImageList(slidesList)
                }else if(slidesList.size < 1){
                    val slideImage = SlideModel(R.drawable.logo)
                    slidesList.add(slideImage)
                    binding.viewSlider.setImageList(slidesList)
                }

                binding.txtType.text = type
                binding.txtPrice.text = "R$ ${decimalFormat(price.toDouble())}"
                binding.localization.text = "$logradouro$number, $district, $city-$state, $cep"
                binding.txtEnergy.text = energy
                binding.water.text = water

            }
    }

    private fun visibleInfoProperty(){
        when{
            stateInfoProperty -> {
                binding.imageInfoProperty.setImageResource(R.drawable.ic_down)
                binding.viewLine2.isVisible = false
                binding.viewLocalization.isVisible = true
                binding.viewLine3.isVisible = true
                binding.titleLocalization.isVisible = true
                binding.localization.isVisible = true
                if(water.isNotEmpty()){
                    binding.viewInfoAdd.isVisible = true
                    binding.titleWater.isVisible = true
                    binding.water.isVisible = true
                }
                if (energy.isNotEmpty()){
                    binding.viewInfoAdd.isVisible = true
                    binding.titleEnergy.isVisible = true
                    binding.txtEnergy.isVisible = true
                }

            }
            !this.stateInfoProperty -> {
                binding.imageInfoProperty.setImageResource(R.drawable.ic_next)
                binding.viewLine2.isVisible = true
                binding.viewLocalization.isVisible = false
                binding.viewInfoAdd.isVisible = false
                binding.viewLine3.isVisible = false
                binding.titleLocalization.isVisible = false
                binding.localization.isVisible = false
                binding.titleEnergy.isVisible = false
                binding.txtEnergy.isVisible = false
                binding.titleWater.isVisible = false
                binding.water.isVisible = false
            }
        }
    }

    private fun visibleInfoRent(){
        when{
            binding.statusInfo.text.toString() == "Alugado" -> {
                binding.txtDate.isVisible = true
                binding.dueDate.isVisible = true
                binding.txtRenter.isVisible = true
                binding.renter.isVisible = true
            }
            binding.statusInfo.text.toString() == "Livre" -> {
                binding.txtDate.isVisible = false
                binding.dueDate.isVisible = false
                binding.txtRenter.isVisible = false
                binding.renter.isVisible = false
            }
        }
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.menu_detail, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        val id = item.itemId
        val documentId = intent.getStringExtra("documentId")!!

        if (id == R.id.menu_edit) {
            val documentId = intent.getStringExtra("documentId")
            val intent = Intent(this, EditApartament::class.java)
            intent.putExtra("documentId", documentId)
            startActivity(intent)
        }
        if (id == R.id.menu_payment){
            if (binding.statusInfo.text == "Alugado"){
                val intent = Intent(this, Payment::class.java)
                intent.putExtra("lastPaymentDate", lastPaymentDate)
                intent.putExtra("value", price)
                intent.putExtra("amountToPay", amountToPay)
                intent.putExtra("documentId", documentId)
                intent.putExtra("numberOfMonth", numberOfMonth)
                startActivity(intent)
            }else{
                Toast.makeText(this, "A propriedade não está alugada!", Toast.LENGTH_SHORT).show()
            }
        }
        if (id == R.id.menu_rent){
            if (state){
                val intent = Intent(this, RentedApartamentRent::class.java)
                intent.putExtra("value", price)
                intent.putExtra("numberOfMonth", numberOfMonth)
                intent.putExtra("documentId", documentId)
                startActivity(intent)
            }else{
                val intent = Intent(this, ApartmentRent::class.java)
                intent.putExtra("value", price)
                intent.putExtra("documentId", documentId)
                startActivity(intent)
            }

        }
        if (id == R.id.menu_delete){
            dialogDelete()
        }
        return super.onOptionsItemSelected(item)
    }

    private fun dialogDelete() {
        val delete = AlertDialog.Builder(this)
            .setTitle("Confirmação")
            .setMessage("Deseja excluir sua propriedade?")
            .setPositiveButton("Sim",
                DialogInterface.OnClickListener { dialogInterface, id ->
                    deleteProperty()
                })
            .setNegativeButton("Não",
                DialogInterface.OnClickListener { dialogInterface, id ->
                    dialog.dismiss()
                })

        dialog = delete.create()
        dialog.show()

    }

    private fun deleteProperty(){
        val db = FirebaseFirestore.getInstance()
        val user = FirebaseAuth.getInstance().currentUser
        val document = intent.getStringExtra("documentId")!!

        db.collection("Properties").document(document)
            .delete().addOnSuccessListener {
                Toast.makeText(this, "Propriedade excluida com sucesso!", Toast.LENGTH_SHORT).show()
                val intent = Intent(this, HomeActivity::class.java)
                intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                startActivity(intent)
                finish()
            }.addOnFailureListener {
                Log.d(TAG, it.toString())
            }
    }

    @Deprecated("Deprecated in Java")
    override fun onBackPressed() {
        super.onBackPressed()
        finish()
    }
}