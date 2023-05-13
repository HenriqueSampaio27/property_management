package com.henriqueapps.administraoDeApartamentos.ui.apartamentList

import android.annotation.SuppressLint
import android.media.Image
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.FirebaseStorage
import com.henriqueapps.administraoDeApartamentos.adapter.AdapterApartament
import com.henriqueapps.administraoDeApartamentos.databinding.FragmentApartamentListBinding
import com.henriqueapps.administraoDeApartamentos.model.Apartament


class ApartamentListFragment : Fragment() {

    private var _binding: FragmentApartamentListBinding? = null
    private val binding get() = _binding!!
    private lateinit var adapterApartament: AdapterApartament
    private val listApartament: MutableList<Apartament> = mutableListOf()

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
                    val district = document.data["district"].toString()

                    if (image.isEmpty()){
                        db.collection("Aplication").document("aplicationLogo")
                            .addSnapshotListener { value, error ->
                                if (value != null){
                                    image = value.getString("logo")!!
                                }
                            }
                    }
                    listApartament.add(Apartament(price,logradouro, district, image,  documentId))
                    adapterApartament.notifyDataSetChanged()
                }

            }

    }
}