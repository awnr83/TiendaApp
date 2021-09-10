package com.moappdev.solutions.tiendaapp.database

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName= "TiendaEntity")
data class Tienda(
    @PrimaryKey(autoGenerate = true)
    var id:Long=0,
    var nombre:String,
    var direccion:String="",
    var telefono: String="",
    var web:String,
    var imagen:String,
    var favorita:Boolean=false)