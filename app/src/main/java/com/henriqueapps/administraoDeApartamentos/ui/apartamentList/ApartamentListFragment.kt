package com.henriqueapps.administraoDeApartamentos.ui.apartamentList

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.henriqueapps.administraoDeApartamentos.R
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
        getApartament()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    private fun getApartament(){
        val ap1 = Apartament("R$ 350,00", "Rua Fernando Sarney", "Vila Marcony", R.drawable.logo)
        listApartament.add(ap1)

        val ap2 = Apartament("R$ 350,00", "Rua Fernando Sarney", "Vila Marcony", R.drawable.ic_launcher_background)
        listApartament.add(ap2)

        val ap3 = Apartament("R$ 350,00", "Rua Fernando Sarney", "Vila Marcony", R.drawable.ic_launcher_background)
        listApartament.add(ap3)

        val ap4 = Apartament("R$ 350,00", "Rua Fernando Sarney", "Vila Marcony", R.drawable.ic_launcher_background)
        listApartament.add(ap4)

        val ap5 = Apartament("R$ 350,00", "Rua Fernando Sarney", "Vila Marcony", R.drawable.ic_launcher_background)
        listApartament.add(ap5)

        val ap6 = Apartament("R$ 350,00", "Rua Fernando Sarney", "Vila Marcony", R.drawable.ic_launcher_background)
        listApartament.add(ap6)

        val ap7 = Apartament("R$ 350,00", "Rua Fernando Sarney", "Vila Marcony", R.drawable.ic_launcher_background)
        listApartament.add(ap7)
    }
}