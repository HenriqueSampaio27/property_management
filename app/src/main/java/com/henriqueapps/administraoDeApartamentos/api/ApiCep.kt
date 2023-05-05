package com.henriqueapps.administraoDeApartamentos.api

import com.henriqueapps.administraoDeApartamentos.model.Address
import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Path

interface ApiCep {
    @GET("ws/{cep}/json/")

    fun setAddresss(@Path("cep") cep: String): Call<Address>
}