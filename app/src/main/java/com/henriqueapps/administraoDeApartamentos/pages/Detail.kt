package com.henriqueapps.administraoDeApartamentos.pages

import android.graphics.Color
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.LayoutInflater
import android.view.Menu
import android.view.MenuItem
import androidx.core.view.isVisible
import com.denzcoskun.imageslider.models.SlideModel
import com.henriqueapps.administraoDeApartamentos.R
import com.henriqueapps.administraoDeApartamentos.databinding.ActivityDetailBinding

class Detail : AppCompatActivity() {

    private lateinit var binding: ActivityDetailBinding
    private val slidesList : MutableList<SlideModel> = mutableListOf()
    private var stateInfoProperty : Boolean = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityDetailBinding.inflate(layoutInflater)
        setContentView(binding.root)

        window.statusBarColor = Color.parseColor("#00A86B")
        title = ""
        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        getImage()
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

    private fun getImage(){
        val image1 = SlideModel(R.drawable.logo)
        val image2 = SlideModel(R.drawable.avatar)
        val image3 = SlideModel(R.drawable.ic_apartment)
        slidesList.add(image1)
        slidesList.add(image2)
        slidesList.add(image3)

        val imageSlider = binding.viewSlider
        imageSlider.setImageList(slidesList)
    }

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

        return super.onOptionsItemSelected(item)
    }
}