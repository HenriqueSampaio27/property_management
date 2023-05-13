package com.henriqueapps.administraoDeApartamentos.pages

import android.content.ContentValues.TAG
import android.content.DialogInterface
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.core.view.isVisible
import com.google.firebase.firestore.FirebaseFirestore
import com.henriqueapps.administraoDeApartamentos.R
import com.henriqueapps.administraoDeApartamentos.databinding.ActivityEditApartamentBinding
import com.henriqueapps.administraoDeApartamentos.databinding.DialogEnergyUpdateBinding
import com.henriqueapps.administraoDeApartamentos.databinding.DialogNumberUpdateBinding
import com.henriqueapps.administraoDeApartamentos.databinding.DialogUpdateEmailBinding
import com.henriqueapps.administraoDeApartamentos.databinding.DialogUpdateValueBinding
import com.henriqueapps.administraoDeApartamentos.databinding.DialogWaterUpdateBinding

class EditApartament : AppCompatActivity() {

    private lateinit var binding: ActivityEditApartamentBinding

    private lateinit var db : FirebaseFirestore
    private lateinit var documentId : String
    private lateinit var dialog : AlertDialog

    private lateinit var updateNumber : String
    private lateinit var updateWater : String
    private lateinit var updateEnergy: String
    private lateinit var updateValue: String

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityEditApartamentBinding.inflate(layoutInflater)
        setContentView(binding.root)

        supportActionBar!!.hide()

        db = FirebaseFirestore.getInstance()
        documentId = intent.getStringExtra("documentId")!!

        verificationOptions()

        binding.buttonBack.setOnClickListener {
            finish()
        }

        binding.addNumber.setOnClickListener {
            dialogUpdateNumber(it)
        }

        binding.addWater.setOnClickListener {
            dialogUpdateWater(it)
        }

        binding.addNumber.setOnClickListener {
            dialogUpdateEnergy(it)
        }

        binding.changeValue.setOnClickListener {
            dialogUpdateValue(it)
        }

        binding.changeImage.setOnClickListener {
            val intent = Intent(this, AddImage::class.java)
            intent.putExtra("documentId", documentId)
            startActivity(intent)
        }

    }

    private fun verificationOptions(){
        var number = ""
        var energy = ""
        var water = ""

        //val documentId = "308d7b6a-d56b-47cc-9c60-c39963c30b57"
        db.collection("Properties").document(documentId!!)
            .get().addOnSuccessListener {   document ->
                number = document.data!!["number"].toString()
                energy = document.data!!["energy"].toString()
                water = document.data!!["water"].toString()
            }.addOnFailureListener {
                Log.d(TAG, it.toString())
            }

        binding.addEnergy.isVisible = energy.isEmpty()
        binding.addWater.isVisible = water.isEmpty()
        binding.addNumber.isVisible = number.isEmpty()

    }

    private fun dialogUpdateNumber(view: View) {
        val inflater = layoutInflater
        val update = AlertDialog.Builder(view.context)
            .setTitle("Insira o número da Propriedade")
            .setView(inflater.inflate(R.layout.dialog_number_update, null))
            .setPositiveButton("Salvar",
                DialogInterface.OnClickListener { dialogInterface, id ->
                    updateNumber = DialogNumberUpdateBinding.inflate(layoutInflater).updateNumber.text.toString()
                    db.collection("Properties").document(documentId)
                        .update(
                            mapOf(
                                "number" to updateNumber
                            )
                        ).addOnSuccessListener {
                            Toast.makeText(this, "Número adicionado com sucesso!", Toast.LENGTH_SHORT).show()
                        }.addOnFailureListener {
                            Log.d(TAG, it.toString())
                        }
                })
            .setNegativeButton("Cancelar",
                DialogInterface.OnClickListener { dialogInterface, id ->
                    dialog.dismiss()
                })

        dialog = update.create()
        dialog.show()

    }

    private fun dialogUpdateValue(view: View) {
        val inflater = layoutInflater
        val update = AlertDialog.Builder(view.context)
            .setTitle("Insira o novo valor da propriedade")
            .setView(inflater.inflate(R.layout.dialog_update_value, null))
            .setPositiveButton("Atualizar",
                DialogInterface.OnClickListener { dialogInterface, id ->
                    updateValue = DialogUpdateValueBinding.inflate(layoutInflater).updateValue.text.toString()
                    db.collection("Properties").document(documentId)
                        .update(
                            mapOf(
                                "price" to updateValue
                            )
                        ).addOnSuccessListener {
                            Toast.makeText(this, "Valor atualizado com sucesso!", Toast.LENGTH_SHORT).show()
                        }.addOnFailureListener {
                            Log.d(TAG, it.toString())
                        }
                })
            .setNegativeButton("Cancelar",
                DialogInterface.OnClickListener { dialogInterface, id ->
                    dialog.dismiss()
                })

        dialog = update.create()
        dialog.show()

    }

    private fun dialogUpdateWater(view: View) {
        val inflater = layoutInflater
        val update = AlertDialog.Builder(view.context)
            .setTitle("Insira o número da água")
            .setMessage("Número localizado na matricula do papel!")
            .setView(inflater.inflate(R.layout.dialog_water_update, null))
            .setPositiveButton("Salvar",
                DialogInterface.OnClickListener { dialogInterface, id ->
                    updateWater = DialogWaterUpdateBinding.inflate(layoutInflater).waterUpdate.text.toString()
                    db.collection("Properties").document(documentId)
                        .update(
                            mapOf(
                                "water" to updateWater
                            )
                        ).addOnSuccessListener {
                            Toast.makeText(this, "Matricula de água adicionada com sucesso!", Toast.LENGTH_SHORT).show()
                        }.addOnFailureListener {
                            Log.d(TAG, it.toString())
                        }
                })
            .setNegativeButton("Cancelar",
                DialogInterface.OnClickListener { dialogInterface, id ->
                    dialog.dismiss()
                })

        dialog = update.create()
        dialog.show()

    }

    private fun dialogUpdateEnergy(view: View) {
        val inflater = layoutInflater
        val update = AlertDialog.Builder(view.context)
            .setTitle("Insira o número da Energia")
            .setMessage("Número localizado na conta contrato do papel!")
            .setView(inflater.inflate(R.layout.dialog_energy_update, null))
            .setPositiveButton("Salvar",
                DialogInterface.OnClickListener { dialogInterface, id ->
                    updateEnergy = DialogEnergyUpdateBinding.inflate(layoutInflater).energyUpdate.text.toString()
                    db.collection("Properties").document(documentId)
                        .update(
                            mapOf(
                                "energy" to updateEnergy
                            )
                        ).addOnSuccessListener {
                            Toast.makeText(this, "Conta contrato adicionada com sucesso!", Toast.LENGTH_SHORT).show()
                        }.addOnFailureListener {
                            Log.d(TAG, it.toString())
                        }
                })
            .setNegativeButton("Cancelar",
                DialogInterface.OnClickListener { dialogInterface, id ->
                    dialog.dismiss()
                })

        dialog = update.create()
        dialog.show()

    }
}