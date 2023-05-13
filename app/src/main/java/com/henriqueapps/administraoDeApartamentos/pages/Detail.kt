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

    @SuppressLint("SetTextI18n")
    private fun getDocuments(){
        val db = FirebaseFirestore.getInstance()
        val documentId = intent.getStringExtra("documentId")

        db.collection("Properties").document(documentId!!)
            .get().addOnSuccessListener {   document ->
                val imageOne = document.data?.get("imageOne")
                val imageTwo = document.data?.get("imageTwo")
                val imageTree = document.data?.get("imageTree")
                val type = document.data?.get("type")
                val cep = document.data?.get("cep")
                val logradouro = document.data?.get("logradouro")
                val district = document.data?.get("district")
                val state = document.data?.get("state")
                val city = document.data?.get("city")
                val water = document.data?.get("water")
                val energy = document.data?.get("energy")
                val price = document.data?.get("price")
                val number = document.data?.get("number")

                if (imageOne.toString().isNotEmpty()){
                    val image_one = SlideModel(imageOne.toString())
                    slidesList.add(image_one)
                }
                if (imageTwo.toString().isNotEmpty()){
                    val image_two = SlideModel(imageTwo.toString())
                    slidesList.add(image_two)
                }
                if (imageTree.toString().isNotEmpty()){
                    val image_tree = SlideModel(imageTree.toString())
                    slidesList.add(image_tree)
                }

                binding.viewSlider.setImageList(slidesList)
                binding.txtType.text = type.toString()
                binding.txtPrice.text = price.toString()
                binding.localization.text = "${logradouro.toString()} N°${number.toString()}, ${district.toString()}, " +
                        "${city.toString()}-${state.toString()}, ${cep.toString()}"
                binding.txtEnergy.text = energy.toString()
                binding.water.text = water.toString()

            }
    }

//    private fun getImage(){
//        val image1 = SlideModel(R.drawable.logo)
//        val image2 = SlideModel(R.drawable.avatar)
//        val image3 = SlideModel(R.drawable.ic_apartment)
//        slidesList.add(image1)
//        slidesList.add(image2)
//        slidesList.add(image3)
//
//        val imageSlider = binding.viewSlider
//        imageSlider.setImageList(slidesList)
//    }

    private fun visibleInfoProperty(){
        when{
            stateInfoProperty -> {
                binding.imageInfoProperty.setImageResource(R.drawable.ic_down)
                binding.viewLine2.isVisible = false
                binding.viewLocalization.isVisible = true
                binding.viewInfoAdd.isVisible = true
                binding.viewLine3.isVisible = true
                binding.titleLocalization.isVisible = true
                binding.localization.isVisible = true
                binding.titleEnergy.isVisible = true
                binding.txtEnergy.isVisible = true
                binding.titleWater.isVisible = true
                binding.water.isVisible = true
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
        val document = "bfeerv"

        db.collection("Properties").document(document)
            .delete().addOnSuccessListener {
                Toast.makeText(this, "Propriedade excluida com sucesso!", Toast.LENGTH_SHORT).show()
                finish()
            }.addOnFailureListener {
                Log.d(TAG, it.toString())
            }
    }
}