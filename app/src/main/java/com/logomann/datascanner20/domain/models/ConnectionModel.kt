package com.logomann.datascanner20.domain.models

sealed class ConnectionModel {
    data class Container(
        val number: String,
        val address: Address?,
        val code: Int,
        val wagon: String?
    ) :
        ConnectionModel()

    data class Car(val vin: String, val address: Address?, val code: Int) : ConnectionModel()
    data class Address(val field: Int, val row: Int, val cell: Int, val code: Int) :
        ConnectionModel()

    data class User(val pinCode: String) : ConnectionModel()
    data class Lot(val number: String, val row: String, val code: Int) : ConnectionModel()
    data class Driver(
        val number: String,
        val code: Int,
        val lot: List<String>
    ) : ConnectionModel()
}