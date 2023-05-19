package com.henriqueapps.administraoDeApartamentos.pages

import android.annotation.SuppressLint
import android.content.ContentValues.TAG
import android.content.DialogInterface
import android.content.Intent
import android.graphics.Color
import android.nfc.Tag
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.core.view.isVisible
import com.denzcoskun.imageslider.models.SlideModel
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.henriqueapps.administraoDeApartamentos.R
import com.henriqueapps.administraoDeApartamentos.databinding.ActivityDetailBinding

class Detail : AppCompatActivity() {

    private lateinit var binding: ActivityDetailBinding
    private val slidesList : MutableList<SlideModel> = mutableListOf()
    private var stateInfoProperty : Boolean = false
    private lateinit var dialog: AlertDialog
    private lateinit var energy: String
    private lateinit var water : String
    private lateinit var price : String

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
                    number = ", $number"
                }
                if (imageOne.isNotEmpty() && slidesList.size <3){
                    val image_one = SlideModel(imageOne)
                    slidesList.add(image_one)
                }
                if (imageTwo.isNotEmpty() && slidesList.size <3){
                    val image_two = SlideModel(imageTwo)
                    slidesList.add(image_two)
                }
                if (imageTree.isNotEmpty() && slidesList.size <3){
                    val image_tree = SlideModel(imageTree)
                    slidesList.add(image_tree)
                }
                binding.viewSlider.setImageList(slidesList)
                binding.txtType.text = type
                binding.txtPrice.text = "R$ ${String.format("%.2f", price.toDouble())}"
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
            !stateInfoProperty -> {
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
        if (id == R.id.menu_edit) {
            val documentId = intent.getStringExtra("documentId")
            val intent = Intent(this, EditApartament::class.java)
            intent.putExtra("documentId", documentId)
            startActivity(intent)
        }
        if (id == R.id.menu_payment){

        }
        if (id == R.id.menu_rent){
            val documentId = intent.getStringExtra("documentID")!!
            val intent = Intent(this, ApartmentRent::class.java)
            intent.putExtra("value", price)
            intent.putExtra("documentId", documentId)
            startActivity(intent)
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
                finish()
            }.addOnFailureListener {
                Log.d(TAG, it.toString())
            }
    }

    override fun onBackPressed() {
        super.onBackPressed()
        finish()
    }
}