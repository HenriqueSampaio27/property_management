package com.henriqueapps.administraoDeApartamentos.adapter

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.graphics.BitmapFactory
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

    @SuppressLint("SetTextI18n")
    override fun onBindViewHolder(holder: ApartamentViewHolder, position: Int) {
        holder.address.text = "${listApartament[position].logradouro}, ${listApartament[position].district}"
        holder.price.text = "R$ ${String.format("%.2f", listApartament[position].price.toString().toDouble())}"
        holder.type.text = listApartament[position].type
        Glide.with(context).asBitmap().load(listApartament[position].image).into(holder.image)

        holder.image.setOnClickListener {
            val intent = Intent(context, Detail::class.java)
            intent.putExtra("documentId", listApartament[position].documentID)
            context.startActivity(intent)
        }
    }

    override fun getItemCount() = listApartament.size

    inner class ApartamentViewHolder(binding: ApartamentItemBinding): RecyclerView.ViewHolder(binding.root) {
        val address = binding.txtAddress
        val price = binding.price
        val image = binding.imageApartament
        val type = binding.idType
    }
}