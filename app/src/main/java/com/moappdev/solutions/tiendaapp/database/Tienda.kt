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
    var web:String="",
    var imagen:String,
    var favorita:Boolean=false){

    //se obtiene con Alt + Ins para comprobacion solo por ID
    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false
        other as Tienda
        if (id != other.id) return false
        return true
    }
    override fun hashCode(): Int {
        return id.hashCode()
    }
}