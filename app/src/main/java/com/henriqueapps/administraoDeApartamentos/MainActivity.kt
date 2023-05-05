package com.henriqueapps.administraoDeApartamentos

import android.content.Intent
import android.graphics.Color
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.util.DisplayMetrics
import com.henriqueapps.administraoDeApartamentos.databinding.ActivityMainBinding
import com.henriqueapps.administraoDeApartamentos.pages.Login

class MainActivity : AppCompatActivity() {
    private lateinit var mBinding: ActivityMainBinding

    private var mWidth: Int? = null
    private var mHeight: Int? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        mBinding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(mBinding.root)

        supportActionBar!!.hide()
        window.statusBarColor = Color.parseColor("#00A86B")

        val metrics = DisplayMetrics()
        val results = windowManager

        //verificando a versao do sdk
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            mWidth = results.currentWindowMetrics.bounds.width()
            mHeight = results.currentWindowMetrics.bounds.height()
        }else{
            results.defaultDisplay.getMetrics(metrics)
            mWidth = metrics.widthPixels
            mHeight = metrics.heightPixels
        }

        val textTitle = mBinding.title
        val progressCircular = mBinding.progressCircular
        val imageView = mBinding.image

        textTitle.y = (mHeight!! * 0.56).toFloat()
        progressCircular.y = (mHeight!! * 0.56).toFloat()
        imageView.x = (-(mWidth!! * 0.76)).toFloat()

        Handler(Looper.getMainLooper()).postDelayed({
            textTitle.animate().duration = 700
            textTitle.animate().yBy(-(mHeight!! * 0.56).toFloat())
            imageView.animate().duration = 700
            imageView.animate().xBy((mWidth!! * 0.76).toFloat())
            progressCircular.animate().startDelay = 900
            progressCircular.animate().duration = 10
            progressCircular.animate().yBy(-(mHeight!! * 0.56).toFloat())
        }, 800)

        Handler(Looper.getMainLooper()).postDelayed({
            var intent = Intent(this, Login::class.java)
            startActivity(intent)
            finish()
        }, 3000)
    }
}