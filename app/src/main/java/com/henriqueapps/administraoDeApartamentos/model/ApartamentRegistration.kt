package com.henriqueapps.administraoDeApartamentos.model

class ApartamentRegistration(
    var cep: String? = null,
    var logradouro: String? = null,
    var district: String? = null,
    var city: String? = null,
    var state: String? = null,
    var number: String? = null,
    var price: Double? = null,
    var energy: String? = null,
    var water: String? = null
) {

    fun getDate(cep: String?, logradouro: String?, district: String?, city: String?, state: String?, number: String?, price: Double?, energy: String?, water: String?){
        this.cep = cep
        this.logradouro = logradouro
        this.district = district
        this.city = city
        this.state = state
        this.number = number
        this.price = price
        this.energy = energy
        this.water = water
    }
}