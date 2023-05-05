package com.henriqueapps.administraoDeApartamentos.adapter

import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.henriqueapps.administraoDeApartamentos.databinding.ApartamentItemBinding
import com.henriqueapps.administraoDeApartamentos.model.Apartament
import com.henriqueapps.administraoDeApartamentos.pages.Detail

class AdapterApartament (private val context: Context, private val listApartament: MutableList<Apartament>):
    RecyclerView.Adapter<AdapterApartament.ApartamentViewHolder>(){

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ApartamentViewHolder {
        val itemList = ApartamentItemBinding.inflate(LayoutInflater.from(context), parent, false)
        return ApartamentViewHolder(itemList)
    }

    override fun onBindViewHolder(holder: ApartamentViewHolder, position: Int) {
        holder.address.text = "${listApartament[position].place}, ${listApartament[position].district}"
        holder.price.text = listApartament[position].price
        Glide.with(context).load(listApartament[position].image).into(holder.image)

        holder.image.setOnClickListener {
            val intent = Intent(context, Detail::class.java)
            context.startActivity(intent)
        }
    }

    override fun getItemCount() = listApartament.size

    inner class ApartamentViewHolder(binding: ApartamentItemBinding): RecyclerView.ViewHolder(binding.root) {
        val address = binding.txtAddress
        val price = binding.price
        val image = binding.imageApartament
    }
}