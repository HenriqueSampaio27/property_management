package com.henriqueapps.administraoDeApartamentos.adapter

import android.annotation.SuppressLint
import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.henriqueapps.administraoDeApartamentos.databinding.NotificationItemBinding
import com.henriqueapps.administraoDeApartamentos.model.NotificationsModel

class AdapterNotifications(private val context: Context, private val listNotifications: MutableList<NotificationsModel>):
    RecyclerView.Adapter<AdapterNotifications.NotificationsViewHolder>(){

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): NotificationsViewHolder {
        val itemList = NotificationItemBinding.inflate(LayoutInflater.from(context), parent, false)
        return NotificationsViewHolder(itemList)
    }

    override fun getItemCount() = listNotifications.size

    @SuppressLint("SetTextI18n")
    override fun onBindViewHolder(holder: NotificationsViewHolder, position: Int) {
        holder.title.text = listNotifications[position].title
        Glide.with(context).asBitmap().load(listNotifications[position].image).into(holder.image)
        holder.price.text = "R$ ${listNotifications[position].price?.toDouble()}"
        holder.date.text = listNotifications[position].date
        holder.address.text = "${listNotifications[position].logradouro}${listNotifications[position].number}"
    }

    inner class NotificationsViewHolder(binding: NotificationItemBinding): RecyclerView.ViewHolder(binding.root){
        val title = binding.textTitle
        val image = binding.imageApartament
        val price = binding.textPrice
        val date  = binding.textDate
        val address = binding.textAddress
    }
}