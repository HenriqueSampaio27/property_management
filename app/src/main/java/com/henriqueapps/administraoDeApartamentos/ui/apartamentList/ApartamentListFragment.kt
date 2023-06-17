package com.henriqueapps.administraoDeApartamentos.ui.apartamentList

import android.annotation.SuppressLint
import android.content.ContentValues.TAG
import android.content.Intent
import android.media.Image
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.FirebaseStorage
import com.henriqueapps.administraoDeApartamentos.adapter.AdapterApartament
import com.henriqueapps.administraoDeApartamentos.databinding.FragmentApartamentListBinding
import com.henriqueapps.administraoDeApartamentos.model.Apartament
import com.henriqueapps.administraoDeApartamentos.ui.registrationApartament.RegistrationApartament
import com.henriqueapps.administraoDeApartamentos.useful.startActivity


class ApartamentListFragment : Fragment() {

    private var _binding: FragmentApartamentListBinding? = null
    private val binding get() = _binding!!
    private lateinit var adapterApartament: AdapterApartament
    private var listApartament: MutableList<Apartament> = mutableListOf()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {


        _binding = FragmentApartamentListBinding.inflate(inflater, container, false)
        val root: View = binding.root

        return root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val recyclerViewApartament = binding.recyclerViewApartament
        recyclerViewApartament.layoutManager = LinearLayoutManager(this.context)
        adapterApartament = AdapterApartament(this.requireContext(), listApartament)
        recyclerViewApartament.adapter = adapterApartament
        recyclerViewApartament.hasFixedSize()

        binding.progressCircular.isVisible = true

        binding.textRegisterAProperty.setOnClickListener {
            startActivity(RegistrationApartament::class.java)
        }

        Handler(Looper.getMainLooper()).postDelayed({
            binding.progressCircular.isVisible = false
            binding.recyclerViewApartament.isVisible = true
        }, 1500)

    }

    override fun onStart() {
        super.onStart()
        getApartament()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    @SuppressLint("NotifyDataSetChanged")
    private fun getApartament(){
        val db = FirebaseFirestore.getInstance()
        val userUUID = FirebaseAuth.getInstance().currentUser!!.uid

        db.collection("Properties").whereEqualTo("owner", userUUID)
            .get().addOnSuccessListener {documents ->
                for (document in documents){
                    val documentId = document.id
                    var image = document.data["imageOne"].toString()
                    val price = document.data["price"].toString()
                    val logradouro = document.data["logradouro"].toString()
                    val number = document.data["number"].toString()
                    val type = document.data["type"].toString()
                    if (image.isEmpty()){
                        db.collection("Aplication").document("aplicationLogo")
                            .addSnapshotListener { value, error ->
                                if (value != null){
                                    image = value.getString("logo")!!
                                    listApartament.add(Apartament(price,logradouro, number, image,  documentId, type))
                                    adapterApartament.notifyDataSetChanged()

                                    binding.textRegisterAProperty.isVisible = listApartament.size == 0
                                }
                            }

                    }else{
                        listApartament.add(Apartament(price,logradouro, number, image,  documentId, type))
                        adapterApartament.notifyDataSetChanged()
                    }
                }
                binding.textRegisterAProperty.isVisible = listApartament.size == 0
            }
    }

    override fun onPause() {
        super.onPause()
        listApartament = mutableListOf()
    }
}